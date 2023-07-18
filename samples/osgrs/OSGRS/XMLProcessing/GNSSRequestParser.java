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

import java.io.StringReader;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * This class parses GNSSRequest xml data
 * It uses the DOM xml parser
 * @author Nam Hoang
 * 
 */

public class GNSSRequestParser
{
	/** The document/source to parse  */
	private Document document;

	/** The logger for this class  */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** The array that contains the list of requested assistance data for all sats */
	private String[] allSatsAssistanceDataList;

	/** The array that contains the list of requested assistance data for sats in view */
	private String[] satsInViewAssistanceDataList;

	/** The array that contains the position of a sats in view request: lat and long */
	private double[] positionDataList;

	/** This is true whenever the parser encounters an error when parsing xml data */
	private boolean parserErrorDetected = false;

	/** The Position element string */
	private final String positionElementString = "Position";

	/** The AssistTypeAllSats element String **/
	private final String assistTypeAllSatsElementString = "AssistTypeAllSats";

	/** The AssistTypeSatsInView element String **/
	private final String assistTypeSatsInViewElementString = "AssistTypeSatsInView";

	/** The attribute "GNSSType" String */
	private final String attr_GNSSTypeString = "GNSSType";

	/** The attribute "NavType" string */
	private final String attr_NavTypeString = "NavType";

	/** The attribute "data" string */
	private final String attr_dataString = "data";

	/**
	 * Constructor for objects of class XMLParser
	 * It initiates the XMLParser and
	 */

	public GNSSRequestParser()
	{
		init();
	}

	private void init()
	{
		setLogger(Logger.getLogger(thisClass));

		this.logger.setLevel(DebugLogger.getOutputVerbosity());

		DebugLogger.recordLogToFile(this.logger);
	}

	/**
	 * @return 0- no parser errors detected, 1- parser errors detected
	 *
	 */
	public int getParserErrors()
	{
		if(this.parserErrorDetected)
		{
			return 1;
		}

		return 0;
	}


	/**
	 * This method parses a GNSSRequest
	 * @param xmlData the xml data to be parsed
	 */
	public void parseXMLData(String xmlData) // :::::: TO DO ::: make public and xmldata as param.
	{
		this.logger.entering(thisClass,"parseXMLData");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			setDocument(db.parse(new InputSource(new StringReader(xmlData))));
		}
		catch(Exception e)
		{
			this.logger.severe("GNSSRequestParser.parserXMLData.error: Exception while parsing xml: " +e);
			this.parserErrorDetected = true;
		}

		if(getParserErrors() == 0)
		{

			processAllSatsAssistanceData(); // process the request for sats in view and all sats if no errors are 
			// detected

			processSatsInViewAssistanceData();

			this.logger.finer("GNSSRequestParser.parseXMLData: XML data parsed sucessfully");

		}

	}

	/**
	 * Extract all sats request from the GNSS Request parsed.
	 */
	private void processAllSatsAssistanceData()
	{
		Element allSatsElement = this.getAllSatsElement(getRootElement());
		if(allSatsElement != null)
		{
			String allSatsAssistanceData = getAttribute(allSatsElement,this.attr_dataString);
			this.allSatsAssistanceDataList = splitWhiteSpacedStringList(allSatsAssistanceData);
		}
		else
		{
			this.allSatsAssistanceDataList = null;
		}
	}

	/**
	 * Extract sats in view request from the GNSS Request parsed.
	 */
	private void processSatsInViewAssistanceData()
	{
		Element satsInViewElement = this.getSatsInViewElement(getRootElement());
		if(satsInViewElement != null)
		{
			String satsInViewAssistanceData = getAttribute(satsInViewElement, this.attr_dataString);

			this.satsInViewAssistanceDataList = splitWhiteSpacedStringList(satsInViewAssistanceData);
		}
		else
		{
			this.satsInViewAssistanceDataList = null;
		}
	}

	/**
	 * Return the parsed list of sats in view data requested by the client
	 * @return satsInViewAssistanceDataList the list of sats in view data requested
	 */
	public String[] getSatsInViewDataList() //grabs position as well
	{
		return this.satsInViewAssistanceDataList;
	}

	/**
	 * Return the parsed list of all sats data requested by the client
	 * @return allSatsAssistanceDataList the list of all sats data requested
	 */
	public String[] getAllSatsDataList()
	{
		return this.allSatsAssistanceDataList;
	}

	/**
	 * Return the position: lat and long
	 * @return positionDataList contains both, the lat and Long
	 */
	public double[] getPositionDataList()
	{
		return this.positionDataList;
	}

	/**
	 * Return the GNSS Type of the request
	 * @return GNSSTypeString the GNSS type
	 */
	public String getGNSSType()
	{
		String GNSSTypeString = getAttribute(getRootElement(),this.attr_GNSSTypeString);
		return GNSSTypeString;
	}

	/**
	 * Return the Navigation Type of the request
	 * @return NavTypeString the navigation type of the request
	 */
	public String getNavType()
	{
		String navTypeString = getAttribute(getRootElement(),this.attr_NavTypeString);
		return navTypeString;
	}

	/**
	 * Return the Lattitude
	 * @return positionDataList[0] the lattitude of the request
	 */
	public double getLat()
	{
		return this.positionDataList[0];
	}

	/**
	 * Return the Longitude
	 * @return positionDataList[1] the longitude of the request
	 */
	public double getLong()
	{
		return this.positionDataList[1];
	}


	/**
	 * This method grabs the <AssistTypeSatsInView> element and its data from the GNSS Request, refer to GRIP
	 * for the request structure.
	 * @param rootElement the parent element of <AssistTypeSatsInView>
	 * @return satsInViewElement the <AssistTypeSatsInView> which contains the attribute "data"
	 */
	private Element getSatsInViewElement(Element rootElement)
	{
		NodeList satsInViewList = getElements(rootElement,this.assistTypeSatsInViewElementString); //get the AssistTypeSatsInView element container
		Element satsInViewElement = (Element)satsInViewList.item(0); //get the AssistTypeSatsInView element
		if(satsInViewElement != null)
		{
			this.positionDataList = GNSSUtil.splitWhiteSpacedDoubleList(getPCData(getPositionElement(satsInViewElement))); //position
		}
		return satsInViewElement;
	}


	/**
	 * Thsi method grabs the <AssistTypeAllSats element and its data from the GNSS Request, refer to GRIP
	 * for the request structure.
	 * @param rootElement the parent element of <AssistTypeAllSats>
	 * @return allSatsiewElement the <AssistTypeAllSats> which contains the attribute "data"
	 */
	private Element getAllSatsElement(Element rootElement)
	{
		NodeList allSatsList = getElements(rootElement,this.assistTypeAllSatsElementString); //get the AssistTypeSatsInView element container
		Element allSatsElement = (Element)allSatsList.item(0);
		return allSatsElement;
	}

	/**
	 * This method is used to extract the lat and long data.
	 * @param satsInViewElement - the parent of the Position element.
	 */
	private Element getPositionElement(Element satsInViewElement)
	{
		NodeList latList = satsInViewElement.getElementsByTagName(this.positionElementString); // get the lat for that NavModel
		Element positionElement = (Element)latList.item(0);
		return positionElement;
	}

	/**
	 * This method returns the highest element of the xml document,
	 * which is the <GNSSRequest> element, and the child elements and their
	 * data.
	 * @return rootElement - the highest element of the xml document.
	 */
	private Element getRootElement()
	{
		Element rootElement = this.document.getDocumentElement(); // get the root element of the xml document
		return rootElement;
	}

	/**
	 * Grabs all Elements matching an element name from a xml String
	 * @param rootElement the parent element
	 * @param elementName the name of the element to be searched for
	 * @return nl the NodeList which contains all the elements matching the element name.
	 */
	private NodeList getElements(Element rootElement, String elementName)
	{
		NodeList nl = rootElement.getElementsByTagName(elementName); //get the element container
		return nl;
	}

	/**
	 * This method is used to extract the parsed character data (PCDATA)
	 * @param element - the element which the PCData belongs to
	 */
	private String getPCData(Element element)
	{
		String pcDataString = element.getFirstChild().getNodeValue();
		return pcDataString;
	}

	/**
	 * grabs an attribute from an element
	 * @param element the Element to which the attribute belongs to
	 * @param attribute the name of the attribute
	 * @return tmpAttribute the data contained within the attribute
	 */
	private String getAttribute(Element element,String attribute)
	{
		String tmpAttribute = element.getAttribute(attribute);
		return tmpAttribute;
	}

	/**
	 * Used to seperate and store a whitespaced string list list into a
	 * new array.
	 * @param whiteSpacedString the string to be processed
	 * @return stringList the array which contains the processed string list.
	 */
	private String[] splitWhiteSpacedStringList(String whiteSpacedString)
	{
		StringTokenizer tk = new StringTokenizer(whiteSpacedString, " ");
		String[] stringList = new String[tk.countTokens()];

		for(int i=0;i<stringList.length;i++)
		{
			stringList[i] = tk.nextToken();
		}

		return stringList;

	}


	/**
	 * @return the document
	 */
	public Document getDocument()
	{
		return this.document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(final Document document)
	{
		this.document = document;
	}

	/**
	 * @return the logger for this class
	 */
	public Logger getLogger()
	{
		return this.logger;
	}

	/**
	 * @param logger the logger for this class
	 */
	public void setLogger(final Logger logger)
	{
		this.logger = logger;
	}

	/* =============   Used for debug/testing only ============= */

	private void printStringArray(String[] s)
	{
		for(int i=0;i<s.length;i++)
		{
			System.out.println(s[i]);
		}
	}

	private void printDoubleArray(double[] s)
	{
		for(int i=0;i<s.length;i++)
		{
			System.out.println(s[i]);
		}
	}

	/* ========================================================= */

}


