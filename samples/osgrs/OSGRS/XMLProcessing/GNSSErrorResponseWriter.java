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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * This is the writer for the Error Repsponse
 * This writer is complient with version 1.5 of the
 * GRIP Protocol. If the error response changes in susequent versions
 * this writer must be modified to comply with these changes
 * 
 * Example of usuage
 * 
 * String[] stringArray;
 * 
 * GNSSErrorResponseWriter  errorResponseWriter = new GNSSErrorResponseWriter();
 * errorResponseWriter.insertErrorDataString(stringArray);
 * errorResponseWriter.generateErrorResponse();
 * 
 * System.out.println(errorResponseWriter.getGNSSErrorResponse());
 * 
 * 
 * @author Manosh Fernando
 */
public class GNSSErrorResponseWriter
{
	/** The full name of this class */
	private final String thisClass = this.getClass().getName();
	
	/** The DOM document containing the GNSSRequest */
	private Document DOMDocument;
	
	/** The string contains the GNSSErrorResponse */
	private String GNSSErrorResponse;
	
	/** The encoding of the GNSSRequest */
	private String GNSSErrorResponseEncoding = "UTF-8";
	
	//elements
	private Element GNSSErrorResponseElement;
	
	//element strings
	/** the string for 'GNSSErrorResponse' element */
	private String GNSSErrorResponseElementString = "GNSSErrorResponse";
	
	/** The string for 'Error' element */
	private String errorElementString = "Error";
	
	//attributes
	/** The array that contains the list of attributes for the element GNSSErrorResponse */
	private Attr[] GNSSErrorResponseAttrList;
	
	/** The attribute "xmlns:xsi" */
	private final String attr_xmlnsxsi = "xmlns:xsi";
	
	/** The attribute "xmlns" */
	private final String attr_xmlns = "xmlns";
	
	/** The data attribute string */
	private String attr_dataString = "data";
	
	//URI
	/** The URI for Schema Instances */
	private final String xmlSchemaInstanceURI = "http://www.w3.org/2001/XMLSchema-instance";
	
	/** The URI for the GRIP xmlns */
	private final String GRIPxmlnsURI = "http://www.gmat.unsw.edu.au/snap/grip/1.5";
	
	private Logger logger;
	
	public GNSSErrorResponseWriter ()
	{
		init();
	}
	
	private void init()
	{
		this.logger = Logger.getLogger(thisClass + "-" + System.currentTimeMillis());
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);
		
		this.GNSSErrorResponseAttrList = new Attr[4];
		
		createDocument();
		
		this.GNSSErrorResponseElement = this.DOMDocument.createElement(this.GNSSErrorResponseElementString);
	}
	
	public void generateErrorResponse()
	{
		generateDOMStructure();
		
		this.GNSSErrorResponse = GNSSUtil.serializeToString (this.DOMDocument,
				this.GNSSErrorResponseEncoding);
	}
	
	private void generateDOMStructure()
	{
		createGNSSErrorRequestElementAttrs();
		this.DOMDocument.appendChild(this.GNSSErrorResponseElement);
	}
	
	private void createGNSSErrorRequestElementAttrs()
	{
		Attr xmlnsxsi = this.DOMDocument.createAttribute(this.attr_xmlnsxsi); //create the xmlns:xsi attribute
		xmlnsxsi.setNodeValue(this.xmlSchemaInstanceURI);
		
		Attr xmlns = this.DOMDocument.createAttribute(this.attr_xmlns); //create the xmlns attribute
		xmlns.setNodeValue(this.GRIPxmlnsURI);
		
		addAttrToAttrList(xmlnsxsi,this.GNSSErrorResponseAttrList);
		addAttrToAttrList(xmlns,this.GNSSErrorResponseAttrList);
		addAttrsToElement(this.GNSSErrorResponseAttrList, this.GNSSErrorResponseElement);
	}
	
	private void createDocument()
	{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try
		{
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			this.DOMDocument = db.newDocument();
		}
		catch(ParserConfigurationException pce) 
		{
			this.logger.severe("GNSSResponseWriter.createDocument.err: Error while trying to instantiate DocumentBuilder " + pce);
		}
	}
	
	public void insertErrorData(String[] errorDataStringArray)
	{
		//count non-null strings in array
		int numberOfElements = 0;
		for(int i = 0; i < errorDataStringArray.length; i++)
		{
			if(errorDataStringArray[i] != null)
			{
				numberOfElements ++;
			}
		}
		
		//create 'Error' Elements
		
		createErrorElements(this.GNSSErrorResponseElement, numberOfElements, errorDataStringArray);
	}
	
	/**
	 * This method creates the <Error> elements, depending on the number specified
	 * data is added from the provided string array, non-null elements and numberOfElements
	 * should be same
	 * @param elementToAppendTo the parent element of the <Error> elements
	 * @param numberOfSats number of error elements.
	 */
	private void createErrorElements(Element elementToAppendTo, int numberOfElements, String[] stringArray)
	{
		Element[] elementList = new Element[numberOfElements];
		
		int n = 0; //index for string array
		for(int i = 0 ; (i<elementList.length && n < stringArray.length); i++)
		{
			elementList[i] = this.DOMDocument.createElement(this.errorElementString);
			
			for(; n < stringArray.length; n++)
			{
				if (stringArray[n] != null)
				{
					createDataAttr(elementList[i], stringArray[n]);
					n++;
					break; 
				}
			}
			
			elementToAppendTo.appendChild(elementList[i]);
		}
		
	}
	
	/**
	 * creates a data attribute
	 * @param element - element to add 'data' attribute to
	 * @param string - value of 'data' attribute
	 */
	private void createDataAttr(Element element, String string)
    {
        Attr data = this.DOMDocument.createAttribute(this.attr_dataString);
        data.setNodeValue(string);
        element.setAttributeNode(data);
    }
	
	
	private void addOptionalElementWithNonEmptyAttribute(Element parent, Element child, String attribute)
	{
		if((child.hasAttribute(attribute)) && (child.getAttribute(attribute) != ""))
		{
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
	 * 
	 * @param list
	 * @param element
	 */
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
	
	/**
	 * adds attribute to first position in an attribute array that isn't empty
	 * @param attr
	 * @param attrList
	 */
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
	
	public String getGNSSErrorResponse() 
	{
		return this.GNSSErrorResponse;
	}

	public String getGNSSErrorResponseEncoding() 
	{
		return this.GNSSErrorResponseEncoding;
	}
	
}