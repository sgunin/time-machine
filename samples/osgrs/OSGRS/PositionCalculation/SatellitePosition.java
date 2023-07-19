/*

	Copyright (C) 2007 SNAPlab

	http://www.gmat.unsw.edu.au/snap/

	This file is part of OSGRS.

	OSGRS is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	OSGRS is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with OSGRS; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */

package OSGRS.PositionCalculation;

import OSGRS.Util.GNSSUtil;
import OSGRS.dataType.SatelliteEphemeris;

/**
 * This class uses satellite ephemeris data to caclulate the position of a 
 * GPS satellite at a moment in time in WGS84 ECEF XYZ
 * @author Manosh Fernando
 *
 */
public class SatellitePosition
{
	private static final double MU = 3.986005e14;
	private static final double  WEDOT = 7.2921151467e-5;
	private static final double  PI = 3.1415926535898;

	private int PRN;

	private int satID;

//	requred parameters
	private double M0, dn, ec, rootA, W0, i0, w, Wdot, Idot, Cuc, Cus,
	Crc, Crs, Cic, Cis, Toe, Ttr;

	//parameters to generate in class
	private double A, n0, T, n, M, E, snu, cnu, nu, phi, du, dr, di, u, r, i, Xdash, Ydash,
	Wc, X, Y, Z; 

	private WGS84ECEFXYZ satellitePositionECEFXYZ;

	public SatellitePosition(SatelliteEphemeris satelliteEphemeris)
	{
		init(satelliteEphemeris);
		run();
	}

	public SatellitePosition(SatelliteEphemeris satelliteEphemeris, double Ttr)
	{
		this.Ttr = Ttr;
		init(satelliteEphemeris);
		run();
	}

	private void init(SatelliteEphemeris satelliteEphemeris)
	{
		getRequiredParametersFromSatelliteEphemeris(satelliteEphemeris);
		convertSemiCircleParametersToRadians();
		//printRequiredEphemerisParameters();
	}

	private void run()
	{
		calculateSatellitePosition();
	}

	private void convertSemiCircleParametersToRadians()
	{	
		this.M0 *= PI;
		this.dn *= PI;
		this.W0 *= PI;
		this.i0 *= PI;
		this.w *= PI;
		this.Wdot *= PI;
		this.Idot *= PI;
	}

	public void calculateSatellitePosition()
	{
		this.A = Math.pow(this.rootA, 2); //Semi major axis 

		this.n0 = Math.sqrt((MU/Math.pow(this.A,3))); //computed mean motion

		this.T = this.Ttr - this.Toe; //time from ephemeris reference epoch

		if(this.T > 302400)
		{
			this.T = this.T - 604800;
		} else
			if(this.T < -302400)
			{
				this.T = this.T+ 604800;
			}

		this.n = this.n0 + this.dn; //corrected mean motion

		this.M = this.M0 + this.n*this.T; //mean anomaly

		//work out e through iteration

		this.E = this.M;

		double Eold;

		do
		{
			Eold = this.E;
			this.E = this.M + this.ec * Math.sin(this.E);
		} while(Math.abs(this.E - Eold) >= 1.0e-8);

		this.snu = (Math.sqrt(1 - this.ec * this.ec)*Math.sin(this.E))
		/(1-this.ec*Math.cos(this.E));//sine of true anomaly

		this.cnu = (Math.cos(this.E) - this.ec)/(1 - this.ec * Math.cos(this.E));//cosine
		//of true anomaly

		this.nu = Math.atan2(this.snu, this.cnu); //true anomaly

		this.phi = this.nu + this.w; //argument of lattitude 

		this.du = this.Cuc * Math.cos(2 * this.phi) + this.Cus * Math.sin(2 * this.phi);
		//du-argument of lattitude correction
		this.dr = this.Crc * Math.cos(2 * this.phi) + this.Crs * Math.sin(2 * this.phi);
		//dr-raduis correction
		this.di = this.Cic * Math.cos(2 * this.phi) + this.Cis * Math.sin(2 * this.phi);
		//di-correction to inclination

		this.u = this.phi + this.du; //corrected argument of lattitude
		this.r = this.A * (1 - this.ec * Math.cos(this.E)) + this.dr; //corrected radius
		this.i = this.i0 + this.Idot * this.T + this.di;//corrected inclination

		this.Xdash = this.r * Math.cos(u); //position x in orbital plane
		this.Ydash = this.r * Math.sin(u); //position y in orbital plane

		this.Wc = this.W0 + (this.Wdot - WEDOT) * this.T - WEDOT * this.Toe;
		//Wc - corrected longitude of ascending node

		this.X = this.Xdash * Math.cos(this.Wc) - this.Ydash * Math.cos(this.i) * 
		Math.sin(this.Wc); //ECEF x
		this.Y = this.Xdash * Math.sin(this.Wc) + this.Ydash * Math.cos(this.i) * 
		Math.cos(this.Wc); //ECEF y
		this.Z = this.Ydash * Math.sin(this.i); //ECEF z

		satellitePositionECEFXYZ = new WGS84ECEFXYZ(this.X, this.Y, this.Z);
	}

	private void getRequiredParametersFromSatelliteEphemeris(SatelliteEphemeris satelliteEphemeris)
	{
		this.satID = satelliteEphemeris.getSatID();

		this.PRN = satelliteEphemeris.getPRN();

		long unscaledRootA = satelliteEphemeris.getSqrtA();
		this.rootA = unscaledRootA * Math.pow(2, -19);

		long unscaledM0 = satelliteEphemeris.getM0_meanAnomoloyAtReferenceTimeSemiCircles();
		this.M0 = GNSSUtil.twosComplementToInteger(unscaledM0, 32) * Math.pow(2, -31);

		int unscaledDN = satelliteEphemeris.getDeltaN_meanMotionDifferenceFromComputedValueSemiCirclesPerSecond();
		this.dn = GNSSUtil.twosComplementToInteger(unscaledDN, 16) * Math.pow(2, -43);

		long unscaledEC = satelliteEphemeris.getE();
		this.ec = unscaledEC * Math.pow(2, -33);

		long unscaledW0 = satelliteEphemeris.getOMEGA0SemiCirclesUnscaled();
		this.W0 = GNSSUtil.twosComplementToInteger(unscaledW0, 32) * Math.pow(2, -31);

		long unscaledi0 = satelliteEphemeris.getI0SemiCircles();
		this.i0 = GNSSUtil.twosComplementToInteger(unscaledi0, 32) * Math.pow(2, -31);

		long unscaledW = satelliteEphemeris.getOmegaSemiCircles();
		this.w = GNSSUtil.twosComplementToInteger(unscaledW, 32) * Math.pow(2, -31);

		int unscaledWdot = satelliteEphemeris.getOMEGADotSemiCirclesUnscaled();
		this.Wdot = GNSSUtil.twosComplementToInteger(unscaledWdot, 24) * Math.pow(2, -43);

		int unscaledIdot = satelliteEphemeris.getIdotSemiCircles();
		this.Idot = GNSSUtil.twosComplementToInteger(unscaledIdot, 14) * Math.pow(2, -43);

		int unscaledCuc = satelliteEphemeris.getCucUnscaled();
		this.Cuc = GNSSUtil.twosComplementToInteger(unscaledCuc, 16) * Math.pow(2, -29);

		int unscaledCus = satelliteEphemeris.getCus();
		this.Cus = GNSSUtil.twosComplementToInteger(unscaledCus, 16) * Math.pow(2, -29);

		int unscaledCrc = satelliteEphemeris.getCrc();
		this.Crc = GNSSUtil.twosComplementToInteger(unscaledCrc, 16) * Math.pow(2, -5);

		int unscaledCrs = satelliteEphemeris.getCrs();
		this.Crs = GNSSUtil.twosComplementToInteger(unscaledCrs, 16) * Math.pow(2, -5);

		int unscaledCic = satelliteEphemeris.getCic();
		this.Cic = GNSSUtil.twosComplementToInteger(unscaledCic, 16) * Math.pow(2, -29);

		int unscaledCis = satelliteEphemeris.getCis();
		this.Cis = GNSSUtil.twosComplementToInteger(unscaledCis, 16) * Math.pow(2, -29);

		int unscaledToe = satelliteEphemeris.getToe();
		this.Toe = unscaledToe * Math.pow(2, 4);

	}

	private void printRequiredEphemerisParameters()
	{
		System.out.println("PRN: " + this.PRN);
		System.out.println("satID: " + this.satID);
		System.out.println("M0: " + this.M0);
		System.out.println("dn: " + this.dn);
		System.out.println("ec: " + this.ec);
		System.out.println("rootA: " + this.rootA);
		System.out.println("W0: " + this.W0);
		System.out.println("i0: " + this.i0);
		System.out.println("w: " + this.w);
		System.out.println("Wdot: " + this.Wdot);
		System.out.println("Idot: " + this.Idot);
		System.out.println("Cuc: " + this.Cuc);
		System.out.println("Cus: " + this.Cus);
		System.out.println("Crc: " + this.Crc);
		System.out.println("Crs: " + this.Crs);
		System.out.println("Cic: " + this.Cic);
		System.out.println("Cis: " + this.Cis);
		System.out.println("Toe: " + this.Toe);
	}

	public double getX() 
	{
		return this.X;
	}

	public double getY() 
	{
		return this.Y;
	}

	public double getZ() 
	{
		return this.Z;
	}

	public int getPRN() 
	{
		return this.PRN;
	}

	public int getSatID() 
	{
		return this.satID;
	}

	public double getTtr() {
		return Ttr;
	}

	public void setTtr(double ttr) {
		Ttr = ttr;
	}

	public WGS84ECEFXYZ getSatellitePositionECEFXYZ() 
	{
		return this.satellitePositionECEFXYZ;
	}
}