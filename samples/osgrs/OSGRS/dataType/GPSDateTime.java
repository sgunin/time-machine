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

import java.text.DateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.logging.*;

import OSGRS.Util.DebugLogger;

/**
 * This class will be used to create and process GPS time
 * TODO: Test this methods in this class further
 * @author mfernando
 *
 */

public class GPSDateTime
{
	/** The logger used for debug for this GPSDateTime instance */
    private Logger logger;
    
    /** The full name of this class */
    private final String thisClass = this.getClass().getName();
	
	/** The modulo which is applied to the GPS week. */
    private final static int GPS_WEEK_MODULUS = 1024;

    /** The number of days in a week. */
    private final static int NUMBER_OF_DAYS_IN_A_WEEK = 7;

    /** The number of weeks in a year. */
    private final static int NUMBER_OF_WEEKS_IN_A_YEAR = 52;
    
    public final static long MILLIS_IN_A_SECOND = 1000;
	
	public final static long MILLIS_IN_A_MINUTE = 60 * MILLIS_IN_A_SECOND;
	
	public final static long MILLIS_IN_A_HOUR = 60 * MILLIS_IN_A_MINUTE;
	
	public final static long MILLIS_IN_A_DAY = 24 * MILLIS_IN_A_HOUR;

    /** The number of GPS seconds in a week. */
    public final static int NUMBER_OF_GPS_SECONDS_PER_WEEK = 60 * 60 * 24 * 7;

    /** The number of milliseconds in a week. */
    public final static int NUMBER_OF_MILLISECONDS_PER_WEEK = NUMBER_OF_GPS_SECONDS_PER_WEEK * 1000;

    /** The number of GPS seconds in half a week. */
    public final static int NUMBER_OF_GPS_SECONDS_PER_HALF_WEEK = NUMBER_OF_GPS_SECONDS_PER_WEEK / 2;

    /** Negative half GPS week in seconds. */
    public final static int NEGATIVE_NUMBER_OF_GPS_SECONDS_PER_HALF_WEEK = -1 * NUMBER_OF_GPS_SECONDS_PER_HALF_WEEK;
    
    public final static double TOW_SCALING_FACTOR = 0.08;

    
    /**
    * This is the GPS Time in calendar format.
    */
    private Calendar gpsTime;

    /**
    * The fractions of seconds that the Calendar gpsTime object does not hold.
    */
    private double gpsFractionsOfSeconds = 0;


    /**
     * This is the GPS start reference time in calendar
     * format. It is initialised and used as a constant
     * for performance reasons.
     */
    private static Calendar gpsStartReferenceTime;

    /**
     * This is the number of milliseconds between the start of the GPS time
     * scale and the start of the UTC time scale. It is a constant value
     * that is calculated once by each of the constructors in case it is needed
     * by the set method. Note that this does <b>not</b> include the UTC time
     * offset which is the leap seconds and time scale drift adjustment.
     */
    private long msBetweenGPSStartUTCStart;


    /**
     * The GPS week number as a full value from the start of GPS time in
     * 1980. Note that the accessor for this field mods it down by 1024.
     */
    private int gpsWeekNumber;

    /**
     * The time in the GPS week in seconds. This rolls over to 0 at midnight Saturday
     * / morning of Sunday each week.
     */
    private double gpsSecondsOfWeek;
    
    /**
     * The UTC timezone static object is used as a basis for the GPS Date Time calendar
     * object. This is then converted over to GPS time in the other attributes.
     */
    private final static TimeZone tz = TimeZone.getTimeZone("UTC");
    
    public GPSDateTime()
    {
    	//set up logger
    	setLogger(Logger.getLogger(thisClass));
    	this.logger.setLevel(DebugLogger.getOutputVerbosity());
    	DebugLogger.recordLogToFile(this.logger);
    	
    	this.logger.entering(thisClass,"Constructor");
    	
//    	//Allocate the object to hold the current time in gps time
//        this.gpsTime = Calendar.getInstance();
//        this.gpsTime.setTimeZone(GPSDateTime.tz);
//        this.gpsTime.setFirstDayOfWeek(Calendar.SUNDAY);
//        this.gpsTime.setLenient(false);
        
        //Allocate and set the attribute to hang onto the GPS start reference time
        this.gpsStartReferenceTime = Calendar.getInstance();
        GPSDateTime.setToGPSReferenceTime(this.gpsStartReferenceTime);
        
        //Set the variable that holds the number of seconds between the start of
        //UTC time and the start of GPS time
        this.msBetweenGPSStartUTCStart = this.gpsStartReferenceTime.getTimeInMillis();
        this.logger.fine("msBetweenGPSStartUTCStart: " + this.msBetweenGPSStartUTCStart);
        
        this.logger.exiting(thisClass, "Constructor");
    }
    
    /**
     * This method generates the GPS time of week using the platform time
     * @param ionUTCModel
     * @return
     */
    
    public void createGPSTowFromSystemTime(IonUTCModel ionUTCModel)
    {
    	this.logger.entering(thisClass, "createGPSTowFromSystemTime");
    	
    	double utcOffsetInSeconds = ionUTCModel.getUTCOffset(System.currentTimeMillis() - this.msBetweenGPSStartUTCStart);
    	this.logger.fine("utcOffsetInSeconds: " + utcOffsetInSeconds);
    	
    	this.logger.fine("current system time: " + System.currentTimeMillis());
    	this.logger.fine("msBetweenGPSStartUTCStart: " + this.msBetweenGPSStartUTCStart);
    	long currentGPSTimeInMillis  = System.currentTimeMillis() - this.msBetweenGPSStartUTCStart + Math.round(utcOffsetInSeconds * 1000);
    	this.logger.fine("currentGPSTimeInMillis: " + currentGPSTimeInMillis);
    	
    	this.gpsWeekNumber = GPSDateTime.calculateGPSWeekNumber(currentGPSTimeInMillis);
    	this.logger.fine("this.gpsWeekNumber: " + this.gpsWeekNumber);
    	
    	this.gpsSecondsOfWeek = GPSDateTime.calculateGPSSecondsOfWeek(currentGPSTimeInMillis);
    	this.logger.fine("this.gpsSecondsOfWeek: " + this.gpsSecondsOfWeek);
    	
    	this.logger.exiting(thisClass, "createGPSTowFromSystemTime");
    }
    
    /**
     * This method sets the passed calendar object to the GPS start reference time
     * which is midnight Jan 5 1980 / Morning of Jan 6 1980.
     */
    public static void setToGPSReferenceTime(Calendar theCalendar)
    {
        theCalendar.clear();
        theCalendar.setTimeZone(GPSDateTime.tz);
        //y,m,d,h,m,s - month is 0 to 11
        theCalendar.set(1980, 0, 6, 0, 0, 0);
    }
    
    /**
     * This method pulls the date time string out of the calendar
     * object in the correct format for display.
     *
     * @param theCalendar the calendar object that represents
     *              the date and time.
     *
     * @return A string that contains the date time as a text
     *              string.
     */
    public static String toDateTimeString(Calendar theCalendar)
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        df.setTimeZone(GPSDateTime.tz);
        String dateTimeString = df.format(theCalendar.getTime());

        return dateTimeString;
    }
    
    /**
     * Accessor for the gpsWeek modulo 1024.
     */
    public int getGpsWeekNumber()
    {
        int moddedDownGPSWeekNumber = this.gpsWeekNumber % GPS_WEEK_MODULUS;
        return moddedDownGPSWeekNumber;
    }
    
    /** Accessor for the gpsWeek. */
    public double getGpsSecondsOfWeek()
    {
        return this.gpsSecondsOfWeek;
    }
    
    /**
     *The time in the GPS week in seconds in unscaled formant. gpsSecondsOfWeekUnscaled = gpsSecondsOfWeek
     */
    
    public int getGpsSecondsOfWeekUnscaled()
    {
    	return (int)(this.gpsSecondsOfWeek/TOW_SCALING_FACTOR);
    }
 
    /**
     * This method calculates the GPS week from the gps time in milliseconds.
     *
     * @param gpsTimeMillis the number of milliseconds from the start of the
     *                gps clock.
     *
     * @return the gps week number.
     */
    public static int calculateGPSWeekNumber(long gpsTimeMillis)
    {
        //assert (gpsTimeMillis >= 0) : DebugLog.die("Precondition failed", "gpsTimeMillis >= 0 - File: GPSDateTime.java");
        return(int)(gpsTimeMillis / NUMBER_OF_MILLISECONDS_PER_WEEK);
    }


    /**
     * This method calculates the seconds of the GPS week from the gps
     * time in milliseconds.
     *
     * @param gpsTimeMillis the number of milliseconds from the start of the
     *                gps clock.
     *
     * @return the gps week number.
     */
    public static double calculateGPSSecondsOfWeek(double gpsTimeMillis)
    {
        //assert (gpsTimeMillis >= 0) : DebugLog.die("Precondition failed", "gpsTimeMillis >= 0 - File: GPSDateTime.java");
        return(gpsTimeMillis - (1L * GPSDateTime.calculateGPSWeekNumber((long)gpsTimeMillis) * NUMBER_OF_MILLISECONDS_PER_WEEK)) / 1000.0;
    }
    
    

    /**
     * Returns a string representation of the object.
     *
     * @return the string representation of the object.
     */
    public String toString()
    {
        return new String
        (
        "GPSWeek: " + this.gpsWeekNumber + " secondsOfGPSWeek: " + this.gpsSecondsOfWeek + " " +
        GPSDateTime.toDateTimeString(this.gpsTime) + " fracs of sec: " + this.gpsFractionsOfSeconds
        );
    }
    
    /**
     * @return the logger
     */
    public Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @param logger the logger to set
     */
    public void setLogger(final Logger logger)
    {
        this.logger = logger;
    }
	
	
	
}