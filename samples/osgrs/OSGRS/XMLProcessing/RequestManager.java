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
import OSGRS.Util.DebugLogger;
import OSGRS.DataManagement.GNSSDataCache;

/**
 * This class is responsible for the co-ordination of two actvities;  parsing xml requests, 
 * generating responses. An instance of this class is needed for each request.
 * @author Manosh Fernando
 *
 */

public class RequestManager
{
	/** The logger used for debug for this OSGRS instance */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	private GNSSDataCache dataCache;

	/** The String containing the xml data */
	private String xmlDataContainerString;

	/** The XMLParser for this connection */
	private GNSSRequestParser XMLParser;

	/** the response manager that will generate response for this request */
	private ResponseManager responseManager;
	
	/** This is for if there is a problem with the request */
	private GNSSErrorResponseWriter errorResponseWriter;

	private RequestValidator requestValidator;
	
	private boolean validationFailed = false;

	/** The lattitude for a position */
	private double latitude;

	/** The longitude for a position */
	private double longitude;

	public RequestManager(String xmlData, GNSSDataCache dataCache)
	{
		this.xmlDataContainerString = xmlData;
		this.dataCache = dataCache;
		this.requestValidator = new RequestValidator(this.xmlDataContainerString);
		init();
		run();
	}

	private void init()
	{
		setLogger(Logger.getLogger(thisClass));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		this.XMLParser = new GNSSRequestParser();
		this.responseManager = new ResponseManager(this.dataCache);
	}

	private void run()
	{
		this.logger.entering(thisClass, "run");

		if(this.requestValidator.getStatusCode() == RequestValidator.VALIDATION_SUCCESFUL)
		{
			this.XMLParser.parseXMLData(this.xmlDataContainerString);
			determineGNSSType();
			determinAllSatsData();
			determineSatsInViewData();
			generateResponse();
		} else //failed validation
		{
			this.validationFailed = true;
			
			//initialise error response writer
			this.errorResponseWriter = new GNSSErrorResponseWriter();
			this.errorResponseWriter.insertErrorData(this.requestValidator.getReasons());
			this.errorResponseWriter.generateErrorResponse();
		}

		this.logger.exiting(thisClass, "run");
	}

	private void generateResponse()
	{
		this.responseManager.generateResponse();
	}

	private void determineGNSSType()
	{
		this.logger.entering(thisClass, "determineGNSSType");
		if (this.XMLParser.getGNSSType().equals("GPS"))
		{
			this.responseManager.setAttrGNSSTypeValue(this.XMLParser.getGNSSType());
			//this.responseManager.setAttrUnsupportedTypes();
		}
		this.logger.exiting(thisClass, "determineGNSSType");
	}

	private void determinAllSatsData()
	{
		this.logger.entering(thisClass, "determinAllSatsData");

		if(this.XMLParser.getAllSatsDataList() != null)
		{
			for(int i = 0;i<this.XMLParser.getAllSatsDataList().length;i++)
			{

				String assistanceDataUpperCase = this.XMLParser.getAllSatsDataList()[i].toUpperCase();
				if(assistanceDataUpperCase.equals("REFTIME"))
				{
					this.responseManager.requestAllSatsRefTimeData();
				}else
					if(assistanceDataUpperCase.equals("RTI"))
					{
						this.responseManager.requestAllSatsRTIData();
					}else
						if(assistanceDataUpperCase.equals("ALMANAC"))
						{
							this.responseManager.requestAllSatsAlmanacData();
						}else
							if(assistanceDataUpperCase.equals("UTC"))
							{
								this.responseManager.requestAllSatsUTCData();
							}else
								if(assistanceDataUpperCase.equals("NAVMODEL"))
								{
									this.responseManager.requestAllSatsNavModelData();
								}else
									if(assistanceDataUpperCase.equals("IONOMODEL"))
									{
										this.responseManager.requestAllSatsIonoModel();
									}
			}
		}

		this.logger.exiting(thisClass, "determinAllSatsData");
	}

	private void determineSatsInViewData()
	{
		this.logger.entering(thisClass, "determineSatsInViewData");
		if(this.XMLParser.getPositionDataList() != null)
		{
			this.responseManager.insertPositionData(this.XMLParser.getLat(), this.XMLParser.getLong());
		}

		if(this.XMLParser.getPositionDataList() != null && this.XMLParser.getSatsInViewDataList() != null)
		{
			for(int i = 0;i<this.XMLParser.getSatsInViewDataList().length;i++)
			{

				String assistanceDataUpperCase = this.XMLParser.getSatsInViewDataList()[i].toUpperCase();
				if(assistanceDataUpperCase.equals("REFTIME"))
				{
					this.responseManager.requestSatsInViewRefTimeData();
				} else
					if(assistanceDataUpperCase.equals("ALMANAC"))
					{
						this.responseManager.requestSatsInViewAlmanacData();
					} else
						if(assistanceDataUpperCase.equals("RTI"))
						{
							this.responseManager.requestSatsInViewRTIData();
						} else
							if(assistanceDataUpperCase.equals("NAVMODEL"))
							{
								this.responseManager.requestSatsInViewNavModelData();
							}else
								if(assistanceDataUpperCase.equals("ACQASS"))
								{
									this.responseManager.requestSatsInViewAcqAssData();
								}else
									if(assistanceDataUpperCase.equals("DGNSS"))
									{
										this.responseManager.requestSatsInViewDGNSSData();
									}
			}
		}

		this.logger.exiting(thisClass, "determineSatsInViewData");
	}

	public Logger getLogger() 
	{
		return this.logger;
	}

	public void setLogger(Logger logger) 
	{
		this.logger = logger;
	}

	public ResponseManager getResponseManager()
	{
		return this.responseManager;
	}

	public boolean isValidationFailed() 
	{
		return this.validationFailed;
	}

	public GNSSErrorResponseWriter getErrorResponseWriter() 
	{
		return this.errorResponseWriter;
	}

}