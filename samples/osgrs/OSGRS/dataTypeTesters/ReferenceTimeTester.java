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

import OSGRS.dataType.ReferenceTime;

public class ReferenceTimeTester
{
	private static byte MAXIMUM_VALUE_1_BIT = (byte)(Math.pow(2, 1) - 1.0);
	
	private static byte MAXIMUM_VALUE_2_BITS = (byte)(Math.pow(2,2) - 1.0);
	
	private static byte MAXIMUM_VALUE_6_BITS = (byte)(Math.pow(2,6) - 1.0);
	
	private static short MAXIMUM_VALUE_10_BITS = (short)(Math.pow(2, 10) - 1.0);
	
	public static short MAXIMUM_VALUE_14_BITS = (short)(Math.pow(2, 14) - 1.0);
	
	private static int MAXIMUM_VALUE_23_BITS = (int)(Math.pow(2, 23) - 1.0);
	
	private static int GPS_TOW_MAX_VALUE = (int)(604799.92/0.08);
	
	/**max number of satellites in GPS fleet */
    private static final int NUMBER_OF_SATS_IN_FLEET = 32;
	
	public static void main (String[] main)
	{
		Date date = new Date();
		
		//GPS Week
		for (int i = -4; i < MAXIMUM_VALUE_10_BITS + 4; i++)
		{
			try
			{
				new ReferenceTime((short) i,
   					 0, 
   					 (byte) 0, 
   					 (short) 0, 
   					 (byte) 0,
   					 (byte) 0,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//GPS TOW
		for (int i = -4; i < GPS_TOW_MAX_VALUE + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 i, 
   					 (byte) 0, 
   					 (short) 0, 
   					 (byte) 0,
   					 (byte) 0,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//satID
		for (int i = -4; i < MAXIMUM_VALUE_6_BITS + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 0, 
   					 (byte) i, 
   					 (short) 0, 
   					 (byte) 0,
   					 (byte) 0,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//tlm message
		for (int i = -4; i < MAXIMUM_VALUE_14_BITS + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 0, 
   					 (byte) 0, 
   					 (short) i, 
   					 (byte) 0,
   					 (byte) 0,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//anti-spoof
		for (int i = -4; i < MAXIMUM_VALUE_1_BIT + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 0, 
   					 (byte) 0, 
   					 (short) 0, 
   					 (byte) i,
   					 (byte) 0,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//alert
		for (int i = -4; i < MAXIMUM_VALUE_1_BIT + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 0, 
   					 (byte) 0, 
   					 (short) 0, 
   					 (byte) 0,
   					 (byte) i,
   					 (byte) 0,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		//tlm reserved
		for (int i = -4; i < MAXIMUM_VALUE_2_BITS + 4; i++)
		{
			try
			{
				new ReferenceTime((short) 0,
   					 0, 
   					 (byte) 0, 
   					 (short) 0, 
   					 (byte) 0,
   					 (byte) 0,
   					 (byte) i,
   					 date);
			}catch (IllegalArgumentException e)
			{
				System.out.println("Exception Caught. Message: " + e.getMessage());
			}
		}
		
		//date, testing passing a null object
		try
		{
			new ReferenceTime((short) 0,
					 0, 
					 (byte) 0, 
					 (short) 0, 
					 (byte) 0,
					 (byte) 0,
					 (byte) 0,
					 null);
		}catch (IllegalArgumentException e)
		{
			System.out.println("Exception Caught. Message: " + e.getMessage());
		}
		
	}
}