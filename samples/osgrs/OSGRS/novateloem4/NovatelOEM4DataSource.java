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
 * this is our novatel oem4 specific datasource. Its designed for our set-up which communcates to the RR 
 * via a terminal server.
 * 
 * @author Nam Hoang, Manosh Fernando
 *
 */
public class NovatelOEM4DataSource implements DataSource 
{

	public final static long MILLIS_IN_A_SECOND = 1000;
	public final static long MILLIS_IN_A_MINUTE = 60 * MILLIS_IN_A_SECOND;
	public final static long MILLIS_IN_A_HOUR = 60 * MILLIS_IN_A_MINUTE;
	public final static long MILLIS_IN_A_DAY = 24 * MILLIS_IN_A_HOUR;

	private long novatelOEM4InitialDelay = 25 * GPSDateTime.MILLIS_IN_A_SECOND;
	private long novatelOEM4NavigationModelPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long novatelOEM4IonUTCPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long novatelOEM4AlmanacPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long novatelOEM4ReferenceTimePeriod = 60 * GPSDateTime.MILLIS_IN_A_SECOND;

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

	public NovatelOEM4DataSource(DataSourceManager dataSourceManager, File configFile)
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

			this._host = properties.getProperty("novatelOEM4Host");
			this._port = Integer.parseInt(properties.getProperty("novatelOEM4Port"));
			this._comport = Integer.parseInt(properties.getProperty("novatelOEM4ComPort"));
			this.commandList = properties.getProperty("novatelOEM4CommandList");

			this.novatelOEM4InitialDelay = Long.parseLong(properties.getProperty("novatelOEM4InitialDelay"));
			this.novatelOEM4NavigationModelPeriod = Long.parseLong(properties.getProperty("novatelOEM4NavigationModelPeriod"));
			this.novatelOEM4IonUTCPeriod = Long.parseLong(properties.getProperty("novatelOEM4IonUTCPeriod"));
			this.novatelOEM4AlmanacPeriod = Long.parseLong(properties.getProperty("novatelOEM4AlmanacPeriod")); 
			this.novatelOEM4ReferenceTimePeriod = Long.parseLong(properties.getProperty("novatelOEM4ReferenceTimePeriod"));
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

		//this thing will be more efficient if it was if-else-if-else... instead of just if's

		if(logType.equals("RAWEPHEMA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			novatelLogCache.addRAWEPHEMLog(log);
		}
		if(logType.equals("IONUTCA"))
		{
			NovatelLog ionUTCLog = new NovatelLog(logType, novatelLog);
			novatelLogCache.addIONUTCLog(ionUTCLog);
		}
		if(logType.equals("GPSEPHEMA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			novatelLogCache.addGPSEPHEMLog(log);
		}
		if(logType.equals("ALMANACA"))
		{
			NovatelLog almanacLog = new NovatelLog(logType, novatelLog);
			novatelLogCache.addALMANACLog(almanacLog);
		}
		if(logType.equals("RAWGPSSUBFRAMEA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			novatelLogCache.addRAWGPSSUBFRAMELog(log);
		}
		if(logType.equals("GPGSV"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);
			novatelLogCache.addGPGSVLog(log);
		}
		if(logType.equals("RAWALMA"))
		{
			NovatelLog log = new NovatelLog(logType, novatelLog);

			novatelLogCache.addRAWALMALog(log);
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

	public SatelliteEphemeris[] getNavModel() //tested this, looks okay
	{
		NovatelLog[] RAWEPHEMLogs = novatelLogCache.getRAWEPHEMLogs();

		SatelliteEphemeris[] satelliteEphemerisArray = new SatelliteEphemeris[RAWEPHEMLogs.length];

		for (int i = 0; i < RAWEPHEMLogs.length; i++)
		{
			if ((RAWEPHEMLogs[i] != null) && RAWEPHEMLogs[i].getLog().contains("RAWEPHEM"))
			{
				try
				{
					satelliteEphemerisArray[i] = 
						NovatelLogProcessing.getSatelliteEphemerisFromNovatelLog(RAWEPHEMLogs[i]);
				}catch(IllegalArgumentException e)
				{
					this.logger.severe("IllegalArgumentException: " + e.getMessage());
				}
			}
		}

		return satelliteEphemerisArray;
	}

	public IonUTCModel getIonUTCModel()//test
	{
		NovatelLog ionUTCModelLogFromLogCache = novatelLogCache.getIONUTCLog();

		if(ionUTCModelLogFromLogCache != null)
		{
			try
			{
				return NovatelLogProcessing.getIonosphereAndUTCModelFromNovatelLog(ionUTCModelLogFromLogCache);
			}catch(IllegalArgumentException e)
			{
				this.logger.severe("IllegalArgumenetException: " + e.getMessage());
				return null;
			}
		} else
		{
			return null;
		}
	}

	public ReferenceTime[] getReferenceTime()
	{
		NovatelLog[] RAWGPSSUBFRAMELogs = novatelLogCache.getRAWGPSSUBFRAMELogs();

		ReferenceTime[] refTimeArray = new ReferenceTime[RAWGPSSUBFRAMELogs.length];

		for(int i = 0; i < refTimeArray.length; i++)
		{
			if((RAWGPSSUBFRAMELogs[i]!= null) && RAWGPSSUBFRAMELogs[i].getLog().contains("RAWGPSSUBFRAMEA"))
			{
				try
				{
					refTimeArray[i] = NovatelLogProcessing.processRAWGPSSUBFRAMELog(RAWGPSSUBFRAMELogs[i]);
				}catch(IllegalArgumentException e)
				{
					this.logger.severe("IllegalArgumentException: " + e.getMessage());
				}
			}
		}

		return refTimeArray;
	}

	public GPSAlmanac getGPSAlmanac()
	{
		NovatelLog almanacLogFromLogCache = novatelLogCache.getALMANACLog();

		if (almanacLogFromLogCache != null)
		{
			try
			{
				return NovatelLogProcessing.getGPSAlmanacFromALMANACLog(almanacLogFromLogCache);
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
				this.logger.severe("IllegalArgumentException: " + e.getMessage());
				return null;
			}
		} else 
		{
			return null;
		}
	}

	public RawAlmanac[] getRawAlmanac()
	{
		NovatelLog RAWALMALogFromCache = novatelLogCache.getRAWALMALog();

		if(RAWALMALogFromCache != null)
		{
			try
			{
				return NovatelLogProcessing.getRawAlmanacFromRAWALMA(RAWALMALogFromCache);
			}catch(IllegalArgumentException e)
			{
				this.logger.severe("IllegalArgumentException: " + e.getMessage());
				return null;
			}
		}else
		{
			return null;
		}
	}

	public RawIonUTCModel getRawIonUTCModel()
	{
		NovatelLog RAWALMALogFromCache = novatelLogCache.getRAWALMALog();

		if(RAWALMALogFromCache != null)
		{
			try
			{
				return NovatelLogProcessing.getRawIonUTCModelFromRAWALMALog(RAWALMALogFromCache);
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

	public long getAlmanacPeriod() 
	{
		return this.novatelOEM4AlmanacPeriod;
	}

	public long getInitialDelay() 
	{
		return this.novatelOEM4InitialDelay;
	}

	public long getIonUTCPeriod() 
	{
		return this.novatelOEM4IonUTCPeriod;
	}

	public long getNavigationModelPeriod() 
	{
		return this.novatelOEM4NavigationModelPeriod;
	}

	public long getReferenceTimePeriod() 
	{
		return this.novatelOEM4ReferenceTimePeriod;
	}
}
