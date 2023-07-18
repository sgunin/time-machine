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

package OSGRS.novateloem4;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import OSGRS.Util.DebugLogger;
import OSGRS.Util.GNSSUtil;

/**
 * This class is used to make a connection to a NovAtel OEM4 RR,
 * connections will be persistent. All logs will be configured upon connection
 * @author Nam Hoang
 *
 */
public class NovatelDataSourceConnector extends Thread
{
    /** The logger used for debug for this OSGRS instance **/
    private Logger logger;

    /** The full name of this class **/
    private final String thisClass = this.getClass().getName();

    /** The connection socket */
    private Socket connectorSocket;

    /** The writer used to queue and send messages to the Novatel **/
    private BufferedWriter outputWriter;

    /** The buffered reader to read the responses from the Novatel RR **/
    private BufferedReader inputReader;

    /** The NovatelDataSource this connector belongs to **/
    private NovatelOEM4DataSource novatelDataSourcea;

    /** The Novatel RR host */
    private String host;

    /** The Novatel RR port  to connect to */
    private int port;

    /** The comport of the Novatel RR to use */
    private int comport;

//    /*** This string contains the entire novatelLog recieved from the receiver **/
//    private String novatelLogHistory = "";

    private final int NOVATELLOGHISTORY_MAX_LENGTH = 15000000;

    /** This String contains the history of all the commands issued to the novatel RR **/
    private String novatelCommandHistory = "";

    /** The string containing the command list to set up the novatel RR upon connect **/
    private String commandList;

    /** This flag is used to indicated whether the response buffer should be reading **/
    private boolean isReading;

    /** The rate of novatel log history output to file**/
    private final int NOVATELLOGHISTOROUTPUTRATE_IN_MILLISECONDS = 60000;

    /** The delay for the readline excecution**/
    private int READ_DELAY_MILLISECONDS = 500;

    /**
     * This is the construcotr of the NovatelDataSourceConnector class
     * @param host - the ip/hostname of the novatel rr.
     * @param port - port number
     * @param comport - the COM port number
     * @param commandList - the commandList to perform upon connection (the commands must be seperated by  a "%" )
     * #### For LOG commands, the comport number does not need to be specified within the command, for all other commands,
     * comport number needs to be specified, for example: ("LOG IONUTCA%UNLOGALL COM2") is valid
     */

    public NovatelDataSourceConnector(NovatelOEM4DataSource novatelDataSourcea, String host,int port, int comport, String commandList)
    {
        this.novatelDataSourcea = novatelDataSourcea;
        this.port = port;
        this.host = host;
        this.comport = comport;
        this.commandList = commandList;

        init();
        start();
    }

    public void run()
    {
        this.logger.entering(thisClass, "run");
        connect();
        processCommandList(this.commandList);  //this should set us up with the correct logs required.

        try{
        this.flushBuffer();
        }
        catch(IOException e){
            this.logger.finer("NovaterlDataSourceConnector.run.err: IOException during flushing buffer");
        }

//        scheduleNovatelLogHistoryOutput(this.NOVATELLOGHISTOROUTPUTRATE_IN_MILLISECONDS);
        readResponseFromBuffer();


    }

    /**
     * This is used to process a list of command, the commands should be seperated by a "%"
     *
     * @param commandList
     */
    private void processCommandList(String commandList)
    {
        this.logger.entering(thisClass, "processCommandList");

        if(this.connectorSocket.isConnected()){
            String[] commandListArray = commandList.split("%");

            this.logger.finer(commandList);

            for(int i = 0;i<commandListArray.length;i++)
            {
                if((commandListArray[i].contains("LOG")) && !commandListArray[i].contains("UNLOGALL"))
                {
                    this.issueLogCommand(commandListArray[i].substring(4));
                    this.logger.finer("NovatelDataSourceConnector.processCommandList:LOG command ISSUED: " + commandListArray[i].substring(4));
                }
                else{

                    this.addCommandToBuffer(commandListArray[i]);

                    this.logger.finer("NovatelDataSourceConnector.processCommandList:Command Issued: " + commandListArray[i]);
                }
            }
        }

    }

    private void init()
    {
        setLogger(Logger.getLogger(thisClass));
        this.logger.setLevel(DebugLogger.getOutputVerbosity());
        DebugLogger.recordLogToFile(this.logger);
        this.logger.finer("NovatelDatSourceConnector.init : init completed sucessfuly...");

    }

    /**
     * This method is used to initiate a connection to the novatel rr.
     * @return whether we have sucessfully made the connection to the novatel rr or not
     */
    private void connect()
    {
        try{
            this.connectorSocket = new Socket(this.host,this.port);

            assert (this.connectorSocket.getOutputStream() != null);
            assert (this.connectorSocket.getInputStream() != null);

            this.outputWriter = new BufferedWriter(new OutputStreamWriter(this.connectorSocket.getOutputStream())); //ok
            this.inputReader = new BufferedReader(new InputStreamReader(this.connectorSocket.getInputStream()));

            this.logger.info("NovatelDataSourceConnector.connect.inf:  Sucessfully connected to : " + this.host +":"+this.port);

            }

        //TODO:###@ retry the connections if an exception occurs @###

            catch(UnknownHostException e)
            {
                this.logger.severe("NovatelDataSourceConnector.init.err: UnknownHostException: "  +e);

            }
            catch(IOException e)
            {
                this.logger.severe("NovatelDataSourceConnector.init.err: : IOException"  +e);
            }
    }

    private void issueLogCommand(String logCommand)
    {
       this.addCommandToBuffer("LOG COM" + this.comport + " " +logCommand);
    }

    /**
     * This method is used to read whatever the novatel is feeding the socketConnector and feed
     * it back the NovatelDataSource. It will keep trying to read if a connection is established with the novatel RR.
     */

    private void readResponseFromBuffer()
    {
        this.logger.entering(thisClass, "readResponseFromBuffer");

     String novatelLog;
      try{
        while(this.connectorSocket.isConnected())
        {
          novatelLog = this.inputReader.readLine();
//          this.addToNovatelLogHistory(novatelLog);

          if(novatelLog.contains("#") || novatelLog.contains("$")) //added $ here to accomodate synch logs like GPGSV 
          {
              String logType = getLogTypeFromNovatelLog(novatelLog);
              if(this.commandHistoryHas(logType))
              {
                  this.novatelDataSourcea.generateAsssitanceDataObjectsFromNovatelLog(logType, novatelLog);
                  this.logger.finer("NovatelDataSourceConnector.readResponseFromBuffer: logType: " + logType + " " + "novatelLog: " + novatelLog);
              }
          }
        }
      }
      catch(IOException e){
          this.logger.severe("NovatelDataSourceConnector.readResponseFromBuffer.err: IOException when trying to read the reasponse");
      }
    }

     /**
     * This method extracts the NovatelLog
     * @param novatelLog
     * @return novatelLog
     */
    private String getLogTypeFromNovatelLog(String novatelLog)
    {
      String[] tmpStrArray  = novatelLog.split(";");

      String novatelLogHeaderString = tmpStrArray[0];

      String[] novatelLogHeaderFields = novatelLogHeaderString.split(",");

      String logType = novatelLogHeaderFields[0].substring(1);

      if(logType.contains("COM"))
      {
          logType = logType.substring(6);
      }

      assert((Integer.parseInt(novatelLogHeaderFields[1].substring(3,4))) == this.comport); // it would be bad if we were getting data designated for another port

      return logType;

    }

    private boolean commandHistoryHas(String logType)
    {
        boolean hasCommand;
        if(this.novatelCommandHistory.contains(logType))
        {
            hasCommand = true;
        }
        else
        {
            hasCommand = false;
        }

        return hasCommand;
    }

    /**
     * This method is used to queue a command to the outputwriter's buffer
     */
    private void addCommandToBuffer(String command)
    {
        this.logger.entering(thisClass, "addCommandToBuffer");

        try{
        this.outputWriter.write(command);

        this.novatelCommandHistory += "\r\n" + GNSSUtil.getTimeStamp()+ command;

        this.outputWriter.newLine();
        }
        catch(IOException e)
        {
            this.logger.severe("NovatelDataSourceConnector.addCommandToBuffer.err: IOException while trying to send out command: " + command);
        }
    }

    private void flushBuffer() throws IOException
    {
       this.logger.entering(thisClass, "flushBuffer");

       this.outputWriter.flush();
    }

//    private void addToNovatelLogHistory(String novatelLog)
//    {
//        if(this.novatelLogHistory.length() > this.NOVATELLOGHISTORY_MAX_LENGTH) //clear the history if the length becomes excessive (exceeding 1.5 million)
//        {
//            this.novatelLogHistory = "";
//            this.logger.finer("NovatelDataSourceConnector.addToNovatelLogHistory.inf: resetted novatelLog history, log exceeded max length of: "  +  this.NOVATELLOGHISTORY_MAX_LENGTH);
//        }
//
//        this.novatelLogHistory += "\r\n" + GNSSUtil.getTimeStamp() + novatelLog;
//
//    }

    /**
     * Generate outputs for the novatelLogs, on a regular basis
     * @param millisecs
     */
//    private void scheduleNovatelLogHistoryOutput(int millisecs){
//
//        this.logger.entering(thisClass, "scheduleNovatelLogHistoryOutput");
//
//        Timer timer = new Timer();
//
//        timer.scheduleAtFixedRate(new TimerTask()
//        {
//            public void run(){
//                writeNovatelLogHistoryToFile();
//            }
//
//        }
//        ,0,millisecs);
//
//        this.logger.finest("NovatelDataSourceConnector.scheduleNovatelLogHistoryOutput.reateOfOutput set to(millisecs): "  + millisecs);
//    }

    /**
     * This is used to write the novatelLog history to file for debugging purposes.
     * This includes the command and response history.
     * A timer is started when this method is called, it is configured to write the NovatelLogHistory file
     * every minute.
     */
//    private void writeNovatelLogHistoryToFile()
//    {
//        try {
//            BufferedWriter out = new BufferedWriter(new FileWriter("novatellogs/" + "COM"+this.comport+"NovatelResponseHistory"));
//            out.write(this.novatelLogHistory);
//            out.close();
//            this.logger.finest("NovatelDataSourceConnector.writeNovatelLogHistoryToFile: novatelLogs written to file at : " + GNSSUtil.getTimeStamp());
//            out = new BufferedWriter(new FileWriter("novatellogs/" + "COM"+this.comport+"NovatelCommandHistory"));
//            out.write(this.novatelCommandHistory);
//            out.close();
//
//        } catch (IOException e) {
//            this.logger.severe("NovatelDataSourceConnector.writeNovatelLogHistoryToFile.IOException: unable to write novatel log history to file: " + e);
//        }
//
//    }

    /**
     * Close all the readers/writer streams and
     * disconnect from the reciever
     * @throws IOException
     */
    public void closeConnection() throws IOException
    {
        this.outputWriter.close();
        this.inputReader.close();
        this.connectorSocket.close();

        this.logger.fine("NovatelDataSourceConnector.closeConnection : Sucessfully closed all writers, connection closed");
    }

    /**
     * @return the logger
     */
    public final Logger getLogger()
    {
        return this.logger;
    }

    /**
     * @param logger the logger to set
     */
    public final void setLogger(final Logger logger)
    {
        this.logger = logger;
    }

    /**
     * @return the formattedNovatelLogHistory
     */

}
