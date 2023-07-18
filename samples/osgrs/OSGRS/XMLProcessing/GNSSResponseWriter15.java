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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/**
 * This class is used to generate the xml for the GNSSResponse defined in GRIP
 * 
 *
 * NOTE: optional attributes and elements:
 * NavType (attr)
 * Unavailable (element)
 * Unsupported (element)
 *
 * When inserting element data/attribute data, the order isnt essential, but call the generateResponse() method
 * only after the appropriate data has been inserted into the writer, as the DOM XML structure will be generated
 * when this method is called.
 *
 * Example of use:
 *
        String[] navModelDataList = new String[2];
        navModelDataList[0] = "2 8B05B03714A55B100142664DC10A4DEA3B5BE3BDDB762A30000024032F988B05B037152B760B25359C5BA4796A09C6048738270A49A10DB6972A307C8B05B03715AD0042D43425D9003526AB277C22AD56C603A4FFA5FC76FCD0";
        navModelDataList[1] = "3 8B05B03714A55B100242664DC10A4DEA3B5BE3BDFB902A300000050B8C088B05B037152B90FB4A2EFA6E8C6FE6FBC8039B6BFE0A18A10D572A2A307C8B05B03715ADFFE2FFE521F0FFCA27A67D3724D211F10D85FFA7C090F598";

        String[] refTimeDataList = new String[2];
        refTimeDataList[0] = "12 789 504799.92 12 10223 1 0 2";
        refTimeDataList[1] = "2 987 204788.92 8 13332 1 0 2";

        String[] almanacDataList = new String[1];
        almanacDataList[0] =  "12 zzzzzzzzzzzzzzzzzz";

        String[] acqAssDataList = new String[2];
        acqAssDataList[0] = "5 3052.25 0.65 87 110 10 2 500 55.12 64.12";
        acqAssDataList[1] = "7 3062.22 0.75 72 102 2 3 47 66.11 44.11";

        XMLWriter xw = new XMLWriter();
        xw.setAttrGNSSTypeValue("GPS");
        xw.setAttrUnsupportedTypes("DGNSS");

        xw.insertAllSatsRTIData("22 5 9");
        xw.insertAllSatsNavModelData(2, navModelDataList);
        xw.insertAllSatsRefTimeData(2, refTimeDataList);
        xw.insertAllSatsUTCData("eeeeeeeeeeeeeee");
        xw.insertAllSatsIonoModelData("23123121e231123231212");
        xw.insertAllSatsAlmanacData(1, almanacDataList);

        xw.insertSatsInViewAcqAssData(2, 441405.35, acqAssDataList);
        xw.insertPositonData("-34.4 150.8");
        xw.insertSatsInViewRTIData("7 8 9");

        xw.generateResponse();

        System.out.println(xw.getGNSSResponse()); // the result will be printed to std out.

        This class will create responses which conform to grip specification 1.5
        @author Nam Hoang and Manosh Fernando
 *
 */

public class GNSSResponseWriter15
{
	/** The filename for the output file  */
	private String filename = "GNSSResponse.xml";

	/** The logger for this class  */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** The DOM document to be written  */
	private Document DOMDocument;

	/** The String containing the GNSSResponse */
	private String GNSSResponse;

	/** The encoding type for the request */
	private String GNSSResponseEncoding = "UTF-8";

	/* == Elements ========================================================= */

	/** This is the root element of a GNSSResponse   */
	private Element GNSSResponseElement;

	/** This is the element Unavailable, it contains a list of unavailable data*/
	private Element unavailableElement;

	/** This is the the element Unsupported, it contains a list of unsupported data types */
	private Element unsupportedElement;

	/*------------- Element Strings ------------------------------------ */

	/** The GNSSResponse element String **/
	private final String GNSSResponseElementString = "GNSSResponse";

	/** The Unavailable element String **/
	private final String unavailableElementString = "Unavailable";

	/** The Unsupported element String **/
	private final String unsupportedElementString = "Unsupported";

	/** The AssistTypeAllSats element String **/
	private final String assistTypeAllSatsElementString = "AssistTypeAllSats";

	/** The AssistTypeSatsInView element String **/
	private final String assistTypeSatsInViewElementString = "AssistTypeSatsInView";

	/** The NavModel element String **/
	private final String navModelElementString = "NavModel";

	/** The RTI element String **/
	private final String RTIElementString = "RTI";

	/** The RefTime element String **/
	private final String refTimeElementString = "RefTime";

	/** The UTC element String **/
	private final String UTCElementString = "UTC";

	/** The IonoModel element String **/
	private final String ionoModelElementString = "IonoModel";

	/** The Almanac element String **/
	private final String almanacElementString = "Almanac";

	/** The AcqAss element String **/
	private final String acqAssElementString = "AcqAss";

	/** The DGNSS element String **/
	private final String DGNSSElementString = "DGNSS";

	/** The sat element string */
	private final String satElementString = "sat";

	private final String dataElementString = "Data";

	/** The BadSats element string */
	private final String badSatsElementString = "badSats";

	/** The TOWAssist element string */
	private final String TOWAssistElementString = "TOWAssist";

	/** The Position element string */
	private final String positionElementString = "Position";

	/*------- end of strings ----------------------------- */

	/*-- AssistTypeAllSats Elements --------------------------------- */

	/** The AssisTypeAllSats element, this contains other assistance data elements */
	private Element assistTypeAllSatsElement;

	/** The NavModel element that contains the Navigation Model- individual satellite data */
	private Element assistTypeAllSatsNavModelElement;

	/** The RTI element that contains the Real Time Integrity(RTI) data attribute */
	private Element assistTypeAllSatsRTIElement;

	/** The RefTime element that contains the ReferenceTime Data data attribute */
	private Element assistTypeAllSatsRefTimeElement;

	/** The UTC element that contains the UTC data */
	private Element assistTypeAllSatsUTCElement;

	/** The UTC element that contains the UTC data */
	private Element assistTypeAllSatsIonoModelElement;

	/** The Almanac element that contains the Almanac- individual satellite data */
	private Element assistTypeAllSatsAlmanacElement;

	/*-------------------------------------------------------------------------*/

	/*-- AssistTypeSatsInView Elements --------------------------------- */

	/** The AssisTypeSatsInView element, this contains other assistance data elements */
	private Element assistTypeSatsInViewElement;

	/** The Position element which contains the lattitude and longitude */
	private Element positionElement;

	/** The AcqAss element, that contains the TOWAssist element and individual sat data elements */
	private Element assistTypeSatsInViewAcqAssElement;

	/** The DGNSS element */
	private Element assistTypeSatsInViewDGNSSElement;

	/** The RTI element */
	private Element assistTypeSatsInViewRTIElement;

	/** The RefTime element */
	private Element assistTypeSatsInViewRefTimeElement;

	/** The Almanac element */
	private Element assistTypeSatsInViewAlmanacElement;

	/** The NavModel element */
	private Element assistTypeSatsInViewNavModelElement;

	/* == end of elements ================================================== */

	/* == Attributes ======================================================= */

	/** The array that contains the list of attributes for the element GNSSResponse */
	private Attr[] GNSSResponseAttrList = new Attr[4];

	/** The attribute "xmlns:xsi" */
	private final String attr_xmlnsxsi = "xmlns:xsi";

	/** The attribute "xmlns" */
	private final String attr_xmlns = "xmlns";

	/** The attribute "GNSSType" */
	private final String attr_GNSSType = "GNSSType";

	/** The attribute "NavType" */
	private final String attr_NavType = "NavType";

	/** The attribute "types" */
	private final String attr_types = "types";

	/** The attribute "reason */
	private final String attr_reason = "reason";

	/** The attribute "number" */
	private final String attr_number = "number";

	/* == end of attributes ================================================ */

	/* == Attribute values  ================================================ */

	/** The string that contains the value of the attribute GNSSType */
	private String attr_GNSSType_value = "";

	/** The string that contains the value of the attribute NavType */
	private String attr_NavType_value = "";

	/** This flag is raised when the setAttrNavTypeValue() method is called */
	private boolean NavTypeAvailable = false;

	/** The string that contains the value of the attribute types that belongs to the unavailable element */
	private String attr_unavailable_types;

	/** This flag is raised when the method setAttrUnavailableTypes() is called */
	private boolean unavailableTypesEnabled = false;

	/** The string that contains the value of the attribute types that belongs to the unsupported element */
	private String attr_unsupported_types;

	/** This flag is raised when the method setAttrUnsupportedTypes() is called */
	private boolean unsupportedTypesEnabled = false;

	/* == end of Attribute values ========================================== */

	/* == URIs ============================================================= */

	/** The URI for Schema Instances */
	private final String xmlSchemaInstanceURI = "http://www.w3.org/2001/XMLSchema-instance";

	/** The URI for the GRIP xmlns */
	private final String GRIPxmlnsURI = "http://www.gmat.unsw.edu.au/snap/grip/1.5";

	/* == end of URIs ====================================================== */

	/**
	 * This is the constructor for the XMLWriter class
	 * it initalises attributes and runs the xml writer.
	 */
	public GNSSResponseWriter15()
	{
		init();
	}

	private void init()
	{
		setLogger(Logger.getLogger(thisClass + "@" + System.currentTimeMillis()));
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		createDocument(); //create the DOM document
		this.GNSSResponseElement = this.DOMDocument.createElement(this.GNSSResponseElementString);
		initOptionalRootElements();
		initAllSatsElements();
		initSatsInViewElements();
	}


	private void initOptionalRootElements() //not neededin 1.5 grip spec
	{
		/* init optional root element */
		this.unavailableElement = this.DOMDocument.createElement(this.unavailableElementString);
		this.unsupportedElement = this.DOMDocument.createElement(this.unsupportedElementString);
		/*---- end ----------------------*/
	}
	private void initAllSatsElements()
	{
		/*---- init all sats elements ----*/
		this.assistTypeAllSatsElement = this.DOMDocument.createElement(this.assistTypeAllSatsElementString);
		this.assistTypeAllSatsNavModelElement = this.DOMDocument.createElement(this.navModelElementString);
		this.assistTypeAllSatsRTIElement = this.DOMDocument.createElement(this.RTIElementString);
		this.assistTypeAllSatsRefTimeElement = this.DOMDocument.createElement(this.refTimeElementString);
		this.assistTypeAllSatsUTCElement = this.DOMDocument.createElement(this.UTCElementString);
		this.assistTypeAllSatsIonoModelElement = this.DOMDocument.createElement(this.ionoModelElementString);
		this.assistTypeAllSatsAlmanacElement = this.DOMDocument.createElement(this.almanacElementString);
		/*---- end ----------------------*/
	}

	private void initSatsInViewElements()
	{
		/*---- init sats in view elements  ---*/
		this.assistTypeSatsInViewElement = this.DOMDocument.createElement(this.assistTypeSatsInViewElementString);
		this.positionElement = this.DOMDocument.createElement(this.positionElementString);
		this.assistTypeSatsInViewAcqAssElement = this.DOMDocument.createElement(this.acqAssElementString);
		this.assistTypeSatsInViewDGNSSElement = this.DOMDocument.createElement(this.DGNSSElementString);
		this.assistTypeSatsInViewRTIElement = this.DOMDocument.createElement(this.RTIElementString);
		this.assistTypeSatsInViewRefTimeElement = this.DOMDocument.createElement(this.refTimeElementString);
		this.assistTypeSatsInViewAlmanacElement = this.DOMDocument.createElement(this.almanacElementString);
		this.assistTypeSatsInViewNavModelElement = this.DOMDocument.createElement(this.navModelElementString);
		/*---- end --------------------------*/
	}

	/**
	 * This method generates the DOM strucute of the request and also
	 * writes the resultant xml to file. It should be called AFTER the
	 * necessary set methods have been executed, such as OPTIONAL elements and attributes.
	 *
	 */

	public void generateResponse()
	{
		createDOMStructure();
		this.setGNSSResponse(GNSSUtil.serializeToString
				(this.DOMDocument,
						this.GNSSResponseEncoding));
		//serializeToFile();
		this.logger.info("GNSSResponseWriter.generateResponse: The response has been sucessfully generated");
	}

	private void createDocument()
	{
		this.logger.entering(thisClass, "createDocument");

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();

			this.DOMDocument = db.newDocument();
		}
		catch(ParserConfigurationException pce) {
			this.logger.severe("GNSSResponseWriter.createDocument.err: Error while trying to instantiate DocumentBuilder " + pce);
		}
	}

	/**
	 * This method generates the XML structure of a GNSSResponse
	 */

	private void createDOMStructure()
	{
		this.logger.entering(thisClass, "createDOMStructure");

		createGNSSResponseElementAttrs(); //create the GNSSResponse element attributes
		createUnavailableElementAttrs(); //create the attributes for the unavailable element
		createUnsupportedElementAttrs();

		/* NOTE: These elements will only be added to the GNSSResponse if they contain an attribute i.e nonempty */
		addOptionalElementWithAttribute(this.GNSSResponseElement, this.unavailableElement,this.attr_types);
		addOptionalElementWithAttribute(this.GNSSResponseElement, this.unsupportedElement,this.attr_types);

		/*-------------------------------------------------- append all possible elements to AssistTypeAllSats */
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsAlmanacElement);
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsNavModelElement);
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsRefTimeElement);
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsRTIElement);
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsIonoModelElement);
		appendNonEmptyElement(this.assistTypeAllSatsElement, this.assistTypeAllSatsUTCElement);

		/*---------------------------------------------------------------------------------------------------- */


		/*-------------------------------------------------- append all possible elements to AssistTypeSatsInView */
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.positionElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewAcqAssElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewDGNSSElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewAlmanacElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewNavModelElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewRefTimeElement);
		appendNonEmptyElement(this.assistTypeSatsInViewElement, this.assistTypeSatsInViewRTIElement);
		/*------------------------------------------------------------------------------------------------------- */

		appendNonEmptyElement(this.GNSSResponseElement, this.assistTypeAllSatsElement);
		appendNonEmptyElement(this.GNSSResponseElement, this.assistTypeSatsInViewElement);

		if(this.GNSSResponseElement.getAttribute(
		"GNSSType").equals(""))
		{
			this.logger.severe("GNSSResponseWriter.createDOMStructure.err: the required atrribute GNSSType is missing");
		}
		else
		{
			this.DOMDocument.appendChild(this.GNSSResponseElement);
		}
	}

	/**
	 * This method appends child elements, but will only do so if the child elements
	 * contain a specific attribute
	 * @param parent the parent element
	 * @param child the child element to append.
	 */
	private void addOptionalElementWithAttribute(Element parent, Element child, String attribute)
	{
		if(child.hasAttribute(attribute)){
			parent.appendChild(child);
		}
	}

	/**
	 * This method appends child elements only if they contain other elements
	 * @param parent
	 * @param child
	 */
	private void appendNonEmptyElement(Element parent, Element child)
	{
		if(child.hasChildNodes() == true)
			parent.appendChild(child);
	}

	/**
	 * This method generates the <GNSSResponse> element attributes:
	 * xmlns:xsi, xmlns, GNSSType and NavType(optional)
	 */

	private void createGNSSResponseElementAttrs()
	{
		Attr xmlnsxsi = this.DOMDocument.createAttribute(this.attr_xmlnsxsi); //create the xmlns:xsi attribute
		xmlnsxsi.setNodeValue(this.xmlSchemaInstanceURI);

		Attr xmlns = this.DOMDocument.createAttribute(this.attr_xmlns); //create the xmlns attribute
		xmlns.setNodeValue(this.GRIPxmlnsURI);

		Attr GNSSType = this.DOMDocument.createAttribute(this.attr_GNSSType); //create the GNSSType attribute
		GNSSType.setNodeValue(this.getAttrGNSSTypeValue());

		Attr NavType = this.DOMDocument.createAttribute(this.attr_NavType);
		NavType.setNodeValue(this.getAttrNavTypeValue());

		if(this.NavTypeAvailable == true){
			addAttrToAttrList(NavType,this.GNSSResponseAttrList);

		}

		addAttrToAttrList(xmlnsxsi,this.GNSSResponseAttrList);
		addAttrToAttrList(xmlns,this.GNSSResponseAttrList);
		addAttrToAttrList(GNSSType,this.GNSSResponseAttrList);
		addAttrsToElement(this.GNSSResponseAttrList, this.GNSSResponseElement); //add attributes list to the GNSSResponse element.

	}

	/**
	 * This method creates the <Unavailable> element attributes
	 * @param unavailableElement the Unavailable element
	 */
	private void createUnavailableElementAttrs()
	{
		if(this.unavailableTypesEnabled == true){
			Attr types = this.DOMDocument.createAttribute(this.attr_types);
			types.setNodeValue(getAttrUnavailableTypes());

			this.unavailableElement.setAttributeNode(types);
		}
	}

	/**
	 * This method creates the <Unsupported> element attributes
	 * @param unsupportedElement the Unsupported element
	 */
	private void createUnsupportedElementAttrs()
	{
		if(this.unsupportedTypesEnabled == true){
			Attr types = this.DOMDocument.createAttribute(this.attr_types);
			types.setNodeValue(getAttrUnsupportedTypes());

			this.unsupportedElement.setAttributeNode(types);
		}
	}

	/**
	 * This method creates the <sat> elements, depending on the number specified
	 * it attatches the required "number" attribute to them.
	 * @param assistanceDataElement the parent element of the <sat> elements
	 * @param numberOfSats number of sat elements.
	 */
	private void createSatElements(Element assistanceDataElement, int numberOfSats)
	{
		Element[] elementList = new Element[numberOfSats];
		for(int i = 0 ; i<elementList.length ; i++)
		{
			Element tmpElement = this.DOMDocument.createElement(this.satElementString);
			Attr number = this.DOMDocument.createAttribute(this.attr_number);
			number.setNodeValue("");
			tmpElement.setAttributeNode(number);
			assistanceDataElement.appendChild(tmpElement);
		}
	}

	/**
	 * 
	 * @param assistanceDataElement
	 * @param reason
	 */
	private void createUnavailableElement(Element assistanceDataElement, String reason)
	{
		Element unavailableElement = this.DOMDocument.createElement(this.unavailableElementString);

		if(reason != null)
		{
			Attr reasonAttribute = this.DOMDocument.createAttribute(this.attr_reason);
			reasonAttribute.setNodeValue(reason);
			unavailableElement.setAttributeNode(reasonAttribute);
		}

		assistanceDataElement.appendChild(unavailableElement);
	}

	/**
	 * 
	 * @param assistanceDataElement
	 * @param reason
	 */
	private void createUnsupportedElement(Element assistanceDataElement, String reason)
	{
		Element unsupportedElement = this.DOMDocument.createElement(this.unsupportedElementString);

		if(reason != null)
		{
			Attr reasonAttribute = this.DOMDocument.createAttribute(this.attr_reason);
			reasonAttribute.setNodeValue(reason);
			unsupportedElement.setAttributeNode(reasonAttribute);
		}

		assistanceDataElement.appendChild(unsupportedElement);
	}

	/**
	 * This method creates the <badSats> element
	 * @param assistanceDataElement the parent of the <badSats> element
	 */
	private void createBadSatsElement(Element assistanceDataElement)
	{
		Element badSatsElement = this.DOMDocument.createElement(this.badSatsElementString);
		assistanceDataElement.appendChild(badSatsElement);
	}

	/**
	 * This method is used to read sat assistance data of type "satDataType1"
	 * and "satListDataType" defined in GRIP GNSSResponse.xsd
	 * @param stringArray
	 * @param assistanceDataElement
	 */
	private void readSatDataFromStringArrayIntoSatElement(String[] stringArray, Element assistanceDataElement)
	{

		for(int i = 0; i<stringArray.length; i++)
		{
			StringTokenizer tokenStr = new StringTokenizer(stringArray[i], " ");
			String satNumberStr = tokenStr.nextToken();
			int satNumber = Integer.valueOf(satNumberStr);

			/*----- Reference time and AcqAss have been defined in GRIP, if the format changes, then this code
              ----- needs to be updated */

			if(assistanceDataElement.equals(this.assistTypeSatsInViewAcqAssElement))
			{
				insertSatData(assistanceDataElement, satNumber,extractWhiteSpacedString(stringArray[i], 1,9));
			}

			if( (assistanceDataElement.equals(this.assistTypeAllSatsRefTimeElement)) ||
					(assistanceDataElement.equals(this.assistTypeSatsInViewRefTimeElement)))
			{
				insertSatData(assistanceDataElement, satNumber, extractWhiteSpacedString(stringArray[i],1,7));
			}


			else
			{
				String satData = tokenStr.nextToken();
				insertSatData(assistanceDataElement, satNumber, satData);
			}
		}

	}

	private void insertSatData(Element assistanceDataElement, int satNumber, String data)
	{
		String satNumberString = Integer.toString(satNumber);
		NodeList nl = assistanceDataElement.getElementsByTagName(this.satElementString);
		for (int i = 0; i<nl.getLength(); i++)
		{
			Element satElement = (Element)nl.item(i); //get the AssistTypeSatsInView element
			if(satElement.getAttribute(this.attr_number).equals(""))
			{
				satElement.setAttribute(this.attr_number, satNumberString);

				Text dataText = this.DOMDocument.createTextNode(data);
				satElement.appendChild(dataText);
				break;
			}
		}
	}

	/**
	 * This method is used to insert AssistTypeAllSats>NavModel satellite data
	 * @param allSatsNavModelNumberOfSats the number of individual satellites
	 * @param satDataStringArray the array that contains the sat number and sat data
	 *
	 * NOTE: the format of satDataStringArray is important, it should contain both sat number and data seperate by a space " "
	 * Example: 2 8B05B03714A55B100142664DC10A4DEA3B5BE3BDD <- this data is only a sample, not actual data.
	 * In this EXAMPLE 2 is the sat number and 8B05B03714A55B100142664DC10A4DEA3B5BE3BDD is the sat data
	 */
	public void insertAllSatsNavModelData(final int allSatsNavModelNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeAllSatsNavModelElement, allSatsNavModelNumberOfSats); //create the number of sat elements
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeAllSatsNavModelElement); //insert the data into these sat elements
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsNavModelUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsNavModelElement, reason);
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsNavModelUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsNavModelElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeAllSats>Almanac data
	 * @param allSatsAlmanacNumberOfSats number of satellites
	 * @param satDataStringArray the array that contains the sat numbers and data
	 * the format is the same as specified in the above method.
	 */

	public void insertAllSatsAlmanacData(final int allSatsAlmanacNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeAllSatsAlmanacElement, allSatsAlmanacNumberOfSats);
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeAllSatsAlmanacElement);
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsAlmanacUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsAlmanacElement, reason);
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsAlmanacUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsAlmanacElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeAllSats>RTI data
	 * @param RTIDataString the string that contains the list of bad satellites
	 *
	 * NOTE: the format of RTIDataString should contain a list of satellites, seperated by a space " "
	 * Example: "1 2 3" represents satellites 1 2 and 3
	 */
	public void insertAllSatsRTIData(String RTIDataString)
	{
		createBadSatsElement(this.assistTypeAllSatsRTIElement);
		readBadSatsDataFromStringIntoBadSatsElement(this.assistTypeAllSatsRTIElement, RTIDataString);
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsRTIUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsRTIElement, reason);
	}

	/**
	 * 
	 * @param reason
	 */
	public void insertAllSatsRTIUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsRTIElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeAllSats>RefTime data     *
	 * @param allSatsRefTimeNumberOfSats number of satellites
	 * @param satDataStringArray the string containing the satellite numbers and data.
	 */
	public void insertAllSatsRefTimeData(final int allSatsRefTimeNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeAllSatsRefTimeElement, allSatsRefTimeNumberOfSats);
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeAllSatsRefTimeElement);
	}

	public void insertAllSatsRefTimeUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsRefTimeElement, reason);
	}

	public void insertAllSatsRefTimeUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsRefTimeElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeAllSats>UTC data
	 * @param UTCDataString the string containing the UTC data
	 */

	public void insertAllSatsUTCData(String UTCDataString)
	{
		Element dataElement = this.DOMDocument.createElement(this.dataElementString);
		Text dataTxt = this.DOMDocument.createTextNode(UTCDataString);
		dataElement.appendChild(dataTxt);
		this.assistTypeAllSatsUTCElement.appendChild(dataElement);
	}

	public void insertAllSatsUTCUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsUTCElement, reason);
	}

	public void insertAllSatsUTCUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsUTCElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeALlSats>IonoModel data
	 * @param ionoModelDataString the string containing the IonoModel data.
	 */

	public void insertAllSatsIonoModelData(String ionoModelDataString)
	{
		Element dataElement = this.DOMDocument.createElement(this.dataElementString);
		Text dataText = this.DOMDocument.createTextNode(ionoModelDataString);
		dataElement.appendChild(dataText);
		this.assistTypeAllSatsIonoModelElement.appendChild(dataElement);
	}

	public void insertAllSatsIonoModelUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeAllSatsIonoModelElement, reason);
	}

	public void insertAllSatsIonoModelUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeAllSatsIonoModelElement, reason);
	}


	/**
	 * This method is used to insert AssistTypeSatsInView>Position data, which contains the lat and long
	 * it also checks the range of these values
	 * @param positionDataString the string containing the lat and long
	 * The format is important, the lat and long should be seperated by a " " space.
	 * Example: -34.5 150.8 represents lat of -34.4 and long of 150.8 - the order is essential
	 * refer to GRIP for more information.
	 */

	//TODO: do not append other elements if the position is wrong.

	public void insertPositionData(String positionDataString)
	{
		double[] positionDataList = GNSSUtil.splitWhiteSpacedDoubleList(positionDataString);
		double latitude = positionDataList[0];
		double longitude = positionDataList[1];

		if((latitude<-90) || (latitude >90) || (longitude <-180) || (longitude>180)) //check the range
		{
			if((latitude<-90) || (latitude >90)) // if it sets to something stupid
			{
				this.logger.severe("GNSSResponseWriter.insertPositionData.err: Latitude out of range");
				Text dataText = this.DOMDocument.createTextNode("-1");
				this.positionElement.appendChild(dataText);
			}
			if((longitude<-180) || (longitude >180))
			{
				this.logger.severe("GNSSResponseWriter.insertPositionData.err: Longitude out of range");
				Text dataText = this.DOMDocument.createTextNode("-1");
				this.positionElement.appendChild(dataText);
			}
		}
		else
		{
			Text dataText = this.DOMDocument.createTextNode(positionDataString);
			this.positionElement.appendChild(dataText);
		}
	}


	/**
	 * This method is used to insert AssistTypeSatsInView>AcqAss.     *
	 * @param satsInViewAcqAssNumberOfSats the number of sats available.
	 * @param TOWAssistString The string containing the TOWAssist
	 * @param satDataStringArray the Stirng containing
	 */
	public void insertSatsInViewAcqAssData(final int satsInViewAcqAssNumberOfSats, double TOWAssist, String[] satDataStringArray)
	{
		createTOWAssistElement(this.assistTypeSatsInViewAcqAssElement, TOWAssist);
		createSatElements(this.assistTypeSatsInViewAcqAssElement, satsInViewAcqAssNumberOfSats);
		this.readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeSatsInViewAcqAssElement);
	}

	/**
	 * insert unvailable element to AcqAss
	 * @param reason
	 */
	public void insertSatsInViewAcqAssUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewAcqAssElement, reason);
	}

	/**
	 * insert unsupported data to AcqAss
	 * @param reason
	 */
	public void insertSatsInViewAcqAssUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewAcqAssElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeSatsInView>DGNSS data
	 * @param satsInViewDGNSSNumberOfSats
	 * @param satDataStringArray
	 */
	public void insertSatsInViewDGNSSData(final int satsInViewDGNSSNumberOfSats, String[] satDataStringArray)
	{
		this.createSatElements(this.assistTypeSatsInViewDGNSSElement, satsInViewDGNSSNumberOfSats);
		this.readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeSatsInViewDGNSSElement);
	}

	public void insertSatsInViewDGNSSUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewDGNSSElement, reason);
	}

	public void insertSatsInViewDGNSSUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewDGNSSElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeSatsInView>RTI data;
	 * @param RTIDataString
	 */
	public void insertSatsInViewRTIData(String RTIDataString)
	{
		createBadSatsElement(this.assistTypeSatsInViewRTIElement);
		readBadSatsDataFromStringIntoBadSatsElement(this.assistTypeSatsInViewRTIElement, RTIDataString);
	}

	public void insertSatsInViewRTIUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewRTIElement, reason);
	}

	public void insertSatsInViewRTIUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewRTIElement, reason);
	}


	/**
	 * This method is used to insert AssistTypeSatsInView>RefTime data.
	 * @param satsInViewRefTimeNumberOfSats
	 * @param satDataStringArray
	 */
	public void insertSatsInViewRefTimeData(final int satsInViewRefTimeNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeSatsInViewRefTimeElement, satsInViewRefTimeNumberOfSats);
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeSatsInViewRefTimeElement);
	}

	public void insertSatsInViewRefTimeUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewRefTimeElement, reason);
	}

	public void insertSatsInViewRefTimeUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewRefTimeElement, reason);
	}


	/**
	 * This method is used to insert AssistTypeSatsInView>Almanac data
	 * @param satsInViewAlmanacNumberOfSats
	 * @param satDataStringArray
	 */

	public void insertSatsInViewAlmanacData(final int satsInViewAlmanacNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeSatsInViewAlmanacElement, satsInViewAlmanacNumberOfSats);
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeSatsInViewAlmanacElement);
	}

	public void insertSatsInViewAlmanacUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewAlmanacElement, reason);
	}

	public void insertSatsInViewAlmanacUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewAlmanacElement, reason);
	}

	/**
	 * This method is used to insert AssistTypeSatsInView>NavModel data
	 * @param satsInViewNavModelNumberOfSats
	 * @param satDataStringArray
	 */
	public void insertSatsInViewNavModelData(final int satsInViewNavModelNumberOfSats, String[] satDataStringArray)
	{
		createSatElements(this.assistTypeSatsInViewNavModelElement, satsInViewNavModelNumberOfSats);
		readSatDataFromStringArrayIntoSatElement(satDataStringArray, this.assistTypeSatsInViewNavModelElement);
	}

	public void insertSatsInViewNavModelUnavailable(String reason)
	{
		createUnavailableElement(this.assistTypeSatsInViewNavModelElement, reason);
	}

	public void insertSatsInViewNavModelUnsupported(String reason)
	{
		createUnsupportedElement(this.assistTypeSatsInViewNavModelElement, reason);
	}


	private void createTOWAssistElement(Element acqAssElement, double TOWAssist)
	{
		Element towAssistElement = this.DOMDocument.createElement(this.TOWAssistElementString);
		String TOWAssistString = Double.toString(TOWAssist);
		Text dataText = this.DOMDocument.createTextNode(TOWAssistString);
		towAssistElement.appendChild(dataText);
		acqAssElement.appendChild(towAssistElement);

	}


	private void readBadSatsDataFromStringIntoBadSatsElement(Element RTIElement, String RTIDataString)
	{
		NodeList nl = RTIElement.getElementsByTagName(this.badSatsElementString);
		Element badSatsElement = (Element)nl.item(0); //get the AssistTypeSatsInView element
		Text dataText = this.DOMDocument.createTextNode(RTIDataString);
		badSatsElement.appendChild(dataText);

	}

	private void addAttrsToElement(Attr[] list, Element element)
	{
		for (int i = 0 ; i <list.length ; i++)
		{
			if(list[i] != null)
			{
				element.setAttributeNode(list[i]);
			}
		}
	}

	private void addAttrToAttrList(Attr attr, Attr[] attrList)
	{
		for(int i = 0 ; i<attrList.length; i++)
		{
			if(attrList[i] == null)
			{
				attrList[i] = attr;
				break;
			}
		}
	}

	/**
	 * This method writes the GNSSResponse to a xml file.
	 * it should be called after the DOM structure has been generated.
	 * NOTE: for testing/debug purposes, you can serialize the response to std out (System.out) instead of
	 * FileOutputStream in the XMLSerializer constructor
	 */
	private void serializeToFile()
	{
		this.logger.entering(thisClass, "serializeToFile");
		try
		{
			OutputFormat format = new OutputFormat(this.DOMDocument);
			format.setIndenting(true);
			format.setEncoding(this.GNSSResponseEncoding);

			XMLSerializer serializer = new XMLSerializer(
					new FileOutputStream(new File(this.filename)), format);

			serializer.serialize(this.DOMDocument);

			this.logger.info("GNSSResponseWriter.serializeToFile: GNSSResponse written to file");

		} catch(IOException e)
		{
			this.logger.severe("GNSSResponseWriter.serializeToFile.err: IOException while trying to serialize to file: " +e );
		}
	}

	/**
	 * This method is used to extract a portion of a string, from a starting to an ending position
	 * @param originalString the original string
	 * @param start the starting token
	 * @param end ending token
	 * @return the extracted string.
	 */
	private String extractWhiteSpacedString(String originalString, int start, int end) //used to extract portions of a string
	{
		int count = 0;
		String extractedString = "";
		StringTokenizer st = new StringTokenizer(originalString, " ");

		for(int i = 0 ; i<end; i++)
		{
			while(count<start)
			{
				count++;
				st.nextToken();
			}
			if(count == start)
			{
				extractedString = extractedString.concat(st.nextToken());
				count++;
			}
			else
			{
				extractedString = extractedString.concat(" ").concat(st.nextToken());
			}

		}
		return extractedString;
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

	/**
	 * @return the attr_GNSSType_value
	 */
	public String getAttrGNSSTypeValue()
	{
		return this.attr_GNSSType_value;
	}

	/**
	 * @param attr_GNSSType_value the attr_GNSSType_value to set
	 */
	public void setAttrGNSSTypeValue(final String attr_GNSSType_value)
	{
		this.attr_GNSSType_value = attr_GNSSType_value;
	}

	/**
	 * @return the attr_NavType_value
	 */
	public String getAttrNavTypeValue()
	{
		return this.attr_NavType_value;
	}

	/**
	 * @param attr_NavType_value the attr_NavType_value to set
	 */
	public void setAttrNavTypeValue(final String attr_NavType_value)
	{
		this.NavTypeAvailable = true; //raise the flag for the optional NavType element.
		this.attr_NavType_value = attr_NavType_value;
	}

	/**
	 * @return the attr_unavailable_types
	 */
	public String getAttrUnavailableTypes()
	{
		return this.attr_unavailable_types;
	}

	/**
	 * @param attr_unavailable_types the attr_unavailable_types to set
	 */
	public void setAttrUnavailableTypes(final String attr_unavailable_types)
	{
		this.unavailableTypesEnabled = true;
		this.attr_unavailable_types = attr_unavailable_types;
	}

	/**
	 * @return the attr_unsupported_types
	 */
	public String getAttrUnsupportedTypes()
	{
		return this.attr_unsupported_types;
	}

	/**
	 * @param attr_unsupported_types the attr_unsupported_types to set
	 */
	public void setAttrUnsupportedTypes(final String attr_unsupported_types)
	{
		this.unsupportedTypesEnabled = true;
		this.attr_unsupported_types = attr_unsupported_types;
	}

	/**
	 * @return the GNSSResponse
	 */
	public String getGNSSResponse()
	{
		this.logger.finer(this.GNSSResponse);
		return this.GNSSResponse;
	}

	/**
	 * @param response the GNSSResponse to set
	 */
	public void setGNSSResponse(String response)
	{
		this.GNSSResponse = response;
	}

	public String getGNSSResponseEncoding() 
	{
		return this.GNSSResponseEncoding;
	}
}

