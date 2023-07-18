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
import java.util.logging.Logger;


/**
 * This class holds Reference Time data
 * @author Manosh Fernando
 *
 */

public class ReferenceTime implements DataType
{

	private static byte MAXIMUM_VALUE_1_BIT = (byte)(Math.pow(2, 1) - 1.0);

	private static byte MAXIMUM_VALUE_2_BITS = (byte)(Math.pow(2,2) - 1.0);

	private static byte MAXIMUM_VALUE_6_BITS = (byte)(Math.pow(2,6) - 1.0);

	private static short MAXIMUM_VALUE_10_BITS = (short)(Math.pow(2, 10) - 1.0);

	public static short MAXIMUM_VALUE_14_BITS = (short)(Math.pow(2, 14) - 1.0);

	private static int MAXIMUM_VALUE_23_BITS = (int)(Math.pow(2, 23) - 1.0);

	/**max value for GPS tow. range is 604799.92*/
	private static int GPS_TOW_MAX_VALUE = (int)(604799.92/0.08);

	/** GPS  week */
	private short GPSWeek;

	/** GPS time of week */
	private int GPSTow;

	///TOW assist parameters

	/**sat Id ranges from 0-63 */
	private byte satID;

	/**TLM Message */
	private short tlmMessage;

	/** anti-spoof bit in HOW */
	private byte antiSpoof;

	/**alert bit in HOW */
	private byte alert;

	/**reserved data in HOW */
	private byte tlmReserved;

	/** Timestamp of Reference Time */
	private Date referenceTimeTimestamp;

	/** The logger used for debug for this OSGRS instance */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();


	public ReferenceTime()
	{
		//TODO: Logging do not use this ctor.
	}

	public ReferenceTime(short GPSWeek,
			int GPSTow, 
			byte satID, 
			short tlmMessage, 
			byte antiSpoof,
			byte alert,
			byte tlmReserved,
			Date referenceTimeTimestamp)
	{
		this.GPSWeek = GPSWeek;
		this.GPSTow = GPSTow;
		this.satID = satID;
		this.tlmMessage = tlmMessage;
		this.antiSpoof = antiSpoof;
		this.alert = alert;
		this.tlmReserved = tlmReserved;
		this.referenceTimeTimestamp = referenceTimeTimestamp;

		checkRange();
	}

	public void checkRange() throws IllegalArgumentException
	{
		if (this.GPSWeek < 0 || this.GPSWeek > MAXIMUM_VALUE_10_BITS)
		{
			throw new IllegalArgumentException("GPSWeek: " + this.GPSWeek + " is set outside of acceptable range of values");
		}

		if (this.GPSTow < 0 || this.GPSTow > GPS_TOW_MAX_VALUE)
		{
			throw new IllegalArgumentException("GPSTow: " + this.GPSTow + " is set outside of acceptable range of values");
		}

		if (this.satID < 0 || this.satID > MAXIMUM_VALUE_6_BITS)
		{
			throw new IllegalArgumentException("satID: " + this.satID + " is set outside of acceptable range of values");
		}

		if (this.tlmMessage < 0 || this.tlmMessage > MAXIMUM_VALUE_14_BITS)
		{
			throw new IllegalArgumentException("tlmMessage: " + this.tlmMessage + " is set outside of acceptable range of values");
		}

		if (this.antiSpoof < 0 || this.antiSpoof > MAXIMUM_VALUE_1_BIT)
		{
			throw new IllegalArgumentException("antiSpoof: " + this.antiSpoof + " is set outside of acceptable range of values");
		}

		if (this.alert < 0 || this.alert > MAXIMUM_VALUE_1_BIT)
		{
			throw new IllegalArgumentException("alert: " + this.alert + " is set outside of acceptable range of values");
		}

		if (this.tlmReserved < 0 || this.tlmReserved > MAXIMUM_VALUE_2_BITS)
		{
			throw new IllegalArgumentException("tlmReserved: " + this.tlmReserved + " is set outside of acceptable range of values");
		}

		if (this.referenceTimeTimestamp == null)
		{
			throw new IllegalArgumentException("date object passed is null");
		}
	}



	//get accessors

	/**
	 * accessor for GPSWeek
	 */
	public short getGPSWeek()
	{
		return this.GPSWeek;
	}

	/** accessor for GPSWeek */
	public int getGPSTow ()
	{
		return this.GPSTow;
	}

	/** accessor for satID */
	public byte getSatID()
	{    	
		return this.satID;
	}

	public byte getPRN()
	{
		return (byte)(this.satID + 1);
	}
	/** accessor for tlmMessage */
	public short getTlmMessage()
	{
		return this.tlmMessage;
	}

	/** accessor for antiSpoof */
	public byte getAntiSpoof()
	{
		return this.antiSpoof;
	}

	/**accessor for alert */
	public byte getAlert()
	{
		return this.alert;
	}

	/**accesspor for tlmReserved */
	public byte getTlmReserved()
	{
		return this.tlmReserved;
	}

	/**accessor for timestamp */
	public Date getReferenceTimeTimestamp()
	{
		return this.referenceTimeTimestamp;
	}

	public String getParametersString()
	{
		return ("satID: " + this.satID + " GPSWeek: " + this.GPSWeek + " GPSTow: " + this.GPSTow + " tlmMessage: "
				+ this.tlmMessage + " antiSpoof: " + this.antiSpoof + " alert: " + this.alert + " tlmReserved: " + 
				this.tlmReserved + " referenceTimeTimestamp: " + this.referenceTimeTimestamp.toString());
	}


}
