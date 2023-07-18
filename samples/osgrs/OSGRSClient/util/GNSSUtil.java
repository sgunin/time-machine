package OSGRSClient.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.StringTokenizer;
import java.util.Date;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import java.util.Calendar;
import java.util.regex.PatternSyntaxException;
import java.lang.NumberFormatException;

/**
 * This class contains common methods used.
 *
 */
public class GNSSUtil
{

    /**
     * Used to seperate and store a whitespaced double list list into a
     * new array.
    * @param whiteSpacedString the string to be processed
    * @return doubleList the array which contains the processed double list.
    */
   public static  double[] splitWhiteSpacedDoubleList(String whiteSpacedString)
    {
        StringTokenizer tk = new StringTokenizer(whiteSpacedString, " ");
        double doubleList[] = new double[tk.countTokens()];

        for(int i=0;i<doubleList.length;i++)
        {
            String tempString = tk.nextToken();
            doubleList[i] = Double.parseDouble(tempString);
        }

        return doubleList;

    }

   /**
    * This method serializes a Document to a string with no whitespaces
    * It should be called after the DOMStructure has been generated
 * @param document The Document object
 * @param encoding The type of encoding for the string
 * @return the serialized string
 */
public static String serializeToString(Document document, String encoding)
   {
       StringWriter stringWriter = new StringWriter();
       try{
       OutputFormat format = new OutputFormat(document);

       format.setIndenting(false);
       format.setLineSeparator(""); // concatenates the <xml> and child elements
       format.setEncoding(encoding);

       XMLSerializer serializer = new XMLSerializer(stringWriter,format);

       serializer.serialize(document);

       }
       catch(IOException e)
       {
           System.out.println("GNSSUtil.serializeToString.err: IOException when serializing document to string");
       }
       return stringWriter.getBuffer().toString();
   }

    /**
     * This method calculates the max value of a two's complement given a scale factor (2^scaleFactor)
     * @param numberOfBits the number of bits
     * @param scaleFactor the scale factor
     * @return the max value attainable for the given params
     */
    public static double calculateMaxTwosByScaleFactor(int numberOfBits, int scaleFactor)
    {
        return (Math.pow(2,numberOfBits-1) - 1) * (Math.pow(2,scaleFactor));

    }

    /**
     * This method calculates the max value of a two's complement given a scale factor (2^scaleFactor)
     * @param numberOfBits the number of bits
     * @param scaleFactor the scale factor
     * @return the min value attainable given the params
     */
    public static double calculateMinTwosByScaleFactor(int numberOfBits, int scaleFactor)
    {
        return -1.0 * (Math.pow(2,numberOfBits-1)) * (Math.pow(2,scaleFactor));
    }

    /**
     * Generates the timestamp of the following format:
     *  [hr:min:sec]
     * @return time - the timestamped time
     */
    public static String getTimeStamp()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int date = c.get(Calendar.DATE);
        int hr = c.get(Calendar.HOUR);
        int mn = c.get(Calendar.MINUTE);
        int sc = c.get(Calendar.SECOND);
        String time = "["+year+month+date+"|"+hr+":"+mn+":"+sc+"]";

        return time;

    }

    /**
     * Gets difference of time provided in string relative to current system time
     * note: the timestamp should preceed the string, and delimitted by "!%"
     * @param stringWithTime
     * @return the time difference
     */

    public static double getTimeDiffeneceInMillis(String stringWithTime)
    {
        String[] stringArray;
        String logTimeString;
        double timeDifference = 0;
        double logTime = 0;

        try
        {
            stringArray = stringWithTime.split("!%");
            logTimeString = stringArray[0];
            logTime = Double.parseDouble(logTimeString);
            timeDifference = (System.currentTimeMillis() - logTime);
        }
        catch(PatternSyntaxException e)
        {
            System.out.println("GNSSUtil.getTimeDiffeneceInMillis.err: PatternSyntaxException when splitting string");
        }
        catch(NumberFormatException e)
        {
            System.out.println("GNSSUtil.getTimeDiffeneceInMillis.err: NumberFormatException when parsing time");
        }
        return timeDifference;
    }
    
    public static long getTimeDifferenceInMillis(Date date)
    {
    	return (System.currentTimeMillis() - date.getTime());
    }
    
    public static long getTimeDifferenceBetweenInMillis(Date date1, Date date2)
    {
    	return (date1.getTime() - date2.getTime());
    }
    
    /**
     * Calculates the integer equivalent of a twos complement number
     * @param twosComplementNumber the twos complement number
     * @param numberOfBits the number of bits in the above number format
     *
     * @return the integer equivalent
     */
    public static int twosComplementToInteger(long twosComplementNumber, int numberOfBits)
    {
        long bitMask = 0x01 << (numberOfBits - 1);
        long complementMask = (long)Math.pow(2, numberOfBits) - 1;


        int intValue = 0;
        if ((twosComplementNumber & bitMask) != 0)
        {
            //intValue = (int)((twosComplementNumber ^ complementMask) - 1) * -1; 
            intValue = (int)((twosComplementNumber ^ complementMask) + 1) * -1; //changed to plus
        }
        else
        {
            intValue = (int)twosComplementNumber;
        }


        return intValue;
    }

    public static long takeTwosComplement(long binaryNumber,int numberOfBits)
    {
        long workingLong;
    	//System.out.println("binaryNumber: " + Long.toBinaryString(binaryNumber));
        workingLong = (~binaryNumber) & (long)(Math.pow(2, numberOfBits) - 1);
        //System.out.println("workingLong: " + Long.toBinaryString(workingLong));
        workingLong += 1;
    	//System.out.println("workingLong: " + Long.toBinaryString(workingLong));

        return workingLong;
    }
    
    public static long putIntoTwosComplementForm(long number, int numberOfBits)
	{
		if(number >= 0)
		{
			//System.out.println("jhggh");
			
			return number;
		} else
		{
			return takeTwosComplement(Math.abs(number), numberOfBits);
		}
	}
    
    public static boolean checkIntAgainstShortArray (int intToCheck, short[] shortArray)
    {
    	boolean intInArray = false;
    	
    	for (int i = 0; i < shortArray.length; i++)
    	{
    		if (intToCheck == shortArray[i])
    		{
    			intInArray = true;
    		}
    	}
    	
    	return intInArray;
    }


    }
