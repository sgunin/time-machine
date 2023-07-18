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

package OSGRS.dataType;

/**
 * Class which hold Almanac parameters in GPSAlmanac
 * @author Manosh Fernando
 *
 */

public class AlmanacSatelliteParameters implements DataType
{
	/**Max value for 6 bit fields*/
	private static final byte MAX_VALUE_6_BITS = (byte)(Math.pow(2, 6)-1.0);

	/**Max value for eight bit fields*/
	private static final short MAX_VALUE_8_BITS = (short)(Math.pow(2, 8)-1.0);

	/**Max value for 11 bit fields*/
	private static final short MAX_VALUE_11_BITS = (short)(Math.pow(2, 11)-1.0);

	/**Max value for 16 bit fields*/
	private static final int MAX_VALUE_16_BITS = (int)(Math.pow(2, 16)-1.0);

	/**Max value for 24 bit fields*/
	private static final int MAX_VALUE_24_BITS = (int)(Math.pow(2, 24) - 1.0);

	/**max number of satellites in GPS fleet */
	private static final int NUMBER_OF_SATS_IN_FLEET = 32;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** array of satID/PRN */
	private byte satID;

	/** array containing eccentricity of each satellite */
	private int e;

	/** array containing almanac refence time t_oa */
	private short t_oa;

	/** array containing angle of inclination relative to 0.3 semi-circles */
	private int deltaT;

	/** array containing rate of right ascension (omegadot) */
	private int omegaDot;

	/** array containing SV Health from almanac */
	private short svHealth;

	/** array containing semi major axis (A)*/
	private int semiMajorAxisA;

	/** array containing right ascension (omega0) */
	private int omega0;

	/** array containing argument of perigee (omega) */
	private int argumentOfPerigeeOmega;

	/** array containing mean anomaly of refence time(M) */
	private int meanAnomalyOfReferenceM;

	/** array containing clock aging parameter, seconds */
	private short af0;

	/** array containing clock aging parameter, seconds/second */
	private short af1;

	/**
	 * 
	 * @param satID
	 * @param e
	 * @param t_oa
	 * @param deltaT
	 * @param omegaDot
	 * @param svHealth
	 * @param semiMajorAxisA
	 * @param omega0
	 * @param argumentOfPerigeeOmega
	 * @param meanAnomalyOfReferenceM
	 * @param af0
	 * @param af1
	 */
	public AlmanacSatelliteParameters(
			byte satID,
			int e,
			short t_oa,
			int deltaT,
			int omegaDot,
			short svHealth,
			int semiMajorAxisA,
			int omega0,
			int argumentOfPerigeeOmega,
			int meanAnomalyOfReferenceM,
			short af0,
			short af1)
	{
		this.satID = satID;
		this.e = e;
		this.t_oa = t_oa;
		this.deltaT = deltaT;
		this.omegaDot = omegaDot;
		this.svHealth = svHealth;
		this.semiMajorAxisA = semiMajorAxisA;
		this.omega0 = omega0;
		this.argumentOfPerigeeOmega = argumentOfPerigeeOmega;
		this.meanAnomalyOfReferenceM = meanAnomalyOfReferenceM;
		this.af0 = af0;
		this.af1 = af1;
		
		checkRange();
	}

	public String getParametersString()
	{
		return "satID: " + this.satID + " e: " + this.e + " t_oa: " + this.t_oa + " deltaT: " + this.deltaT
		+ " omegaDot: " + this.omegaDot + " svHealth: " + this.svHealth + " semiMajorAxisA: " + this.semiMajorAxisA
		+ " omega0: " + this.omega0 + " argumentOfPerigeeOmega: " + this.argumentOfPerigeeOmega 
		+ " meanAnomalyOfReferenceM: " + this.meanAnomalyOfReferenceM + " af0: " + this.af0 + " af1: " + this.af1;
	}



	public void checkRange() throws IllegalArgumentException
	{
		if (this.e < 0 || this.e > MAX_VALUE_16_BITS)
		{
			throw new IllegalArgumentException("e is out of range. e: " + this.e);
		}

		if(this.t_oa < 0 || this.t_oa > MAX_VALUE_8_BITS)
		{
			throw new IllegalArgumentException("t_oa is out of range. t_oa: " + this.t_oa);
		}

		if(this.deltaT < 0 || this.deltaT > MAX_VALUE_16_BITS)
		{
			throw new IllegalArgumentException("deltaT is out of range. deltaT: " + this.deltaT);
		}

		if(this.omegaDot < 0 || this.omegaDot > MAX_VALUE_16_BITS)
		{
			throw new IllegalArgumentException("omegaDot is out of range. omegaDot: " + this.omegaDot);
		}

		if(this.semiMajorAxisA < 0 || this.semiMajorAxisA > MAX_VALUE_24_BITS)
		{
			throw new IllegalArgumentException("semiMajorAxisA is out of range. semiMajorAxisA: " + this.semiMajorAxisA);
		}

		if(this.omega0 < 0 || this.omega0 > MAX_VALUE_24_BITS)
		{
			throw new IllegalArgumentException("omega0 is out of range. omega0: " + this.omega0);
		}

		if (this.argumentOfPerigeeOmega < 0 || this.argumentOfPerigeeOmega > MAX_VALUE_24_BITS)
		{
			throw new IllegalArgumentException("argumentOfPerigeeOmega is out of range. argumentOfPerigeeOmega: " 
					+ this.argumentOfPerigeeOmega);
		}

		if(this.meanAnomalyOfReferenceM < 0 || this.meanAnomalyOfReferenceM > MAX_VALUE_24_BITS)
		{
			throw new IllegalArgumentException("meanAnomalyOfReferenceM is out of range. meanAnomalyOfReferenceM: "
					+ this.meanAnomalyOfReferenceM);
		}

		if (this.af0 < 0 || this.af0 > MAX_VALUE_11_BITS)
		{
			throw new IllegalArgumentException("af0 is out of range. af0: " + this.af0);
		}

		if(this.af1 < 0 || this.af1 > MAX_VALUE_11_BITS)
		{
			throw new IllegalArgumentException("af1 is out of range. af1: " + this.af1);
		}

		if(this.svHealth < 0 || this.svHealth > MAX_VALUE_8_BITS)
		{
			throw new IllegalArgumentException("svHealth is out of range. svHealth: " + this.svHealth);
		}

		if(this.satID < 0 || this.satID > NUMBER_OF_SATS_IN_FLEET -1)
		{
			throw new IllegalArgumentException("satId is out of range. satID: " + this.satID);
		}
	}

	public short getAf0() 
	{
		return this.af0;
	}

	public short getAf1() 
	{
		return this.af1;
	}

	public int getArgumentOfPerigeeOmega() 
	{
		return this.argumentOfPerigeeOmega;
	}

	public int getDeltaT() 
	{
		return this.deltaT;
	}

	public int getE() 
	{
		return this.e;
	}

	public int getMeanAnomalyOfReferenceM() 
	{
		return this.meanAnomalyOfReferenceM;
	}

	public int getOmega0() 
	{
		return this.omega0;
	}

	public int getOmegaDot() 
	{
		return this.omegaDot;
	}

	public byte getSatID() 
	{
		return this.satID;
	}

	public int getSemiMajorAxisA() 
	{
		return this.semiMajorAxisA;
	}

	public short getSvHealth() 
	{
		return this.svHealth;
	}

	public short getT_oa() 
	{
		return this.t_oa;
	}
}