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

import OSGRS.DataManagement.DataSourceManager;
import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * this is the cache of the novatel logs for the OEM 3 data source. This was originally written for the OEM 4. 
 * The original code will be used as a guide and a base for the OEM 3 version of this class.
 * @author Manosh Fernando
 */

public class NovatelLogCache
{
	/**array is indexed according to satID */
	private NovatelLog[] GPSEPHEMLogs;
	private NovatelLog[] FRMALogs;
	private NovatelLog[] GPALMLogs;
	private NovatelLog[] REPALogs;
	private NovatelLog[] GPGSVLogs;
	private NovatelLog[] tempGPGSVLogs = null;

	private NovatelLog ALMANACLog;
	private NovatelLog RASALog;
	private NovatelLog IONUTCLog;

	private DataSourceManager dataSourceManager;
	private NovatelOEM3DataSource dataSource;

	//variables for REPA Log
	private Date firstREPATimestamp = null;

	//variables for gpglog caching
	int totalNumberOfMessages;
	int messageNumber;
	int n; //GPGSVLogs array index

	boolean processingGPGSVComplete = false;

	//variables for rawgpssubframe caching
	long previousLogTime = System.currentTimeMillis();

	int numberOfSatellitesInView;

	int m; //RAWGPSSUBFRAME array index

	boolean processingRAWGPSSUBFRAMEComplete = false;

	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	public NovatelLogCache(DataSourceManager dataSourceManager, NovatelOEM3DataSource dataSource)
	{
		setLogger(Logger.getLogger(thisClass));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		this.logger.entering(thisClass, "Constructor");

		this.dataSourceManager = dataSourceManager;
		this.dataSource = dataSource;

		this.GPSEPHEMLogs = new NovatelLog[32];
		this.FRMALogs = new NovatelLog[32];
		this.REPALogs = new NovatelLog[32];
		this.GPALMLogs = new NovatelLog[32];

		this.logger.fine("log cache created");

		this.logger.exiting(thisClass, "Constructor");
	}

	public void addGPGSVLog(NovatelLog GPGSVLog)
	{
		String GPGSVLogString = GPGSVLog.getLog();

		String[] SplitGPGSVLogString = GPGSVLogString.split(",");

		totalNumberOfMessages = Integer.parseInt(SplitGPGSVLogString[1]);

		messageNumber = Integer.parseInt(SplitGPGSVLogString[2]);

		if (processingGPGSVComplete || (tempGPGSVLogs == null)) //first log
		{
			this.logger.finer("Step1 ");

			processingGPGSVComplete = false;

			tempGPGSVLogs = new NovatelLog[totalNumberOfMessages];

			n = 0;

			tempGPGSVLogs[n] = GPGSVLog;
			this.logger.finest("Log: " + tempGPGSVLogs[n].getLog() + " Timestamp: " + tempGPGSVLogs[n].getLogTimestamp().toString());

			if( messageNumber == totalNumberOfMessages)
			{
				processingGPGSVComplete = true;
			}

		} else if (messageNumber<totalNumberOfMessages)
		{
			this.logger.finer("step2");
			n++;
			tempGPGSVLogs[n] = GPGSVLog;

			this.logger.finest("Log: " + tempGPGSVLogs[n].getLog() + " Timestamp: " + tempGPGSVLogs[n].getLogTimestamp().toString());

		} else
		{
			this.logger.finer("step3");
			n++;
			tempGPGSVLogs[n] = GPGSVLog;
			finalizeGPGSVLogs(tempGPGSVLogs);
			processingGPGSVComplete = true;

			this.logger.finest("Log: " + tempGPGSVLogs[n].getLog() + " Timestamp: " + tempGPGSVLogs[n].getLogTimestamp().toString());
		}
	}

	/**
	 * Stores GPSEPHEM log in array based on satID
	 * @param GPSEPHEMLog
	 */
	public void addGPSEPHEMLog(NovatelLog GPSEPHEMLog)
	{
		this.GPSEPHEMLogs[GPSEPHEMLog.getSatID()] = GPSEPHEMLog;
	}

	public void addGPALMLog(NovatelLog GPALMLog)
	{
		int satID = GPALMLog.getSatID();

		if((satID >= 0) && (satID < this.GPALMLogs.length) && GPALMLog.getLog().contains("GPALM"))
		{
			this.GPALMLogs[satID] = GPALMLog;
		} else
		{
			this.logger.severe("Incorrect GPALM Log passed to log cache");
			this.logger.severe("GPALMLog.length: " + this.GPALMLogs.length + " satID: " + GPALMLog.getSatID() 
					+ " log: " + GPALMLog.getLog());
		}
	}

	public void addRASALog(NovatelLog RASALog)
	{
		this.logger.entering(thisClass, "addRASALog");

		this.RASALog = RASALog;
		this.logger.finer("RASALog added to log cache. Log: " + this.RASALog.getLog());
		this.logger.exiting(thisClass, "addRASALog");
	}

	public void addFRMALog(NovatelLog FRMALog)
	{	
		this.logger.entering(thisClass, "addFRMALog");

		this.FRMALogs[FRMALog.getSatID()] = FRMALog;
		this.logger.finest("FRMALog added. Log: " + this.FRMALogs[FRMALog.getSatID()].getLog());

		this.logger.exiting(thisClass, "addFRMALog");		
	}

	public void addREPALog(NovatelLog REPALog)
	{
		this.logger.entering(thisClass, "addREPALog");
		if (this.firstREPATimestamp == null)
		{
			this.firstREPATimestamp = REPALog.getLogTimestamp();
			this.logger.finer("first rawephemlog");
		}

		this.REPALogs[REPALog.getSatID()] = REPALog;

		this.logger.finest("Timestamp: " + REPALog.getLogTimestamp().toString() + " satid: " + REPALog.getSatID() 
				+ " log: " + this.REPALogs[REPALog.getSatID()].getLog());

		if (GNSSUtil.getTimeDifferenceBetweenInMillis(REPALog.getLogTimestamp(), this.firstREPATimestamp) > 5000)
		{
			this.logger.finer("new rawephem log");
			this.logger.finer("timeDifference: " + GNSSUtil.getTimeDifferenceBetweenInMillis(REPALog.getLogTimestamp(),
					this.firstREPATimestamp));
			if(this.dataSourceManager != null) 
			{
				this.dataSourceManager.forceCacheNavigationModel(this.dataSource);
			}
		}
		this.logger.exiting(thisClass, "addREPALog");
	}

	public void addIONUTCLog(NovatelLog IONUTCLog)
	{
		this.IONUTCLog = IONUTCLog;
	}

	public void addALMANACLog (NovatelLog ALMANACLog)
	{
		this.ALMANACLog = ALMANACLog;
	}

	private void finalizeGPGSVLogs (NovatelLog[] finalGPGSVLogs)
	{
		this.GPGSVLogs = finalGPGSVLogs;
	}

	public void finalizeFRMALogs(NovatelLog[] tempFRMALogs)
	{
		this.FRMALogs = tempFRMALogs;
	}

	//get accessors

	/** Accessor for GPSEPHEMLogs */
	public NovatelLog[] getGPSEPHEMLogs ()
	{
		return this.GPSEPHEMLogs;
	}

	/** Accessot for RAWGPSSUBFRAMELogs */
	public NovatelLog[] getFRMALogs ()
	{
		return this.FRMALogs;
	}

	/**Accessor for RAWEPHEMLogs */
	public NovatelLog[] getREPALogs ()
	{
		return this.REPALogs;
	}

	/**Accessor for ALMANACLog */
	public NovatelLog getALMANACLog ()
	{
		return this.ALMANACLog;
	}

	/**Accessor for IONUTCLog */
	public NovatelLog getIONUTCLog ()
	{
		return this.IONUTCLog;
	}

	/**Accessor for GPGSVLogs */
	public NovatelLog[] getGPGSVLogs()
	{
		return this.GPGSVLogs;
	}

	/**Accessor for RASALogs */
	public NovatelLog getRASALog() 
	{
		return this.RASALog;
	}

	public Logger getLogger() 
	{
		return logger;
	}

	public void setLogger(Logger logger) 
	{
		this.logger = logger;
	}

	public NovatelLog[] getGPALMLogs() 
	{
		return this.GPALMLogs;
	}
}