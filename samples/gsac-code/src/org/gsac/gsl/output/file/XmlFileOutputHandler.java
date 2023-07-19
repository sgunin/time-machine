/*
 * Copyright 2010 UNAVCO, 6350 Nautilus Drive, Boulder, CO 80301
 * http://www.unavco.org
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package org.gsac.gsl.output.file;




import org.gsac.gsl.*;
import org.gsac.gsl.model.*;
import org.gsac.gsl.output.*;

import java.io.*;

import java.text.DateFormat;

import javax.servlet.*;
import javax.servlet.http.*;
import java.util.Date;
import java.text.SimpleDateFormat;


/**
 * Class description
 *
 *
 * @version        Enter version here..., Wed, May 19, '10
 * @author         Enter your name here...
 */
public class XmlFileOutputHandler extends GsacOutputHandler {

    /** output id */
    public static final String OUTPUT_FILE_XML = "file.gsacxml";


    /**
     * ctor
     *
     * @param gsacRepository the repository
     * @param resourceClass _more_
     */
    public XmlFileOutputHandler(GsacRepository gsacRepository,
                                ResourceClass resourceClass) {
        super(gsacRepository, resourceClass);
        getRepository().addOutput(getResourceClass(), new GsacOutput(this, OUTPUT_FILE_XML, "File GSAC XML", "/files.xml", true));

    }


    /**
     * handle the request
     *
     *
     * @param request the request
     * @param response the response
     *
     *
     * @throws Exception on badness
     */
    public void handleResult(GsacRequest request, GsacResponse response)
            throws Exception {
        // import java.text.SimpleDateFormat;
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); // IS0 8601 
        System.out.println("GSAC: request for GSACs own format 'file XML output;'  time "+ft.format(now)+", from IP "+request.getOriginatingIP() );
        response.startResponse(GsacResponse.MIME_XML);
        PrintWriter pw  = response.getPrintWriter();
        String      xml = getRepository().encodeObject(response.getFiles());
        pw.print(xml);
        response.endResponse();
    }


}
