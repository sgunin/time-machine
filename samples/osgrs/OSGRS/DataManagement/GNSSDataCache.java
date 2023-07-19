package OSGRS.DataManagement;

import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;
import OSGRS.dataType.GPSAlmanac;
import OSGRS.dataType.GPSDateTime;
import OSGRS.dataType.IonUTCModel;
import OSGRS.dataType.RTI;
import OSGRS.dataType.RawAlmanac;
import OSGRS.dataType.RawIonUTCModel;
import OSGRS.dataType.ReferenceTime;
import OSGRS.dataType.SatelliteEphemeris;
import OSGRS.dataType.SatellitesInView;

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
 * This class is the cache of OSGRS. It contains all the datatypes which will be
 * handed out to clients. The dataTypes are stored as a single private object or array of
 * dataType objects. I havn't used sychronisation as i found it made things slower.
 * Any methods or classes that link to these objects should take into account that these
 * objects may be changed at any time i.e, in navModel array, one of the pointers may 
 * made null if that object has passed staleness time.
 * 
 * It might be better latter on if this class is replaced with a 'database' or
 * better way of holding data. 
 * 
 * @author Manosh Fernando
 *
 */
public class GNSSDataCache {

	//dataSourceManager

	DataSourceManager dataSourceManager;

	//cached data

	private GPSAlmanac cachedGPSAlmanac;

	/** cached Raw Almanac */
	private RawAlmanac[] cachedRawAlmanacArray;

	/** cached array of SatelliteEphemeis (navigation model)  index of array should be organised by (PRN-1)*/
	private SatelliteEphemeris[] cachedSatelliteEphemeris;

	/** cached ionoshpere and UTC model */
	private IonUTCModel cachedIonUTCModel;

	/** cached rawIonUTCModel */
	private RawIonUTCModel cachedRawIonUTCModel;

	/** cached  array of ReferenceTime */
	private ReferenceTime[] cachedReferenceTime;

	/** cached Real Time integrity */
	private RTI cachedRTI;

	/** Logger for this class*/
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** timer for monitoring data */
	private Timer monitorTimer;

	//flags for reference time 
	private boolean referenceTimeReady;

	//locks for each stored data type

//	private Object cachedAlmanacLock = new Object();
//	private Object cachedRawAlmanacLock = new Object();
//	private Object cachedSatelliteEphemerisLock = new Object();
//	private Object cachedIonUTCModelLock = new Object();
//	private Object cachedReferenceTimeLock = new Object();
//	private Object cachedRTILock = new Object();
//	private Object cachedRawIonUTCModelLock = new Object();

	//staleness time for datatypes
	private long almanacStalenessTime = 1 * GPSDateTime.MILLIS_IN_A_DAY;
	private long satelliteEphemerisStalenessTime = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long ionutcStalenessTime = 2 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long referenceTimeStalenessTime =  6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long rtiStalenessTime = 2 * GPSDateTime.MILLIS_IN_A_HOUR;

	//periods for monitoring timer
	private long initialDelay = 20 * GPSDateTime.MILLIS_IN_A_SECOND;
	private long almanacMonitoringPeriod = 6 * GPSDateTime.MILLIS_IN_A_HOUR;
	private long satelliteEphemerisMonitoringPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long ionutcMonitoringPeriod = 20 * GPSDateTime.MILLIS_IN_A_MINUTE;
	private long referenceTimeMonitoringPeriod = 30 * GPSDateTime.MILLIS_IN_A_SECOND;
	private long rtiMonitoringPeriod = 10 * GPSDateTime.MILLIS_IN_A_MINUTE;

	//constants
	public final static int NUMBER_OF_SATELLITES_IN_FLEET = 32;

	public GNSSDataCache(DataSourceManager dataSourceManager)
	{
		setLogger(Logger.getLogger(thisClass));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);
		this.logger.entering(thisClass, "Constructor");
		this.dataSourceManager = dataSourceManager;
		init();
		start();
		this.logger.exiting(thisClass, "Constructor");
	}

	private void init()
	{
		this.logger.entering(thisClass, "init");
		this.monitorTimer = new Timer();

		getConfiguredTimerValues();

		this.cachedSatelliteEphemeris = new SatelliteEphemeris[GNSSDataCache.NUMBER_OF_SATELLITES_IN_FLEET];
		this.cachedReferenceTime = new ReferenceTime[GNSSDataCache.NUMBER_OF_SATELLITES_IN_FLEET];
		this.cachedRawAlmanacArray = new RawAlmanac[GNSSDataCache.NUMBER_OF_SATELLITES_IN_FLEET];

		this.logger.exiting(thisClass, "init");
	}

	/**
	 * This method initises the scheduling of the timerTasks which are used to monitor the
	 * data in the datacache.
	 *
	 */
	private void start()
	{
		this.logger.entering(thisClass, "start");

		this.monitorTimer.scheduleAtFixedRate(new checkSatelliteEphemeris(),
				initialDelay, satelliteEphemerisMonitoringPeriod);
		this.monitorTimer.scheduleAtFixedRate(new checkIonUTCModel(), initialDelay, ionutcMonitoringPeriod);
		this.monitorTimer.scheduleAtFixedRate(new checkReferenceTime(), initialDelay, referenceTimeMonitoringPeriod);
		this.monitorTimer.scheduleAtFixedRate(new checkRTI(), initialDelay, rtiMonitoringPeriod);
		this.monitorTimer.scheduleAtFixedRate(new checkRawIonUTCModel(), initialDelay, ionutcMonitoringPeriod);
		this.monitorTimer.scheduleAtFixedRate(new checkGPSAlmanac(), initialDelay, almanacMonitoringPeriod);

		this.logger.exiting(thisClass, "start");
	}

	public SatellitesInView getSatellitesInViewOfReciever()
	{
		return this.dataSourceManager.getSatellitesInViewOfReciever();
	}

	//get Accessors

	public RawAlmanac[] getRawAlmanac()
	{
		return this.cachedRawAlmanacArray;
	}

	public SatelliteEphemeris[] getCachedSatelliteEphemeris()
	{
		return this.cachedSatelliteEphemeris;
	}

	public IonUTCModel getCachedIonUTCModel()
	{
		return this.cachedIonUTCModel;
	}

	public RawIonUTCModel getRawIonUTCModel()
	{
		return this.cachedRawIonUTCModel;
	}

	public ReferenceTime[] getCachedReferenceTime()
	{
		return this.cachedReferenceTime;
	}

	public RTI getCachedRTI()
	{
		return this.cachedRTI;
	}

	//set methods

	/**
	 * adds provided GPS almanac to datacache . If provided gpsalmanc's timestamp  exceeds the staleness time, it is not
	 * added to cache. If a gpsAlmanac object is already present in the datacache, it will only be replaced if  the 
	 * provided object is newer than the one already in the cache.
	 */
	public void addGPSAlmanac(GPSAlmanac gpsAlmanacToCache)
	{
		this.logger.entering(thisClass, "addGPSAlmanac");

		if((gpsAlmanacToCache != null) && (GNSSUtil.getTimeDifferenceInMillis(gpsAlmanacToCache.getAlmanacTimestamp())
				< this.almanacStalenessTime))
		{
			if((this.cachedGPSAlmanac != null) && 
					(gpsAlmanacToCache.getAlmanacTimestamp().getTime() > this.cachedGPSAlmanac.getAlmanacTimestamp().getTime()))
			{

				this.cachedGPSAlmanac = gpsAlmanacToCache;
			}else if (this.cachedGPSAlmanac == null)
			{
				this.cachedGPSAlmanac = gpsAlmanacToCache;
			}

			this.logger.finest("GPSAlmanac added to cache");
		}

		this.logger.exiting(thisClass, "addGPSAlmanac");
	}

	/**
	 *adds rawalmanac objects to data cache from RawAlmanac array. All objects must not exceed staleness time if they
	 *are to be added. Also if an object already exists for that satID, it will onjly be replaced if the rawalmanac object
	 *provided are newer
	 */
	public void addRawAlmanac(RawAlmanac[] rawAlmanacArrayToCache)
	{
		this.logger.entering(thisClass, "addRawAlmanac");
		if (rawAlmanacArrayToCache != null)
		{
			for (int i = 0; i < rawAlmanacArrayToCache.length; i++)
			{
				if((rawAlmanacArrayToCache[i] != null) && (rawAlmanacArrayToCache[i].getSVID() > 0) && 
						(rawAlmanacArrayToCache[i].getSVID() <= 32) && 
						GNSSUtil.getTimeDifferenceInMillis(rawAlmanacArrayToCache[i].getRawAlmanacTimestamp()) <
						this.almanacStalenessTime)
				{
					if(this.cachedRawAlmanacArray[rawAlmanacArrayToCache[i].getSVID()-1] == null)
					{
						this.cachedRawAlmanacArray[rawAlmanacArrayToCache[i].getSVID()-1] = rawAlmanacArrayToCache[i];
						this.logger.finer("adding raw almanac to cache. PRN/\"satID\": " + 
								rawAlmanacArrayToCache[i].getSVID());
					}else if(rawAlmanacArrayToCache[i].getRawAlmanacTimestamp().getTime() > 
					this.cachedRawAlmanacArray[rawAlmanacArrayToCache[i].getSVID()-1].getRawAlmanacTimestamp().getTime())
					{
						this.cachedRawAlmanacArray[rawAlmanacArrayToCache[i].getSVID()-1] = rawAlmanacArrayToCache[i];
					}
					this.logger.finer("adding raw almanac to cache. PRN/\"satID\": " + 
							rawAlmanacArrayToCache[i].getSVID());
				}
			}
		}
		this.logger.exiting(thisClass, "addRawAlmanac");
	}

	/**
	 * Used to add satephem to data cache. If a satellite ephemeris object for a PRN exists, it will only be replaced if 
	 * its timestamp indicates that it is a newer log. All objects to be added are also checked not to have passed the 
	 * staleness time
	 */
	public void addSatelliteEphemeris(SatelliteEphemeris[] satelliteEphemerisArray)
	{
		this.logger.entering(thisClass, "addSatelliteEphemeris");

		if(satelliteEphemerisArray != null)
		{
			for(int i = 0; i < satelliteEphemerisArray.length; i++)
			{
				if ((satelliteEphemerisArray[i] != null) && 
						(GNSSUtil.getTimeDifferenceInMillis(satelliteEphemerisArray[i].getSatelliteEphemerisTimestamp())
								< this.satelliteEphemerisStalenessTime))
				{
					if(this.cachedSatelliteEphemeris[(satelliteEphemerisArray[i].getPRN()-1)] == null)
					{
						this.cachedSatelliteEphemeris[(satelliteEphemerisArray[i].getPRN()-1)]= satelliteEphemerisArray[i];
					}else if (satelliteEphemerisArray[i].getSatelliteEphemerisTimestamp().getTime() > 
					this.cachedSatelliteEphemeris[(satelliteEphemerisArray[i].getPRN()-1)].getSatelliteEphemerisTimestamp().getTime())
					{
						this.cachedSatelliteEphemeris[(satelliteEphemerisArray[i].getPRN()-1)]= satelliteEphemerisArray[i];
					}
				}else if (satelliteEphemerisArray[i] != null)
				{
					Logger logger = Logger.getLogger("addSatelliteEphemeris staleness debug. satID " 
							+ satelliteEphemerisArray[i].getSatID() + " " +System.currentTimeMillis());
					logger.setLevel(Level.OFF);

					DebugLogger.recordLogToFile(logger);

					logger.fine("date: " + satelliteEphemerisArray[i].getSatelliteEphemerisTimestamp().toString());
					logger.fine("satemphem staleness time: " + this.satelliteEphemerisStalenessTime);
					logger.fine("time difference: " + 
							GNSSUtil.getTimeDifferenceInMillis(satelliteEphemerisArray[i].getSatelliteEphemerisTimestamp()));
					logger.fine("");
				}
			}

		}

		addRTI(RTI.generateRTIFromSatelliteEphemerisArray(this.cachedSatelliteEphemeris));

		this.logger.exiting(thisClass, "addSatelliteEphemeris");
	}

	/**
	 * method is used to add an ionUTCModel object to the data cache. If the data has exceeded the staleness
	 * time, the data isn't added to the cache. If there is another ionUTC model present in the datachache, 
	 * IonUTCModelToCache will only replace the version in the cache if it is newer.
	 * @param IonUTCModelToCache
	 */	
	public void addIonUTCModel(IonUTCModel IonUTCModelToCache)
	{
		this.logger.entering(thisClass, "setIonUTCModel");

		if ((IonUTCModelToCache != null) && 
				(GNSSUtil.getTimeDifferenceInMillis(IonUTCModelToCache.getIonUTCTimestamp()) < this.ionutcStalenessTime))
		{
			if((this.cachedIonUTCModel != null) && (IonUTCModelToCache.getIonUTCTimestamp().getTime() > 
			this.cachedIonUTCModel.getIonUTCTimestamp().getTime()))
			{
				this.cachedIonUTCModel = IonUTCModelToCache;
				this.logger.finer("IonUTCModel added to cache");
			}else if (this.cachedIonUTCModel == null)
			{
				this.cachedIonUTCModel = IonUTCModelToCache;
			}
		}

		this.logger.exiting(thisClass, "setIonUTCModel");
	}

	/**
	 * method is used to add a rawionUTCModel object to the data cache. If the data has exceeded the staleness
	 * time, the data isn't added to the cache. If there is another ionUTC model present in the datachache, 
	 * IonUTCModelToCache will only replace the version in the cache if it is newer.
	 * @param rawIonUTCModelToCache
	 */	
	public void addRawIonUTCModel(RawIonUTCModel rawIonUTCModelToCache)
	{
		this.logger.entering(thisClass, "setRawIonUTCModel");
		if ((rawIonUTCModelToCache != null) && 
				(GNSSUtil.getTimeDifferenceInMillis(rawIonUTCModelToCache.getRawIonUTCModelTimestamp()) 
						< this.ionutcStalenessTime))
		{
			if((this.cachedRawIonUTCModel != null) && (rawIonUTCModelToCache.getRawIonUTCModelTimestamp().getTime() >
			this.cachedRawIonUTCModel.getRawIonUTCModelTimestamp().getTime()))
			{
				this.cachedRawIonUTCModel = rawIonUTCModelToCache;
				this.logger.finer("RawIonUTCModel added to cache");
			} else if (this.cachedRawIonUTCModel == null)
			{
				this.cachedRawIonUTCModel = rawIonUTCModelToCache;
			}
		}
		this.logger.exiting(thisClass,"setRawIonUTCModel");
	}

	/**
	 * same logic of other 'add' functions above
	 * @param RTIToCache
	 */
	public void addRTI (RTI RTIToCache)
	{
		this.logger.entering(thisClass, "setRTI");
		if((RTIToCache != null) && (GNSSUtil.getTimeDifferenceInMillis(RTIToCache.getRTITimestamp()) 
				< this.rtiStalenessTime))
		{
			if((this.cachedRTI != null) && (RTIToCache.getRTITimestamp().getTime() > 
			this.cachedRTI.getRTITimestamp().getTime()))
			{
				this.cachedRTI = RTIToCache;
				this.logger.finest("RTI added to cache");
			}else if (this.cachedRTI == null)
			{
				this.cachedRTI = RTIToCache;
			}
		}
		this.logger.exiting(thisClass, "setRTI");
	}

	/**
	 * same logic as other 'add' functions above
	 */
	public void addReferenceTime(ReferenceTime[] referenceTimeArray)
	{
		this.logger.entering(thisClass, "addReferenceTime");

		if(referenceTimeArray != null)
		{
			for(int i = 0; i < referenceTimeArray.length; i++)
			{
				if ((referenceTimeArray[i] != null) &&  (referenceTimeArray[i].getPRN() > 0) && 
						(referenceTimeArray[i].getPRN() <= 32) && 
						(GNSSUtil.getTimeDifferenceInMillis(referenceTimeArray[i].getReferenceTimeTimestamp()) < 
								this.referenceTimeStalenessTime))
				{
					if((this.cachedReferenceTime[(referenceTimeArray[i].getPRN()-1)] != null) && 
							(referenceTimeArray[i].getReferenceTimeTimestamp().getTime() > 
							this.cachedReferenceTime[(referenceTimeArray[i].getPRN()-1)].getReferenceTimeTimestamp().
							getTime()))
					{
						this.cachedReferenceTime[(referenceTimeArray[i].getPRN()-1)]= referenceTimeArray[i];
					}else if (this.cachedReferenceTime[(referenceTimeArray[i].getPRN()-1)] == null)
					{
						this.cachedReferenceTime[(referenceTimeArray[i].getPRN()-1)]= referenceTimeArray[i];
					}
				}
			}

		}

		this.logger.exiting(thisClass, "addReferenceTime");
	}

	private class checkGPSAlmanac extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class **/
		private final String thisClass = this.getClass().getName();

		public void run()
		{
			this.logger.entering(thisClass, "run");

			long timeDifference = (long) 0;

			if (cachedGPSAlmanac != null)
			{
				timeDifference = System.currentTimeMillis()-cachedGPSAlmanac.getAlmanacTimestamp().getTime();
			}

			if (cachedGPSAlmanac == null)
			{
				this.logger.fine("GPSAlmanac not set in cache");
			} else 
				if (timeDifference < almanacStalenessTime)
				{
					this.logger.finer("cached GPSalmanc is okay. timeDifference: " + timeDifference);
				}else
				{
					this.logger.fine("cached GPSalmanac has passed exiration period. timeDifference: " + timeDifference);
				}

			this.logger.exiting(thisClass, "run");
		}
	}

	private class checkSatelliteEphemeris extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class **/
		private final String thisClass = this.getClass().getName();

		public void run()
		{
			this.logger.entering(thisClass, "run");
			if (cachedSatelliteEphemeris == null)
			{
				this.logger.fine("Satellite Ephemeris not set in cache");
			} else
			{
				this.logger.fine("checking satellite ephermeris array");
				for (int i = 0; i < cachedSatelliteEphemeris.length; i ++)
				{
					long timeDifference = (long) 0; //zero added to stop eclipse reporting error :P

					if (cachedSatelliteEphemeris[i] != null)
					{
						timeDifference = System.currentTimeMillis() - 
						cachedSatelliteEphemeris[i].getSatelliteEphemerisTimestamp().getTime();
						this.logger.finer("timeDifference: " + timeDifference);
					}

					if (cachedSatelliteEphemeris[i] == null)
					{
						this.logger.finest("no satellite ephemeris object at position: " + i);
					} else
						if(timeDifference < satelliteEphemerisStalenessTime)
						{
							this.logger.finest("satellite ephemeris object okay sat id: " 
									+ cachedSatelliteEphemeris[i].getSatID() + " positionInArray: " + i);
						} else
						{
							this.logger.fine("satellite ephemeris object has passed exiration period. satID: "  
									+ cachedSatelliteEphemeris[i].getSatID());
							cachedSatelliteEphemeris[i] = null;
							this.logger.fine("satellie ephemeris object dereferenced");
						}
				}
			}
			this.logger.exiting(thisClass, "run");
		}
	}

	private class checkIonUTCModel extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class **/
		private final String thisClass = this.getClass().getName();

		public void run()
		{
			this.logger.entering(thisClass, "run");
			long timeDifference = (long) 0;

			if(cachedIonUTCModel != null)
			{
				timeDifference = System.currentTimeMillis() - cachedIonUTCModel.getIonUTCTimestamp().getTime();
			}

			if (cachedIonUTCModel == null)
			{
				this.logger.fine("Ion UTC model not set in cache");
			} else
				if (timeDifference < ionutcStalenessTime)
				{
					this.logger.finer("cached IonUTCModel is okay. timeDifference: " + timeDifference);
				}  else
				{
					this.logger.fine("cached IonUTCModel has passed expiration period. timeDifference: " 
							+ timeDifference);
				}
			this.logger.exiting(thisClass, "run");
		}
	}

	private class  checkRawIonUTCModel extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class */
		private final String thisClass = this.getClass().getName();

		public void run ()
		{
			this.logger.entering(thisClass, "run");

			long timeDifference = (long)0;

			if (cachedRawIonUTCModel != null)
			{
				timeDifference = System.currentTimeMillis() - 
				cachedRawIonUTCModel.getRawIonUTCModelTimestamp().getTime();
			}

			if (cachedRawIonUTCModel == null)
			{
				this.logger.fine("Ion UTC model not set in cache");
			} else
				if (timeDifference < ionutcStalenessTime)
				{
					this.logger.finer("cached RawIonUTCModel is okay. timeDifference: " + timeDifference);
				} else
				{
					this.logger.fine("cached IonUTCModel has passes expiration period. timeDifference: " + timeDifference);
				}

			this.logger.exiting(thisClass, "run");
		}
	}

	private class checkRawAlmanac extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/**The full name of this class */
		private final String thisClass = this.getClass().getName();

		public void run ()
		{
			this.logger.entering(thisClass, "run");

			if (cachedRawAlmanacArray == null)
			{
				this.logger.fine("Raw Almanac not set in cache");
			} else
			{
				this.logger.fine("checking raw almanac array");
				for (int i = 0; i < cachedRawAlmanacArray.length; i ++)
				{
					long timeDifference = (long) 0; //zero added to stop eclipse reporting error :P

					if (cachedRawAlmanacArray[i] != null)
					{
						timeDifference = System.currentTimeMillis() - 
						cachedRawAlmanacArray[i].getRawAlmanacTimestamp().getTime();
						this.logger.finer("timeDifference: " + timeDifference);
					}

					if (cachedRawAlmanacArray[i] == null)
					{
						this.logger.finest("no satellite ephemeris object at position: " + i);
					} else
						if(timeDifference < almanacStalenessTime)
						{
							this.logger.finest("satellite ephemeris object okay sat id: " 
									+ cachedRawAlmanacArray[i].getSVID() + " positionInArray: " + i);
						} else
						{
							this.logger.fine("satellite ephemeris object has passed exiration period. satID: "  
									+ cachedRawAlmanacArray[i].getSVID());
							cachedRawAlmanacArray[i] = null;
							this.logger.fine("satellie ephemeris object dereferenced");
						}
				}
			}

			this.logger.exiting(thisClass, "run");
		}
	}

	private class checkReferenceTime extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class **/
		private final String thisClass = this.getClass().getName();

		public void run ()
		{
			this.logger.entering(thisClass, "run");

			if (cachedReferenceTime == null)
			{
				this.logger.fine("cached Reference Time not set in Cache");
			} else
			{
				//this.logger.finer("checking reference time array");
				for (int i = 0; i < cachedReferenceTime.length; i++)
				{
					long timeDifference = (long)0;

					if (cachedReferenceTime[i] != null)
					{
						timeDifference = System.currentTimeMillis() - cachedReferenceTime[i].getReferenceTimeTimestamp().getTime();
						this.logger.finer("timeDifference: " + timeDifference);
					}

					if (cachedReferenceTime[i] == null)
					{
						this.logger.finest("no reference time at array position i: " + i);
					}else
						if (timeDifference < referenceTimeStalenessTime)
						{
							this.logger.finer("cached reference time okay. i: " + i + " satID: " 
									+ cachedReferenceTime[i].getSatID());
						} else
						{
							this.logger.fine("cached reference time has passed expiration time. i: " + i
									+ " satID: " + cachedReferenceTime[i].getSatID());

							cachedReferenceTime[i] = null;
							this.logger.fine("cached Reference time deallocated");
						}
				}

			}
			this.logger.exiting(thisClass, "run");
		}
	}

	private class checkRTI extends TimerTask
	{
		/** logger for this class */
		private Logger logger = getLogger();

		/** The full name of this class **/
		private final String thisClass = this.getClass().getName();



		public void run()
		{
			this.logger.entering(thisClass, "run");

			long timeDifference = (long)0;

			if (cachedRTI != null)
			{
				timeDifference = System.currentTimeMillis() - cachedRTI.getRTITimestamp().getTime();
				this.logger.finest("timeDifference: " + timeDifference);
			}

			if (cachedRTI == null)
			{
				this.logger.fine("cached RTI not set");
			} else
				if(timeDifference < rtiStalenessTime)
				{
					this.logger.finer("cached RTI is okay");
				} else
				{
					this.logger.fine("cached RTI has passed expiration period");
				}

			this.logger.exiting(thisClass, "run");
		}
	}

	private void printTimerValues()
	{
		System.out.println("------printing timer values------");
		System.out.println("almanacStalenessTime: " + this.almanacStalenessTime);
		System.out.println("ionutcStalenessTime: " + this.ionutcStalenessTime);
		System.out.println("referenceTimeStalenessTime: " + this.referenceTimeStalenessTime);
		System.out.println("satelliteEphemerisStalenessTime: " + this.satelliteEphemerisStalenessTime);
		System.out.println("rtiStalenessTime: " + this.rtiStalenessTime);
		System.out.println("---------------------------------");
		System.out.println("initialDelay: " + this.initialDelay);
		System.out.println("almanacMonitoringPeriod: " + this.almanacMonitoringPeriod);
		System.out.println("satelliteEphemerisMonitoringPeriod: " + this.satelliteEphemerisMonitoringPeriod);
		System.out.println("ionutcMonitoringPeriod: " + this.ionutcMonitoringPeriod);
		System.out.println("referenceTimeMonitoringPeriod: " + this.referenceTimeMonitoringPeriod);
		System.out.println("rtiMonitoringPeriod: " + this.rtiMonitoringPeriod);
	}

	private void getConfiguredTimerValues()
	{
		try{
			this.almanacStalenessTime = Long.parseLong(System.getProperty("almanacStalenessTime"));
			this.satelliteEphemerisStalenessTime = Long.parseLong(System.getProperty("satelliteEphemerisStalenessTime"));
			this.ionutcStalenessTime = Long.parseLong(System.getProperty("ionutcStalenessTime"));
			this.referenceTimeStalenessTime = Long.parseLong(System.getProperty("referenceTimeStalenessTime"));
			this.rtiStalenessTime = Long.parseLong(System.getProperty("rtiStalenessTime"));

			this.initialDelay = Long.parseLong(System.getProperty("initialDataCacheDelay"));
			this.almanacMonitoringPeriod = Long.parseLong(System.getProperty("almanacMonitoringPeriod"));
			this.satelliteEphemerisMonitoringPeriod = Long.parseLong(System.getProperty
					("satelliteEphemerisMonitoringPeriod"));
			this.ionutcMonitoringPeriod = Long.parseLong(System.getProperty("ionutcMonitoringPeriod"));
			this.referenceTimeMonitoringPeriod = Long.parseLong(System.getProperty("referenceTimeMonitoringPeriod"));
			this.rtiMonitoringPeriod =  Long.parseLong(System.getProperty("rtiMonitoringPeriod"));
		}catch(Exception e)
		{
			System.out.println("caught exception");
			System.out.println("e: " + e.toString());
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

	public GPSAlmanac getCachedGPSAlmanac() 
	{
		return this.cachedGPSAlmanac;
	}

	public void setCachedSatelliteEphemeris(
			SatelliteEphemeris[] cachedSatelliteEphemeris) 
	{
		this.cachedSatelliteEphemeris = cachedSatelliteEphemeris;
	}
}

