package OSGRSClient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import java.util.Date;


//TODO: Make DebugLogger standalone so it doesnt require OSGRS to load the configuration

/**
 * This class is used to write logs to file
 * The levels of debugging are: 0,1,2 as defined in the systemConfig.conf file
 * this corrosponds with the levels of OFF, SEVERE, INFO.
 *
 * MISC INFO: putting the debugger into level 3 running under eclipse IDE will terminate the application after init
 */

public class DebugLogger
{

    /** The configured output level */
    private static Level outputLevel;

    /** The output directory of debug files */
    public static String outputDir = "D:\\ANSA\\My Documents\\osgrs logs\\testClientLogs\\";

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
     * @param logger the logger
     */
    public static void recordLogToFile(Logger logger)
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

    /**
     * Record a class' logger to a log file. This method should be used when there are several instances of a class
     * The file system checks to see if the configured output directory exists,
     * if it doesnt then it will create it.
     * @param logger the logger
     * @param instance the instance number/param
     */
    public static void recordLogToFile(Logger logger,String instance)
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
         else{
                File file = new File(outputDir);
                file.mkdir();
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
        int outputVerbosityInt = 0;//Integer.parseInt(System.getProperty("outputVerbosity"));//fix this
        
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

