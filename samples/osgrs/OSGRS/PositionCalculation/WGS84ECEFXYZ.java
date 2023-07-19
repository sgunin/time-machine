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

/**
 * This class will be used to represent positions in 
 * ECEF XYZ co-ordinates. It also can perferm calculations on two 
 * WGS84ECEFXYZ objects to derive the azimuth and elevation
 * @author Manosh Fernando
 *
 */
public class WGS84ECEFXYZ
{
	/** The WGS84 ellipsoid semi major axis. */
	public final static double WGS84_SEMI_MAJOR_AXIS = 6378137.0;

	/** The WGS84 ellipsoid semi minor axis. */
	public final static double WGS84_SEMI_MINOR_AXIS = 6356752.3142;

	/** The WGS84 flattening constant. */
	protected final static double WGS84_FLATTENING_CONSTANT = (WGS84_SEMI_MAJOR_AXIS - WGS84_SEMI_MINOR_AXIS) / WGS84_SEMI_MAJOR_AXIS;

	/** The WGS84 eccentricity squared. */
	protected final static double WGS84_ECCENTRICITY_SQUARED = (2 * WGS84_FLATTENING_CONSTANT) - Math.pow(WGS84_FLATTENING_CONSTANT,2);

	/** X co-ordinate in meters in WGS84 */
	private double X;

	/** Y co-ordinate in meters in WGS84 */
	private double Y;

	/** Z co-ordinate in meters in WGS84 */
	private double Z;

	/**
	 * default constructor
	 */
	public WGS84ECEFXYZ()
	{

	}

	/**
	 * Sets object parameters using WGS84 XYZ co-ordinates
	 * @param X - WGS84 X co-ordinate in meters
	 * @param Y	- WGS84 Y co-ordinate in meters
	 * @param Z - WGS84 Z co-ordinate in meters
	 */
	public WGS84ECEFXYZ (double X, double Y, double Z)
	{
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	/**
	 * Sets object parameters from latitude, longitude and altitude 
	 * @param latRads - Latitude in radians in the WGS84 geodetic coordinate system.
	 * @param longRads - Longitude in radians in the WGS84 geodetic coordinate system.
	 * @param alt - Altitude in meters in the WGS84 geodetic coordinate system.
	 */
	public void setFromLongLatAlt(double latRads, double longRads, double alt) throws IllegalArgumentException
	{
		if(!((Math.toDegrees(latRads) >= -90 && Math.toDegrees(latRads) <= 90 &&
				Math.toDegrees(longRads) >= -180 && Math.toDegrees(longRads) <= 180)))
		{
			throw new IllegalArgumentException("parameters outside range. lat: " +Math.toDegrees(latRads) 
					+ " long: " + Math.toDegrees(longRads));
		}

		double N = WGS84_SEMI_MAJOR_AXIS/(Math.sqrt(1 - WGS84_ECCENTRICITY_SQUARED*Math.sin(latRads)*Math.sin(latRads)));

		this.X = (N + alt) * Math.cos(latRads) * Math.cos(longRads);
		this.Y = (N + alt) * Math.cos(latRads) * Math.sin(longRads);
		this.Z = (N*(1 - WGS84_ECCENTRICITY_SQUARED) + alt) * Math.sin(latRads);

	}

	/**
	 * 
	 * @param that - position of object to find azimut and elevation of
	 * @return AzimuthElevation - Azimuth and elevation of position provided with
	 *  						  respect to this position 
	 * 
	 * http://home.tiscali.nl/~samsvl/calcazel.htm
	 * I got the equations and stuff for this method from the above site
	 * .It assumes a spherical model of the earth
	 * 
	 */
	public AzimuthElevation caclulateAzimuthElevation(WGS84ECEFXYZ that)
	{
		double p = Math.sqrt((this.X*this.X) + (this.Y*this.Y));
		double R = Math.sqrt((this.X*this.X)+ (this.Y*this.Y) + (this.Z*this.Z));

		//north,east and up unit vectors
		double[] east = new double[3];
		double[] north = new double[3];
		double[] up = new double[3];

		//unit direction vector from reciever to the SV
		double[] V = new double[3];

		east[0] = -1 * this.Y/p;
		east[1] = this.X/p;
		east[2] = 0;

		north[0] = -1 * (this.X * this.Z)/(p * R);
		north[1] = -1 * (this.Y * this.Z)/(p * R);
		north[2] =  p / R;

		up[0] = this.X / R;
		up[1] = this.Y / R;
		up[2] = this.Z / R;

		double D = distanceTo(that);

		V[0] = (that.getX() - this.X) / D;
		V[1] = (that.getY() - this.Y) / D;
		V[2] = (that.getZ() - this.Z) / D;

		double scalarProduct = 0;

		for(int i = 0; i < 3; i++)
		{
			scalarProduct += V[i] * up[i];
		}

		//Round the scalar produce off so that it dosen't go over the limits
		//of asin

		if(scalarProduct < -1)
		{
			scalarProduct = -1;
		} else
		{
			if(scalarProduct > 1)
			{
				scalarProduct =1;
			}
		}

		double elevationRads = Math.asin(scalarProduct);

		double northComponent = 0;
		double eastComponent = 0;

		for(int i = 0; i < 3; i++)
		{
			northComponent += V[i] * D * north[i];
			eastComponent += V[i] * D * east[i];
		}

		double azimuthRadians = 0;

		if (northComponent == 0)
		{
			if (eastComponent > 0)
			{
				azimuthRadians = Math.toRadians(90);
			}
			else
			{
				if (eastComponent < 0)
				{
					azimuthRadians = Math.toRadians(270);
				}
			}
		}
		else
		{
			azimuthRadians = Math.atan(eastComponent / northComponent);
		}

//		Get the azimuth into the range of 0 to 360 degrees
		if (azimuthRadians == -0)
		{
			azimuthRadians = 0;
		}
		else
		{
			if (northComponent < 0)
			{
				azimuthRadians = Math.toRadians(180) + azimuthRadians; 
			}
			else if ((northComponent > 0) && (eastComponent < 0))
			{
				azimuthRadians = Math.toRadians(360) + azimuthRadians;
			}
		}

		if(!(elevationRads >= Math.toRadians(-90) && elevationRads <= Math.toRadians(90)))
		{
			throw new IllegalArgumentException("elevation out of range. elevation(deg): " + 
					Math.toDegrees(elevationRads));
		}

		if(!(azimuthRadians >= 0 && azimuthRadians <= Math.toRadians(360)))
		{
			throw new IllegalArgumentException("azimuth out of range. azimuth(deg): " + 
					Math.toDegrees(azimuthRadians));
		}

		return new AzimuthElevation(azimuthRadians, elevationRads);
	}

	/**
	 * gets distance from this position to position provided
	 * @param that
	 * @return
	 */
	private double distanceTo(WGS84ECEFXYZ that)
	{
		double difX = that.getX() - this.X;
		double difY = that.getY() - this.Y;
		double difZ = that.getZ() - this.Z;

		double D = Math.sqrt((difX*difX) + (difY*difY) + (difZ*difZ));

		return D;
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

}