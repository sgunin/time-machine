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

package OSGRS.Util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class is used to write logs to file
 * The levels of debugging are: 0,1,2,3,4,5,6 as defined in the systemConfig.conf file
 * this corrosponds with the levels of OFF, SEVERE, INFO, CONFIG, FINE, FINER, FINEST.
 * @author Nam Hoang, Manosh Fernando
 *
 */

public class DebugLogger
{

    /** The configured output level */
    private static Level outputLevel;

    /** The output directory of debug files */
    public static String outputDir = System.getProperty("logDirectory");

    /** The output format of debug files */
    private static String outputFormat = ".log";

    /** The flag to indicate whethere the OSGRS is in debug or not **/
    private static boolean debugMode;


    public static void startInDbg(){
        debugMode = true;
    }
    /**
     * Record a class' logger to a log file.
     * The file system checks to see if the configured output directory exists,
     * if it doesnt then it will create it.
     * If level of log is set to 'OFF' or 'INFO', this function does nothing as there is no point
     * in making files for empty logs
     * @param logger the logger
     */
    public static void recordLogToFile(Logger logger)
    {
    	if((logger.getLevel() != Level.OFF) && logger.getLevel() != Level.INFO)
    	{
    	
	        if(outputDirExists(logger))
	        try
	            {
	                tidyUpDirectory(logger);
	                FileHandler handler = new FileHandler(outputDir + logger.getName() + outputFormat);
	                logger.addHandler(handler);
	            }
	            catch(IOException e)
	            {
	                System.out.println("DebugLogger.recordLogToFile.err: IO ERROR- cannot write log to file: " + logger.getName() + ".log");
	            }
	         else{
	                File file = new File(outputDir);
	                file.mkdir();
	             }
	        
    	}
    }

    /**
     * Record a class' logger to a log file. This method should be used when there are several instances of a class
     * The file system checks to see if the configured output directory exists,
     * if it doesnt then it will create it.
     * @param logger the logger
     * @param instance the instance number/param
     */
    public static void recordLogToFile(Logger logger,String instance)
    {

    	if((logger.getLevel() != Level.OFF) && (logger.getLevel() != Level.INFO))
    	{
	    	
	        Date date = new Date();
	        String outputDir = DebugLogger.outputDir;
	
	        if(outputDirExists(logger))
	        try
            {
                tidyUpDirectory(logger);
                FileHandler handler = new FileHandler(outputDir + logger.getName()+ "." + instance + "." + date.getTime() + outputFormat);
                logger.addHandler(handler);
            }
            catch(IOException e)
            {
                System.out.println("DebugLogger.recordLogToFile.err: IO ERROR- cannot write log to file: " + logger.getName() + ".log");
                System.out.println(e.toString());
            }
			else
			{
		        File file = new File(outputDir);
		        file.mkdir();
			}
    	}
    }

    /**
     * Checks whether a log file exists and deletes it
     * @param logger the logger
     */
    private static void tidyUpDirectory(Logger logger)
    {
        File file = new File(outputDir + logger.getName() + outputFormat);
        if(file.exists()){
            file.delete();
        }
    }

    private static boolean outputDirExists(Logger logger)
    {
        File file = new File(outputDir);
        boolean fileExists = false;
        if(file.exists()){
           fileExists = true;
        }
        return fileExists;
    }

    /**
      * @return outputLevel the output verbosity level configured
     */
    public static Level getOutputVerbosity()
    {
        int outputVerbosityInt = Integer.parseInt(System.getProperty("outputVerbosity"));
        
        switch (outputVerbosityInt){
            case 0: outputLevel = Level.OFF; break;
            case 1: outputLevel = Level.SEVERE; break;
            case 2: outputLevel = Level.INFO; break;
            case 3: outputLevel = Level.CONFIG; break;
            case 4: outputLevel = Level.FINE; break;
            case 5: outputLevel = Level.FINER; break;
            case 6: outputLevel = Level.FINEST; break;
        }
        if(debugMode)
        {
            outputLevel = Level.FINEST;
        }

        return outputLevel;

    }

    /**
     * Set the outputVerbosity level
     * @param level
     */
    public static void setOutputVerbosity(final Level level)
    {
        outputLevel = level;
    }

//  /**
//  * Implement the next 2 methods at a later stage if you got time, instead of calling
//  * the ConfigManager to get the outputVerbosity just load it seperately so
//  * that the DebugLogger can be independant.
//  * @param classname the classname
//  * @return the logger to be used, it is set to the output verbosity configured
//  */
// public static Logger getLogger(String classname)
// {
//     Logger tmpLogger = Logger.getLogger(classname);
//
//     Properties propertiesHolder = loadPropertiesFromFile("SystemConfig.conf");
//
//     int level  = Integer.parseInt(propertiesHolder.getProperty("outputVerbosity"));
//
//     switch (level)
//     {
//         case 0: outputLevel = Level.OFF; break;
//         case 1: outputLevel = Level.SEVERE; break;
//         case 2: outputLevel = Level.INFO; break;
//     }
//
//     tmpLogger.setLevel(getOutputVerbosity());
//     return tmpLogger;
//
// }
//
// private static Properties loadPropertiesFromFile(String configFileName)
// {
//     Properties propertiesHolder = new Properties();
//     try{
//     propertiesHolder.load(new FileInputStream(configFileName + ".conf"));
//     }
//     catch(IOException e)
//     {
//         System.out.println("DebugLogger.loadPropertiesFromFile.err: Unable to obtain config file");
//         System.exit(1);
//     }
//
//     return propertiesHolder;
//
// }

}
