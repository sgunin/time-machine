package OSGRSClient.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Logger;
import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

//TODO: Position format checking

/**
 * This class is used to construct a GNSSRequest
 * Example of it's use:
 *
     GNSSRequestWriter xr = new GNSSRequestWriter();

     xr.insertAssistTypeAllSatsAssistanceData("IonoModel RefTime NavModel RTI");
     xr.insertAssstTypeSatsInViewAssistanceData("AcqAss Almanac");
     xr.insertPositonData("-34.4 150.8");
     xr.setAttrGNSSTypeValue("GPS");

     xr.generateRequest();

     String GNSSRequestString = xr.getGNSSRequest();
 *
 */
public class GNSSRequestWriter
{
    /** The logger for this class  */
    private Logger logger;

    /** The full name of this class */
    private final String thisClass = this.getClass().getName();

    /** The DOM document containing the GNSSRequest */
    private Document DOMDocument;

    /** The string containign the GNSSRequest */
    private String GNSSRequest;

    /** The encoding of the GNSSRequest */
    private String GNSSRequestEncoding = "UTF-8";
    
    private String filename = "GNSSRequest.xml";

    /*---------- Elements------------------------------------*/

    /** The GNSSRequest element */
    private Element GNSSRequestElement;

    /** The AssistTypeAllSatsElement */
    private Element assistTypeAllSatsElement;

    /** The AssistTypeSatsInView Element */
    private Element assistTypeSatsInViewElement;

    private Element positionElement;

    /*---------------------- end ----------------------------*/

    /*---------------- element strings ----------------------*/

    /** The GNSSRequest element string */
    private final String GNSSRequestElementString = "GNSSRequest";

    /** The AssistTypeAllSats element string */
    private final String assistTypeAllSatsElementString = "AssistTypeAllSats";

    /** The AssistTypeSatsInView element string */
    private final String assistTypeSatsInViewElementString = "AssistTypeSatsInView";

    private final String positionElementString = "Position";

    /*---------- GNSSRequest Attributes ----------------------------------------------*/

    /** The array that contains the list of attributes for the element GNSSResponse */
    private Attr[] GNSSRequestAttrList = new Attr[4];

    /** The attribute "xmlns:xsi" */
    private final String attr_xmlnsxsi = "xmlns:xsi";

    /** The attribute "xmlns" */
    private final String attr_xmlns = "xmlns";

    /** The attribute "GNSSType" */
    private final String attr_GNSSType = "GNSSType";

    /** The attribute "NavType" */
    private final String attr_NavType = "NavType";

    /*------------------------------ end -------------------------------------------*/

    /** The string that contains the value of the attribute GNSSType */
    private String attr_GNSSType_value = "";

    /** The string that contains the value of the attribute NavType */
    private String attr_NavType_value = "";

    /** The data attribute string */
    private String attr_dataString = "data";

    private String attr_dataAssistTypeAllSatsValue = "";

    private String attr_dataAssistTypeSatsInViewValue = "";

    /*-------------------------------- URIs -----------------------------------------*/

    /** The URI for Schema Instances */
    private final String xmlSchemaInstanceURI = "http://www.w3.org/2001/XMLSchema-instance";

    /** The URI for the GRIP xmlns */
    private final String GRIPxmlnsURI = "http://www.gmat.unsw.edu.au/snap/grip/1.4";

    /*------------------------------ end -------------------------------------------*/

    /** This flag is raised when the setAttrNavTypeValue() method is called */
    private boolean NavTypeAvailable = false;


    /**
     * This is the constructor of the GNSSRequestWriter class.
     */
    public GNSSRequestWriter()
    {
        init();
    }

    private void init()
    {
        setLogger(Logger.getLogger(thisClass));
        this.logger.setLevel(Level.FINER);
        DebugLogger.recordLogToFile(this.logger);

        this.createDocument();

        this.GNSSRequestElement = this.DOMDocument.createElement(this.GNSSRequestElementString);
        this.assistTypeAllSatsElement = this.DOMDocument.createElement(this.assistTypeAllSatsElementString);
        this.assistTypeSatsInViewElement = this.DOMDocument.createElement(this.assistTypeSatsInViewElementString);
        this.positionElement = this.DOMDocument.createElement(this.positionElementString);
        this.logger.finer("GNSSRequestWriter init done...");

    }

    public void generateRequest()
    {
        generateDOMStructure();
        this.setGNSSRequest(GNSSUtil.serializeToString(this.DOMDocument, this.GNSSRequestEncoding));
        serializeToFile();
    }

    private void createDocument()
    {
        this.logger.entering(thisClass, "createDocument");

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try{
            DocumentBuilder db = dbf.newDocumentBuilder();

            this.DOMDocument = db.newDocument();
        }
        catch(ParserConfigurationException pce) {
            this.logger.severe("GNSSRequestWriter.createDocument.err: Error while trying to instantiate DocumentBuilder " + pce);
        }
    }

    private void generateDOMStructure()
    {
        this.logger.entering(thisClass, "generateDOMStructure");

        createGNSSRequestElementAttrs();
        createDataAttr(this.assistTypeAllSatsElement);
        createDataAttr(this.assistTypeSatsInViewElement);

        this.logger.finer(this.getAssistTypeAllSatsAssistanceData());
        addOptionalElementWithNonEmptyAttribute(this.GNSSRequestElement, this.assistTypeAllSatsElement, this.attr_dataString);

        this.appendNonEmptyElement(this.assistTypeSatsInViewElement, this.positionElement);
        addOptionalElementWithNonEmptyAttribute(this.GNSSRequestElement, this.assistTypeSatsInViewElement, this.attr_dataString);

            this.DOMDocument.appendChild(this.GNSSRequestElement);
    }

    private void createDataAttr(Element element)
    {
        Attr data = this.DOMDocument.createAttribute(this.attr_dataString);
        if(element.equals(this.assistTypeAllSatsElement)){
        data.setNodeValue(this.attr_dataAssistTypeAllSatsValue);
        }
        else{
            data.setNodeValue(this.attr_dataAssistTypeSatsInViewValue);
        }
        element.setAttributeNode(data);
    }

    private void createGNSSRequestElementAttrs()
    {

        if(getAttrGNSSTypeValue() == ""){
            this.logger.severe("GNSSRequestWrriter.createGNSSRequestElementAttrs.err: GNSSType required");
            System.exit(1);
        }
        else{
        Attr xmlnsxsi = this.DOMDocument.createAttribute(this.attr_xmlnsxsi); //create the xmlns:xsi attribute
        xmlnsxsi.setNodeValue(this.xmlSchemaInstanceURI);

        Attr xmlns = this.DOMDocument.createAttribute(this.attr_xmlns); //create the xmlns attribute
        xmlns.setNodeValue(this.GRIPxmlnsURI);

        Attr GNSSType = this.DOMDocument.createAttribute(this.attr_GNSSType); //create the GNSSType attribute
        GNSSType.setNodeValue(this.getAttrGNSSTypeValue());

        Attr NavType = this.DOMDocument.createAttribute(this.attr_NavType);
        NavType.setNodeValue(this.getAttrNavTypeValue());

        if(this.NavTypeAvailable == true){
            addAttrToAttrList(NavType,this.GNSSRequestAttrList);

        }

        addAttrToAttrList(xmlnsxsi,this.GNSSRequestAttrList);
        addAttrToAttrList(xmlns,this.GNSSRequestAttrList);
        addAttrToAttrList(GNSSType,this.GNSSRequestAttrList);
        addAttrsToElement(this.GNSSRequestAttrList, this.GNSSRequestElement); //add attributes list to the GNSSResponse element.
        }
    }

    /**
     * This method is used to insert AssistTypeSatsInView>Position data, which contains the lat and long
     * it also checks the range of these values
     * @param positionDataString the string containing the lat and long
     * The format is important, the lat and long should be seperated by a " " space.
     * Example: -34.5 150.8 represents lat of -34.4 and long of 150.8 - the order is essential
     * refer to GRIP for more information.
     */
    public void insertPositonData(String positionDataString)
    {
        double[] positionDataList = GNSSUtil.splitWhiteSpacedDoubleList(positionDataString);
        double latitude = positionDataList[0];
        double longitude = positionDataList[1];

        if((latitude<-90) || (latitude >90) || (longitude <-180) || (longitude>180)) //check the range
        {
            if((latitude<-90) || (latitude >90)) //if it sets something stupid
            {
                this.logger.severe("GNSSRequestWriter.insertPositionData.err: Latitude out of range");
                Text dataText = this.DOMDocument.createTextNode("-1");
                this.positionElement.appendChild(dataText);
            }
            if((longitude<-180) || (longitude >180))
            {
                this.logger.severe("GNSSRequestWriter.insertPositionData.err: Longitude out of range");
                Text dataText = this.DOMDocument.createTextNode("-1");
                this.positionElement.appendChild(dataText);
            }
        }
        else{
            Text dataText = this.DOMDocument.createTextNode(positionDataString);
            this.positionElement.appendChild(dataText);
        }
    }

    /**
     * This method appends child elements, but will only do so if the child elements
     * contains a non empty attribute
     * @param parent the parent element
     * @param child the child element to append.
     */
    private void addOptionalElementWithNonEmptyAttribute(Element parent, Element child, String attribute)
    {
       if((child.hasAttribute(attribute)) && (child.getAttribute(attribute) != "")){
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
     * This method writes the GNSSReqest to a xml file.
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
            format.setEncoding(this.GNSSRequestEncoding);

            XMLSerializer serializer = new XMLSerializer(
            new FileOutputStream(new File(this.filename)), format);

            serializer.serialize(this.DOMDocument);

            this.logger.info("GNSSRequestWriter.serializeToFile: GNSSRequest written to file");

        } catch(IOException e)
        {
            this.logger.severe("GNSSRequestWriter.serializeToFile.err: IOException while trying to serialize to file: " +e );
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
        this.NavTypeAvailable = true;
        this.attr_NavType_value = attr_NavType_value;
    }

    /**
     * @return the gNSSRequest
     */
    public String getGNSSRequest()
    {
        return this.GNSSRequest;
    }

    /**
     * @param request the gNSSRequest to set
     */
    public void setGNSSRequest(final String request)
    {
        this.GNSSRequest = request;
    }

    /**
     * This method is used to insert AllSats assistance data
     * @param assistanceData the list of AssistTypeAllSats assistance data
     */
    public void insertAssistTypeAllSatsAssistanceData(String assistanceData)
    {
        this.attr_dataAssistTypeAllSatsValue = assistanceData;
        this.logger.finer("GNSSRequestWriter.insertAssistTypeAllSatsAssistanceData: " + assistanceData);
    }

    /**
     * This method is used to insert SatsInView assistance data
     * @param assistanceData
     */
    public void insertAssstTypeSatsInViewAssistanceData(String assistanceData)
    {
        this.attr_dataAssistTypeSatsInViewValue = assistanceData;
        this.logger.finer("GNSSRequestWriter.insertAssistTypeSatsInViewAssistanceData: " + assistanceData);
    }

    /**
     * @return the assistance data list of AssistTypeAllSats assistance data
     */
    public String getAssistTypeAllSatsAssistanceData()
    {
        return this.attr_dataAssistTypeAllSatsValue;
    }

}
