/*

    Copyright (C) 2007 SNAPlab
	
	http://www.gmat.unsw.edu.au/snap/

	This file is part of OSGRS.

    This program is free software; you can redistribute it and/or modify it under the terms 
    of the GNU General Public License as published by the Free Software Foundation; either 
    version 2 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
    See the GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along with this program;
    if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, 
    MA 02110-1301 USA

    Linking OSGRS statically or dynamically with other modules is making a combined work based 
    on OSGRS. Thus, the terms and conditions of the GNU General Public License cover the whole 
    combination.

    In addition, as a special exception, the copyright holders of OSGRS give you permission to 
    combine OSGRS program with free software programs or libraries that are released under the 
    GNU GPL and with code included in the standard release of Jetty Web Server under the 
    Apache 2.0 license (or modified versions of such code, with unchanged license). You may 
    copy and distribute such a system following the terms of the GNU GPL for OSGRS and the 
    licenses of the other code concerned, provided that you include the source code of that 
    other code when and as the GNU GPL requires distribution of source code.

    Note that people who make modified versions of OSGRS are not obligated to grant this special 
    exception for their modified versions; it is their choice whether to do so. The GNU General 
    Public License gives permission to release a modified version without this exception; this 
    exception also makes it possible to release a modified version which carries forward this 
    exception.

*/

package OSGRS.HTTPserver;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.util.URIUtil;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import OSGRS.OSGRS.OSGRS;
import OSGRS.Util.DebugLogger;
import OSGRS.XMLProcessing.GNSSErrorResponseWriter;
import OSGRS.XMLProcessing.GNSSResponseWriter15;
import OSGRS.XMLProcessing.RequestManager;
import OSGRS.XMLProcessing.ResponseManager;

/**
 * This class is responsible for the HTTP server component of OSGRS. 
 * The accepting, closing, etc of incoming connections is all handled by the 'Jetty' Server
 * class. The servlet OSGRSMainServlet, is used to handled requests once they have
 * been reciever by the Jetty web server container.
 * 
 * This implementation uses the deafault connector type of jetty.
 * @author Manosh Fernando
 *
 */
public class HTTPServer
{
	/** osgrs reference */
	private OSGRS osgrs;

	/** Jetty server object */
	private Server server;

	private Context context;

	/** Logger iof this class */
	private Logger logger;

	/** Name of this class*/
	private String thisClass = this.getClass().getName();

	/** name of server */
	private String nameOfServer = "OSGRS 1.0";

	/**
	 * The constructor of this class. Needs to be passed an OSGRS reference, so 
	 * methods in this class have acces to other OSGRS classes like the DataSource Manager
	 * and GNSSDataCache
	 * @param osgrs
	 * @throws Exception
	 */
	public HTTPServer(OSGRS osgrs) throws Exception
	{
		this.osgrs = osgrs;
		init();
		run();
	}

	private void init()
	{
		this.logger = Logger.getLogger(thisClass + "@" + System.currentTimeMillis());
		this.logger.setLevel(DebugLogger.getOutputVerbosity());
		DebugLogger.recordLogToFile(this.logger);

		this.server = new Server(Integer.parseInt(System.getProperty("listenerPort")));

		this.context = new Context(this.server,"/", Context.SESSIONS);
		this.context.addServlet(new ServletHolder(new OSGRSMainServlet()), "/*");

	}

	private void run()
	{
		try
		{
			this.server.start();
			this.server.join();
		} catch (Exception e)
		{
			this.logger.severe("e:" + e.toString());
		}
	}

	/**
	 * Main servlet that is used to handle POST requests, and send responses
	 * back to the client.
	 * @author mfernando
	 *
	 */
	private class OSGRSMainServlet extends HttpServlet
	{
		private Logger logger = getLogger();

		private String thisClass = this.getClass().toString();

		/**
		 * Method used to co-ordinate handling of POST requests, creating XML responses and  sending 
		 * responses back to the client.
		 */
		protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
		{
			this.logger.entering(thisClass, "doPost");

			String xmlRequestString;
			RequestManager requestManager;
			ResponseManager responseManager;

			xmlRequestString = getXmlDataStringFromRequest(request);
			requestManager = new RequestManager(xmlRequestString, osgrs.getDataSourceManager().getDataCache());

			if (!requestManager.isValidationFailed()) 
			{
				responseManager = requestManager.getResponseManager();
				sendResponse(response, responseManager);
			}else
			{
				sendErrorResponse(response, requestManager.getErrorResponseWriter());
			}
			this.logger.exiting(thisClass, "doPost");
		}

		/**
		 * method used for handling of GET requests. Not used for anythingl at present.
		 * Maybe of use later for maybe creating a web based config and monitoring page
		 */
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
		{
			this.logger.entering(thisClass, "doGet");

			System.out.println("doGet");

			System.out.println(request.toString());

			System.out.println("Request body: ");

			try
			{
				String requestLine = request.getReader().readLine();

				while(requestLine != null)
				{
					System.out.println(requestLine);
					requestLine = request.getReader().readLine();
				}

			} catch (IOException e)
			{
				System.out.println("ioexception: " + e.toString());
			}

			response.setContentType("text/html");
			response.setStatus(HttpServletResponse.SC_OK);

			try{
				response.getWriter().println("<h1>Hi, You've reached OSGRS!</h1>");
				response.getWriter().println("session="+request.getSession(true).getId());
			} catch(IOException e)
			{
				System.out.println(e.toString());
			}


			this.logger.exiting(thisClass, "doGet");
		}

		private void printRequest(HttpServletRequest request)
		{
			System.out.println("printRequestMethod");

			System.out.println(request.toString());

			System.out.println("Request body: ");

			try
			{
				String requestLine = request.getReader().readLine();

				while(requestLine != null)
				{
					System.out.println(requestLine);

					String decodedRequestLine = URIUtil.decode(requestLine);
					requestLine = request.getReader().readLine();
				}
			}catch (IOException e)
			{
				System.out.println("ioexception: " + e.toString());
			}

			try
			{
				request.getReader().mark(0);//resets position of buffered reader
				request.getReader().reset();
			}catch(IOException e)
			{
				System.out.println("e: " + e);
			}
		}

		/**
		 * gets the string containg xml request. Since part of OSGRS specification is to put request sting into
		 * URI encoding, this method also decodes the URI encoding.
		 * @param request
		 */

		private String getXmlDataStringFromRequest(HttpServletRequest request)
		{
			this.logger.entering(thisClass, "getXmlDataStringFromRequest");

			String xmlDataString = null;

			try
			{
				xmlDataString = request.getReader().readLine();
			}catch(IOException e)
			{
				this.logger.fine("e: " + e.toString());
			}

			if (xmlDataString != null && xmlDataString.contains("request=") && (xmlDataString.length() > 
			"request=".length()))
			{
				String[] splitXMLDataString = xmlDataString.split("=");

				xmlDataString = splitXMLDataString[1];
			}

			if (xmlDataString.contains("%"))
			{
				try
				{
					xmlDataString = URIUtil.decode(xmlDataString);
				}catch(IOException e)
				{
					this.logger.fine("e: " + e.toString());
				}
			}
			this.logger.exiting(thisClass, "getXmlDataStringFromRequest");

			return xmlDataString;
		}

		/**
		 * Sends rsponse back to the client.
		 * @param response
		 * @param responseManager
		 */
		private void sendResponse(HttpServletResponse response, ResponseManager responseManager )
		{
			this.logger.entering(thisClass, "sendResponse");

			GNSSResponseWriter15 responseWriter = responseManager.getResponseWriter();

			response.setContentType("application/xml; charset=" + responseWriter.getGNSSResponseEncoding());
			response.addHeader("Server", nameOfServer);
			response.setDateHeader("Date", System.currentTimeMillis());
			response.setStatus(HttpServletResponse.SC_OK);

			try
			{
				response.getWriter().println(responseWriter.getGNSSResponse());
			}catch (IOException e)
			{
				this.logger.fine("e: " + e.toString());
			}

			this.logger.exiting(thisClass, "sendResponse");
		}

		/**
		 * used to send an error response to the client
		 * @param response
		 * @param errorResponseWriter
		 */
		private void sendErrorResponse(HttpServletResponse response, GNSSErrorResponseWriter errorResponseWriter )
		{
			response.setContentType("application/xml; charset=" + 
					errorResponseWriter.getGNSSErrorResponseEncoding());
			response.addHeader("Server", nameOfServer);
			response.setDateHeader("Date", System.currentTimeMillis());
			response.setStatus(HttpServletResponse.SC_OK);

			try
			{
				response.getWriter().println(errorResponseWriter.getGNSSErrorResponse());
			}catch (IOException e)
			{
				this.logger.fine("e: " + e.toString());
			}
		}
	}

	public Logger getLogger() 
	{
		return this.logger;
	}

	public void setLogger(Logger logger) 
	{
		this.logger = logger;
	}

}