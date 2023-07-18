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
 * the attribute satID is 
 * @author mfernando
 *
 */

public class RawAlmanac implements DataType
{
	/**svID 0-63 */
	private byte svID;

	/**almanac in hex */
	private String rawAlmanacString;

	/**Max value for 6 bit fields*/
	private static final byte MAX_VALUE_6_BITS = (byte)(Math.pow(2, 6)-1.0);

	Date rawAlmanacTimestamp;

	public RawAlmanac (byte svID, String rawAlmanacString, Date rawAlmanacTimestamp)
	{
		this.svID = svID;
		this.rawAlmanacString = rawAlmanacString;
		this.rawAlmanacTimestamp = rawAlmanacTimestamp;

		checkRange();
	}

	public void checkRange() throws IllegalArgumentException
	{
		if(this.svID < 0 || this.svID > MAX_VALUE_6_BITS)
		{
			throw new IllegalArgumentException("svID outside acceptable range. svID: " + this.svID);
		}

		if(this.rawAlmanacString == null)
		{
			throw new IllegalArgumentException("null hex string passed to Raw Almanac object");
		}

		if(this.rawAlmanacString.length() != 60)
		{
			throw new IllegalArgumentException("length of hex raw almanac is not 60 characters. length: " 
					+ this.rawAlmanacString.length() + " rawAlmanacString: " + this.rawAlmanacString);
		}

		if(this.rawAlmanacTimestamp == null)
		{
			throw new IllegalArgumentException("date object passed is null");
		}
	}

	/**
	 * Output data information in a string format.
	 * @return the string that represents an instance of the data model.
	 */
	public String toString()
	{
		return ("svID: " + svID + " rawAlmanacString: " +  rawAlmanacString);
	}

	public static void  printRawAlmanacToLog(RawAlmanac[] rawAlmanacArray)
	{
		Logger logger = Logger.getLogger("printRawAlmanacToLog@" + System.currentTimeMillis());
		logger.setLevel(Level.FINE);
		DebugLogger.recordLogToFile(logger);

		if (rawAlmanacArray != null)
		{
			logger.fine("array length: " + rawAlmanacArray.length);

			for (int i = 0; i < rawAlmanacArray.length; i++)
			{
				if (rawAlmanacArray[i] != null)
				{
					logger.fine("i: " + i + " " + rawAlmanacArray[i].toString());
				}else
				{
					logger.fine("no RawAlmanac object at position " + i);
				}
			}
		} else
		{
			logger.fine("Array was not initialised");
		}
	}

	public byte getSVID()
	{
		return this.svID;
	}

	public String getRawAlmanacString() 
	{
		return this.rawAlmanacString;
	}

	public Date getRawAlmanacTimestamp() 
	{
		return this.rawAlmanacTimestamp;
	}
}