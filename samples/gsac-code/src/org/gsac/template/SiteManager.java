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

/* CHANGE above, make sure that both lines show your GSAC package name */

import ucar.unidata.util.Misc;
import ucar.unidata.util.StringUtil;

import org.gsac.gsl.*;
import org.gsac.gsl.model.*;
import org.gsac.gsl.metadata.*;
import org.gsac.gsl.util.*;
import org.gsac.gsl.metadata.gnss.*;
import org.gsac.gsl.output.HtmlOutputHandler;
import org.gsac.gsl.ramadda.sql.Clause;
import org.gsac.gsl.ramadda.sql.SqlUtil;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.text.DateFormat;


/**
 * Handles all of the site related repository requests
 * The main entry point is the {@link #doGetQueryCapabilities} 
 * and   {@link #handleRequest} methods.
 * Look for the CHANGEME comments
 * 
 *
 * @author   GSAC Development Team
 */
public class @MACRO.PREFIX@SiteManager extends SiteManager {

    
    /**
     * ctor
     *
     * @param repository the repository
     */
    public @MACRO.PREFIX@SiteManager(@MACRO.PREFIX@Repository repository) {
        super(repository);
    }


    /**
     CHANGEME
     * Get the extra site search capabilities. 
     *
     * @return site search capabilities
     */
    public List<Capability> doGetQueryCapabilities() {
        List<Capability> capabilities = new ArrayList<Capability>();
        /*
          you can use the default site capabilities:
          addDefaultCapabilities(capabilities);
          or add you own, e.g.:
          Add in an example fruit enumerated query capability
          String[]values = {"banana","apple","orange"};
          Arrays.sort(values);
          capabilities.add(new Capability("fruit", "Fruit Label", values, true));
          See org.gsac.gsl.GsacSiteManager for how the default capabilities are created
        */
        return capabilities;
    }


    /** 
        This is the main entry point for handling queries
     **/
    public void handleRequest(GsacRequest request, GsacResponse response)
            throws Exception {
        //CHANGEME 

        /*
          if you call the base class handleRequest method it will make a database query
          from columns and clauses that you can specify in later methods
         */
        //        super.handleRequest(request, response);

        //Or you can handle the request directly here


        StringBuffer msgBuff = new StringBuffer();

        /**
           Here's how to access the arguments
           if (request.defined(ARG_NORTH)) {
                request.get(ARG_NORTH, 0.0);
           }
           ARG_SOUTH,ARG_EAST, ARG_WEST
        */

        //Create new GsacSite objects and call response.addResource
        //        GsacSite site = new GsacSite(....);
        //response.addResource(site);

        //This just sets the message shown to the user
        setSearchCriteriaMessage(response, msgBuff);
    }


    /**
     * CHANGEME
     * create and return the resource (site) identified by the given resource id
     *
     * @param resourceId resource id. This isn't the resource code but actually the monument id
     *
     * @return the resource or null if not found
     *
     * @throws Exception on badness
     */
    public GsacResource getResource(String resourceId) throws Exception {
        /* e.g.:
        //Here is some example code from Unavco for making a query

        Clause clause = Clause.eq(Tables.MV_DAI_PRO.COL_MON_ID,
                                  new Integer(resourceId).intValue());
        Statement statement = getDatabaseManager().select(
                                                          getResourceSelectColumns(), 
                                                          clause.getTableNames(), clause);
        try {
            ResultSet results = statement.getResultSet();
            if (!results.next()) {
                results.close();
                return null;
            }
            GsacSite site = (GsacSite) makeResource(results);
            results.close();
            return site;
        } finally {
            getDatabaseManager().closeAndReleaseConnection(statement);
        }
        */
        return  null;
    }




    /**
     * get all of the metadata for the given site
     *
     * @param gsacResource resource
     *
     * @throws Exception On badness
     */
    public void doGetMetadata(int level, GsacResource gsacResource) throws Exception {
        //The Unavco repository adds in GnssEquipment metadata and other things
    }


    /**
     * Get the site group list. This is used by the addDefaultSiteCapabilities 
     *
     * @return resource group list
     */
    public List<ResourceGroup> doGetResourceGroups() {
        List<ResourceGroup> groups = new ArrayList<ResourceGroup>();
        /**
           CHANGEME
        groups.add(new ResourceGroup("group1","Group 1"));
        groups.add(new ResourceGroup("group2", "Group 2"));
        groups.add(new ResourceGroup("group3","Group 3"));
        Collections.sort((List) groups);
        */
        return groups;
    }


    /*************************************************************************************************
     * The code below here is used to provide database query information to the base GsacResourceManager class
    *************************************************************************************************/



    /**
     * Get a comma separated list of the columns that are selected on a search
     *
     * @param request the request
     *
     * @return comma delimited fully qualified column names to select on
     */
    public String getResourceSelectColumns() {
        return  SqlUtil.comma(new String[] {
                    "YourSiteTable.column1",
                    "YourSiteTable.column2",
                    "etc"
                });
    }


    /**
     * An example method that shows how to use the Clause class to assemble a set of database
     * search clauses from the URL arguments
     *
     * @param request the resquest
     * @param response the response
     * @param msgBuff buffer to append search criteria to
     *
     * @return list of clauses for selecting sites
     */
    public List<Clause> getResourceClauses(GsacRequest request,
                                           GsacResponse response,
                                           List<String> tableNames,
                                           StringBuffer msgBuff) {
        List<Clause> clauses = new ArrayList();
        String latCol = "replace me with correct column name";
        String lonCol = "replace me with correct column name";
        if (request.defined(ARG_NORTH)) {
            clauses.add(Clause.le(latCol,
                                  request.get(ARG_NORTH, 0.0)));
            appendSearchCriteria(msgBuff, "north&lt;=",
                                 "" + request.get(ARG_NORTH, 0.0));
        }
        if (request.defined(ARG_SOUTH)) {
            clauses.add(Clause.ge(latCol,
                                  request.get(ARG_SOUTH, 0.0)));
            appendSearchCriteria(msgBuff, "south&gt;=",
                                 "" + request.get(ARG_SOUTH, 0.0));
        }
        if (request.defined(ARG_EAST)) {
            clauses.add(Clause.le(lonCol,
                                  request.get(ARG_EAST, 0.0)));
            appendSearchCriteria(msgBuff, "east&lt;=",
                                 "" + request.get(ARG_EAST, 0.0));
        }
        if (request.defined(ARG_WEST)) {
            clauses.add(Clause.ge(lonCol,
                                  request.get(ARG_WEST, 0.0)));
            appendSearchCriteria(msgBuff, "west&gt;=",
                                 "" + request.get(ARG_WEST, 0.0));
        }


        List   args         = null;
        //Add in the site type, status, etc
        /*
        if (request.defined(GsacArgs.ARG_SITE_TYPE)) {
            args = (List<String>) request.getDelimiterSeparatedList(
                                                                    GsacArgs.ARG_SITE_TYPE);
            clauses.add(Clause.or(Clause.makeStringClauses("TODO: fully qualified db column name",
                                                           args)));
            addSearchCriteria(msgBuff, argValues[2], args);
          }
        */


        //Add in the site code and site name queries
        /*
        addStringSearch(request, ARG_SITECODE, ARG_SITECODE_SEARCHTYPE,
                        msgBuff, "Site Code",
                        "TODO: fully qualified db column name", clauses);
        addStringSearch(request, ARG_SITENAME, ARG_SITENAME_SEARCHTYPE,
                        msgBuff, "Site Name",
                        Tables.SITE_INFORMATION.COL_SITE_NAME, clauses);

        */


        /*
        //other search arguments
        if (request.defined(ARG_SITE_GROUP)) {
            //...
        }
        if (request.defined(ARG_SITE_ID)) {
            //....
            //args = (List<String>) request.getDelimiterSeparatedList(GsacArgs.ARG_SITE_ID);
            //...
        }
        */

        return clauses;
    }






    /**
     * Get the order by clause
     *
     * @param request the request
     *
     * @return order by clause
     */
    public String getResourceOrder(GsacRequest request) {
        //e.g.:
        //        return  " ORDER BY  " + "YourSiteTable.sitecode" + " ASC ";
        return null;
    }


    /**
     * CHANGEME
     * Create a single site from the DB results
     *
     * @param results db results
     *
     * @return the site
     *
     * @throws Exception on badness
     */
    public GsacResource makeResource(ResultSet results) throws Exception {
        return null;
        /** e.g.:
        int    colCnt     = 1;
        int    monId      = results.getInt(colCnt++);
        String fourCharId = results.getString(colCnt++);
        String name       = results.getString(colCnt++);
        double latitude   = results.getDouble(colCnt++);
        double longitude  = results.getDouble(colCnt++);
        double elevation  = results.getDouble(colCnt++);
        String type       = results.getString(colCnt++);
        if (type == null) {
            type = "";
        }
        String  status = results.getString(colCnt++);
        String  groups = results.getString(colCnt++);

        GsacSite site = new GsacSite("" + monId, fourCharId, name,
                                  latitude, longitude, elevation);
        site.setType(new ResourceType(type));
        if ((groups != null) && (groups.trim().length() > 0)) {
            List<String> toks = new ArrayList<String>();
            for (String tok : groups.split(",")) {
                toks.add(tok.trim());
            }
            Collections.sort(toks);
            for (String tok : (List<String>) toks) {
                site.addResourceGroup(new ResourceGroup(tok));
            }
        }

        //Add icons based on type
        if (type.toLowerCase().equals(SITETYPE_CAMPAIGN)) {
            site.addMetadata(
            new IconMetadata(
                    "http://www.unavco.org/data/gnss/lib/DAI/images/icon1.png"));
        } else {
            site.addMetadata(
            new IconMetadata(
                    "http://www.unavco.org/data/gnss/lib/DAI/images/icon1.png"));
        }
        return site;
        */
    }





}
