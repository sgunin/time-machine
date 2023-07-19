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

/**
 * Used to hold Almanac data.
 * @author Manosh Fernando
 *
 */

public class GPSAlmanac implements DataType
{
	/** The full name of this class */
    private final String thisClass = this.getClass().getName();
	
    /**Max value for 6 bit fields*/
    private static final short MAX_VALUE_8_BITS = (short)(Math.pow(2, 8)-1.0);
    
    /**max number of satellites in GPS fleet */
    private static final int NUMBER_OF_SATS_IN_FLEET = 32;
    
    /**Timestamp for this Almanac instance */
    private Date almanacTimestamp;
    
	/**Total number of sattelites in almanac */
    private byte numSatsTotal;
    
    /** Week number */
    private short weekNumber;
    
    /** contains parameters for satellites in almanac */
    private AlmanacSatelliteParameters[]  almanacSatelliteInformation;
    
    public GPSAlmanac(Date almanacTimestamp, byte numSatsTotal, short weekNumber,
    		AlmanacSatelliteParameters[]  almanacSatelliteInformation)
    {
    	this.almanacTimestamp = almanacTimestamp;
    	this.numSatsTotal = numSatsTotal;
    	this.weekNumber = weekNumber;
    	this.almanacSatelliteInformation = almanacSatelliteInformation;
    	
    	checkRange();
    }
    
    public String getParametersString()
    {
    	return "numSatsTotal: " + this.numSatsTotal + " weekNumber: " + this.weekNumber + " almanacTimestamp: "
    	+ this.almanacTimestamp.toString();
    }
    
    public AlmanacSatelliteParameters[] getAlmanacSatelliteParameters() 
    {
		return this.almanacSatelliteInformation;
	}

	public Date getAlmanacTimestamp() 
	{
		return this.almanacTimestamp;
	}

	public byte getNumSatsTotal() 
	{
		return this.numSatsTotal;
	}

	public String getThisClass()
	{
		return this.thisClass;
	}

	public short getWeekNumber() 
	{
		return this.weekNumber;
	}

	public void checkRange() throws IllegalArgumentException
	{
    	if(this.weekNumber < 0 || this.weekNumber > MAX_VALUE_8_BITS)
    	{
    		throw new IllegalArgumentException("weekNumber out of range. weekNumber: " + this.weekNumber);
    	}
    	
    	if(this.numSatsTotal < 0 || this.numSatsTotal > NUMBER_OF_SATS_IN_FLEET)
    	{
    		throw new IllegalArgumentException("numSatsTotal out of range. numSatsTotal: " + this.numSatsTotal);
    	}
    	
    	if(this.almanacSatelliteInformation == null)
    	{
    		throw new IllegalArgumentException("almanacSatelliteInformation array null");
    	} else
    	{
    		int numOfSats = 0;
    		for(int i = 0; i < this.almanacSatelliteInformation.length; i++)
    		{
    			if(this.almanacSatelliteInformation[i] != null)
    			{
    				numOfSats++;
    			}
    		}
    		
    		if(numOfSats != this.numSatsTotal)
    		{
    			throw new IllegalArgumentException("numSatsTotal dosen't match number of valid objects in array \n" +
    					"numSatsTotal: " + this.numSatsTotal + " actual number of objects: " + numOfSats);
    		} 
    	}
    	
	}
}