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

import OSGRS.dataType.IonUTCModel;

public class IonUTCModelTester
{
	/**
     * The minimum value of the A0 field.
     * This is the minimum 2's complement value by the scale factor for the A0 field
     */
    private final static double GPS_A0_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,32);

    /**
     * The maximum value of the A0 field.
     * This is the maximum 2's complement value by the scale factor for the A0 field
     */
    private final static double GPS_A0_FIELD_MAX_VALUE = (Math.pow(2,32) - 1);

    /**
     * The minimum value of the A1 field.
     * This is the minimum 2's complement value by the scale factor for the A1 field
     */
    private final static double GPS_A1_FIELD_MIN_VALUE = 0;//-1.0 * Math.pow(2,24);

    /**
     * The maximum value of the A1 field.
     * This is the maximum 2's complement value by the scale factor for the A1 field
     */
    private final static double GPS_A1_FIELD_MAX_VALUE = (Math.pow(2,24) - 1);

    /** The maxiumum value for range checking for the 8 bit fields, dtLS, WNt,WNlsf. */
    private final static short GPS_8_BIT_FIELD_MAX_VALUE = (short)(Math.pow(2,8) - 1);

    /** The maxiumum value for range checking for the tot field (reference time for UTC data). */
    private final static short GPS_TOT_FIELD_MAX_VALUE = (short)(602112.0/Math.pow(2,12));

    /** The maxiumum value for range checking for the DN field. */
    private final static short GPS_DN_FIELD_MIN_VALUE = 1;
    
    /** The maxiumum value for range checking for the DN field. */
    private final static short GPS_DN_FIELD_MAX_VALUE = 7;
	
	public static void main (String[] args)
	{
		Date date = new Date();
		
		System.setProperty("outputVerbosity", "0");
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) i,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) i,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) i,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) i,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(int i = -4; i < GPS_TOT_FIELD_MAX_VALUE + 4; i++)
		{
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) i,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
		}
		
		for(long i = -4; i < GPS_A0_FIELD_MAX_VALUE + 4; i++)
		{
//			if(i == 5) //to speed things up
//			{
//				i = (long)GPS_A0_FIELD_MAX_VALUE -3;
//			}
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) i,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		for(int i = -4; i < GPS_A1_FIELD_MAX_VALUE + 4; i++)
		{
			
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 i,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) i,
		                (short) 1,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		for(int i = -4; i < GPS_DN_FIELD_MAX_VALUE + 4; i++)
		{
			
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) i,
		                (short) 0,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) i,
		                (short) 0,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		for(int i = -4; i < GPS_8_BIT_FIELD_MAX_VALUE + 4; i++)
		{
			
			try
			{
				new IonUTCModel(
		                //IONOSPHERIC PARAMS

		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,
		                (short) 0,

		                //UTC PARAMS
		                (short) 0,
		                (short) 0,
		                (long) 0,
		                 0,
		                (short) 0,
		                (short) 1,
		                (short) 0,
		                (short) i,
		                date);
			}catch(IllegalArgumentException e)
			{
				System.out.println("Exception caught. Message: " + e.getMessage());
			}
			
			//System.out.println("i: " + i);
		}
		
		try
		{
			new IonUTCModel(
	                //IONOSPHERIC PARAMS

	                (short) 0,
	                (short) 0,
	                (short) 0,
	                (short) 0,
	                (short) 0,
	                (short) 0,
	                (short) 0,
	                (short) 0,

	                //UTC PARAMS
	                (short) 0,
	                (short) 0,
	                (long) 0,
	                 0,
	                (short) 0,
	                (short) 1,
	                (short) 0,
	                (short) 0,
	                null
	                );
		}catch(IllegalArgumentException e)
		{
			System.out.println("Exception caught. Message: " + e.getMessage());
		}
	}
}