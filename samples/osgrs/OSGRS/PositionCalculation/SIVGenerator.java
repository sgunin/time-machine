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

import java.util.Date;

import OSGRS.dataType.GPSDateTime;
import OSGRS.dataType.IonUTCModel;
import OSGRS.dataType.SatelliteEphemeris;
import OSGRS.dataType.SatellitesInView;

/**
 * This class is used to generate a SIV object from ephemiris data given a 
 * users lat,long, alt.
 * @author Manosh Fernando
 *
 */
public class SIVGenerator
{
	private static final int ELEVATION_CUTOFF_ANGLE = 0;

	private SatelliteEphemeris[] satelliteEphemerisArray;

	private double latitude;
	private double longitude;
	private double altitude;

	private SatellitesInView SIVObject;
	private WGS84ECEFXYZ clientPosition;
	private GPSDateTime referenceTime;
	private IonUTCModel ionUTCModel;

	private Integer[] tempSIV; //to hold PRNS of sats if they are deemed to be above elevation cut-off
	private int n; //index variable for tempSIVarray

	public SIVGenerator (double latitude, double longitude, double altitude, SatelliteEphemeris[] satelliteEphemerisArray,
			IonUTCModel ionUTCModel) throws IllegalArgumentException
	{
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.satelliteEphemerisArray = satelliteEphemerisArray;
		this.ionUTCModel = ionUTCModel;

		init();
		run();
	}

	private void init()
	{
		this.clientPosition = new WGS84ECEFXYZ();
		this.clientPosition.setFromLongLatAlt(Math.toRadians(this.latitude), Math.toRadians(this.longitude)
				, this.altitude);

		this.tempSIV = new Integer[32];
		this.n = 0;
	}

	private void run()
	{
		checkElevation();
	}

	/**
	 * This method checks the elevation of all the satellites this class has data for
	 * and if its above the cut-off angle will deem to be in view and add it to 
	 * the tempSIV array.
	 *
	 */
	private void checkElevation()
	{
		if(this.satelliteEphemerisArray != null && this.ionUTCModel != null)
		{
			this.referenceTime = new GPSDateTime();
			this.referenceTime.createGPSTowFromSystemTime(this.ionUTCModel);

			double timeOfWeek = this.referenceTime.getGpsSecondsOfWeek();

			for(int i = 0; i < this.satelliteEphemerisArray.length; i++ )
			{
				if(this.satelliteEphemerisArray[i] != null)
				{
					SatellitePosition satellitePosition = new SatellitePosition(this.satelliteEphemerisArray[i], 
							timeOfWeek);

					WGS84ECEFXYZ satellitePositionECEF = satellitePosition.getSatellitePositionECEFXYZ();


					AzimuthElevation azimuthElevation = 
						this.clientPosition.caclulateAzimuthElevation(satellitePositionECEF);

					if(azimuthElevation.getElevationDegrees() > ELEVATION_CUTOFF_ANGLE )
					{
						this.tempSIV[n] = new Integer(this.satelliteEphemerisArray[i].getPRN());
						n++;
					}
				}
			}

		}else
		{
			throw new IllegalArgumentException("null object/s passed to SIV Generator");
		}
	}

	/**
	 * creates SIV object from PRNS in tempSIV
	 * @return SIV object
	 */
	public SatellitesInView getSatellitesInView()
	{
		//count integers in tempSIV array
		
		int numberOfSats = 0;
		
		for (int i = 0; i < this.tempSIV.length; i++)
		{
			if(this.tempSIV[i] != null)
			{
				numberOfSats++;
			}
		}
		
		short[] badSats = new short[numberOfSats];
		
		int p = 0; //index variable for badSats
		
		for(int i = 0; i < this.tempSIV.length; i++)
		{
			if(this.tempSIV[i] != null)
			{
				badSats[p] = this.tempSIV[i].shortValue();
				p++;
			}
		}
		
		return new SatellitesInView((short)numberOfSats, badSats, new Date());
	}
}