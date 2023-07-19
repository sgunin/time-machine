/*
 * Copyright 2013; 2014-2016 UNAVCO, 6350 Nautilus Drive, Boulder, CO 80301
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

package org.gsac.gsl.output.site;


import org.gsac.gsl.*;
import org.gsac.gsl.metadata.*;
import org.gsac.gsl.metadata.gnss.*;
import org.gsac.gsl.model.*;
import org.gsac.gsl.output.*;
import org.gsac.gsl.util.*;

import ucar.unidata.util.HtmlUtil;
import ucar.unidata.util.Misc;
import ucar.unidata.util.TwoFacedObject;

import java.io.*;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.GregorianCalendar;
import java.util.ArrayList;
import java.util.Date;
import java.lang.Double;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;


/* 
 * Class description: makes site and instrument information in the "GSAC Full CSV" format.
 *
 * The format convention is the "Standard CSV File Format" ; see http://www.unavco.org/data/gsacws/docs/UNAVCO_standard_CSV_format.html 
 * and see  http://ramadda.org/repository/entry/show/Home/RAMADDA+Information/Development/CF+for+CSV?entryid=23652828-c6f4-482b-bb2f-041dae14542e
 *
 * This GSAC Full Csv format file has values for:
 * ID,station name,latitude,longitude,ellip. height,monument description,IERSDOMES,db record start date,db record stop date,antenna type,dome type,antenna SN,Ant dZ,Ant dN,Ant dE,receiver type, firmware version,receiver SN,sample interval, site count
 * The fields are separated with commas. Leading and trailing space-characters adjacent to comma field separators are ignored.
 * NO commas are allowed here inside a field value; this usually pertains to only site descriptions in geodesy data. Any commas in such a field are replaced with " ".
 *
 * To conform with other GSAC repositories we ask you not to revise this format.  You are very welcome to make a new similar but altered 
 * handler .java class for your use.  Add its call to the class file SiteManager.java and rebuild GSAC.  Do not commit your core 
 * GSAC code changes in this case into GSAC without consulting UNAVCO.
 * For bug reports and suggested improvments please contact UNAVCO.
 *
 * About ellipsoidal height: use ellipsoid height, not elevation (hgt above some geoid model surface),  the usual case for GSAC geodesy GPS instrument sites:
 *
 * @author  SK Wier   Nov 30, 2012; Dec 7, 2012; 
 * revised, new name to avoid conflict with another similar class now reinstituted. Feb. 25 2013. SKW.
 * @author  SK Wier   Nov 15, 2013 ; 21 Nov 2013 added sample interval and SwVer to output.
 * revised: added code to remove commas in char String values, which ruins csv formatting. Feb 6,7 2014. remove SwVer from output.
 * revised: SKW revised 23 May 2014.
 * revised: SKW revised 27 Aug 2014.  add city state country x y z
 * revised: SKW correct the code for metpackname, metpackSN  2 Apr 2015
 * Add NEW value 'networks' string at end of line (before counter) SKW Nov 04 2105
 */
public class CsvFullSiteOutputHandler extends GsacOutputHandler {

    String id ="";
    String name ="";
    String oldname ="";
    String latitude ="";
    String longitude ="";
    String ellipsoidalheight ="";
    String Xcoordinate="";
    String Ycoordinate="";
    String Zcoordinate="";
    String mondesc ="";
    String iersdomes ="";
    String cdpnum ="";
    String indate ="";
    String country= "";
    String sampIntstr =null;
    String state ="";
    String city   ="";
    String Xstr ="";
    String Ystr ="";
    String Zstr ="";
    String agencyname ="";
    String metpackname ="";
    String metpackSN ="";

    int sitecount=0;

    /** output id */
    public static final String OUTPUT_SITE_FULL_CSV = "sitefull.csv";

    /** date formatter  note NO "T", and we do not know if it is UTC time or what.*/
    /* somehow the Z here results in a value like "2001-07-11 00:00:00 -0600" with no Z */
    private SimpleDateFormat dateTimeFormatnoT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");

    /** formatter for GNSS antenna value of offset of phase center from instrument center */
    private DecimalFormat offsetFormat = new DecimalFormat("####0.####");

    /** _more_          */
    private DecimalFormat latLonFormat = new DecimalFormat("####0.####");

    /**  to format elevation or ellipsoidal height values sometimes called elevation in GSAC code.  */
    private DecimalFormat elevationFormat = new DecimalFormat("####0.##");

    /**
     * replace any commas in the input string 's' with " " to prevent contamination of the csv data line with non-separator commas. 
     *
     * @param s  string to remove commas from
     *
     * @return _more_
     */
    private String cleanString(String s) {
        s = s.replaceAll(",", " ");
        // or could replace with s = s.replaceAll(",", "_COMMA_");
        return s;
    }

    /**
     * ctor; and make file name for result file.
     *
     * @param gsacRepository the repository
     * @param resourceClass _more_
     */
    public CsvFullSiteOutputHandler (GsacRepository gsacRepository, ResourceClass resourceClass) {
        super(gsacRepository, resourceClass);
        getRepository().addOutput(getResourceClass(), new GsacOutput(this, OUTPUT_SITE_FULL_CSV, "GSAC Sites info, full csv", "/fullsites.csv", true)); 

        // note with  .csv extension, some browsers want to show the results in Excel or other packages which is NOT ! useful for geodesy.
    }


    /**
     * handle the request: format the sites' information in  csv lines per site and per equipment session
     *
     * @param request the request
     * @param response the response to write to
     *
     * @throws Exception on badness
     */
    public void handleResult(GsacRequest request, GsacResponse response)
            throws Exception {
        // System.out.println("       CsvFullSiteOutputHandler.java output handler calls handleResult" );

        // The next line sets output mime type (how browser handles it; text lets user see query results in a browser, and can also get form the gsac client with file name sitesfull.csv)
        // But in this case where file extension is ".csv"  the browser may probably ignore this 'text' value and try to load the file in some doc processor like Excel.
        response.startResponse(GsacResponse.MIME_CSV);
        PrintWriter pw = response.getPrintWriter();
        addHeader(pw);
        //Get all the sites in the results (response) from the GSAC site query made by the user: 
        List<GsacSite> sites = response.getSites();

        sitecount=0; // LOOK FIX may be a bug; resetting sitecount to zero in long lists

        //For each site, get and append the information :
        for (GsacSite site : sites) {
            //Call this to ensure that all of the metadata is added to the site
            getRepository().doGetFullMetadata(-1, site);
            addSiteIdentification(pw, site);
            addSiteLocation(pw, site);
            addSiteEquipment(pw, site);
        }
        response.endResponse();
    }


    /**
     *  header for this case.
     *
     * @param pw _more_
     */
    private void addHeader (PrintWriter pw) {
        pw.append("#fields=ID[type='string'],station_name[type='string'],latitude,longitude,ellip_height[unit='m'],monument_description[type='string'],IERSDOMES[type='string'],session_start_time[type='date' format='yyyy-MM-ddTHH:mm:ss zzzzz'],session_stop_time[type='date' format='yyyy-MM-ddTHH:mm:ss zzzzz'],antenna_type[type='string'],dome_type[type='string'],antenna_SN[type='string'],Ant_dZ,Ant_dN,Ant_dE,receiver_type[type='string'],firmware_version[type='string'],receiver_SN[type='string'],sample_interval,city_locale[type='string'],state_prov[type='string'],country[type='string'],X,Y,Z,agencyname[type='string'],metpackname[type='string'],metpackSN[type='string'],networks[type='string'],latest_data_time[type='date' format='yyyy-MM-dd HH:mm:ss'],site_count\n");
        pw.append("#   Generated by "+ getRepository().getRepositoryName()  + " on "+ iso8601UTCDateTime(new Date()) + " \n");
        pw.append("#   Missing times (no characters in ,,) may mean 'not removed' or 'no change.' The CSV convention for point data is 'CF-for-CSV' or 'Well Structured CSV.' \n");
        pw.append("#   See http://ramadda.org/repository/entry/show/Home/RAMADDA+Information/Development/CF+for+CSV?entryid=23652828-c6f4-482b-bb2f-041dae14542e \n");
        // or see https://www.google.com/search?client=ubuntu&channel=fs&q=csf+cf+fopr+point+data+ramadda&ie=utf-8&oe=utf-8 \n");
    }

    /**
     * get site id details for this format style
     *
     * @param pw _more_
     * @param site _more_
     *
     * @throws Exception _more_
     */
    private void addSiteIdentification(PrintWriter pw, GsacSite site)
            throws Exception {
        id = site.getShortName();
        // count distinct sites
        if ( id != oldname ) {
            sitecount++;
            oldname = id;
        }
        name = cleanString( site.getLongName() ); // cleanString removes unwanted commas in the station name which mess up the csv line

        /*  if ( sampIntstr==null ) {
             ; //sampIntstr ="";
        }
        else {
           ; //System.out.println("   CsvFullSiteOutputHandler() site "+id+" sample interval is as "+sampIntstr);
        } */
        city       =getProperty(site, GsacExtArgs.ARG_CITY, ""); 
        state      =getProperty(site, GsacExtArgs.ARG_STATE, ""); 
        country    =getProperty(site, GsacExtArgs.ARG_COUNTRY, ""); 
        Xstr       =getProperty(site, GsacExtArgs.SITE_TRF_X, ""); 
        Ystr       =getProperty(site, GsacExtArgs.SITE_TRF_Y, ""); 
        Zstr       =getProperty(site, GsacExtArgs.SITE_TRF_Z, ""); 
        mondesc    =getProperty(site, GsacExtArgs.SITE_METADATA_MONUMENTDESCRIPTION, ""); 
        sampIntstr =getProperty(site, GsacExtArgs.ARG_SAMPLE_INTERVAL, "");  // station not datafile sample interval
        // DEBUG debug looks correct System.out.println("   CsvFull site "+id+" sample int "+sampIntstr + " monum="+mondesc+"  zstr="+Zstr);

        agencyname =getProperty(site, GsacExtArgs.SITE_METADATA_NAMEAGENCY, ""); 

        //  must have first set this value in MySiteManager.java:
        //  as with site.addMetadata(new PropertyMetadata(GsacExtArgs.SITE_METADATA_IERDOMES, iersdomes,  "IERS DOMES" ))
        iersdomes =getProperty(site,  GsacExtArgs.SITE_METADATA_IERDOMES, "");

        // these next two are not put in results output file yet.
        //cdpnum =getProperty(site, GsacExtArgs.SITE_METADATA_CDPNUM, "");
        // Date date = site.getFromDate(); // when station started; will use equip session start date instead
        // if (date != null) { indate = iso8601UTCDateTime(date); }

    }

    /**
     * print results of site location for this format style
     *
     * @param pw _more_
     * @param site _more_
     *
     * @throws Exception _more_
     */
    private void addSiteLocation(PrintWriter pw, GsacSite site)
            throws Exception {
        List<GsacMetadata> politicalMetadata =
            site.findMetadata(
                new GsacMetadata.ClassMetadataFinder(
                    PoliticalLocationMetadata.class));
        PoliticalLocationMetadata plm = null;
        //Just use the first one
        if (politicalMetadata.size() > 0) {
            plm = (PoliticalLocationMetadata) politicalMetadata.get(0);
        }
        if (plm == null) {
            plm = new PoliticalLocationMetadata();
        }

        EarthLocation el = site.getEarthLocation();
        latitude =formatLocation(el.getLatitude())  ;
        longitude =formatLocation(el.getLongitude()) ;
        ellipsoidalheight =elevationFormat.format(el.getElevation()) ;
        if (el.hasXYZ()) {
            Xcoordinate= Double.toString(el.getX() );
            Ycoordinate= Double.toString(el.getY() );
            Zcoordinate= Double.toString(el.getZ() );
        } else {
            Xcoordinate="";
            Ycoordinate="";
            Zcoordinate="";
        }

    }


    /**
     *  from the input GSAC 'site' object, extract the value of the named field or API argument.
     *
     * @param site _more_
     * @param propertyId _more_
     * @param dflt _more_
     *
     * @return _more_
     */
    private String getProperty(GsacResource site, String propertyId,
                               String dflt) {
        List<GsacMetadata> propertyMetadata =
            (List<GsacMetadata>) site.findMetadata(
                new GsacMetadata.ClassMetadataFinder(PropertyMetadata.class));
        for (int i = 0; i < propertyMetadata.size(); i++) {
            PropertyMetadata metadata =
                (PropertyMetadata) propertyMetadata.get(i);
            if (metadata.getName().equals(propertyId)) {
                return metadata.getValue();
            }
        }
        return "";
    }



    /**
     * get  site equipment ('sessions') for this format style
     *
     * @param pw _more_
     * @param site _more_
     *
     * @throws Exception _more_
     */
    private void addSiteEquipment(PrintWriter pw, GsacSite site)
            throws Exception {
        String starttime ="";
        String stoptime ="";
        String antht ="";
        String antn ="";
        String ante ="";
        String anttype ="";
        String dome ="";
        String antsn ="";
        String rectype ="";
        String firmvers ="";
        String recsn ="";

        List<GsacMetadata> equipmentMetadata = site.findMetadata( new GsacMetadata.ClassMetadataFinder(GnssEquipment.class));
             
        // get equip details for one session (one site, one time interval, the instrumentation details:
        for (GsacMetadata metadata : equipmentMetadata) {
            GnssEquipment equipment = (GnssEquipment) metadata;
            //gotsession=1;

            Date toDate = null;
            if (equipment.hasReceiver()) {
                toDate = equipment.getToDate();
                starttime= getNonNullString(iso8601UTCDateTime( equipment.getFromDate()));
                stoptime= getNonNullString(iso8601UTCDateTime( equipment.getToDate()));

                // for rectype, handle case of value 'unknown' or 'not provided'
                    String answer = equipment.getReceiver();
                    answer = answer.replaceAll(",", " ");
                    if ( answer.contains("unknown") || answer.contains("not provided") || answer.equals("") || answer.equals(" ")) {
                       answer="";
                    }
                rectype = answer;

                // for recsn, handle case of value 'unknown' or 'not provided'
                    answer = equipment.getReceiverSerial();
                    answer = answer.replaceAll(",", " ");
                    if ( answer.contains("unknown") || answer.contains("not provided") || answer.equals("") || answer.equals(" ")) {
                       answer="";
                    }
                recsn= answer;

                // for firmvers, handle case of value 'unknown' or 'not provided'
                    answer = equipment.getReceiverFirmware();
                    answer = answer.replaceAll(",", " ");
                    if ( answer.contains("unknown") || answer.contains("not provided")  || answer.equals("none given") || answer.equals(" ") ) {
                       answer="";
                    }
                firmvers = answer;

                //swVer=equipment.getSwVer();
                //swVer=swVer.replaceAll(",", " ");

                // select the var sampIntstr to use in the output line:
                float esi=equipment.getSampleInterval(); // a float value
                if (esi > 0.0)  { 
                   String siStr= ""+esi ;
                   sampIntstr= siStr; 
                   //System.out.println("    but session sample interval at site "+id+"  is = "+sampIntstr);
                }
                // else use old value for site's sampIntstr
                //if (null==sampIntstr || sampIntstr=="") { sampIntstr=""+sampInt; } // replace value if not got from GsacExtArgs etc.
            }


            if (equipment.hasAntenna()) {
                double[] xyz = equipment.getXyzOffset();
                antht = offsetFormat.format(xyz[2]);
                if (antht.equals("0")) { antht = "0.0000"; }
                if (antht == null) { antht = "0.0000"; }
                antn = offsetFormat.format(xyz[1]);
                if (antn.equals("0")) { antn = "0.0000"; }
                if (antn == null) { antn = "0.0000"; }
                ante = offsetFormat.format(xyz[0]);
                if (ante.equals("0")) { ante = "0.0000"; }
                if (ante == null) { ante = "0.0000"; }
                anttype=getNonNullString(equipment.getAntenna());
                // for antsn, handle case of value 'unknown' or 'not provided'
                String answer = equipment.getAntennaSerial();
                answer = answer.replaceAll(",", " ");
                if ( answer.contains("unknown") || answer.contains("not provided") || answer.equals("none given") || answer.equals(" ")) {
                       answer="";
                }
                antsn = answer;
                dome = getNonNullString(equipment.getDome());
                starttime= getNonNullString(iso8601UTCDateTime( equipment.getFromDate()));
                stoptime= getNonNullString(iso8601UTCDateTime( equipment.getToDate()));
            }

            metpackname=    equipment.getMetpackname();
            if (metpackname == null) { metpackname=""; }
            metpackSN  =    equipment.getMetpackSerial();
            if (metpackSN == null) { metpackSN=""; }
            //System.out.println("GSAC:     CsvFullSiteOutputHandler() for site "+id+" metpack name="+metpackname+"  metpack SN="+metpackSN);

            /* from ops xml code:
            // networks in GSAC are stored in the site.getResourceGroups  Networks 
            int gotnet=0;
            List<ResourceGroup> networks = site.getResourceGroups();
            if ( networks != null) {
                //System.out.println("   list of networks size is "+ networks.size() );
                if ( networks.size() >= 1) {
                    for (ResourceGroup rg : networks) {
                        if ( rg != null) {
                            //System.out.println("   network is "+rg.getName() );
                            pw.append( XmlUtil.tag(XmlSiteLog.TAG_NETWORK, "", rg.getName() ) );
                            gotnet=1;
                        }
                    }
                }
            }
            */
            String networkstring ="";
            List<ResourceGroup> networks = site.getResourceGroups();
            if ( networks != null && networks.size() >= 1) {
                //System.out.println("  CsvFull:  list of networks size is "+ networks.size() );
                for (ResourceGroup rg : networks) {
                    if ( rg != null) {
                            //System.out.println("   network is "+rg.getName() );
                            networkstring +=  rg.getName() +";" ;
                        }
                    }
                // debug only System.out.println("   site "+id+"  network list is "+ networkstring );
            }

            
            String lasttimestr = "";
            // if the LatestData time exists and  is < this equipment session's stop time, show it:
            // i.e. show the latest data time in the session to which it belogs, if possible.  It MAY no tbe shown!
            Date ldt = site.getLatestDataDate();
            if ( ldt != null) {
                if ( (toDate==null) || (toDate != null &&  toDate.compareTo(ldt) == 1 ) ) {   // i.e. toDate > ldt
                    //lasttimestr = formatDateTimeHHmmss (site.getLatestDataDate());
                    lasttimestr = formatDateTimeHHmmss ( ldt );
                    //pw.append(formEntry(request, msgLabel("Latest Data Time"), dateString));
                    //System.out.println("   site "+id+"  lasttimestr ="+lasttimestr  );
                }
            }
           

            // Finally, compose the csv file line for this equipment session at this site:   
            pw.append(id+"," +name+"," +latitude+","+longitude+","+ellipsoidalheight+","+mondesc+","+iersdomes+","+   
               starttime+"," +stoptime+","+anttype+"," +dome+"," +antsn+"," +antht+"," +antn+"," +ante+"," +rectype+"," +
               firmvers+"," +recsn+","+sampIntstr+","+city+","+state+","+country+","+Xstr+","+Ystr+","+Zstr+","+agencyname+","+
               metpackname+","+metpackSN+","+networkstring+","+lasttimestr+","+sitecount+"\n");

        //System.out.println("GSAC:     CsvFullSiteOutputHandler() wrote line with site "+id+"  start="+starttime+"  stop="+stoptime+" metpackname="+metpackname+" metpackSN="+metpackSN);

        } // end get equip details for this session 

    }     // end addSiteEquipment


    /**
     *  make this date-time in UTC, in ISO 8601 format
     *
     * @param date _more_
     *
     * @return _more_
     */
    private String iso8601UTCDateTime(Date date) {
        if (date == null) { return ""; }
        /*synchronized (dateTimeFormatnoT) {
            return dateTimeFormatnoT.format(date); } */
        // make this date-time in UTC, in ISO 8601 format 
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format( date );
        return utcTime;
    }

    /**
     *  if 's' is null return ""; else return 's'.
     *
     * @param s  input String object
     *
     * @return  a string
     */
    private String getNonNullString(String s) {
        if (s == null) {
            return "";
        }
        return s;
    }

    /**
     * format a double value (such as lat and long) to 4 sig figs. 0.0001 lat res is about 1/100 of a km or 10 meters.
     *
     * @param v  a double number
     *
     * @return a String 
     */
    private String formatLocation(double v) {
        v = (double) Math.round(v * 10000) / 10000;
        String s = latLonFormat.format(v);
        return s;
    }

}
