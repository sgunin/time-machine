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

package OSGRS.novateloem3;

import java.util.Date;
import java.util.logging.Logger;
import java.util.logging.Level;
import OSGRS.Util.DebugLogger;

/** this class is used to store novatel Logs for caching and future processing,
 * 	@author Manosh Fernando
 *
 */

public class NovatelLog
{
	/**the type of Log */
	private String logType;
	
	/**satID of log if log is satellite specific */
	private int satID;
	
	/**String containing log */
	private String log;
	
	/**timestamp of	log, made at creation of class */
	private Date logTimestamp;
	
	/**logger of this class */
	
	private Logger logger;
	
	public NovatelLog (String logType, String log)
	{
		this.logType = logType;
		this.log = log;
		
		logTimestamp = new Date(System.currentTimeMillis());
		
		//System.out.println(logTimestamp.toString());
		//System.out.println(log);
		
		checkType(logType);
		
	}
	
	/**
	 * Used at the moment for determining the type of log and extracting the satID if applicable
	 * @param logType
	 */
	
	private void checkType(String logType)
	{
		//maybe make this if-else, 
		
		if(logType.equals("GPSEPHEMA")) //oem 4 code
		{
			String[] GPSEPHEMArray = log.split(";");
	    	
	    	String GPSEPHEMParameters = GPSEPHEMArray[1];
	    	
	    	String[] GPSEPHEMParametersArray = GPSEPHEMParameters.split(",");
	    	
	    	satID = (byte)(Byte.parseByte(GPSEPHEMParametersArray[0])- 1);
	    	
	    	//System.out.println("satID: " + satID);
		}
		
		if(logType.equals("GPALM"))
		{
			String[] splitGPALMLog = log.split(",");
			
			this.satID = Integer.parseInt(splitGPALMLog[3]) -1;
		}
		
//		if(logType.equals("RAWGPSSUBFRAMEA"))//oem4 code
//		{
//			String[] RAWGPSSUBFRAMEAArray = log.split(";");
//			
//			String RAWGPSSUBFRAMEAParameters = RAWGPSSUBFRAMEAArray[1];
//			
//			String[] RAWGPSSUBFRAMEAParametersArray = RAWGPSSUBFRAMEAParameters.split(",");
//			
//			satID = (byte)(Integer.parseInt(RAWGPSSUBFRAMEAParametersArray[1]) - 1);
//			
//			//System.out.println("satID: " + satID);
//		}
		
		if(logType.equals("REPA"))
		{
			String[] REPAParametersArray = this.log.split(",");
			
			this.satID = (byte)(Integer.parseInt(REPAParametersArray[1])-1);
			
//			System.out.println(logTimestamp.toString());
//			System.out.println("satID: " + satID);
//			System.out.println(log);
		}
		
		if(logType.equals("FRMA"))
		{
			String[] FRMAPararmetersArray = this.log.split(",");
			
			this.satID = (byte)(Integer.parseInt(FRMAPararmetersArray[3]) - 1);
		}
		
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
    
    //get ascessors

    
    /**
     * accessor for satID
     */
    public int getSatID()
    {
    	return this.satID;
    }
    
    /**
     * accessor for log Type
     */
    public String getLogType()
    {
    	return this.logType;
    }
    
    /**
     * accessor for log
     * @return log
     */
    public String getLog()
    {
    	return this.log;
    }
    
    /**
     * accessor for logTimestamp
     * @return timestamp made upon log creation
     */
    public Date getLogTimestamp()
    {
    	return this.logTimestamp;
    }
}