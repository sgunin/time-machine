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

package gov.nasa.cddis.gsac;


import org.gsac.gsl.*;
import org.gsac.gsl.metadata.*;
import org.gsac.gsl.model.*;
import org.gsac.gsl.output.HtmlOutputHandler;
import org.gsac.gsl.util.*;


import org.ramadda.sql.Clause;
import org.ramadda.sql.SqlUtil;

import ucar.unidata.util.Misc;
import ucar.unidata.util.StringUtil;
import ucar.unidata.xml.XmlUtil;


import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;


/**
 * Cddis site manager. This class handles the site searches. It also creates the Site query capabilities.
 *
 * @author   Jeff McWhirter
 */
public class CddisSiteManager extends SiteManager implements CddisArgs {



    /** _more_ */
    public static final String MODE_SITECODE = "mode.sitecode";

    /** _more_ */
    public static final String MODE_SITENAME = "mode.sitename";

    /** _more_ */
    public static final String MODE_DOMESNUMBER = "mode.domesnumber";

    /** column name for the common columns across the different site tables */
    public static final String COL_SITE_NAME = "site_name";

    /** column name for the common columns across the different site tables */
    public static final String COL_LATITUDE_DECIMAL = "latitude_decimal";

    /** column name for the common columns across the different site tables */
    public static final String COL_LONGITUDE_DECIMAL = "longitude_decimal";

    /** column name for the common columns across the different site tables */
    public static final String COL_STATE = "state";

    /** column name for the common columns across the different site tables */
    public static final String COL_COUNTRY = "country";

    /** column name for the common columns across the different site tables */
    public static final String COL_REGION = "region";




    /**
     *   info for creating the query capabilities that are string based
     *   The CddisSearchInfo class holds the site type, the URL argument, the column to search on,
     *   the label, the search type and what group the capability should be added to
     */
    private CddisSearchInfo[] SEARCH_STRINGS = { new CddisSearchInfo(
                                                   CddisType.TYPE_NAME_SLR,
                                                   ARG_SLR_STATION,
                                                   Tables.SITE_INFO_SLR.COL_STATION,
                                                   "SLR Station",
                                                   Capability.TYPE_STRING,
                                                   CAPABILITY_GROUP_ADVANCED),
            new CddisSearchInfo(CddisType.TYPE_NAME_VLBI, ARG_VLBI_STATION,
                                Tables.SITE_INFO_VLBI.COL_STATION,
                                "VLBI Station", Capability.TYPE_STRING,
                                CAPABILITY_GROUP_ADVANCED), };


    /** info for creating the boolean query capabilities */
    private CddisSearchInfo[] SEARCH_BOOLEANS = { new CddisSearchInfo(
                                                    CddisType.TYPE_NAME_GNSS,
                                                    ARG_HIGHRATE,
                                                    Tables.SITE_INFO_GNSS.COL_HIGH_RATE,
                                                    "GNSS High Rate",
                                                    Capability.TYPE_BOOLEAN,
                                                    CAPABILITY_GROUP_ADVANCED),
            new CddisSearchInfo(CddisType.TYPE_NAME_GNSS, ARG_HOURLY,
                                Tables.SITE_INFO_GNSS.COL_HOURLY,
                                "GNSS Hourly", Capability.TYPE_BOOLEAN,
                                CAPABILITY_GROUP_ADVANCED),
            new CddisSearchInfo(CddisType.TYPE_NAME_GNSS, ARG_GLONASS,
                                Tables.SITE_INFO_GNSS.COL_GLONASS,
                                "GNSS GLONASS", Capability.TYPE_BOOLEAN,
                                CAPABILITY_GROUP_ADVANCED) };

    /**
     * ctor
     *
     * @param repository the repository
     */
    public CddisSiteManager(CddisRepository repository) {
        super(repository);
    }


    /**
     * This method does all of the work of processing the queries
     *
     * @param request the request
     * @param response the response
     *
     * @throws Exception on badness
     */
    public void handleRequest(GsacRequest request, GsacResponse response)
            throws Exception {

        /*
          Check for searches that are coming from the federated search.
          If the user specified a site group or a site status then we don't do anything
          since CDDIS does not have groups or statuses
        */
        if (request.defined(ARG_SITE_GROUP)
                || request.defined(ARG_SITE_STATUS)) {
            return;
        }

        //get the types for this request. Default is all 4 types - gnss, doris, vlbi and slr
        List<CddisType> siteTypes = CddisType.getSiteTypes(request);

        //If we did not find any of the cddis types and there was a type argument
        //that means we are in a federated search where the used specified one or more types,
        //none of which are cddis types
        if ((siteTypes.size() == 0) && request.defined(ARG_SITE_TYPE)) {
            return;
        }

        int          count   = 0;
        StringBuffer msgBuff = new StringBuffer();


        //If there are specific sites specified then just find them and return
        if (request.defined(ARG_SITE_ID)) {
            for (String siteId :
                    (List<String>) (List<String>) request.getList(
                        ARG_SITE_ID)) {
                appendSearchCriteria(msgBuff, "Site Id=", siteId);
                GsacSite site = (GsacSite) getResource(siteId);
                if (site != null) {
                    response.addResource(site);
                }
            }
            setSearchCriteriaMessage(response, msgBuff);
            return;
        }

        Hashtable<String, List<Clause>> clauseMap = new Hashtable<String,
                                                        List<Clause>>();
        boolean haveDoneSpecial = false;



        //First, go through each of the types and get the query clauses.
        //If any of them added type specific search then we only search for that type
        //So we clear out the clauses and just use the one

        for (CddisType type : siteTypes) {
            boolean[] addedAnySpecificTypeClauses = { false };
            List<Clause> clauses = getSiteClauses(request, response, type,
                                       addedAnySpecificTypeClauses, msgBuff);
            //If we are searching for a specific type's field then we just narrow the search to that type
            if (addedAnySpecificTypeClauses[0]) {
                if (haveDoneSpecial) {
                    throw new IllegalArgumentException(
                        "Error: You are trying to do searches for 2 different site types");

                }
                haveDoneSpecial = true;
                clauseMap       = new Hashtable<String, List<Clause>>();
            }
            if (addedAnySpecificTypeClauses[0] || !haveDoneSpecial) {
                clauseMap.put(type.getType(), clauses);
            }
        }


        //Now, go through the sites. If there is a clause for that type then do the search
        //We also keep track of the count of the results so we handle the offset and limit
        for (CddisType type : siteTypes) {
            List<Clause> clauses = clauseMap.get(type.getType());
            if (clauses == null) {
                continue;
            }
            if (siteTypes.size() != CddisType.TYPES.length) {
                appendSearchCriteria(msgBuff, "Site Type=", type.getType());
            }
            count += handleSiteRequest(request, response, type, clauses,
                                       msgBuff, count);
        }
        setSearchCriteriaMessage(response, msgBuff);
    }


    /**
     * This does the real work for a particular site type
     *
     * @param request the request
     * @param response the response
     * @param type the type we are looking for (e.g., gnss, doris, slr, vlbi)
     * @param clauses the site search clauses
     * @param msgBuff search criteria buff
     * @param count How many so far
     *
     * @return how many sites were found
     *
     * @throws Exception on badness
     */
    private int handleSiteRequest(GsacRequest request, GsacResponse response,
                                  CddisType type, List<Clause> clauses,
                                  StringBuffer msgBuff, int count)
            throws Exception {
        int    limit           = request.getLimit();
        int    offset          = request.getOffset();

        String columnsToSelect = type.getSiteColumns();

        //Make the main and clause
        Clause       clause     = Clause.and(clauses);

        List<String> tableNames = new ArrayList<String>();

        //Get all of the table names used by the clauses
        tableNames.add(type.getSiteTable());
        tableNames = clause.getTableNames(tableNames);

        //Do the select
        Statement statement = getDatabaseManager().select(columnsToSelect,
                                  tableNames, clause,
                                  getResourceSelectSuffix(request), -1);
        //Iterate on the results (note: the Iterator takes care of closing and release the DB resources)
        SqlUtil.Iterator iter  = SqlUtil.getIterator(statement, 0, -1);
        int              myCnt = 0;
        //TODO: take into account the offset and limit
        while (true) {
            GsacSite site = makeBaseSite(iter, type);
            if (site == null) {
                break;
            }
            myCnt++;
            response.addResource(site);
        }
        return myCnt;
    }


    /**
     * create the site if there is anything there
     *
     * @param iter iterator
     * @param type what type of site
     *
     * @return The site or null
     *
     * @throws Exception on badness
     */
    private GsacSite makeBaseSite(SqlUtil.Iterator iter, CddisType type)
            throws Exception {
        //Strip off the "table." prefix
        String siteCodeColumn = SqlUtil.unDot(type.getSiteCodeColumn());

        while (iter.getNext() != null) {
            ResultSet results  = iter.getResults();
            String    siteCode = results.getString(siteCodeColumn);
            //TODO: When selecting from the view we get nulls
            if (siteCode == null) {
                continue;
            }
            GsacSite site = makeSite(results, type);
            site.setShortName(siteCode);
            //This runs through the given columns in the resultset and adds a PropertyMetadata element for each
            addPropertyMetadata(results, site,
                                new String[] { Tables.SITE_INFO_SLR.COL_STATE,
                    Tables.SITE_INFO_SLR.COL_COUNTRY,
                    Tables.SITE_INFO_SLR.COL_REGION });
            //Add the site type specific metadata
            if (type.isGnss()) {
                addPropertyMetadata(
                    results, site,
                    new String[] { Tables.SITE_INFO_GNSS.COL_MONUMENT_NAME,
                                   Tables.SITE_INFO_GNSS.COL_DOMES_NUMBER,
                //                    Tables.SITE_INFO_GNSS.COL_GPS,
                Tables.SITE_INFO_GNSS.COL_GLONASS,
                //                    Tables.SITE_INFO_GNSS.COL_IGS,
                //                    Tables.SITE_INFO_GNSS.COL_GLOBAL,
                Tables.SITE_INFO_GNSS.COL_HIGH_RATE,
                Tables.SITE_INFO_GNSS.COL_HOURLY });
            } else if (type.isDoris()) {
                addPropertyMetadata(
                    results, site,
                    new String[] { Tables.SITE_INFO_DORIS.COL_DOMES_NUMBER });
            } else if (type.isSlr()) {
                addPropertyMetadata(results, site,
                                    new String[] {
                                        Tables.SITE_INFO_SLR.COL_STATION,
                                        Tables.SITE_INFO_SLR
                                            .COL_DOMES_NUMBER, Tables
                                            .SITE_INFO_SLR.COL_SITE_TYPE });
            } else if (type.isVlbi()) {
                addPropertyMetadata(results, site,
                                    new String[] {
                                        Tables.SITE_INFO_VLBI.COL_IVS_CODE,
                                        Tables.SITE_INFO_VLBI.COL_VLBI_NAME,
                                        Tables.SITE_INFO_VLBI.COL_STATION,
                                        Tables.SITE_INFO_VLBI
                                            .COL_DOMES_NUMBER, Tables
                                            .SITE_INFO_VLBI.COL_SITE_TYPE });
            }
            return site;
        }
        return null;
    }



    /**
     * Make the search clauses
     * @param request the resquest
     * @param response the response
     * @param type the type
     * @param addedAnySpecificTypeClauses any site specific clauses
     * @param msgBuff buffer to append search criteria to
     *
     * @return list of clauses for selecting sites
     */
    public List<Clause> getSiteClauses(GsacRequest request,
                                       GsacResponse response, CddisType type,
                                       boolean[] addedAnySpecificTypeClauses,
                                       StringBuffer msgBuff) {


        String       tableName      = type.getSiteTable();
        String       siteCodeColumn = type.getSiteCodeColumn();
        List         args           = null;
        List<Clause> clauses        = new ArrayList<Clause>();
        clauses.add(Clause.isNotNull(siteCodeColumn));
        addBBOXSearchCriteria(request, clauses,
                              tableName + "." + COL_LATITUDE_DECIMAL,
                              tableName + "." + COL_LONGITUDE_DECIMAL,
                              msgBuff);




        for (CddisSearchInfo info : SEARCH_BOOLEANS) {
            if ( !info.getSiteType().equals(type.getType())) {
                continue;
            }
            if (request.defined(info.getUrlArg())) {
                addedAnySpecificTypeClauses[0] = true;
                if (request.get(info.getUrlArg(), true)) {
                    appendSearchCriteria(msgBuff, info.getLabel() + "=",
                                         "true");
                    clauses.add(Clause.eq(info.getDbCol(), "Y"));
                } else {
                    appendSearchCriteria(msgBuff, info.getLabel() + "=",
                                         "false");
                    clauses.add(Clause.neq(info.getDbCol(), "Y"));
                }
            }
        }



        for (CddisSearchInfo info : SEARCH_STRINGS) {
            if ( !info.getSiteType().equals(type.getType())) {
                continue;
            }
            if (request.defined(info.getUrlArg())) {
                addedAnySpecificTypeClauses[0] = true;
                addStringSearch(request, info.getUrlArg(),
                                info.getUrlArg() + SEARCHTYPE_SUFFIX, false,
                                msgBuff, info.getLabel(), info.getDbCol(),
                                clauses);
            }
        }


        //Add in the site type, status, etc
        String[][] enumArgs = {
            { GsacExtArgs.ARG_REGION, tableName + "." + COL_REGION,
              "Region" },
            { GsacExtArgs.ARG_STATE, tableName + "." + COL_STATE, "State" }
        };

        for (String[] argValues : enumArgs) {
            if (request.defined(argValues[0])) {
                //There might be more than one argument and also it can be comma separated
                addEnumeratedSearch(request, argValues[0], argValues[1],
                                    argValues[2], msgBuff, clauses);
            }
        }

        //Check for the general site text search and just set the appropriate url args
        if (request.defined(ARG_SITE_TEXT)) {
            String text = request.get(ARG_SITE_TEXT, "");
            String searchType = request.get(ARG_SITE_TEXT
                                            + SEARCHTYPE_SUFFIX, "");
            String mode = request.get(ARG_SITE_TEXT_MODE, "");
            if (mode.equals(MODE_SITECODE)) {
                request.put(ARG_SITECODE, text);
                request.put(ARG_SITECODE_SEARCHTYPE, searchType);
            } else if (mode.equals(MODE_SITENAME)) {
                request.put(ARG_SITENAME, text);
                request.put(ARG_SITENAME_SEARCHTYPE, searchType);
            } else if (mode.equals(MODE_DOMESNUMBER)) {
                request.put(ARG_DOMES_NUMBER, text);
                request.put(ARG_DOMES_NUMBER + SEARCHTYPE_SUFFIX, searchType);
            }
        }

        //Add in the site code, site name  and domes number queries
        addStringSearch(request, ARG_SITECODE, ARG_SITECODE_SEARCHTYPE,
                        msgBuff, "Site Code", siteCodeColumn, clauses);

        addStringSearch(request, ARG_SITENAME, ARG_SITENAME_SEARCHTYPE,
                        msgBuff, "Site Name",
                        tableName + "." + COL_SITE_NAME, clauses);

        addStringSearch(request, ARG_DOMES_NUMBER,
                        ARG_DOMES_NUMBER + SEARCHTYPE_SUFFIX, msgBuff,
                        "DOMES Number", type.getDomesNumberColumn(), clauses);


        //Add the specific type clauses
        if (type.isGnss()) {}
        else if (type.isDoris()) {}
        else if (type.isSlr()) {
            if (request.defined(ARG_SLR_SITE_TYPE)) {
                addedAnySpecificTypeClauses[0] = true;
                args = (List<String>) request.getDelimiterSeparatedList(
                    ARG_SLR_SITE_TYPE);
                clauses.add(
                    Clause.or(
                        Clause.makeStringClauses(
                            Tables.SITE_INFO_SLR.COL_SITE_TYPE, args)));
                addSearchCriteria(msgBuff, "SLR Site Type", args);
            }
        } else if (type.isVlbi()) {
            if (request.defined(ARG_VLBI_SITE_TYPE)) {
                addedAnySpecificTypeClauses[0] = true;
                args = (List<String>) request.getDelimiterSeparatedList(
                    ARG_VLBI_SITE_TYPE);
                clauses.add(
                    Clause.or(
                        Clause.makeStringClauses(
                            Tables.SITE_INFO_VLBI.COL_SITE_TYPE, args)));
                addSearchCriteria(msgBuff, "VLBI Site Type", args);
            }
        }

        return clauses;
    }


    /**
     * Get the site from the database
     *
     * @param siteId site id. This is different for the different types.
     *
     * @return the site or null if not found
     *
     * @throws Exception on badness
     */
    public GsacResource getResource(String siteId) throws Exception {
        List<String> tuple = CddisType.getTypeAndFields(siteId);
        CddisType    type  = CddisType.getType(tuple.get(0));
        String       name  = tuple.get(1);
        Clause clause = Clause.eq(type.getSiteTable() + "." + COL_SITE_NAME,
                                  name);
        if (type.isGnss()) {
            clause =
                Clause.and(clause,
                           Clause.eq(Tables.SITE_INFO_GNSS.COL_MONUMENT_NAME,
                                     tuple.get(2)));
        }

        Statement statement =
            getDatabaseManager().select(type.getSiteColumns(),
                                        clause.getTableNames(), clause);
        try {
            SqlUtil.Iterator iter = SqlUtil.getIterator(statement, 0, -1);
            GsacSite         site = makeBaseSite(iter, type);
            iter.close();
            return site;
        } finally {
            getDatabaseManager().closeAndReleaseConnection(statement);
        }
    }



    /**
     * Makes the site id for the given site type
     *
     * @param type site type
     * @param name site name
     * @param results db result set
     *
     * @return site id
     *
     * @throws Exception on badness
     */
    private String makeSiteId(CddisType type, String name, ResultSet results)
            throws Exception {
        String extra = null;
        if (type.isGnss()) {
            extra = results.getString(
                SqlUtil.unDot(Tables.SITE_INFO_GNSS.COL_MONUMENT_NAME));
        } else if (type.isDoris()) {
            //            extra = results.getString(SqlUtil.unDot(Tables.SITE_INFO_GNSS.COL_MONUMENT_NAME));
        }
        return type.makeId(new String[] { name, extra });
    }



    /**
     * All sites have the same set of basic columns, e.g., name,lat,lon, etc.
     * This method creates the site from those columns
     *
     *
     * @param results result set
     * @param type site type
     *
     * @return the site
     *
     * @throws Exception on badness
     */
    public GsacSite makeSite(ResultSet results, CddisType type)
            throws Exception {
        //COL_SITE_NAME, COL_LATITUDE, COL_LONGITUDE, COL_LATITUDE_DECIMAL, COL_LONGITUDE_DECIMAL, COL_STATE, COL_COUNTRY, COL_REGION

        int      colCnt   = 1;
        String   siteName = results.getString(colCnt++);
        GsacSite site     = null;
        //skip the non-decimal lat and lon
        colCnt += 2;

        double latitude  = results.getDouble(colCnt++);
        double longitude = results.getDouble(colCnt++);
        String state     = results.getString(colCnt++);
        String country   = results.getString(colCnt++);
        String region    = results.getString(colCnt++);
        double elevation = 0;


        site = new GsacSite(makeSiteId(type, siteName, results), "FOOBAR",
                            siteName, latitude, longitude, elevation);
        site.setType(new ResourceType(type.getType(),
                                      type.getType().toUpperCase()));
        return site;
    }



    /**
     * get all of the metadata for the given site
     *
     *
     * @param level metadata level
     * @param gsacSite site
     *
     * @throws Exception On badness
     */
    public void doGetMetadata(int level, GsacResource gsacSite)
            throws Exception {
        //The unavcorepository adds in GnssEquipment metadata and other things
    }


    /**
     * _more_
     *
     * @param capabilities _more_
     */
    public void addDefaultCapabilities(List<Capability> capabilities) {
        String              help = HtmlOutputHandler.stringSearchHelp;
        Capability          siteCode;
        List<ResourceGroup> siteGroups    = doGetResourceGroups();
        List<IdLabel>       siteCodeModes = IdLabel.toList(new String[][] {
            { MODE_SITECODE, "Site Code" }, { MODE_SITENAME, "Site Name" },
            { MODE_DOMESNUMBER, "DOMES Number" },
        });

        Capability[] dflt = { initCapability(new Capability(ARG_SITE_TYPE,
                                "Site Type", new ArrayList<IdLabel>(),
                                true), CAPABILITY_GROUP_SITE_QUERY,
                                       "Type of the site", null,
                                       makeVocabulary(ARG_SITE_TYPE)),
                              siteCode = initCapability(
                                  new Capability(
                                      ARG_SITE_TEXT, "Search Field",
                                      Capability.TYPE_STRING_BUTTONS,
                                      siteCodeModes,
                                      false), CAPABILITY_GROUP_SITE_QUERY,
                                          "Short name of the site",
                                          "Short name of the site. " + help),
                              initCapability(new Capability(ARG_SITE_STATUS,
                                  "Site Status", new ArrayList<IdLabel>(),
                                  true), CAPABILITY_GROUP_ADVANCED, "", "",
                                         makeVocabulary(ARG_SITE_STATUS)),
                              ((siteGroups.size() == 0)
                               ? null
                               : initCapability(
                                   new Capability(
                                       makeUrlArg(ARG_SUFFIX_GROUP),
                                       "Site Group",
                                       IdLabel.toList(siteGroups),
                                       true), CAPABILITY_GROUP_ADVANCED,
                                           null)),
                              initCapability(new Capability(ARG_BBOX, "Bounds", Capability
                                  .TYPE_SPATIAL_BOUNDS), CAPABILITY_GROUP_SITE_QUERY, "Spatial bounds within which the site lies") };
        siteCode.setBrowse(true);
        for (Capability capability : dflt) {
            if (capability != null) {
                capabilities.add(capability);
            }
        }
    }


    /**
     * Get the extra site search capabilities.
     *
     * @return site search capabilities
     */
    public List<Capability> doGetQueryCapabilities() {
        Capability capability;

        String[]   values;
        String[][] tuples;

        try {
            List<Capability> capabilities = new ArrayList<Capability>();
            addDefaultCapabilities(capabilities);

            String[][] regions = {
                { "AF", "Africa" }, { "ANT", "Antarctica" },
                { "ASIA", "Asia" }, { "ATL", "Atlantic Ocean" },
                { "AUS", "Australia" }, { "CA", "California" },
                { "EU", "Europe" }, { "NA", "North America" },
                { "PAC", "Pacific Ocean" }, { "SA", "South America" }
            };
            addLabels(GsacExtArgs.ARG_REGION, regions);
            capabilities.add(capability =
                new Capability(GsacExtArgs.ARG_REGION, "Region",
                               IdLabel.toList(regions), true));

            capability.setGroup(CAPABILITY_GROUP_ADVANCED);
            Statement statement =
                getDatabaseManager().select(
                    distinct(Tables.SITE_INFORMATION.COL_STATE),
                    Tables.SITE_INFORMATION.NAME);


            values = SqlUtil.readString(SqlUtil.getIterator(statement), 1);
            Arrays.sort(values);
            tuples = new String[values.length][];
            for (int i = 0; i < values.length; i++) {
                String id = values[i];
                String value = getRepository().getProperty("state."
                                   + id.toLowerCase());
                if (value == null) {
                    value = id;
                }
                tuples[i] = new String[] { id, value };
                addLabel(GsacExtArgs.ARG_STATE, id, value);
            }
            capabilities.add(capability =
                new Capability(GsacExtArgs.ARG_STATE, "State",
                               IdLabel.toList(tuples), true));

            capability.setGroup(CAPABILITY_GROUP_ADVANCED);
            String     urlArg;


            String[][] vlbiSiteTypes = {
                { "F", "Fixed" }, { "M", "Mobile" }
            };
            addLabels(ARG_VLBI_SITE_TYPE, vlbiSiteTypes);
            capabilities.add(capability = new Capability(ARG_VLBI_SITE_TYPE,
                    "VLBI Site Type", IdLabel.toList(vlbiSiteTypes), true));

            capability.setGroup(CAPABILITY_GROUP_ADVANCED);


            String[][] slrSiteTypes = {
                { "F", "Fixed" }, { "M", "Mobile" }, { "E", "Engineering" }
            };
            addLabels(ARG_SLR_SITE_TYPE, slrSiteTypes);
            capabilities.add(capability = new Capability(ARG_SLR_SITE_TYPE,
                    "SLR Site Type", IdLabel.toList(slrSiteTypes), true));
            capability.setGroup(CAPABILITY_GROUP_ADVANCED);

            //            capabilities.add(capability = new Capability(ARG_DOMES_NUMBER,
            //                    "Domes Number", Capability.TYPE_STRING));
            //            capability.setGroup(CAPABILITY_GROUP_SITE_QUERY);

            for (CddisSearchInfo info : SEARCH_STRINGS) {
                capabilities.add(capability =
                    new Capability(info.getUrlArg(), info.getLabel(),
                                   Capability.TYPE_STRING));
                capability.setGroup(info.getGroup());
            }

            for (CddisSearchInfo info : SEARCH_BOOLEANS) {
                capabilities.add(capability =
                    Capability.makeBooleanCapability(info));
                capability.setGroup(info.getGroup());
            }
            return capabilities;
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }


    }


}
