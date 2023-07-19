/*
 * Copyright 2013 UNAVCO, 6350 Nautilus Drive, Boulder, CO 80301
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

package @MACRO.PACKAGE@;
/* CHANGEME - use the correct name of package in line above*/

import org.gsac.gsl.*;
import org.gsac.gsl.model.*;
import org.gsac.gsl.util.*;

import org.gsac.gsl.ramadda.sql.Clause;
import org.gsac.gsl.ramadda.sql.SqlUtil;

import ucar.unidata.util.DateUtil;
import ucar.unidata.util.StringUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Calendar;


/**
 * Handles all of the resource related repository requests. The main
 * entry point is {@link #handleRequest}
 * Look for the CHANGEME comments
 *
 * @author  Jeff McWhirter
 */
public class @MACRO.PREFIX@FileManager extends FileManager {

    /**
     * ctor
     *
     * @param repository the repository
     */
    public @MACRO.PREFIX@FileManager(@MACRO.PREFIX@Repository repository) {
        super(repository);
    }



    public List<Capability> doGetQueryCapabilities() {
        List<Capability> capabilities = new ArrayList<Capability>();
        //CHANGEME
        /*
          you can use the default file capabilities:
          addDefaultCapabilities(capabilities);
          or add you own, e.g.:
          Add in an example fruit enumerated query capability
          String[]values = {"banana","apple","orange"};
          Arrays.sort(values);
          capabilities.add(new Capability("fruit", "Fruit Label", values, true));
        */
        return capabilities;
    }



    /**
     * CHANGEME
     * handle the search request
     *
     * @param request The request
     * @param response The response
     *
     * @throws Exception on badness
     */
    public void handleRequest(GsacRequest request,
                              GsacResponse response)
            throws Exception {
        //Some example code

        //The msgBuff holds the html that describes what is being searched for
        StringBuffer msgBuff = new StringBuffer();
        if (request.defined(ARG_FILESIZE_MIN)) {
            int size = request.get(ARG_FILESIZE_MIN, 0);
            appendSearchCriteria(msgBuff, "Filesize&gt;=",
                                 "" + request.get(ARG_FILESIZE_MIN, 0));
        }

        if (request.defined(ARG_FILESIZE_MAX)) {
            int size = request.get(ARG_FILESIZE_MAX, 0);
            appendSearchCriteria(msgBuff, "Filesize&lt;=",
                                 "" + request.get(ARG_FILESIZE_MAX, 0));
        }

        if (request.defined(ARG_FILE_TYPE)) {
            List<String> types = (List<String>) request.getList(ARG_FILE_TYPE);
            addSearchCriteria(msgBuff, "Resource Type", types,
                              ARG_FILE_TYPE);
        }

        Date[] publishDateRange =
            request.getDateRange(ARG_FILE_PUBLISHDATE_FROM,
                                 ARG_FILE_PUBLISHDATE_TO, null, null);

        if (publishDateRange[0] != null) {
            appendSearchCriteria(msgBuff, "Publish date&gt;=",
                                 "" + format(publishDateRange[0]));
        }

        if (publishDateRange[1] != null) {
            appendSearchCriteria(msgBuff, "Publish date&lt;=",
                                 "" + format(publishDateRange[1]));
        }


        Date[] dataDateRange = request.getDateRange(ARG_FILE_DATADATE_FROM,
                                                    ARG_FILE_DATADATE_TO, null,
                                                    null);

        if (dataDateRange[0] != null) {
            appendSearchCriteria(msgBuff, "Publish date&gt;=",
                                 "" + format(dataDateRange[0]));
        }

        if (dataDateRange[1] != null) {
            appendSearchCriteria(msgBuff, "Publish date&lt;=",
                                 "" + format(dataDateRange[1]));
        }

        //find and create the files
        /** 
            e.g.:
        GsacResource site = theSiteForThisFile; may be null
        String type = someType;
        GsacFile resource = new GsacFile(resourceId,
                                    new FileInfo(filePath, fileSize, md5),
                                    site,
                                    publishTime, fromTime, toTime,
                                    toResourceType(type));

                                    response.addResource(resource);
        **/
        setSearchCriteriaMessage(response, msgBuff);
    }

    

    /**
     * CHANGEME
     * This takes the resource id that is used to identify files and
     * creates a GsacFile object
     *
     * @param resourceId file id
     *
     * @return GsacFile
     *
     * @throws Exception On badness
     */
    public GsacResource getResource(String resourceId) throws Exception {
        return null;
    }



    /**
     * Create the list of resource types that are shown to the user. This is
     * called by the getDefaultCapabilities
     *
     * @return resource types
     */
    public List<ResourceType> doGetResourceTypes() {
        List<ResourceType> resourceTypes = new ArrayList<ResourceType>();
        resourceTypes.add(new ResourceType("rinex","RINEX Files"));
        resourceTypes.add(new ResourceType("qc","QC Files"));
        return resourceTypes;
    }




    /**
     * helper method
     *
     * @return sitemanager
     */
    public @MACRO.PREFIX@SiteManager getSiteManager() {
        return (@MACRO.PREFIX@SiteManager) getRepository().getResourceManager(GsacSite.CLASS_SITE);
    }


}
