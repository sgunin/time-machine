/*

    Copyright (C) 2007 SNAPlab
	
	http://www.gmat.unsw.edu.au/snap/

	This file is part of OSGRS.

    This program is free software; you can redistribute it and/or modify it under the terms 
    of the GNU General Public License as published by the Free Software Foundation; either 
    version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program;
    if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston,
    MA 02110-1301 USA

    Linking OSGRS statically or dynamically with other modules is making a combined work based
    on OSGRS. Thus, the terms and conditions of the GNU General Public License cover the whole 
    combination.

    In addition, as a special exception, the copyright holders of OSGRS give you permission to 
    combine OSGRS program with free software programs or libraries that are released under the 
    GNU GPL and with code included in the standard release of Xerces Java Parser 2.9.0 under 
    the Apache 2.0 license (or modified versions of such code, with unchanged license). You 
    may copy and distribute such a system following the terms of the GNU GPL for OSGRS and the 
    licenses of the other code concerned, provided that you include the source code of that 
    other code when and as the GNU GPL requires distribution of source code.

    Note that people who make modified versions of OSGRS are not obligated to grant this special 
    exception for their modified versions; it is their choice whether to do so. The GNU General 
    Public License gives permission to release a modified version without this exception; this 
    exception also makes it possible to release a modified version which carries forward this 
    exception.

*/

package OSGRS.XMLProcessing;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * This class is responsible for validation of the xml request.
 * The contructor accepts the request as a string, and serializes this
 * to a file and then performs the validation using the SAX api
 * 
 * A status code is used to indicate whether the validatioin was 
 * succefull. If exceptions are thrown in the validation, their messages
 * are stored in a string array
 * 
 * @author Manosh Fernando
 *
 */

public class RequestValidator
{
	public static final int VALIDATION_SUCCESFUL = 100;
	public static final int VALIDATION_UNSUCCESFUL = 101;
	public static final int PROBLEM_PARSING_REQUEST = 102;

	//Feature Strings 

	private final String validationFeature 
	= "http://xml.org/sax/features/validation";

	private final String schemaFeature 
	= "http://apache.org/xml/features/validation/schema";

	//property Strings

	private final String externalSchemaLocationProperty 
	= "http://apache.org/xml/properties/schema/external-schemaLocation"; 

	private XMLReader xmlReader;
	
	private String xmlRequestToParse;

	private File xmlWorkingDirectory;
	private File xmlRequestFile;

	File requestSchema;
	
	private String workingDirectory;
	private String fileSeparator;

	private Document requestDocument;

	//attributes related to validation satus

	private int statusCode;

	/** string which will hold any exception messages as a result of the  */
	private String[] reasons;

	private final int arraySize = 20;
	private int n; //index variable for 'reasons' array

	private Logger logger;

	private String thisClass = this.getClass().getName();

	public RequestValidator(String xmlRequest)
	{
		this.xmlRequestToParse = xmlRequest;
		init();
		run();
	}

	private void init()
	{
		this.logger = Logger.getLogger(thisClass + " " + System.currentTimeMillis());
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);
		
		this.requestSchema = new File(System.getProperty("requestSchemaFile"));

		this.fileSeparator = System.getProperty("file.separator");

		this.reasons = new String[this.arraySize];

		this.n = 0;

		this.statusCode = VALIDATION_SUCCESFUL;

		try 
		{
			this.xmlReader = XMLReaderFactory.createXMLReader();
			this.xmlReader.setFeature(validationFeature,true);
			this.xmlReader.setFeature(schemaFeature,true);
			this.xmlReader.setProperty(externalSchemaLocationProperty, "http://www.gmat.unsw.edu.au/snap/grip/1.5 " +
			this.requestSchema.toURI().toString());
			this.xmlReader.setErrorHandler(new RequestErrorHandler());
		} catch (SAXNotRecognizedException e) 
		{
			this.logger.severe("SAXNotRecognizedException e: " + e.getMessage());
		} catch (SAXNotSupportedException e) 
		{
			this.logger.severe("SAXNotSupportedException e: " + e.getMessage());
		} catch (SAXException e) 
		{
			this.logger.severe("SAXException e: " + e.getMessage());
		}

		createWorkingDirectory();
		createRequestFile();
	}

	private void run()
	{
		//System.out.println("Request Validator run");
		try 
		{
			this.xmlReader.parse(this.xmlRequestFile.toURI().toString());
		} catch (IOException e) 
		{
			if(n < this.arraySize)
			{
				this.reasons[n] = "IOException: " + e.getMessage();
				n++;
			}
			this.statusCode = PROBLEM_PARSING_REQUEST;
		} catch (SAXException e) 
		{
			if(n < this.arraySize)
			{
				this.reasons[n] = "SAXException: " + e.getMessage();
				n++;
			}
			this.statusCode = PROBLEM_PARSING_REQUEST;
		} finally
		{
			deleteRequestFile();
		}
	}

	private void createWorkingDirectory()
	{

		this.workingDirectory = System.getProperty("xmlWorkingDirectory");

		if(this.workingDirectory == null)
		{
			this.workingDirectory = System.getProperty("user.dir") + "xmlWorkingDirectory" + this.fileSeparator;
		}

		this.xmlWorkingDirectory = new File(this.workingDirectory);

		if (!this.xmlWorkingDirectory.exists())
		{
			this.xmlWorkingDirectory.mkdirs();
		}
	}

	private void createRequestFile()
	{

		String systemTime = Long.toString(System.currentTimeMillis());

		this.xmlRequestFile = new File(this.xmlWorkingDirectory.getPath() + this.fileSeparator + "GNNSRequest" 
				+ systemTime + ".xml");

		int i =1; //in case a file with the current filename exists

		while(this.xmlRequestFile.exists())
		{
			this.xmlRequestFile = new File(this.xmlWorkingDirectory.getPath() + this.fileSeparator + "GNNSRequest" 
					+ systemTime + "-" + i + ".xml");

			i++;
		}

		try 
		{
			this.requestDocument = GNSSUtil.parseXMLData(this.xmlRequestToParse);
		} catch (ParserConfigurationException e) 
		{
			if(n < this.arraySize)
			{
				this.reasons[n] = "ParserConfigurationException: " + e.getMessage();
				n++;
			}

			this.statusCode = PROBLEM_PARSING_REQUEST;
		} catch (SAXException e) 
		{
			if(n < this.arraySize)
			{
				this.reasons[n] = "SAXException: " + e.getMessage();
				n++;
			}

			this.statusCode = PROBLEM_PARSING_REQUEST;
		} catch (IOException e) 
		{
			if(n < this.arraySize)
			{
				this.reasons[n] = "IOException: " + e.getMessage();
				n++;
			}

			this.statusCode = PROBLEM_PARSING_REQUEST;
		}
		GNSSUtil.serializeToFile(this.requestDocument, this.xmlRequestFile);
	}

	private boolean deleteRequestFile()
	{
		boolean fileDeleted;

		int i = 0;
		do
		{
			fileDeleted = this.xmlRequestFile.delete();

			if(fileDeleted)
			{
			}else
			{
				this.logger.severe("file failed to be deleted. Path: " + this.xmlRequestFile.getPath());
			}

			i++;

		}while(!fileDeleted && i < 20);

		return fileDeleted;
	}

	private class RequestErrorHandler extends DefaultHandler
	{
		public void warning(SAXParseException e) throws SAXException 
		{
			if(n < arraySize)
			{
				reasons[n] = "Warning - " + getSAXParseExceptionString(e);
				n++;
			}

			statusCode = VALIDATION_UNSUCCESFUL;
		}
		public void error(SAXParseException e) throws SAXException 
		{
			if(n < arraySize)
			{
				reasons[n] = "Error - " + getSAXParseExceptionString(e);
				n++;
			}

			statusCode = VALIDATION_UNSUCCESFUL;
		}
		public void fatalError(SAXParseException e) throws SAXException 
		{
			if(n < arraySize)
			{
				reasons[n] = "Fatal Error - " + getSAXParseExceptionString(e);
				n++;
			}

			statusCode = VALIDATION_UNSUCCESFUL;
		} 

		public void startDocument()throws SAXException
		{
			System.out.println("start document");
		}

		public void endDocument()throws SAXException
		{
			System.out.println("end document");
		}

		private void printInfo(SAXParseException e) 
		{
			System.out.println("   Public ID: "+e.getPublicId());
			System.out.println("   System ID: "+e.getSystemId());
			System.out.println("   Line number: "+e.getLineNumber());
			System.out.println("   Column number: "+e.getColumnNumber());
			System.out.println("   Message: "+e.getMessage());
		}

		private String getSAXParseExceptionString(SAXParseException e)
		{
			String exceptionInfo = "Public ID: " + e.getPublicId() +
			" System ID: " + e.getSystemId() +
			" Line number: "+e.getLineNumber() +
			" Column number: " + e.getColumnNumber() +
			" Message: "+e.getMessage();

			return exceptionInfo;
		}
	}

	public String[] getReasons() 
	{
		return this.reasons;
	}

	public int getStatusCode() 
	{
		return this.statusCode;
	}
}