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

import OSGRS.dataType.AlmanacSatelliteParameters;

public class AlmanacSatelliteParametersTester
{
	/**Max value for 6 bit fields*/
    private static final byte MAX_VALUE_6_BITS = (byte)(Math.pow(2, 6)-1.0);
    
    /**Max value for eight bit fields*/
    private static final short MAX_VALUE_8_BITS = (short)(Math.pow(2, 8)-1.0);
    
    /**Max value for 11 bit fields*/
    private static final short MAX_VALUE_11_BITS = (short)(Math.pow(2, 11)-1.0);
    
    /**Max value for 16 bit fields*/
    private static final int MAX_VALUE_16_BITS = (int)(Math.pow(2, 16)-1.0);
    
    /**Max value for 24 bit fields*/
    private static final int MAX_VALUE_24_BITS = (int)(Math.pow(2, 24) - 1.0);
	
	public static void main(String[] agrs)
	{
		//System.out.println("checking e");
		
		for(int i = -10; i < MAX_VALUE_6_BITS + 4; i++ )
		{
			new AlmanacSatelliteParameters((byte)i,0,(short)0,0,0,(short)0,0,0,0,0,(short)0,(short)0);
		}
	}
}
