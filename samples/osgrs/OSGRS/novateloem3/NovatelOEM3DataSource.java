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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import OSGRS.DataManagement.DataSource;
import OSGRS.DataManagement.DataSourceManager;
import OSGRS.Util.DebugLogger;
import OSGRS.dataType.GPSAlmanac;
import OSGRS.dataType.GPSDateTime;
import OSGRS.dataType.IonUTCModel;
import OSGRS.dataType.RawAlmanac;
import OSGRS.dataType.RawIonUTCModel;
import OSGRS.dataType.ReferenceTime;
import OSGRS.dataType.SatelliteEphemeris;
import OSGRS.dataType.SatellitesInView;


/**
 * this is our novatel oem3 specific datasource. Its designed for our set-up which communciates to the 
 * RR via a terminal server. It was derived from the OEM 4 dataSource so, many things are similar.
 * 
 * @author Nam Hoang, Manosh Fernando
 * 
 */
public class NovatelOEM3DataSource implements DataSource 
{

	public final static long MILLIS_IN_A_SECOND = 1000;
	public final static long MILLIS_IN_A_MINUTE = 60 * MILLIS_IN_A_SECOND;
	public final static long MILLIS_IN_A_HOUR = 60 * MILLIS_IN_A_MINUTE;
	public final static long MILLIS_IN_A_DAY = 24 * MILLIS_IN_A_HOUR;

	private long novatelOEM3InitialDelay = 25 * GPSDateTime.MILLIS_IN_A_SECOND;
	private long novatelOEM3NavigationModelPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long novatelOEM3IonUTCPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long novatelOEM3AlmanacPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long novatelOEM3ReferenceTimePeriod = 60 * GPSDateTime.MILLIS_IN_A_SECOND;

	/** The logger used for debug for this OSGRS instance **/
	private Logger logger;

	/** The full name of this class **/
	private final String thisClass = this.getClass().getName();

	/** The the ip address of the novatel source */
	private String _host;

	/** The port of the novatel source */
	private int _port;

	/** The comport of the novatel source */
	private int _comport;

	/** The commandList to setup the correct logging for this NovatelDataSource **/
	private String commandList;

	/** The connector for this NovatelDataSource **/
	private NovatelDataSourceConnector novatelDataSourceConnector;

	/** Cache for logs */
	private NovatelLogCache novatelLogCache;

	/** data source manager */
	private DataSourceManager dataSourceManager;

	private File configFile;

	public NovatelOEM3DataSource(DataSourceManager dataSourceManager, File configFile)
	{
		this.configFile = configFile;

		this.dataSourceManager = dataSourceManager;

		this.init();
		this.start();
	}

	private void init()
	{
		novatelLogCache = new NovatelLogCache(this.dataSourceManager, this);
		setLogger(Logger.getLogger(thisClass));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);
		getValuesFromConfigFile();
		this.logger.finer("NovatelDatSourceConnector.init : init completed sucessfuly...");
	}

	private void start()
	{
		this.logger.entering(thisClass, "start");

		this.novatelDataSourceConnector = new NovatelDataSourceConnector(this,this._host,this._port,this._comport,
				this.commandList);

		this.logger.fine("NovatelDataSourceConnector.start.inf: Sucessfully created data connector");
	}

	/**
	 * This method loads values from the config file
	 *
	 */
	private void getValuesFromConfigFile()
	{
		Properties properties = new Properties();

		try 
		{
			FileInputStream fileInputStream = new FileInputStream(this.configFile);
			properties.load(fileInputStream);

			this._host = properties.getProperty("novatelOEM3Host");
			this._port = Integer.parseInt(properties.getProperty("novatelOEM3Port"));
			this._comport = Integer.parseInt(properties.getProperty("novatelOEM3ComPort"));
			this.commandList = properties.getProperty("novatelOEM3CommandList");

			this.novatelOEM3InitialDelay = Long.parseLong(properties.getProperty("novatelOEM3InitialDelay"));
			this.novatelOEM3NavigationModelPeriod = Long.parseLong(properties.getProperty
					("novatelOEM3NavigationModelPeriod"));
			this.novatelOEM3IonUTCPeriod = Long.parseLong(properties.getProperty("novatelOEM3IonUTCPeriod"));
			this.novatelOEM3AlmanacPeriod = Long.parseLong(properties.getProperty("novatelOEM3AlmanacPeriod")); 
			this.novatelOEM3ReferenceTimePeriod = Long.parseLong(properties.getProperty
					("novatelOEM3ReferenceTimePeriod"));
		} catch (NumberFormatException e) 
		{
			this.logger.severe("NumberFormatException: " + e.getMessage());
		} catch (FileNotFoundException e) 
		{
			this.logger.severe("FileNotFoundException: " + e.getMessage());
		} catch (IOException e) 
		{
			this.logger.severe("IOException: " + e.getMessage());
		}

	}

	public void generateAsssitanceDataObjectsFromNovatelLog(String logType, String novatelLog)
	{
		this.logger.entering(thisClass,"generateAsssitanceDataObjectsFromNovatelLog");

		if(logType.equals("REPA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			this.novatelLogCache.addREPALog(log);
		}

		if(logType.equals("RASA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			this.novatelLogCache.addRASALog(log);
		}

		if(logType.equals("GPALM"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			this.novatelLogCache.addGPALMLog(log);
		}

		if(logType.equals("FRMA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			novatelLogCache.addFRMALog(log);
		}

		if(logType.equals("GPGSV"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			this.logger.finest("GPGSV Timestamp: " + log.getLogTimestamp().toString());
			this.logger.finest("Log: " + log.getLog());
			novatelLogCache.addGPGSVLog(log);
		}

		this.logger.exiting(thisClass, "generateAsssitanceDataObjectsFromNovatelLog");
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

	public SatelliteEphemeris[] getNavModel()
	{
		NovatelLog[] REPALogs = this.novatelLogCache.getREPALogs();

		if(REPALogs != null)
		{
			SatelliteEphemeris[] satelliteEphemerisArray = new SatelliteEphemeris[REPALogs.length];

			for (int i = 0; i < satelliteEphemerisArray.length; i++)
			{
				if((REPALogs[i] != null))
				{
					try
					{
						satelliteEphemerisArray[i] = NovatelLogProcessing.getSatelliteEphemerisFromREPALog(REPALogs[i]);
					}catch (IllegalArgumentException e)
					{
						this.logger.severe("IllegalArgumentException: " + e.getMessage());
					}

				}
			}

			return satelliteEphemerisArray;

		}else
		{
			System.out.println("REPALogs null");
			return null;
		}

	}

	/**
	 * returns an ionUTCmodel object
	 * 
	 */
	public IonUTCModel getIonUTCModel()
	{
		RawIonUTCModel rawIonUTCModel = getRawIonUTCModel();

		if(rawIonUTCModel != null)
		{
			try
			{
				return NovatelLogProcessing.getIonUTCModelFromRawIonUTCModel(rawIonUTCModel);
			}catch (IllegalArgumentException e)
			{
				this.logger.severe("IllegalArgumentException: " + e.getMessage());
				return null;
			}
		} else
		{
			return null;
		}
	}

	public ReferenceTime[] getReferenceTime()
	{
		NovatelLog[] FRMALogs = this.novatelLogCache.getFRMALogs();

		if(FRMALogs != null)
		{
			ReferenceTime[] refTimeArray = new ReferenceTime[FRMALogs.length];

			for(int i = 0; i < refTimeArray.length; i++)
			{
				NovatelLog FRMALog = FRMALogs[i];

				if((FRMALog != null) && FRMALog.getLog().contains("FRMA"))
				{
					try
					{
						refTimeArray[i] = NovatelLogProcessing.getReferenceTimeFRMALog(FRMALog);
					} catch(IllegalArgumentException e)
					{
						this.logger.severe("IllegalArgumentException: " + e.getMessage());
					}
				}
			}

			return refTimeArray;
		} else
		{
			return null;
		}
	}

	public GPSAlmanac getGPSAlmanac()
	{
		NovatelLog[] GPALMLogs = this.novatelLogCache.getGPALMLogs();

		try
		{
			return NovatelLogProcessing.getGPSAlmanacFromGPALMLogs(GPALMLogs);
		}catch(IllegalArgumentException e)
		{
			this.logger.severe("IllegalArgumentException: " + e.getMessage());
			return null;
		}catch (NullPointerException e)
		{
			this.logger.severe("NullPointerException: " + e.getMessage());
			return null;
		}
	}

	public SatellitesInView getSatellitesInViewOfReciever()
	{

		NovatelLog[] GPGSVLogsFromLogCache = novatelLogCache.getGPGSVLogs();

		if (GPGSVLogsFromLogCache != null)
		{
			try
			{
				return NovatelLogProcessing.processGPGSVLogs(GPGSVLogsFromLogCache);
			}catch(IllegalArgumentException e)
			{
				this.logger.severe("IllegalArgumentException " + e.getMessage());
				return null;
			}
		} else 
		{
			return null;
		}
	}

	public RawAlmanac[] getRawAlmanac()
	{
		NovatelLog RASALogFromCache = this.novatelLogCache.getRASALog();

		if(RASALogFromCache != null)
		{
			try
			{
				return NovatelLogProcessing.getRawAlmanacFromRASALog(RASALogFromCache);
			}catch(IllegalArgumentException e)
			{
				this.logger.severe("IllegalArgumentException: " + e.getMessage());
				return null;
			}
		} else
		{
			return null;
		}
	}

	public RawIonUTCModel getRawIonUTCModel()
	{
		NovatelLog RASALogFromCache = this.novatelLogCache.getRASALog();

		if(RASALogFromCache != null)
		{
			try 
			{
				return NovatelLogProcessing
				.getRawIonUTCModelFromRASALogs(RASALogFromCache);
			} catch (IllegalArgumentException e) 
			{
				this.logger.severe("IllegalArgumentException: " + e.getMessage());
				return null;
			}			
		} else
		{
			return null;
		}
	}

	public long getAlmanacPeriod() 
	{
		return this.novatelOEM3AlmanacPeriod;
	}

	public long getInitialDelay() 
	{
		return this.novatelOEM3InitialDelay;
	}

	public long getIonUTCPeriod() 
	{
		return this.novatelOEM3IonUTCPeriod;
	}

	public long getNavigationModelPeriod() 
	{
		return this.novatelOEM3NavigationModelPeriod;
	}

	public long getReferenceTimePeriod() 
	{
		return this.novatelOEM3ReferenceTimePeriod;
	}
}
