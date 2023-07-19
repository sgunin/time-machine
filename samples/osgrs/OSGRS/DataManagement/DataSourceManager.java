package OSGRS.DataManagement;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import OSGRS.Util.DebugLogger;
import OSGRS.dataType.SatellitesInView;
import OSGRS.novateloem3.NovatelOEM3DataSource;
import OSGRS.novateloem4.NovatelOEM4DataSource;

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

/**
 * This class is used to Manage the initilisation of the dataSource and dataCache and caching
 * of data from datasources.
 * @author Manosh Fernando
 */
public class DataSourceManager
{
//	/** The oem4 specific datasource used by this OSGRS implementation */
//	private DataSource novatelDataSource;
	
	/**DataSource array which contains all the dataSources used by this OSGRS*/
	private DataSource[] dataSourceArray;
	
	/** data cache */
	private GNSSDataCache dataCache;
	
	//
	
	/** Timer object that will do caching at regular intervals, this array is 
	 *  aligned with dataSource array i.e dataSourceArray[0]'s associated timer is 
	 *  timerArrayp[0]*/
	private Timer[] timerArray;
	
	/** the logger for this class */
	private Logger logger;
	
	/** The full name of this class */
    private final String thisClass = this.getClass().getName();
    
    /** Total number of dataSources */
    private int numberOfDataSources;
    
    /** number of OEM3 data sources */
    private int numberOfOEM3DataSources;
    
    /** number of OEM4 data source */
    private int numberOfOEM4DataSources;
	
	public DataSourceManager ()
	{
//		set up logger
    	setLogger(Logger.getLogger(this.thisClass));
    	this.logger.setLevel(DebugLogger.getOutputVerbosity());
    	DebugLogger.recordLogToFile(this.logger);
    	
    	this.logger.entering(thisClass, "Constructor");
		
		init();
		start();
		
		this.logger.exiting(thisClass, "Constructor");
	}
	
	/**
	 * The init method contains initialising of dataSources and thier associated
	 * timers rsponsible for scheduled caching of data. Declaring dataSources is done here
	 * so if dataSources are to be added or deleted, it must be done in this method.
	 *
	 */
	private void init()
	{
		this.logger.entering(thisClass, "init");
		
		initilizeDataSources();
		
		this.timerArray = new Timer[this.dataSourceArray.length];
		
		dataCache = new GNSSDataCache(this);
		
		for(int i = 0; i < this.timerArray.length; i++)
		{
			this.timerArray[i] = new Timer("chache thread-" + i);
		}
		
		//new ForceCacheTestThread(this, this.dataSourceArray[1]).start();
		
		this.logger.exiting(thisClass,"init");
	}
	
	/**
	 * this method initialises the schedling  for caching data to the dataCache
	 *
	 */
	private void start()
	{
		this.logger.entering(thisClass, "start");
		
		//start timers
		
		for(int i = 0; i < this.timerArray.length; i++)
		{
		
			this.timerArray[i].scheduleAtFixedRate(new cacheNavigationModel(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(),
				this.dataSourceArray[i].getNavigationModelPeriod());
			this.timerArray[i].scheduleAtFixedRate(new cacheIonUTCModel(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(), 
				this.dataSourceArray[i].getIonUTCPeriod());
			this.timerArray[i].scheduleAtFixedRate(new cacheRawIonUTCModel(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(), 
				this.dataSourceArray[i].getIonUTCPeriod());
			this.timerArray[i].scheduleAtFixedRate(new cacheReferenceTime(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(), 
				this.dataSourceArray[i].getReferenceTimePeriod());
//		timer.scheduleAtFixedRate(new cacheAlmanacModel(), this.novatelDataSource.getInitialDelay(), 
//				this.novatelDataSource.getAlmanacPeriod());
			this.timerArray[i].scheduleAtFixedRate(new cacheRawAlmanac(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(), 
				this.dataSourceArray[i].getAlmanacPeriod());
			this.timerArray[i].scheduleAtFixedRate(new cacheGPSAlmanac(this.dataSourceArray[i]), this.dataSourceArray[i].getInitialDelay(), 
				this.dataSourceArray[i].getAlmanacPeriod());
//		timer.scheduleAtFixedRate(new forceGarbageCollect(), 20 * GPSDateTime.MILLIS_IN_A_MINUTE, 
//				20 * GPSDateTime.MILLIS_IN_A_MINUTE);
		
		}
		
		this.logger.exiting(thisClass, "start");
	}
	
	/**
	 * This is the method that initilizes all the data sources from the main config file.
	 * It will have to be updated if additional data sources are written
	 *
	 */
	private void initilizeDataSources()
	{
		this.numberOfOEM3DataSources = Integer.parseInt(System.getProperty("numberNovatelOEM3DataSource"));
		this.numberOfOEM4DataSources = Integer.parseInt(System.getProperty("numberNovatelOEM4DataSource"));
		
		this.numberOfDataSources = this.numberOfOEM3DataSources + this.numberOfOEM4DataSources;
		
		this.dataSourceArray = new DataSource[this.numberOfDataSources];
		int n = 0; //index for above array
		
		//OEM 3
		for(int i = 0; i < this.numberOfOEM3DataSources; i++)
		{
			File configFile = new File(System.getProperty("novatelOEM3ConfigFile" + (i+1)));
			
			this.dataSourceArray[n] = new NovatelOEM3DataSource(this, configFile);
			n++;
		}
		
		//OEM4
		for(int i = 0; i < this.numberOfOEM4DataSources; i++)
		{
			File configFile = new File(System.getProperty("novatelOEM4ConfigFile" + (i+1)));
			
			this.dataSourceArray[n] = new NovatelOEM4DataSource(this, configFile);
			n++;
		}
		
	}
	
	//force methods, used mainly to force update of dataCahe when say a new
	//log is recieved
	                  
	public void forceCacheNavigationModel(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheNavigationModel");
		
		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheNavigationModel(dataSource), 0);
		
		this.logger.fine("cacing of navigation model has been forced");
		this.logger.exiting(thisClass, "forceCacheNavigationModel");
	}
	
	public void forceCacheIonUTCModel(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheIonUTCModel");

		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheIonUTCModel(dataSource), 0);
		
		this.logger.exiting(thisClass, "forceCacheIonUTCModel");
	}
	
	public void forceCacheRawIonUTCModel(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheRawIonUTCModel");
		
		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheRawIonUTCModel(dataSource), 0);
		
		this.logger.exiting(thisClass, "forceCacheRawIonUTCModel");
	}
	
	public void forceCacheReferenceTime(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheReferenceTime");
		
		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheReferenceTime(dataSource), 0);
		
		this.logger.exiting(thisClass, "forceCacheReferenceTime");
	}
	
	public void forceCacheRawAlmanac(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheRawAlmanac");
		
		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheRawAlmanac(dataSource), 0);
		
		this.logger.exiting(thisClass, "forceCacheRawAlmanac");
	}
	
	public void forceCacheGPSAlmanac(DataSource dataSource)
	{
		this.logger.entering(thisClass, "forceCacheGPSAlmanac");

		Timer timer = matchDataSourceWithCorrectTimer(dataSource);
		timer.schedule(new cacheGPSAlmanac(dataSource), 0);
		
		this.logger.exiting(thisClass, "forceCacheGPSAlmanac");
	}
	
	/**
	 * This matches data source with the correct timer so force function is made to the 
	 * correct Timer/DataSource
	 * @param dataSource
	 * @return timer object which corresponds to dataSource
	 */
	private Timer matchDataSourceWithCorrectTimer(DataSource dataSource)
	{
		//boolean dataSourceFound=false;
		
		int n=0; //index of found dataSource
		
		for(int i = 0; i < this.dataSourceArray.length; i++)
		{
			if(dataSource == this.dataSourceArray[i])
			{
				n=i;
				break;
			}
		}
		
		return this.timerArray[n];
	}
	
	//timerTasks for Timers
	//These are the classes that run when data is to be cached.
	
	private class cacheNavigationModel extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**This class's logger */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheNavigationModel(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
		
		public void run()
		{
			this.logger.entering(thisClass, "run");
			
			dataCache.addSatelliteEphemeris(this.dataSource.getNavModel());
			this.logger.exiting(thisClass, "run");
		}	
	}
	
	private class cacheIonUTCModel extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**logger of this class */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheIonUTCModel(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
	    
		public void run()
		{
			this.logger.entering(thisClass, "run");
			
			dataCache.addIonUTCModel(this.dataSource.getIonUTCModel());
			this.logger.exiting(thisClass, "run");
		}
	}
	
	private class cacheRawIonUTCModel extends TimerTask
	{
		/**The full name of this class */
		private final String thisClass = this.getClass().getName();
		
		/**logger of this class */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheRawIonUTCModel(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
	    
	    public void run()
		{
			this.logger.entering(thisClass, "run");
			
			dataCache.addRawIonUTCModel(this.dataSource.getRawIonUTCModel());
			this.logger.exiting(thisClass, "run");
		}
	}
	
	private class cacheGPSAlmanac extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**This class's logger */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheGPSAlmanac(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
	    
	    public void run()
	    {
	    	this.logger.entering(thisClass, "run");
	    	
	    	dataCache.addGPSAlmanac(this.dataSource.getGPSAlmanac());
	    	
	    	this.logger.exiting(thisClass, "run");
	    }
	}
	
	private class cacheReferenceTime extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**This class's logger */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheReferenceTime(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
	    
		public void run ()
		{
			this.logger.entering(thisClass,"run");
			
			dataCache.addReferenceTime(this.dataSource.getReferenceTime());
			this.logger.exiting(thisClass, "run");
		}
	}
	
	private class cacheRawAlmanac extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**This class's logger */
	    private Logger logger = getLogger();
	    
	    private DataSource dataSource;
	    
	    public cacheRawAlmanac(DataSource dataSource)
	    {
	    	super();
	    	this.dataSource = dataSource;
	    }
		
		public void run ()
		{
			this.logger.entering(thisClass, "run");
			
			dataCache.addRawAlmanac(this.dataSource.getRawAlmanac());
			this.logger.exiting(thisClass, "run");
		}
	}
	
	private class forceGarbageCollect extends TimerTask
	{
		/** The full name of this class */
	    private final String thisClass = this.getClass().getName();
	    
	    /**This class's logger */
	    private Logger logger = getLogger();
	    
	    public void run()
	    {
	    	this.logger.entering(thisClass, "run");
	    	System.gc();
	    	this.logger.fine("made gc() call to system");
	    	this.logger.exiting(thisClass, "run");
	    }
	}
	
	/**
	 * Used for Satellites In View caculation. Currently only gets the satellites in view of 
	 * one of the recievers. A proper SIV engine which calulates satellites in view from a 
	 * lat and long should replace this method.
	 * @return SatellitesInView object
	 */
	public SatellitesInView getSatellitesInViewOfReciever()
	{
		return this.dataSourceArray[0].getSatellitesInViewOfReciever();
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

	public GNSSDataCache getDataCache() 
	{
		return this.dataCache;
	}
	
	/**
	 * used for testing of force cache methods.
	 *
	 */
	private class ForceCacheTestThread extends Thread
	{
		private DataSourceManager dataSourceManager;
		
		private DataSource dataSource;
		
		public ForceCacheTestThread(DataSourceManager dataSourceManager, DataSource dataSource)
		{
			super();
			
			this.dataSourceManager = dataSourceManager;
			this.dataSource = dataSource;
		}
		
		public void run()
		{
			try 
			{
		        Thread.sleep(30000);
		    } catch (InterruptedException e) 
		    {
		        System.out.println("We've been interrupted! ");
		        return;
		    }
		    
		    System.out.println("starting test thread");
		    
		    //this.dataSourceManager.forceCacheGPSAlmanac(this.dataSource);
		    //this.dataSourceManager.forceCacheIonUTCModel(this.dataSource);
		    //this.dataSourceManager.forceCacheNavigationModel(dataSource);
		    //this.dataSourceManager.forceCacheRawAlmanac(this.dataSource);
		    //this.dataSourceManager.forceCacheRawIonUTCModel(this.dataSource);
		    //this.dataSourceManager.forceCacheReferenceTime(this.dataSource);
		}
	}

}
