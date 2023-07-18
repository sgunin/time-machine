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

package OSGRS.OSGRS;

import java.io.File;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import OSGRS.DataManagement.DataSourceManager;
import OSGRS.HTTPserver.HTTPServer;
import OSGRS.Util.ConfigManager;
import OSGRS.Util.DebugLogger;

/**
 *  * This class co-ordinates the overall activities of the OSGRS.
 *  @author Nam Hoang, Manosh Fernando
 *
 */

public class OSGRS
{

	/** This is the Config Manager used by the OSGRS */
	private ConfigManager cfgMgr;

	/** dataSourceManager for this OSGRS */
	private DataSourceManager dataSourceManager;

	/** The logger used for debug for this OSGRS instance */
	private Logger logger;

	/** The full name of this class */
	private final String thisClass = this.getClass().getName();

	/** Whether the OSGRS is in debug or not */
	private static int debug = 0;

	/**
	 * This is the main class for the OSGRS class
	 * It creates a new OSGRS instance when invoked, and also checks whether the
	 * OSGRS is put into debug mode or not.
	 *
	 * The OSGRS can be put into debug by calling the argument dbg passing it a value of 1
	 * i.e "dbg=1", this must be the first parameter parsed
	 * @param args
	 */
	public static void main(String[] args)
	{
		if(args.length>0)
		{
			for(int i=0; i<args.length; i++)
			{
				if(args[i].equals("cleanLogsNow")) //use this to clean up old debug log files on launch
				{
					System.out.println("########Cleaning Log directory now....########\r\n");
					OSGRS.cleanUpLogs("D:\\ANSA\\My Documents\\osgrs logs\\dbg1\\","NOW");
				}

				if(args[i].equals("dbg=1")) //checks for debug mode
				{
					debug = 1;
				}
				if(args[i].equals("cleanLogsOnExit")) //use this to clean up old debug log files on exit
					//CAUTION: USE WITH CARE, refer to API for termination of VM process:
					//System class
				{
					System.out.println("########Old log files will be deleted on exit...########");
					OSGRS.cleanUpLogs("D:\\ANSA\\My Documents\\osgrs logs\\dbg1\\","ONEXIT");
				}

			}

		}
		new OSGRS();
	}

	/**
	 * This is the constructor of the OSGRS class, it initialises the OSGRS and runs it.
	 */
	public OSGRS()

	{
		init();
		run();
		this.logger.info("OSGRS started");
	}

	/**
	 *  This method is used to intialise the OSGRS,
	 *  it initialises attributes of the OSGRS and invokes the config
	 *  manager to load configurations file.
	 *
	 *  It also checks whether the OSGRS is in debug or not.
	 *
	 */
	private void init()
	{
		setCfgMgr(new ConfigManager()); 
		OSGRS.cleanUpLogs(System.getProperty("xmlWorkingDirectory"), "NOW");
		OSGRS.cleanUpLogs(System.getProperty("logDirectory"), "NOW");


		setLogger(Logger.getLogger(thisClass));

		if(debug == 0)
		{
			this.logger.setLevel(DebugLogger.getOutputVerbosity()); // get the output verbosity
		}
		else{
			if(debug == 1)
			{
				DebugLogger.startInDbg();
				DebugLogger.setOutputVerbosity(Level.FINEST); // override config settings if in debug mode

				assert(DebugLogger.getOutputVerbosity() == Level.FINEST);

				this.logger.setLevel(DebugLogger.getOutputVerbosity()); // get the output verbosity

				System.out.println("###########D      E      B      U      G###########");
			}

		}

		DebugLogger.recordLogToFile(this.logger);

	}

	/**
	 * Starts the OSGRS and opens the client listener for client
	 * connections.
	 *
	 */
	private void run()
	{
		this.logger.entering(thisClass,"run");

		this.dataSourceManager = new DataSourceManager();

		try
		{
			HTTPServer httpServer = new HTTPServer(this);
		}catch(Exception e)
		{
			this.logger.severe(e.toString());
		}
		this.logger.finer("OSGRS.run: listening");
	}

	/**
	 * @return the Config Manager
	 */
	public ConfigManager getCfgMgr()
	{
		return this.cfgMgr;
	}

	/**
	 * @param cfgMgr the Config Manager to set
	 */
	public void setCfgMgr(final ConfigManager cfgMgr)
	{
		this.cfgMgr = cfgMgr;
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
	 * This is used to clean up old log files in the "dbg" directory.
	 * all ".log" files will be removed
	 * @param logPath this is the log path/directory
	 * @param execution when to execute the cleanup
	 */

	public static void cleanUpLogs(String logPath,String execution)
	{
		File file = new File(logPath);

		if((file.isDirectory())){
			System.out.println("Deleting: " +file.getAbsolutePath());

			if(!file.delete())
			{
				System.out.println("Directory is not empty.. deleting all files and subdirs...");

				String[] fileList = file.list();

				for(int i =0; i<fileList.length; i++)
				{
					File f = new File(logPath,fileList[i]);
					if(execution.equals("NOW"))
					{
						boolean isDeleted = f.delete();
						if(isDeleted)
						{
							System.out.println("Deleted : " + fileList[i]);
						}

					}
					if(execution.equals("ONEXIT"))
					{
						f.deleteOnExit(); //delete the files on exit (excluding the new logs retreived)
					}
				}

			}

		}
	}

	public DataSourceManager getDataSourceManager() 
	{
		return this.dataSourceManager;
	}
}
