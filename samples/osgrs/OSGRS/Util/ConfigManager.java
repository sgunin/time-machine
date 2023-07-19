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

package OSGRS.Util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Logger;

import OSGRS.OSGRS.OSGRS;
import OSGRS.dataType.GPSDateTime;

/**
 * This class manages the configurable keys and values,
 * it can also generate default values for these days.
 * @author Nam Hoang, Manosh Fernando
 */

public class ConfigManager
{
	/** The OSGRS instance this Config Manager belongs to */
	private OSGRS OSGRS;

	/** The properties instance used to write keys and values to config file */
	private Properties properties;

	/** The properties instance used to load keys and values from config file */
	private Properties propertiesHolder;

	/** The logger used for debug for this class */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** The configuration file name*/
	private final String configFileName = "SystemConfig";

	/**--- Default configurations ---*/
//	/** The default configuration for global data timeout interval */
//	private final int globalDataTimeOutIntervalMinutes = 100;

//	/** The default configuration for almanac data timeout (in days) */
//	private final int almanacDataTimeOutIntervalDays = 1;

//	/** The default configuration for TOW assist data timeout (in seconds) */
//	private final int TOWAssistDataTimeOutIntervalSeconds = 250;

	// staleness time for datatypes
	private final long almanacStalenessTime = 1 * GPSDateTime.MILLIS_IN_A_DAY;
	private final long satelliteEphemerisStalenessTime = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long ionutcStalenessTime = 2 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long referenceTimeStalenessTime =  6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long rtiStalenessTime = 2 * GPSDateTime.MILLIS_IN_A_HOUR;

	//periods for monitoring timer
	private final long initialDataCacheDelay = 20 * GPSDateTime.MILLIS_IN_A_SECOND;
	private final long almanacMonitoringPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long satelliteEphemerisMonitoringPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long ionutcMonitoringPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long referenceTimeMonitoringPeriod = 30 * GPSDateTime.MILLIS_IN_A_SECOND;
	private final long rtiMonitoringPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;

	//
	private final long novatelOEM4InitialDelay = 25 * GPSDateTime.MILLIS_IN_A_SECOND;
	private final long novatelOEM4NavigationModelPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long novatelOEM4IonUTCPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long novatelOEM4AlmanacPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long novatelOEM4ReferenceTimePeriod = 60 * GPSDateTime.MILLIS_IN_A_SECOND;

	private final long novatelOEM3InitialDelay = 25 * GPSDateTime.MILLIS_IN_A_SECOND;
	private final long novatelOEM3NavigationModelPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long novatelOEM3IonUTCPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private final long novatelOEM3AlmanacPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private final long novatelOEM3ReferenceTimePeriod = 60 * GPSDateTime.MILLIS_IN_A_SECOND;

	/** The default configuration for the output verbosity
	 * note: this corrosponds with the levels of the Logging component: OFF, SEVERE, INFO AND FINER */
	private final int outputVerbosity = 0;

	/** The default configuration for the port inwhich the OSGRS accepts connections on */
	private final int listenerPort= 8080;

	private final int novatelOEM4ComPort = 1;

	private final int novatelOEM4Port = 2300;

	private final int novatelOEM3ComPort = 1;

	private final int novatelOEM3Port = 2600;

	/** This array contains the long values of configurables */
	private long[] configLongValues = {this.almanacStalenessTime,
			this.satelliteEphemerisStalenessTime,
			this.ionutcStalenessTime,
			this.referenceTimeStalenessTime,
			this.rtiStalenessTime,

			this.initialDataCacheDelay,
			this.almanacMonitoringPeriod,
			this.satelliteEphemerisMonitoringPeriod,
			this.ionutcMonitoringPeriod,
			this.referenceTimeMonitoringPeriod,
			this.rtiMonitoringPeriod,

			this.novatelOEM4InitialDelay,
			this.novatelOEM4NavigationModelPeriod,
			this.novatelOEM4IonUTCPeriod,
			this.novatelOEM4AlmanacPeriod,
			this.novatelOEM4ReferenceTimePeriod,

			this.novatelOEM3InitialDelay,
			this.novatelOEM3NavigationModelPeriod,
			this.novatelOEM3IonUTCPeriod,
			this.novatelOEM3AlmanacPeriod,
			this.novatelOEM3ReferenceTimePeriod
	};

	/** This array contains the corrosponding key values of long configurables above*/
	private String[] configLongKeys = {"almanacStalenessTime",
			"satelliteEphemerisStalenessTime",
			"ionutcStalenessTime",
			"referenceTimeStalenessTime",
			"rtiStalenessTime",

			"initialDataCacheDelay",
			"almanacMonitoringPeriod",
			"satelliteEphemerisMonitoringPeriod",
			"ionutcMonitoringPeriod",
			"referenceTimeMonitoringPeriod",
			"rtiMonitoringPeriod",

			"novatelOEM4InitialDelay",
			"novatelOEM4NavigationModelPeriod",
			"novatelOEM4IonUTCPeriod",
			"novatelOEM4AlmanacPeriod",
			"novatelOEM4ReferenceTimePeriod",

			"novatelOEM3InitialDelay",
			"novatelOEM3NavigationModelPeriod",
			"novatelOEM3IonUTCPeriod",
			"novatelOEM3AlmanacPeriod",
			"novatelOEM3ReferenceTimePeriod"
	};

	/** This array contains the int values of configurables */
	private int[] configIntValues = {this.outputVerbosity,
			this.listenerPort,
			this.novatelOEM4ComPort,
			this.novatelOEM4Port,
			this.novatelOEM3ComPort,
			this.novatelOEM3Port
	};

	private String[] configIntKeys = {"outputVerbosity",
			"listenerPort",
			"novatelOEM4ComPort",
			"novatelOEM4Port",
			"novatelOEM3ComPort",
			"novatelOEM3Port"
	};

	/** The default configuration for the Data Source Plugin Module */
	private final String GNSSDataSource = "NovatelDataSource";

	/** The default configuration for the GRIP version */
	private final String gripVersion = "1.4";

	private final String logDirectory = "D:\\ANSA\\My Documents\\osgrs logs\\dbg1\\";

	private final String novatelOEM4Host = "10.102.200.3";

	private final String novatelOEM3Host = "10.102.200.3";

	private final String novatelOEM4CommandList = "UNLOGALL COM1%LOG GPGSV ONTIME 5%LOG RAWEPHEMA ONCHANGED" +
	"%LOG IONUTCA ONCHANGED%LOG RAWALMA ONCHANGED%LOG ALMANACA ONCHANGED%LOG GPSEPHEMA ONCHANGED" +
	"%LOG RAWGPSSUBFRAMEA ONNEW";

	private final String novatelOEM3CommandList = "UNLOGALL COM1%LOG GPGSV ONTIME 5%LOG RASA ONCHANGED%LOG REPA ONCHANGED" +
	"%LOG FRMA ONNEW%LOG GPALM ONCHANGED"; 

	/** This array contains the string values of configurables */
	private String[] configStringValues = {this.GNSSDataSource,
			this.gripVersion,
			this.novatelOEM4Host,
			this.novatelOEM3Host,
			this.novatelOEM4CommandList,
			this.novatelOEM3CommandList,
			this.logDirectory};

	/** This array contains the corrosponding key values of string configurables above*/
	private String[] configStringKeys = {"GNSSDataSource",
			"gripVersion",
			"novatelOEM4Host",
			"novatelOEM3Host",
			"novatelOEM4CommandList",
			"novatelOEM3CommandList",
	"logDirectory"};

	/**--- End of default configurations ---*/

	/**
	 * This is the constructor for the Config Manager class
	 * @param OSGRS the OSGRS inwhich this instance belongs to
	 * @param state the OSGRS state, 1 indicates OSGRS has been initialised
	 */
	public ConfigManager()
	{
		init();
	}

	/**
	 * This method initialises attributes of the Config Manager
	 * it also loads the properties calling on the loadProprties method passing it a state
	 * @param state is the state of the OSGRS
	 */
	private void init()
	{
		this.properties = new Properties();
		this.propertiesHolder = new Properties();

		setLogger(Logger.getLogger(thisClass)); // get a logger for this class

//		####### use these methods to write default configurables to files #######
//		this.writeDefaultConfigItems();                                      //#
//		this.writePropertiesToFile();                                       //#
//		#########################################################################

		loadPropertiesFromFile();


		this.logger.setLevel(DebugLogger.getOutputVerbosity());

		DebugLogger.recordLogToFile(this.logger);

	}

	/**
	 * This method is used to generate the default config items
	 * and write them to file.
	 */
	private void writeDefaultConfigItems()
	{
		this.setConfigDefaultLongValues();
		this.setConfigDefaultIntValues();
		this.setConfigDefaultStringValues();
	}

	/**
	 * This method sets the default string values
	 */
	private void setConfigDefaultStringValues()
	{
		for(int i=0;i<this.configStringValues.length;i++)
		{
			setProperty(this.configStringKeys[i],this.configStringValues[i]);
		}
	}

	/**
	 * This method sets the default Long values
	 */
	private void setConfigDefaultLongValues()
	{
		for(int i=0; i<this.configLongValues.length; i++)
		{
			String tempString;
			tempString = Long.toString(this.configLongValues[i]);
			setProperty(this.configLongKeys[i],tempString);
		}
	}

	/**
	 * This method sets the default int values
	 */

	private void setConfigDefaultIntValues()
	{
		for(int i=0; i<this.configIntValues.length; i++)
		{
			String tempString;
			tempString = Integer.toString(this.configIntValues[i]);
			setProperty(this.configIntKeys[i],tempString);
		}
	}

	/**
	 * Set a property for a Key with a value.
	 * @param key is the key
	 * @param value value for that key
	 */
	private void setProperty(String key, String value)
	{
		this.properties.setProperty(key, value);
	}

	/**
	 * This method is used to output the configurables to a .conf file
	 * corrosponding to the configFileName attribute.
	 */
	private void writePropertiesToFile()
	{
		this.logger.entering(thisClass, "writePropertiesToFile");

		try
		{
			this.properties.store(new FileOutputStream(this.configFileName + ".conf"), null);
			this.logger.info("Configuration file sucessfully written to file");

		}
		catch(Exception e){}
	}

	/**
	 * Load the configurables from a file with the corrosponding
	 * configFileName attribute file name.
	 * @param state the state of a OSGRS
	 */
	private void loadPropertiesFromFile()
	{
		try{
			this.propertiesHolder.load(new FileInputStream(configFileName + ".conf"));

			//##Add configurables to the System##

			//   ### Staleness timers ###

			Enumeration propertyNames = this.propertiesHolder.propertyNames();

			String[] propertyNamesStringArray = new String[50];

			String[] propertyValuesStringArray = new String[50];

			for(int i=0; i < propertyNamesStringArray.length; i++)
			{
				if(propertyNames.hasMoreElements())
				{
					propertyNamesStringArray[i] = (String)propertyNames.nextElement();
					//System.out.println("i: " + i + " propertyName: " + propertyNamesStringArray[i]);
				}
			}

			for(int i = 0; i < propertyValuesStringArray.length; i++)
			{
				if(propertyNamesStringArray[i] != null)
				{
					propertyValuesStringArray[i] = this.propertiesHolder.getProperty(propertyNamesStringArray[i]);
					System.setProperty(propertyNamesStringArray[i], propertyValuesStringArray[i]);
				}
			}

//			String globalDataTimeOutIntervalMinutesString = 
//			this.propertiesHolder.getProperty("globalDataTimeOutIntervalMinutes");
//			String almanacDataTimeOutIntervalDaysString = 
//			this.propertiesHolder.getProperty("almanacDataTimeOutIntervalDays");
//			String TOWAssistDataTimeOutIntervalSecondsString = 
//			this.propertiesHolder.getProperty("TOWAssistDataTimeOutIntervalSeconds");

//			System.setProperty("globalDataTimeOutIntervalMinutes",globalDataTimeOutIntervalMinutesString);
//			System.setProperty("almanacDataTimeOutIntervalDays", almanacDataTimeOutIntervalDaysString);
//			System.setProperty("TOWAssistDataTimeOutIntervalSeconds", TOWAssistDataTimeOutIntervalSecondsString);


//			//   ### Output Verbosity ###
//			String outputVerbosityString = this.propertiesHolder.getProperty("outputVerbosity");

//			System.setProperty("outputVerbosity", outputVerbosityString);

//			// ###GNSS Data Source locale ###

//			String GNSSDataSourceString = this.propertiesHolder.getProperty("GNSSDataSource");

//			System.setProperty("GNSSDataSource", GNSSDataSourceString);

//			// #### Listener Port ###

//			String listenerPortString = this.propertiesHolder.getProperty("listenerPort");

//			System.setProperty("listenerPort",listenerPortString);

			this.logger.info("Configuration file sucessfully loaded");

		}

		catch(IOException e)
		{
			this.logger.severe("ConfigManager.loadPropertiesFromFile.err: IOException when loading config: " + e);
			System.exit(1);
		}
		catch(NullPointerException e)
		{
			this.logger.severe("ConfigManager.loadPropertiesFromFile.err: NullPointerException when loading config: " + e);
			System.exit(1);
		}
	}




	/* =============   Used for debug/testing only ============= */
	private void printDefaultProperties()
	{
		printConfigIntValues();
		printConfigStringValues();
	}


	private void printConfigIntValues()
	{
		for(int i=0;i<this.configLongValues.length; i++)
		{
			System.out.println(this.configLongKeys[i] + "= " + this.propertiesHolder.getProperty(this.configLongKeys[i]));
		}
	}

	private void printConfigStringValues()
	{
		for(int i=0;i<this.configStringValues.length;i++)
		{
			System.out.println(this.configStringKeys[i] + "= " + this.propertiesHolder.getProperty(this.configStringKeys[i]));
		}
	}
	/* ========================================================= */

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

//	=========================================================

//	============================================================================================================================
	/* Stuff discarded --
	 *
	 * private void printConfigBooleanValues()
  {
      for(int i=0;i<this.configBooleanValues.length;i++)
      {
          System.out.println(this.configBooleanKeys[i] + "= " + this.propertiesHolder.getProperty(this.configBooleanKeys[i]));
      }
  }*/

	/*
	 * This is no longer used after 1.1 review of CWB
     private boolean navModelSupport = false;
     private boolean ionosphereSupport = false;
     private boolean RTISupport = false;
     private boolean acqAssSupport = false;
     private boolean UTCSupport = false;
     private boolean referenceTimeSupport = false;
     private boolean almanacSupport = false;
     private boolean DGNSSSupport = false;

     private boolean[] configBooleanValues = {this.navModelSupport,
                                              this.ionosphereSupport,
                                              this.RTISupport,
                                              this.acqAssSupport,
                                              this.UTCSupport,
                                              this.referenceTimeSupport,
                                              this.almanacSupport,
                                              this.DGNSSSupport};

    private String[] configBooleanKeys = {"navModelSupport",
                                          "ionosphereSupport",
                                          "RTISupport",
                                          "acqAssSupport",
                                          "UTCSupport",
                                          "referenceTimeSupport",
                                          "almanacSupport",
                                          "DGNSSSupport"};*/

	/*   private void setConfigDefaultBooleanValues()
  {
      for(int i=0;i<this.configBooleanValues.length;i++)
      {
          String tempBoolean;
          tempBoolean = Boolean.toString(this.configBooleanValues[i]);
          setProperty(this.configBooleanKeys[i],tempBoolean);

      }
  }*/
//	============================================================================================================================


//	/**
//	* return the listener port
//	* @return lpInt, this is the listener port
//	*/
//	public int getListenerPort()
//	{
//	this.logger.entering(thisClass, "getListenerPort");


//	String lpString = getProperty("listenerPort");
//	int lpInt = Integer.valueOf(lpString).intValue(); //int value of integer(from a string)

//	return lpInt;
//	}

//	/** return the output verbosity
//	* @return ovInt, this is the output verbosity integer
//	*/
//	public int getOutputVerbosity()
//	{
//	this.logger.entering(thisClass, "getOutputVerbosity");

//	String ovString = getProperty("outputVerbosity");
//	int ovInt = Integer.valueOf(ovString).intValue();
//	return ovInt;
//	}


//	/**
//	* This sets the default configurable attributes (ints annd strings) of this class
//	*/
//	private void setDefaultProperties()
//	{
//	this.logger.entering(thisClass, "setDefaultProperties");

//	setConfigDefaultStringValues();
//	setConfigDefaultIntValues();
//	}

}
