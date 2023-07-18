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

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import OSGRS.Util.DebugLogger;

/**
 * This class holds real time integrity information
 * @author Manosh Fernando
 *
 */
public class RTI implements DataType
{
	private static final short PRN_MAXIMUM_VALUE = 32;

	/** number of bad sattelites */
	private byte numberOfBadSatellites;

	/** array containing svID's (same as PRN) of bad satellites */
	private byte[] badSVID;

	private Date RTITimestamp;

	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	public RTI(byte numberOfBadSatellites, byte[] badSVID, Date RTITimestamp)
	{	
		this.numberOfBadSatellites = numberOfBadSatellites;
		this.badSVID = badSVID;
		this.RTITimestamp = RTITimestamp;

		checkRange();
	}

	public void checkRange() throws IllegalArgumentException
	{
		if(this.badSVID == null)
		{
			throw new IllegalArgumentException("null badSVID");
		}

		if(this.numberOfBadSatellites != this.badSVID.length)
		{
			throw new IllegalArgumentException("numberOfBadSatellites doesn't match array length"
					+ " numberOfBadSatellites: " + this.numberOfBadSatellites + " array length: " 
					+ this.badSVID.length);
		}

		for(int i = 0; i < this.badSVID.length; i++)
		{
			if(this.badSVID[i] < 1 || this.badSVID[i] > PRN_MAXIMUM_VALUE)
			{
				throw new IllegalArgumentException("bad sat outside acceptable range. i : " + i + 
						" svID: " + this.badSVID[i]);
			}
		}

		if(this.RTITimestamp == null)
		{
			throw new IllegalArgumentException("date object(time stamp) is null");
		}
	}

	public static RTI generateRTIFromSatelliteEphemerisArray(SatelliteEphemeris[] satelliteEphemerisArray)
	{

		Logger logger = Logger.getLogger("generateRTIFromSatelliteEphemerisArray " + new Date().getTime());
		logger.setLevel(DebugLogger.getOutputVerbosity());

		if(logger.getLevel() != Level.OFF)
		{
			DebugLogger.recordLogToFile(logger);
		}
		logger.entering("RTI", "generateRTIFromSatelliteEphemerisArray");

		byte numberOfBadSatellites = 0;
		byte[] badSVID;

		int satelliteHealth;

		Date RTITimestamp;

		//get number of bad satellites first

		for (int i = 0; i < satelliteEphemerisArray.length; i++)
		{
			if ((satelliteEphemerisArray[i] != null) && !(satelliteEphemerisArray[i].isSatelliteHealthy()))
			{
				numberOfBadSatellites++;

				logger.fine("bad satellite found. satID: " + satelliteEphemerisArray[i].getPRN());
			} else
				if (satelliteEphemerisArray[i] == null)
				{
					logger.fine("no sat ephem object. i: " + i );
				}
		}
		logger.fine("numberOfBadSatellites: " + numberOfBadSatellites);

		if (numberOfBadSatellites == 0)
		{
			//stuff for zero bad sats goes here

			badSVID = new byte[0];
			logger.fine("array created for 0 bad sats");
		} else
			//if (numberOfBadSatellites > 0)
		{
			int n = 0; //index variable for badSVID array

			badSVID = new byte[numberOfBadSatellites];

			for (int i = 0; i < satelliteEphemerisArray.length; i++)
			{
				if ((satelliteEphemerisArray[i]!= null) && (!satelliteEphemerisArray[i].isSatelliteHealthy()))
				{
					badSVID[n] = (byte)satelliteEphemerisArray[i].getPRN();
					n++;
				}
			}
			logger.fine("array created for " + numberOfBadSatellites + " satellites. n: " + n);
		}

		logger.exiting("RTI", "generateRTIFromSatelliteEphemerisArray");

		RTITimestamp = new Date();

		return new RTI (numberOfBadSatellites, badSVID, RTITimestamp);
	}

	public String toString()
	{
		if ((this.numberOfBadSatellites <= 0))
		{
			return "";
		} else 
		{
			String RTIString;

			RTIString = Byte.toString(this.badSVID[0]);

			for (int i =1; i < badSVID.length; i++)
			{
				RTIString = RTIString + " " + badSVID[i];
			}

			return RTIString;
		}
	}

	//get accessors

	/**accessor for numberOfBadSatellites */
	public short getNumberOfBadSatellites()
	{
		return this.numberOfBadSatellites;
	}

	/**accessor for badSVID */
	public byte[] getBadSVID()
	{
		return this.badSVID;
	}

	/**accessor for timestamp */
	public Date getRTITimestamp()
	{
		return this.RTITimestamp;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
}