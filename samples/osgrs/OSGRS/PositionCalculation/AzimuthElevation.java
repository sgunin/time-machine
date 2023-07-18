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
 * This class is used to hold Azimuth,Elevation.
 * Both prameters are stored as radians but their values
 * in degrees can be requested from two private methods
 * in this object
 * @author Manosh Fernando
 *
 */
public class AzimuthElevation
{
	private double azimuthRad;
	
	private double elevationRad;
	
	public AzimuthElevation(double azimuthRad, double elevationRad)
	{
		this.azimuthRad = azimuthRad;
		this.elevationRad = elevationRad;
	}

	public double getAzimuthRad() 
	{
		return this.azimuthRad;
	}

	public double getElevationRad() 
	{
		return this.elevationRad;
	}
	
	public double getAzimuthDegrees()
	{
		return Math.toDegrees(this.azimuthRad);
	}
	
	public double getElevationDegrees()
	{
		return Math.toDegrees(this.elevationRad);
	}
}