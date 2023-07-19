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

import OSGRS.dataType.RawIonUTCModel;

public class RawIonUTCModelTester
{
	private static String acceptableIonoString = "0703FFFE2A06FFF8";
	
	private static String acceptableUTCString = "00000B000000034E950E4B070E";
	
	public static void main (String[] args)
	{
		boolean exceptionCaught;
		
		Date date = new Date();
		
		String exceptionMessage = "";
		String expectedExceptionMessage = "";
		
		System.out.println("Test1: testing acceptable papameters");
		
		try
		{
			new RawIonUTCModel (acceptableIonoString, acceptableUTCString, date);
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
		
		System.out.println("\nTest2: testing sending null ionoString");
		
		try
		{
			new RawIonUTCModel (null, acceptableUTCString, date);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "ionosphere string is null";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test2: Passed");
		}else
		{
			System.out.println("Test1: Failed");
		}
		
		System.out.println("\nTest3: testing sending null UTCString");
		
		try
		{
			new RawIonUTCModel (acceptableIonoString, null, date);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "UTC string is null";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test3: Passed");
		}else
		{
			System.out.println("Test3: Failed");
		}
		
		System.out.println("\nTest4: testing sending ionoString of incorrect length");
		
		try
		{
			new RawIonUTCModel ("AC43BD", acceptableUTCString, date);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "ionosphere length not equal to 16. length: 6 rawIonosphereModelString: AC43BD";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test4: Passed");
		}else
		{
			System.out.println("Test4: Failed");
		}
		
		System.out.println("\nTest5: testing sending UTCString of incorrect length");
		
		try
		{
			new RawIonUTCModel (acceptableIonoString, "AC43BD", date);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "UTC string not equal to 26. length: 6 rawUTCModelString: AC43BD";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test5: Passed");
		}else
		{
			System.out.println("Test5: Failed");
		}
		
		System.out.println("\nTest6: testing sending UTCString of incorrect length");
		
		try
		{
			new RawIonUTCModel (acceptableIonoString, acceptableUTCString, null);
			exceptionCaught = false;
		}catch(IllegalArgumentException e)
		{
			exceptionMessage = e.getMessage();
			
			System.out.println("Exception caught. Message: " + e.getMessage());
			exceptionCaught = true;
		}
		
		expectedExceptionMessage = "date object(time stamp) is null";
		
		if(exceptionCaught && exceptionMessage.equals(expectedExceptionMessage))
		{
			System.out.println("Test6: Passed");
		}else
		{
			System.out.println("Test6: Failed");
		}
	
	}
}