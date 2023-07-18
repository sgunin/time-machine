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
 * This class holds ionoshpere and UTC model from subframe 4 page 18
 * in raw hex 
 * @author Manosh Fernando
 *
 */
public class RawIonUTCModel implements DataType
{
	private String rawIonosphereModelString;
	
	private String rawUTCModelString;
	
	private Date rawIonUTCModelTimestamp;
	
	public RawIonUTCModel (String rawIonosphereModelString, String rawUTCModelString, Date rawIonUTCModelTimestamp)
	{
		this.rawIonosphereModelString = rawIonosphereModelString;
		this.rawUTCModelString = rawUTCModelString;
		this.rawIonUTCModelTimestamp = rawIonUTCModelTimestamp;
		
		checkRange();
	}
	
	public void checkRange() throws IllegalArgumentException
	{
		if(this.rawIonosphereModelString == null)
		{
			throw new IllegalArgumentException("ionosphere string is null");
		} 
		
		if(this.rawIonosphereModelString.length() != 16)
		{
			throw new IllegalArgumentException("ionosphere length not equal to 16. length: " 
					+ this.rawIonosphereModelString.length() + " rawIonosphereModelString: " 
					+ this.rawIonosphereModelString);
		}
		
		if(this.rawUTCModelString == null)
		{
			throw new IllegalArgumentException("UTC string is null");
		}
		
		if(this.rawUTCModelString.length() != 26)
		{
			throw new IllegalArgumentException("UTC string not equal to 26. length: " + this.rawUTCModelString.length()
					+ " rawUTCModelString: " + this.rawUTCModelString);
		}
		
		if(this.rawIonUTCModelTimestamp == null)
		{
			throw new IllegalArgumentException("date object(time stamp) is null");
		}
	}
	
	/**
     * Output data information in a string format.
     * @return the string that represents an instance of the data model.
     */
    public String toString()
    {
    	return ("rawIonosphereModelString: " + rawIonosphereModelString + " rawUTCModelString: " + rawUTCModelString);
    }

	public String getRawIonosphereModelString() 
	{
		return this.rawIonosphereModelString;
	}

	public Date getRawIonUTCModelTimestamp() 
	{
		return this.rawIonUTCModelTimestamp;
	}

	public String getRawUTCModelString() 
	{
		return this.rawUTCModelString;
	}
}