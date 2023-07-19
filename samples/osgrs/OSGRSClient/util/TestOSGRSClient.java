package OSGRSClient.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import java.util.logging.*;

import org.w3c.dom.Document;
//import org.w3c.dom.
import org.xml.sax.InputSource;

import org.apache.xerces.jaxp.DocumentBuilderImpl;
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.Header;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class TestOSGRSClient 
{
	/**Logger for this class */
	private Logger logger;
	
	/**name of this Class */
	private String thisClass = this.getClass().getName();
	
	//http request&response related variables
	
	/**Http client object*/
	private HttpClient httpClient;
	
	/**post method objext */
	private PostMethod postMethod;
	
	/**URL of OSGRS */
	private String osgrsURL;
	
	/**name of client, will be used in request header */
	private String clientName = "TestOSGRSClient";
	
	/**response body string */
	private String responseBodyString;
	
	/**http status code of response */
	private int result;
	
	//xml request related variables
	
	private GNSSRequestWriter requestWriter;
	
	/**String containing allsats assistance data to be requested */
	private String allSatsAssistanceDataString;
	
	/**String containing satsInView assistance data to be requested */
	private String satsInViewAssistanceDataString;
	
	/**Gnss type */
	private String gnssTypeValue;
	
	/**lattitude of request */
	private double latittude;
	
	/**longitude of request */
	private double longitude;
	
	//xml response related variables
	
	/**'document' of response */
	private Document document;
	
	/**xml response */
	private String xmlResponse;
	
	private String filename = "D:\\ANSA\\My Documents\\osgrs logs\\testClientLogs\\GNSSResponse.xml";
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
        TestOSGRSClient testOSGRSClient = new TestOSGRSClient();
	}
	
	public TestOSGRSClient()
	{
		init();
		run();
	}
	
	private void init()
	{
		this.logger = Logger.getLogger(thisClass + "@" + System.currentTimeMillis());
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);
		
		this.requestWriter = new GNSSRequestWriter();
		this.allSatsAssistanceDataString = "";//"Navmodel IonoModel UTC RTI Almanac RefTime";
		this.satsInViewAssistanceDataString = "RefTime Almanac RTI Navmodel";
		this.gnssTypeValue = "GPS";
		this.latittude = -34.4;
		this.longitude = 150.8;
		
		this.osgrsURL = "http://10.102.150.23:8080/";
		this.httpClient = new HttpClient();
		this.postMethod = new PostMethod(this.osgrsURL);
	}
	
	private void run()
	{
		this.logger.entering(thisClass, "run");
		
		setHeader();
		setBody();
		sendRequest();
		
		this.logger.exiting(thisClass, "run");
	}

	private void setHeader()
	{
		this.logger.entering(thisClass, "setHeader");
		
		this.postMethod.setRequestHeader("User-Agent", clientName);
		
		//this.postMethod.setRequestHeader("Content-Length", Integer.toString(10));
		//this.postMethod.setRequestHeader("Charset", "UTF-8");
		this.postMethod.setRequestHeader("Content-Type", "text/xml; charset=UTF-8" );
		
		this.logger.exiting(thisClass, "setHeader");
	}
	
	private void setBody()
	{
		this.logger.entering(thisClass, "setBody");
		
	    this.requestWriter.insertAssistTypeAllSatsAssistanceData(this.allSatsAssistanceDataString);
	    this.requestWriter.setAttrGNSSTypeValue(this.gnssTypeValue);
	     
	    if(this.satsInViewAssistanceDataString != null)
	    {
	    	this.requestWriter.insertAssstTypeSatsInViewAssistanceData(this.satsInViewAssistanceDataString);
	    	this.requestWriter.insertPositonData(this.latittude + " " + this.longitude);
	    }

	    this.requestWriter.generateRequest();
	    System.out.println(this.requestWriter.getGNSSRequest());
	    
	    this.postMethod.addParameter(new NameValuePair("request", (String)this.requestWriter.getGNSSRequest()));
	    
	    NameValuePair postMethodValuePair = this.postMethod.getParameter("request");
	    
	    System.out.println("name: " + postMethodValuePair.getName() + " value: " + postMethodValuePair.getValue());
	    
	    System.out.println("Content-Length: " + this.postMethod.getRequestHeader("Content-Length"));
		
		this.logger.exiting(thisClass, "setBody");
	}
	
	private void sendRequest()
	{
		this.logger.entering(thisClass, "sendRequest");
		
		try
		{
			this.result = httpClient.executeMethod(this.postMethod);
			this.logger.fine("result: " + this.result);
			this.logger.fine("response body: ");  
			this.logger.fine(this.postMethod.getResponseBodyAsString());
			printResponseHeaders(this.postMethod.getResponseHeaders());
			this.responseBodyString = this.postMethod.getResponseBodyAsString();
			createXmlResponseFile(this.responseBodyString);
			
		} catch (IOException e)
		{
			this.logger.fine("e: " + e.toString());
		} finally
		{
			this.postMethod.releaseConnection();
		}
		
		this.logger.exiting(thisClass, "sendRequest");
	}
	
	private void createXmlResponseFile(String xmlResponse)
	{
		this.logger.entering(thisClass, "createXmlResponseFile");
		
		if (xmlResponse.contains("%"))
		{
			try
			{
				xmlResponse = URIUtil.decode(xmlResponse);
			}catch (IOException e)
			{
				System.out.println("e: " + e.toString());
			}
		}
		
		String[] splitXmlResponse = xmlResponse.split("=" , 2);
		
		xmlResponse = splitXmlResponse[1];
		
		System.out.println("xmlResponse: " + xmlResponse);
		
		parseXMLData(xmlResponse);
		
		serializeToFile();
		 
		this.logger.exiting(thisClass, "createXmlResponseFile");
	}
	
	private void parseXMLData(String xmlData) // :::::: TO DO ::: make public and xmldata as param.
    {
        this.logger.entering(thisClass,"parseXMLData");

        DocumentBuilderFactoryImpl dbf = new DocumentBuilderFactoryImpl();
        
        try
        {
        	DocumentBuilderImpl db = (DocumentBuilderImpl)dbf.newDocumentBuilder();
                setDocument(db.parse(new InputSource(new StringReader(xmlData))));
        }
        catch(Exception e)
        {
            this.logger.severe("e: " +e);
        }
    }
	
	private void serializeToFile()
    {
        this.logger.entering(thisClass, "serializeToFile");
        try
        {
            OutputFormat format = new OutputFormat(this.document);
            format.setIndenting(true);
            format.setEncoding("UTF-8");

            XMLSerializer serializer = new XMLSerializer(
            new FileOutputStream(new File(this.filename)), format);

            serializer.serialize(this.document);

            this.logger.info("TestOSGRSClient.serializeToFile: GNSSResponse written to file");

        } catch(IOException e)
        {
            this.logger.severe("GNSSResponseWriter.serializeToFile.err: IOException while trying to serialize to file: " +e );
        }
    }
	
	private void printResponseHeaders (Header[] responseHeaders)
	{
		System.out.println("printing response headers: ");
		for (int i = 0; i < responseHeaders.length; i++ )
		{
			System.out.println(responseHeaders[i].toString());
		}
	}

	public Document getDocument() 
	{
		return this.document;
	}

	public void setDocument(Document document) 
	{
		this.document = document;
	}
	
}
