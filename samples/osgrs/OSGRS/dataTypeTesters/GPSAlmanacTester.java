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

package OSGRS.dataTypeTesters;

import java.util.Date;

import OSGRS.dataType.AlmanacSatelliteParameters;
import OSGRS.dataType.GPSAlmanac;


public class GPSAlmanacTester
{
	/**Max value for 6 bit fields*/
    private static final short MAX_VALUE_8_BITS = (short)(Math.pow(2, 8)-1.0);
    
    /**max number of satellites in GPS fleet */
    private static final int NUMBER_OF_SATS_IN_FLEET = 32;
	
	public static void main(String[] agrs)
	{
		Date date = new Date();
		
		AlmanacSatelliteParameters[] almanacParameters = new AlmanacSatelliteParameters[30];
		
		for(int i = 0; i < almanacParameters.length; i++ )
		{
			almanacParameters[i] = 
				new AlmanacSatelliteParameters((byte)0,0,(short)0,0,0,(short)0,0,0,0,0,(short)0,(short)0);
		}
		
		for (int i = 28; i <  33; i++)
		{
			//new GPSAlmanac(date, (byte)i, (short)0, null);
			new GPSAlmanac(date, (byte)i, (short) 0, almanacParameters);
		}
		
		//new GPSAlmanac(date, (byte)26, (short) 0, almanacParameters);
	}
}