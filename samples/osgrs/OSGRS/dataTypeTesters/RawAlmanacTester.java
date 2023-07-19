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

import OSGRS.dataType.RawAlmanac;

public class RawAlmanacTester
{
	/**Max value for 6 bit fields*/
	private static final byte MAX_VALUE_6_BITS = (byte)(Math.pow(2, 6)-1.0);

	public static void main(String[] args)
	{
		Date date = new Date();
		
		String acceptableRawAlmanacString = "8B06541CA737499BE14E0F95FD6C00A10D3E8158A236052BE34111090012";
		
		System.out.println("starting testing");
		
		for (int i = -4; i < MAX_VALUE_6_BITS + 4; i++)
		{
			try
			{
				new RawAlmanac ((byte)i, acceptableRawAlmanacString, date);
			} catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
		}
		
		try
		{
			new RawAlmanac ((byte)0, null, date);
		} catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
		}
		
		try
		{
			new RawAlmanac ((byte)0, "AB23456", date);
		} catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
		}
		
		try
		{
			new RawAlmanac ((byte)0, acceptableRawAlmanacString + "EBC", date);
		} catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
		}
		
		try
		{
			new RawAlmanac ((byte)0, acceptableRawAlmanacString, null);
		} catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
		}

	}
}