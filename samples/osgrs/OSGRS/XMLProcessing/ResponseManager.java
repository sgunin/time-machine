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

package OSGRS.XMLProcessing;

import java.util.logging.Logger;

import OSGRS.DataManagement.GNSSDataCache;
import OSGRS.PositionCalculation.SIVGenerator;
import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;
import OSGRS.dataType.GPSDateTime;
import OSGRS.dataType.IonUTCModel;
import OSGRS.dataType.RTI;
import OSGRS.dataType.RawAlmanac;
import OSGRS.dataType.RawIonUTCModel;
import OSGRS.dataType.ReferenceTime;
import OSGRS.dataType.SatelliteEphemeris;
import OSGRS.dataType.SatellitesInView;

/**
 * This class is responsible for the co-ordination of the generation of xml responses. An Instance of this Class
 * is needed for each response that is to be generated.
 * 
 * Note: position data should be inserted before any satINViwew type is generated.
 * @author Manosh Fernando
 *
 */

public class ResponseManager
{
	/**the dataCache */
	GNSSDataCache dataCache;

	/**the responseWriter */
	GNSSResponseWriter15 responseWriter;

	/**the SatellitesInView */
	SatellitesInView satellitesInView;

	/**logger for this class */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	//private final String attr_unsupported_types = "AcqAss DGNSS"; //changed behavior of 'unsupported types data field

//	private String attr_unsupported_types = null;

//	private String attr_unavailable_types = null;

	private double lattitude = 0;

	private double longitude = 0;

	//below are the booleans representing dataTypes unvailability(false by default)
	//when these are set true, the corresponding datatype is listed as 'unavailable' in the xml response

	//unavailable & unsupported booleans

	//all sats unavailable booleans
	private boolean allSatsNavModelUnavailable = false;
	private boolean allSatsRefTimeUnavailable = false;
	private boolean allSatsRTIUnavailable = false;
	private boolean allSatsAlmanacUnavailable = false;
	private boolean allSatsIonoModelUnavailable = false;
	private boolean allSatsUTCUnavailable = false;

	//sats in view unavailable booleans
	private boolean satsInViewNavModelUnavailable = false;
	private boolean satsInViewRefTimeUnavailable = false;
	private boolean satsInViewRTIUnavailable = false;
	private boolean satsInViewAlmanacUnavailable = false;
	private boolean satsInViewAcqAssUnavailable = false;
	private boolean satsInViewDGNSSUnavailable = false;

	//all sats unsupported booleans
	private boolean allSatsNavModelUnsupported = false;
	private boolean allSatsRefTimeUnsupported = false;
	private boolean allSatsRTIUnsupported = false;
	private boolean allSatsAlmanacUnsupported = false;
	private boolean allSatsIonoModelUnsupported = false;
	private boolean allSatsUTCUnsupported = false;

	//sats in view unsupported booleans
	private boolean satsInViewNavModelUnsupported = false;
	private boolean satsInViewRefTimeUnsupported = false;
	private boolean satsInViewRTIUnsupported = false;
	private boolean satsInViewAlmanacUnsupported = false;
	private boolean satsInViewAcqAssUnsupported = false;
	private boolean satsInViewDGNSSUnsupported = false;

	//'reason' attribute strings

	//all sats unavailable reason strings
	private String allSatsNavModelUnavailableReason = null;
	private String allSatsRefTimeUnavailableReason = null;
	private String allSatsRTIUnavailableReason = null;
	private String allSatsAlmanacUnavailableReason = null;
	private String allSatsIonoModelUnavailableReason = null;
	private String allSatsUTCUnavailableReason = null;

	//sats in view unavailable reason strings
	private String satsInViewNavModelUnavailableReason = null;
	private String satsInViewRefTimeUnavailableReason = null;
	private String satsInViewRTIUnavailableReason = null;
	private String satsInViewAlmanacUnavailableReason = null;
	private String satsInViewAcqAssUnavailableReason = null;
	private String satsInViewDGNSSUnavailableReason = null;

	//all sats unsupported reason strings
	private String allSatsNavModelUnsupportedReason = null;
	private String allSatsRefTimeUnsupportedReason = null;
	private String allSatsRTIUnsupportedReason = null;
	private String allSatsAlmanacUnsupportedReason = null;
	private String allSatsIonoModelUnsupportedReason = null;
	private String allSatsUTCUnsupportedReason = null;

	//sats in view unsupported reason strings
	private String satsInViewNavModelUnsupportedReason = null;
	private String satsInViewRefTimeUnsupportedReason = null;
	private String satsInViewRTIUnsupportedReason = null;
	private String satsInViewAlmanacUnsupportedReason = null;
	private String satsInViewAcqAssUnsupportedReason = null;
	private String satsInViewDGNSSUnsupportedReason = null;  

	public ResponseManager (GNSSDataCache dataCache)
	{
		this.dataCache = dataCache;
		init();
	}

	private void init()
	{
		this.logger = Logger.getLogger(thisClass + "@" + System.currentTimeMillis());
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		this.responseWriter = new GNSSResponseWriter15();
	}

	public void createTestResponse1()
	{
		//GNSSResponseWriter responseWriter = new GNSSResponseWriter();

		setAttrGNSSTypeValue("GPS");

		insertPositionData(-34.5, 150.8);

		setAttrUnsupportedTypes();

		requestAllSatsNavModelData();
		requestAllSatsRTIData();
		requestAllSatsAlmanacData();
		requestAllSatsIonoModel();
		requestAllSatsUTCData();
		requestAllSatsRefTimeData();
		requestSatsInViewNavModelData();
		requestSatsInViewRTIData();
		requestSatsInViewAlmanacData();
		requestSatsInViewRefTimeData();
		generateResponse();
	}

	public void generateResponse()
	{
		setAttrUnsupportedTypes();
		setAttrUnavailableTypes();
		this.responseWriter.generateResponse();
	}

	public void setAttrUnsupportedTypes()
	{
		//all sats
		if(this.allSatsAlmanacUnsupported)
		{
			this.responseWriter.insertAllSatsAlmanacUnsupported(this.allSatsAlmanacUnsupportedReason);
		}
		if(this.allSatsIonoModelUnsupported)
		{
			this.responseWriter.insertAllSatsIonoModelUnsupported(this.allSatsIonoModelUnsupportedReason);
		}
		if(this.allSatsNavModelUnsupported)
		{
			this.responseWriter.insertAllSatsNavModelUnsupported(this.allSatsNavModelUnsupportedReason);
		}
		if(this.allSatsRefTimeUnsupported)
		{
			this.responseWriter.insertAllSatsRefTimeUnsupported(this.allSatsRefTimeUnsupportedReason);
		}
		if(this.allSatsRTIUnsupported)
		{
			this.responseWriter.insertAllSatsRTIUnsupported(this.allSatsRTIUnsupportedReason);
		}
		if(this.allSatsUTCUnsupported)
		{
			this.responseWriter.insertAllSatsUTCUnsupported(this.allSatsUTCUnsupportedReason);
		}

		//sats in view
		if(this.satsInViewAlmanacUnsupported)
		{
			this.responseWriter.insertSatsInViewAlmanacUnsupported(this.satsInViewAlmanacUnsupportedReason);
		}
		if(this.satsInViewNavModelUnsupported)
		{
			this.responseWriter.insertSatsInViewNavModelUnsupported(this.satsInViewNavModelUnsupportedReason);
		}
		if(this.satsInViewRefTimeUnsupported)
		{
			this.responseWriter.insertSatsInViewRefTimeUnsupported(this.satsInViewRefTimeUnsupportedReason);
		}
		if(this.satsInViewRTIUnsupported)
		{
			this.responseWriter.insertSatsInViewRTIUnsupported(this.satsInViewRTIUnsupportedReason);
		}
		if(this.satsInViewAcqAssUnsupported)
		{
			this.responseWriter.insertSatsInViewAcqAssUnsupported(this.satsInViewAcqAssUnsupportedReason);
		}
		if(this.satsInViewDGNSSUnsupported)
		{
			this.responseWriter.insertSatsInViewDGNSSUnsupported(this.satsInViewDGNSSUnsupportedReason);
		}
	}

	/**
	 * determines which dataTypes are unvaiable according to their booleans
	 * calls to the appropriate methods in the response writer to reflet this
	 * in the xml response
	 *
	 */
	public void setAttrUnavailableTypes()
	{
		//all sats
		if(this.allSatsAlmanacUnavailable)
		{
			this.responseWriter.insertAllSatsAlmanacUnavailable(this.allSatsAlmanacUnavailableReason);
		}
		if(this.allSatsIonoModelUnavailable)
		{
			this.responseWriter.insertAllSatsIonoModelUnavailable(this.allSatsIonoModelUnavailableReason);
		}
		if(this.allSatsNavModelUnavailable)
		{
			this.responseWriter.insertAllSatsNavModelUnavailable(this.allSatsNavModelUnavailableReason);
		}
		if(this.allSatsRefTimeUnavailable)
		{
			this.responseWriter.insertAllSatsRefTimeUnavailable(this.allSatsRefTimeUnavailableReason);
		}
		if(this.allSatsRTIUnavailable)
		{
			this.responseWriter.insertAllSatsRTIUnavailable(this.allSatsRTIUnavailableReason);
		}
		if(this.allSatsUTCUnavailable)
		{
			this.responseWriter.insertAllSatsUTCUnavailable(this.allSatsUTCUnavailableReason);
		}

		//sats in view
		if(this.satsInViewAlmanacUnavailable)
		{
			this.responseWriter.insertSatsInViewAlmanacUnavailable(this.satsInViewAlmanacUnavailableReason);
		}
		if(this.satsInViewNavModelUnavailable)
		{
			this.responseWriter.insertSatsInViewNavModelUnavailable(this.satsInViewNavModelUnavailableReason);
		}
		if(this.satsInViewRefTimeUnavailable)
		{
			this.responseWriter.insertSatsInViewRefTimeUnavailable(this.satsInViewRefTimeUnavailableReason);
		}
		if(this.satsInViewRTIUnavailable)
		{
			this.responseWriter.insertSatsInViewRTIUnavailable(this.satsInViewRTIUnavailableReason);
		}
		if(this.satsInViewAcqAssUnavailable)
		{
			this.responseWriter.insertSatsInViewAcqAssUnavailable(this.satsInViewAcqAssUnavailableReason);
		}
		if(this.satsInViewDGNSSUnavailable)
		{
			this.responseWriter.insertSatsInViewDGNSSUnavailable(this.satsInViewDGNSSUnavailableReason);
		}
	}

	public void setAttrGNSSTypeValue(String GNSSTypeValue)
	{
		this.responseWriter.setAttrGNSSTypeValue(GNSSTypeValue);
	}

	public void getAttrUnavailableTypes(String attr_unavailable_types)
	{
		this.responseWriter.setAttrUnavailableTypes(attr_unavailable_types);
	}

	public void insertPositionData(double latitude, double longitude)
	{
		this.lattitude = latitude;
		this.longitude = longitude;

		this.logger.finer("latlong set. lattitude: "  + this.lattitude + " longitude: " + this.longitude);

		this.responseWriter.insertPositionData(this.lattitude + " " + this.longitude);

		this.satellitesInView = getSatellitesInView();

		this.logger.finer("satelliteInView set");
	}

	/**
	 * when all sats nav model is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsNavModelData()
	{

		//must implement new approach for this and almanac
		this.logger.entering(thisClass, "requestAllSatsNavModelData");

		int totalNumberOfSats = 0;

		String[] satDataStringArray;

		SatelliteEphemeris[] navModelArray = this.dataCache.getCachedSatelliteEphemeris();

		//SatelliteEphemeris[] navModelArray = null;//new SatelliteEphemeris[32]; //i used this to test 'unavailable'
		//functionality

		if(navModelArray != null)
		{
			//work out number sats with data

			SatelliteEphemeris[] secureNavModelArray = new SatelliteEphemeris[32]; 

			for (int i = 0; i < navModelArray.length; i++)
			{
				if (navModelArray[i]!=null)
				{
					secureNavModelArray[totalNumberOfSats] = navModelArray[i];

					totalNumberOfSats++;
					this.logger.finer("nav model found at position i: " + i);
				}else
				{
					this.logger.finer("no nav data at position i: " + i);
				}
			}

			this.logger.fine("totalNumberOfSats: " + totalNumberOfSats);

			if (totalNumberOfSats > 0)
			{

				//fill string array

				satDataStringArray = new String[totalNumberOfSats];

				int n = 0; //index variable for  satDataStringArray

				for (int i = 0; i < secureNavModelArray.length; i++)
				{
					if (secureNavModelArray[i] != null && n < totalNumberOfSats)
					{
						satDataStringArray[n] = (secureNavModelArray[i].getPRN() + " " + secureNavModelArray[i].getRawSubframeDataString());
						this.logger.finer("satDataStringArray[" + n + "]: " + satDataStringArray[n]);
						n++;
					}
				}

				assert(totalNumberOfSats == satDataStringArray.length);

				this.responseWriter.insertAllSatsNavModelData(satDataStringArray.length, satDataStringArray);
			} else
			{
				//System.out.println("totalNumberOfSats: " + totalNumberOfSats);
				this.allSatsNavModelUnavailable = true;
			}

		} else
		{
			//struff to do when null is returned from data cache

			//System.out.println("null returned from cache");
			this.allSatsNavModelUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestAllSatsNavModelData");
	}

	/**
	 * when all sats RTI model is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsRTIData()
	{
		this.logger.entering(thisClass, "requestAllSatsRTIData");

		String RTIDataString;

		RTI RTI = this.dataCache.getCachedRTI();

		if(RTI != null)
		{
			RTIDataString = RTI.toString();

			this.logger.finer("RTIDataString: " + RTIDataString);

			this.responseWriter.insertAllSatsRTIData(RTIDataString);
		} else
		{
			//System.out.println("null returned from cache");
			this.allSatsRTIUnavailable = true;
		}
		this.logger.exiting(thisClass, "requestAllSatsRTIData");
	}

	/**
	 * when all sats almanac model is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsAlmanacData()
	{
		this.logger.entering(thisClass, "requestAllSatsNavModelData");

		String[] almanacDataString;

		int totalNumberOfSats = 0;

		int n = 0; //index variable for almanacDataString

		RawAlmanac[] rawAlmanacArray = this.dataCache.getRawAlmanac();

		//RawAlmanac[] rawAlmanacArray = null;//new RawAlmanac[32]; //used for testing 'unavailable' feature

		if(rawAlmanacArray != null)
		{
			RawAlmanac[] securedRawAlmanacArray = new RawAlmanac[32];

			//work out total number of sats

			for (int i = 0; i < rawAlmanacArray.length; i++)
			{
				if ((rawAlmanacArray[i]!=null) && (rawAlmanacArray[i].getSVID() > 0) 
						&& (rawAlmanacArray[i].getSVID() <= 32))
				{
					this.logger.finer("Acceptable rawalmanac found. i: " + i + " svid: " + rawAlmanacArray[i].getSVID());
					securedRawAlmanacArray[totalNumberOfSats] = rawAlmanacArray[i];
					totalNumberOfSats++;
				}
			}
			if(totalNumberOfSats > 0)
			{
				almanacDataString = new String[totalNumberOfSats];

				for (int i = 0; i < securedRawAlmanacArray.length; i++)
				{
					if ((securedRawAlmanacArray[i] != null) && (securedRawAlmanacArray[i].getSVID() > 0) 
							&& (securedRawAlmanacArray[i].getSVID() <= 32) && (n < totalNumberOfSats))
					{
						almanacDataString[n] = 	securedRawAlmanacArray[i].getSVID() + " " + securedRawAlmanacArray[i].getRawAlmanacString();
						this.logger.finer(almanacDataString[n]);
						n++;
					}
				}

				this.responseWriter.insertAllSatsAlmanacData(totalNumberOfSats, almanacDataString);
			} else
			{
				//System.out.println("totalNumberOfSats: " + totalNumberOfSats);
				this.allSatsAlmanacUnavailable = true;
			}

		} else
		{
			//System.out.println("null returned from datacache");
			this.allSatsAlmanacUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestAllSatsNavModelData");
	}

	/**
	 * when all sats ionomodel is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsIonoModel()
	{
		this.logger.entering(thisClass, "requestAllSatsIonoModel");

		String ionModelString;

		RawIonUTCModel rawIonUTCModel = this.dataCache.getRawIonUTCModel();

		if (rawIonUTCModel != null)
		{
			ionModelString = rawIonUTCModel.getRawIonosphereModelString();

			this.logger.finer("ionModelString: " + ionModelString);

			this.responseWriter.insertAllSatsIonoModelData(ionModelString);
		} else
		{
			//System.out.println("null returned");
			this.allSatsIonoModelUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestAllSatsIonoModel");
	}

	/**
	 * when all sats UTC model is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsUTCData()
	{
		this.logger.entering(thisClass, "requestAllSatsUTCData");

		String UTCModelString;

		RawIonUTCModel rawIonUTCModel = this.dataCache.getRawIonUTCModel();

		if(rawIonUTCModel != null)
		{
			UTCModelString = rawIonUTCModel.getRawUTCModelString();

			this.logger.fine("UTCModelString: " + UTCModelString);

			this.responseWriter.insertAllSatsUTCData(UTCModelString);
		}else
		{
			//System.out.println("null returned from datacache");
			this.allSatsUTCUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestAllSatsUTCData");
	}

	/**
	 * when all sats reference time is requested, this method pulls this data out of the cache
	 * and sends it to the reponsewriter in the correct format
	 *
	 */
	public void requestAllSatsRefTimeData()
	{
		this.logger.entering(thisClass, "requestAllSatsRefTimeData");

		int totalNumberOfSats = 0;

		String[] refTimeDataString;

		ReferenceTime[] referenceTimeFromCache; //pointer to ref time from cache

		referenceTimeFromCache = this.dataCache.getCachedReferenceTime();

		IonUTCModel ionUTCModelFromCache = this.dataCache.getCachedIonUTCModel();

		if((referenceTimeFromCache != null) && (ionUTCModelFromCache != null))
		{
			ReferenceTime[] referenceTimeArray = new ReferenceTime[32]; //this methods reftime array

			//fill reftimearray

			for (int i = 0; i < referenceTimeFromCache.length; i++)
			{
				if (referenceTimeFromCache[i] != null)
				{
					this.logger.finer("reftime object found at position i: " + i);
					referenceTimeArray[totalNumberOfSats] = referenceTimeFromCache[i];
					totalNumberOfSats++;
				}
			}

			this.logger.finer("totalNumberOfSats: " + totalNumberOfSats);

			refTimeDataString = new String[totalNumberOfSats];

			GPSDateTime gpsDateTime = new GPSDateTime();

			gpsDateTime.createGPSTowFromSystemTime(ionUTCModelFromCache);

			for (int i = 0; i < refTimeDataString.length; i++)
			{
				refTimeDataString[i] = referenceTimeArray[i].getPRN() + " " + gpsDateTime.getGpsWeekNumber() + " " 
				+ gpsDateTime.getGpsSecondsOfWeekUnscaled() + " " + referenceTimeArray[i].getPRN() + " "
				+ referenceTimeArray[i].getTlmMessage() + " " + referenceTimeArray[i].getAntiSpoof()
				+ " " + referenceTimeArray[i].getAlert() + " " + referenceTimeArray[i].getTlmReserved();

				this.logger.finer(refTimeDataString[i]);
			}

			this.responseWriter.insertAllSatsRefTimeData(refTimeDataString.length, refTimeDataString);
		}else
		{
			//System.out.println("null returned from data cache");
			this.allSatsRefTimeUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestAllSatsRefTimeData");
	}

	/**
	 * when sats in view nav model is requested, this method pulls this data out of the cache
	 * sorts data relating to satellites that are visible and sends it to the reponsewriter in 
	 * the correct format
	 */
	public void requestSatsInViewNavModelData()
	{
		this.logger.entering(thisClass, "requestSatsInViewNavModelData");

		int totalNumberOfSats = 0;

		String[] satDataStringArray;

		SatelliteEphemeris[] cacheNavModelArray = this.dataCache.getCachedSatelliteEphemeris();

		//SatelliteEphemeris[] cacheNavModelArray = null;//new SatelliteEphemeris[32];//used for testing 'unavalable'
		//feature

		if((cacheNavModelArray != null) && (this.satellitesInView != null))
		{

			SatelliteEphemeris[] navModelArray = new SatelliteEphemeris[32];

			if(this.satellitesInView.getNumberOfSatsInView() == this.satellitesInView.getSatsInView().length)
			{
				this.logger.finer("satellitesInView.getNumberOfSatsInView() == satellitesInView.getSatsInView().length: " +
						(this.satellitesInView.getNumberOfSatsInView() == this.satellitesInView.getSatsInView().length));
			} else
			{
				this.logger.info("problem with satellitesInView, values not equal");
			}

			//work out number sats with data

			for (int i = 0; i < cacheNavModelArray.length; i++)
			{
				if ((cacheNavModelArray[i] != null) && (GNSSUtil.checkIntAgainstShortArray(cacheNavModelArray[i].getPRN(), 
						this.satellitesInView.getSatsInView()))) 
				{
					navModelArray[totalNumberOfSats] = cacheNavModelArray[i];
					totalNumberOfSats++;
					this.logger.finer("nav model found at position i: " + i);
				} else
				{
					this.logger.finer("no suitable data at position i: " + i);
				}
			}

			this.logger.fine("totalNumberOfSats: " + totalNumberOfSats);

			if(totalNumberOfSats > 0)
			{
				//fill string array

				satDataStringArray = new String[totalNumberOfSats];

				int n = 0; //index variable for  satDataStringArray

				for (int i = 0; i < navModelArray.length; i++)
				{
					if (navModelArray[i] != null && n < totalNumberOfSats 
							&& (GNSSUtil.checkIntAgainstShortArray(navModelArray[i].getPRN(), 
									this.satellitesInView.getSatsInView())))
					{
						satDataStringArray[n] = (navModelArray[i].getPRN() + " " + 
								navModelArray[i].getRawSubframeDataString());
						this.logger.fine("satDataStringArray[" + n + "]: " + satDataStringArray[n]);
						n++;
					}
				}

				assert(totalNumberOfSats == (n+1));

				responseWriter.insertSatsInViewNavModelData(totalNumberOfSats, satDataStringArray);

				this.logger.fine("nav model for sats in view written");
			}else
			{
				this.satsInViewNavModelUnavailable = true;
			}

		}else
		{
			//System.out.println("null returned from datacache");
			this.satsInViewNavModelUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestSatsInViewNavModelData");
	}

	/**
	 * when sats in view RTI is requested, this method pulls this data out of the cache
	 * sorts data relating to satellites that are visible and sends it to the reponsewriter in 
	 * the correct format
	 */
	public void requestSatsInViewRTIData()
	{
		this.logger.entering(thisClass, "requestSatsInViewRTIData");

		int numberOfBadSatsInView = 0;

		String RTIDataString;

		RTI RTI = this.dataCache.getCachedRTI();

		if((RTI != null) && (this.satellitesInView != null))
		{
			short[] satsInViewSVIDs = new short[RTI.getNumberOfBadSatellites()]; 

			//find numberOfBadSatsInView

			for (int i = 0; i < RTI.getBadSVID().length;i++)
			{
				if(GNSSUtil.checkIntAgainstShortArray(RTI.getBadSVID()[i],this.satellitesInView.getSatsInView()) &&
						(numberOfBadSatsInView < satsInViewSVIDs.length))
				{
					satsInViewSVIDs[numberOfBadSatsInView] = RTI.getBadSVID()[i];

					numberOfBadSatsInView++;
				}
			}

			this.logger.fine("numberOfBadSatsInView: " + numberOfBadSatsInView);

			if (numberOfBadSatsInView == 0)
			{
				RTIDataString = "";
			}else if (numberOfBadSatsInView == 1)
			{
				RTIDataString = Short.toString(satsInViewSVIDs[0]);
			}else
			{
				RTIDataString = Short.toString(satsInViewSVIDs[0]);

				for (int i = 1; i < numberOfBadSatsInView; i++ )
				{
					RTIDataString = RTIDataString + " " + satsInViewSVIDs[i];
				}
			}

			this.logger.fine("RTIDataString: " + RTIDataString);

			responseWriter.insertSatsInViewRTIData(RTIDataString);
		}else
		{
			this.satsInViewRTIUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestSatsInViewRTIData");
	}

	/**
	 * when sats in view Almanac data is requested, this method pulls this data out of the cache
	 * sorts data relating to satellites that are visible and sends it to the reponsewriter in 
	 * the correct format
	 */
	public void requestSatsInViewAlmanacData()
	{
		this.logger.entering(thisClass, "requestSatsInViewAlmanacData");

		String[] almanacDataString;

		int totalNumberOfSatsInView = 0;

		int n = 0; //

		RawAlmanac[] cacheRawAlmanacArray = this.dataCache.getRawAlmanac();

		//RawAlmanac[] cacheRawAlmanacArray = null;//new RawAlmanac[32];//used to teset unavialable feature

		if((cacheRawAlmanacArray != null) && (this.satellitesInView != null))
		{
			RawAlmanac[] rawAlmanacArray = new RawAlmanac[32];

			//work out total number of sats

			for (int i = 0; i < cacheRawAlmanacArray.length; i++)
			{
				if ((cacheRawAlmanacArray[i]!=null) && (cacheRawAlmanacArray[i].getSVID() > 0) 
						&& (cacheRawAlmanacArray[i].getSVID() <= 32) && 
						GNSSUtil.checkIntAgainstShortArray(cacheRawAlmanacArray[i].getSVID(), 
								this.satellitesInView.getSatsInView()))
				{
					this.logger.finer("Acceptable rawalmanac found. i: " + i + " svid: " + 
							cacheRawAlmanacArray[i].getSVID());
					rawAlmanacArray[totalNumberOfSatsInView] =  cacheRawAlmanacArray[i];
					totalNumberOfSatsInView++;
				}
			}

			if(totalNumberOfSatsInView > 0)
			{
				almanacDataString = new String[totalNumberOfSatsInView];

				for (int i = 0; i < rawAlmanacArray.length; i++)
				{
					if ((rawAlmanacArray[i]!=null) && (rawAlmanacArray[i].getSVID() > 0) 
							&& (rawAlmanacArray[i].getSVID() <= 32) && (n < totalNumberOfSatsInView) &&
							GNSSUtil.checkIntAgainstShortArray(rawAlmanacArray[i].getSVID(), 
									this.satellitesInView.getSatsInView()))
					{
						almanacDataString[n] = 	rawAlmanacArray[i].getSVID() + " " + rawAlmanacArray[i]
						                       	.getRawAlmanacString();
						this.logger.fine(almanacDataString[n]);
						n++;
					}
				}

				responseWriter.insertSatsInViewAlmanacData(n, almanacDataString);
			}else
			{
				//System.out.println("totalNumberOfSatsInView: " + totalNumberOfSatsInView);
				this.satsInViewAlmanacUnavailable = true;
			}
		}else
		{
			//System.out.println("null returned from cache");
			this.satsInViewAlmanacUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestSatsInViewAlmanacData");
	}

	/**
	 * when sats in view Reference data is requested, this method pulls this data out of the cache
	 * sorts data relating to satellites that are visible and sends it to the reponsewriter in 
	 * the correct format
	 */
	public void requestSatsInViewRefTimeData()
	{
		this.logger.entering(thisClass, "requestSatsInViewRefTimeData");

		int totalNumberOfSatsInView = 0;

		String[] refTimeDataString;

		ReferenceTime[] referenceTimeFromCache; //pointer to ref time from cache

		referenceTimeFromCache = this.dataCache.getCachedReferenceTime();

		IonUTCModel ionUTCModelFromCache = this.dataCache.getCachedIonUTCModel();

		//referenceTimeFromCache = new ReferenceTime[32];//used for testing unavailable feature

		if((referenceTimeFromCache != null) && (this.satellitesInView != null) && (ionUTCModelFromCache != null))
		{
			ReferenceTime[] referenceTimeArray = new ReferenceTime[32]; //this methods reftime array

			//fill reftimearray

			for (int i = 0; i < referenceTimeFromCache.length; i++)
			{
				if (referenceTimeFromCache[i] != null && 
						GNSSUtil.checkIntAgainstShortArray(referenceTimeFromCache[i].getPRN(), 
								this.satellitesInView.getSatsInView())
								&& (totalNumberOfSatsInView < referenceTimeArray.length))
				{
					this.logger.finer("reftime object found at position i: " + i);
					referenceTimeArray[totalNumberOfSatsInView] = referenceTimeFromCache[i];
					totalNumberOfSatsInView++;
				}
			}

			if(totalNumberOfSatsInView > 0)
			{
				refTimeDataString = new String[totalNumberOfSatsInView];

				GPSDateTime gpsDateTime = new GPSDateTime();

				gpsDateTime.createGPSTowFromSystemTime(ionUTCModelFromCache);

				for (int i = 0; i < refTimeDataString.length; i++)
				{
					refTimeDataString[i] = referenceTimeArray[i].getPRN() + " " + gpsDateTime.getGpsWeekNumber() + " " 
					+ gpsDateTime.getGpsSecondsOfWeekUnscaled() + " " + referenceTimeArray[i].getPRN() + " "
					+ referenceTimeArray[i].getTlmMessage() + " " + referenceTimeArray[i].getAntiSpoof()
					+ " " + referenceTimeArray[i].getAlert() + " " + referenceTimeArray[i].getTlmReserved();

					this.logger.finer(refTimeDataString[i]);
				}

				responseWriter.insertSatsInViewRefTimeData(totalNumberOfSatsInView, refTimeDataString);
			}else
			{
				//System.out.println("totalNumberOfSatsInView: " + totalNumberOfSatsInView);
				this.satsInViewRefTimeUnavailable = true;
			}
		}else
		{
			//System.out.println("null returned from datacache");
			this.satsInViewRefTimeUnavailable = true;
		}

		this.logger.exiting(thisClass, "requestSatsInViewRefTimeData");
	}

	/**
	 * Both these methods just add to unsupported types string as acqAss and DGNSS is not supported by OSGRS
	 * at present
	 *
	 */
	public void requestSatsInViewAcqAssData ()
	{
		this.satsInViewAcqAssUnsupported = true;
		this.satsInViewAcqAssUnsupportedReason = "AcqAss not supported by OSGRS at present";
	}

	/**
	 * Both these methods just add to unsupported types string as acqAss and DGNSS is not supported by OSGRS
	 * at present
	 *
	 */
	public void requestSatsInViewDGNSSData ()
	{
		this.satsInViewDGNSSUnsupported = true;
		this.satsInViewDGNSSUnsupportedReason = "DGNSS not supported by OSGRS at present";
	}

	/**
	 * this method gets the satellies in view of the reciever, which might be acceptable if client is near the reciever.
	 * This should be replaced with a "SIV Engine" which is able to calculate the satellites in view from the lat, long
	 * @param lattitude
	 * @param longditude
	 * @return
	 */
	private SatellitesInView getSatellitesInView()
	{
		//since altitude is not specified in the spec, we
		//have to use a default value (0).

		try {
			SIVGenerator sivGenerator = new SIVGenerator(this.lattitude, this.longitude, 0, this.dataCache.getCachedSatelliteEphemeris(), 
					this.dataCache.getCachedIonUTCModel());

			SatellitesInView  siv = sivGenerator.getSatellitesInView();

			//return this.dataCache.getSatellitesInViewOfReciever();

			return siv;
		} catch (IllegalArgumentException e) {
			return null;
		}
	}	

	/**
	 * a method to waste time
	 * @param period
	 */
	private void delay (long period)
	{
		try {
			Thread.sleep(period);
		} catch (InterruptedException e) {
			System.out.println("We've been interrupted! ");
			return;
		}
	}

	public GNSSResponseWriter15 getResponseWriter() 
	{
		return this.responseWriter;
	}

}

