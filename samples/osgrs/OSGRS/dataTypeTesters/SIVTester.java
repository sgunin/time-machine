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

import OSGRS.dataType.SatellitesInView;

public class SIVTester
{
	private static short[] acceptableShortArray = {(short)1,(short)4,(short)24, (short)26, (short)16, (short)13, 
		(short)11, (short)29};

	private static final short MAX_NUMBER_OF_SATS_IN_FLEET = 32;

	private static final short PRN_MAXIMUM_VALUE = 32;

	public static void main(String[] args)
	{
		boolean exceptionCaught=false;

		Date date = new Date();

		String exceptionMessage = "";
		String expectedExceptionMessage = "";

		System.out.println("test1: test acceptable values for numOfSatsInView");

		try
		{
			for(int i=0; i < MAX_NUMBER_OF_SATS_IN_FLEET + 1; i++)
			{
				System.out.println("i: " + i);
				
				short[] shortArray = new short[i];
				
				for(int x = 0; x < shortArray.length; x++)
				{
					shortArray[x] = (short)1;
				}
				
				new SatellitesInView((short)i, shortArray, date);
			}
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}

		if(exceptionCaught)
		{
			System.out.println("Test1: Failed");
		} else
		{
			System.out.println("Test1: Passed");
		}
		
		System.out.println("test2: testing unacceptable values for numOfSatsInView");
		
		boolean test2Passed = true;
		exceptionCaught = false;
		
		for(int i = -4; i < MAX_NUMBER_OF_SATS_IN_FLEET + 3; i++)
		{
			if(i == 0)
			{
				i = (short)33;
			}
			System.out.println("i: " + i);
			try
			{
				new SatellitesInView((short)i, acceptableShortArray, date);
			}catch(IllegalArgumentException e)
			{
				exceptionMessage = e.getMessage();
				System.out.println("Exception caught. Message: " + e.getMessage());
				exceptionCaught = true;
			}
			expectedExceptionMessage = "numberOfSatsInView is outside acceptable values. numberOfSatsInView:" + i;
			
			if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
			{
				test2Passed = true;
			} else
			{
				System.out.println("test2: failed");
				test2Passed = false;
				break;
			}
			
		}
		
		if(test2Passed)
		{
			System.out.println("test2: passed");
		}
		
		System.out.println("test3: testing sending null array");
		
		exceptionCaught = false;
		
		try
		{
			new SatellitesInView((short)1, null, date);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		
		expectedExceptionMessage = "satsInView array null";
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test3: Passed");
		} else
		{
			System.out.println("Test3: Failed");
		}
		
		System.out.println("test4: testing acceptable values for satInView");
		
		exceptionCaught = false;
		
		for(int i = 1; i < 33; i++)
		{
			System.out.println("i: " + i);
			try
			{
				acceptableShortArray[0] = (short)i;
				new SatellitesInView((short)acceptableShortArray.length, acceptableShortArray, date);
				exceptionCaught = false;
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
				exceptionCaught = true;
			}
		}
		
		if(exceptionCaught)
		{
			System.out.println("test4: failed");
		}else
		{
			System.out.println("test4: passed");
		}
		
		System.out.println("test5: testing unacceptable values for satInView");
		
		boolean test5Passed = true;
		exceptionCaught =false;
		
		for(int i = -4; i < PRN_MAXIMUM_VALUE +4; i++)
		{
			if(i == 1)
			{
				i = 33;
			}
			
			System.out.println("i: " + i);
			try
			{
				acceptableShortArray[0] = (short)i;
				new SatellitesInView((short)acceptableShortArray.length, acceptableShortArray, date);
				exceptionCaught = false;
			}catch(IllegalArgumentException e)
			{
				exceptionMessage = e.getMessage();
				System.out.println("Exception caught. Message: " + e.getMessage());
				exceptionCaught = true;
			}
			
			expectedExceptionMessage = "satInView at position i: 0 is outside acceptable range. satsInView[i]: " +i;
			
			if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
			{
				test5Passed = true;
			} else
			{
				test5Passed = false;
				break;
			}
		}
		
		if(test5Passed)
		{
			System.out.println("test5: passed");
		}else
		{
			System.out.println("test5: failed");
		}
		
		acceptableShortArray[0] = (short)1;
		
		System.out.println("test6: testing sending null date object");
		
		try
		{
			new SatellitesInView((short)acceptableShortArray.length, acceptableShortArray, null);
			exceptionCaught = false;
		}catch (IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "date object (timestamp) set to null";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("test6: passed");
		} else
		{
			System.out.println("test6: failed");
		}
		
	}
}