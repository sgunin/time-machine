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

package OSGRS.novateloem4;

import java.util.Date;
import java.util.logging.Logger;

import OSGRS.DataManagement.DataSourceManager;
import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * this is the cache of the novatel logs. When a data Type is requested
 * from the data source, the logs used in this cache are used to create
 * the new datatype object. The synchronization stuff in this class 
 * should be removed at some point
 * @author Manosh Fernando
 */

public class NovatelLogCache
{
	/**array is indexed according to satID */
	private NovatelLog[] GPSEPHEMLogs;
	private NovatelLog[] RAWGPSSUBFRAMELogs;
	private NovatelLog[] RAWEPHEMLogs;
	private NovatelLog[] GPGSVLogs;
	private NovatelLog[] tempGPGSVLogs = null;
	private NovatelLog ALMANACLog;
	private NovatelLog RAWALMALog;
	private NovatelLog IONUTCLog;

	private DataSourceManager dataSourceManager;
	private NovatelOEM4DataSource dataSource;

	//variables for RAWEPHEMPROCESSING
	private Date firstRAWEPHEMTimestamp = null;

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

	public NovatelLogCache(DataSourceManager dataSourceManager, NovatelOEM4DataSource dataSource)
	{
		setLogger(Logger.getLogger(thisClass));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		this.logger.entering(thisClass, "Constructor");

		this.dataSourceManager = dataSourceManager;
		this.dataSource = dataSource;

		GPSEPHEMLogs = new NovatelLog[32];
		RAWGPSSUBFRAMELogs = new NovatelLog[32];
		RAWEPHEMLogs = new NovatelLog[32];

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

	public void addRAWALMALog(NovatelLog RAWALMALog)
	{
		this.logger.entering(thisClass, "addRAWALMALog");
		this.RAWALMALog = RAWALMALog;
		this.logger.finer("RAWALMALog added to log cache. Log: " + RAWALMALog.getLog());
		this.logger.exiting(thisClass, "addRAWALMALog");
	}

	public void addRAWGPSSUBFRAMELog(NovatelLog RAWGPSSUBFRAMELog)
	{	
		this.logger.entering(thisClass, "addRAWGPSSUBFRAMELog");

		this.RAWGPSSUBFRAMELogs[RAWGPSSUBFRAMELog.getSatID()] = RAWGPSSUBFRAMELog;
		this.logger.finest("RAWGPSSUBFRAMELog added. Log: " + this.RAWGPSSUBFRAMELogs[RAWGPSSUBFRAMELog.getSatID()].
				getLog());

		this.logger.exiting(thisClass, "addRAWGPSSUBFRAMELog");		
	}

	public void addRAWEPHEMLog(NovatelLog RAWEPHEMLog)
	{
		this.logger.entering(thisClass, "addRAWEPHEMLog");

		if (firstRAWEPHEMTimestamp == null)
		{
			firstRAWEPHEMTimestamp = RAWEPHEMLog.getLogTimestamp();
			this.logger.finer("first rawephemlog");
		}

		this.RAWEPHEMLogs[RAWEPHEMLog.getSatID()] = RAWEPHEMLog;

		this.logger.finest("Timestamp: " + RAWEPHEMLog.getLogTimestamp().toString() + " satid: " + RAWEPHEMLog.getSatID() 
				+ " log: " + this.RAWEPHEMLogs[RAWEPHEMLog.getSatID()].getLog());

		if (GNSSUtil.getTimeDifferenceBetweenInMillis(RAWEPHEMLog.getLogTimestamp(), firstRAWEPHEMTimestamp) > 5000)
		{
			this.logger.finer("new rawephem log");
			this.logger.finer("timeDifference: " + GNSSUtil.getTimeDifferenceBetweenInMillis(RAWEPHEMLog.getLogTimestamp(), firstRAWEPHEMTimestamp));

			if(this.dataSourceManager != null) 
			{
				this.dataSourceManager.forceCacheNavigationModel(this.dataSource);
			}
		}
		this.logger.exiting(thisClass, "addRAWEPHEMLog");
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

	public void finalizeRAWGPSSUBFRAMELogs(NovatelLog[] tempRAWGPSSUBFRAMELogs)
	{
		this.RAWGPSSUBFRAMELogs = tempRAWGPSSUBFRAMELogs;
	}

	//get accessors

	/** Accessor for GPSEPHEMLogs */
	public NovatelLog[] getGPSEPHEMLogs ()
	{
		return this.GPSEPHEMLogs;
	}

	/** Accessot for RAWGPSSUBFRAMELogs */
	public NovatelLog[] getRAWGPSSUBFRAMELogs ()
	{
		return this.RAWGPSSUBFRAMELogs;
	}

	/**Accessor for RAWEPHEMLogs */
	public NovatelLog[] getRAWEPHEMLogs ()
	{
		return this.RAWEPHEMLogs;
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

	/**Accessor for RAWALMALogs */
	public NovatelLog getRAWALMALog() 
	{
		return this.RAWALMALog;
	}

	public Logger getLogger() 
	{
		return logger;
	}

	public void setLogger(Logger logger) 
	{
		this.logger = logger;
	}
}