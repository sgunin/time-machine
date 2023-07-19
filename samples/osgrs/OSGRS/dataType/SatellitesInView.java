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
 * Class used to hold the 'satellites in view'.
 * @author Manosh Fernando
 *
 */
public class SatellitesInView implements DataType
{
	private static final short MAX_NUMBER_OF_SATS_IN_FLEET = 32;
	
	private static final short PRN_MAXIMUM_VALUE = 32;
	
	/**number of satellites in view */
	private short numberOfSatsInView;
	
	/**Array containing PRNs of sats in view */
	private short[] satsInView;
	
	private Date SIVTimestamp;
	
	public SatellitesInView(short numberOfSatsInView, short[] satsInView, Date SIVTimestamp)
	{
		this.numberOfSatsInView = numberOfSatsInView;
		this.satsInView = satsInView;
		this.SIVTimestamp = SIVTimestamp;
		
		checkRange();
	}
	
	/**
     * This method should be implemented to check parameters of a data type.
     * @throws IllegalArgumentException - IllegalArgumentException should be thrown when a value is out of range.
     */
    public void checkRange() throws IllegalArgumentException
    {
    	if (this.numberOfSatsInView < 0 || this.numberOfSatsInView > MAX_NUMBER_OF_SATS_IN_FLEET)
    	{
    		throw new IllegalArgumentException("numberOfSatsInView is outside acceptable values. numberOfSatsInView:" 
    				+ this.numberOfSatsInView);
    	}
    	
    	if(this.satsInView == null)
    	{
    		throw new IllegalArgumentException("satsInView array null");
    	}
    	
    	if(this.satsInView.length != this.numberOfSatsInView)
    	{
    		throw new IllegalArgumentException("array lenngth does not match parameter numberOfSatsInView"
    				+ " array length: " + this.satsInView.length + " numberOfSatsInView: " + this.numberOfSatsInView);
    	}
    	
		for (int i = 0; i < satsInView.length; i++) 
		{
			if(this.satsInView[i] < 1 || this.satsInView[i] > PRN_MAXIMUM_VALUE)
			{
				throw new IllegalArgumentException("satInView at position i: " + i + " is outside acceptable range." +
						" satsInView[i]: " + this.satsInView[i]);
			}
		}
		
		if(this.SIVTimestamp == null)
		{
			throw new IllegalArgumentException("date object (timestamp) set to null");
		}
    }


    /**
     * Output data information in a string format.
     * @return the string that represents an instance of the data model.
     */
    public String toString()
    {
    	return "";
    }
    
    public short getNumberOfSatsInView()
    {
    	return this.numberOfSatsInView;
    }
    
    public short[] getSatsInView()
    {
    	return this.satsInView;
    }
    
    public Date getSIVTimestamp()
    {
    	return this.SIVTimestamp;
    }
}