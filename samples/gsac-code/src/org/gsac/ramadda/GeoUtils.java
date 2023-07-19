/*
 * This is copied from the Unavco org.unavco.util package
 * So, don't change it here, change it there (I know, I know, its
 * a hack but sometimes ...)
 */

package org.gsac.ramadda;


import java.io.*;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


/**
 * A set of utility methods for dealing with geographic things
 * Note: this file lives in the NLAS SF SVN repository AND the
 * Unavco GSAC SVN repository. (I needed to share but couldn't
 * bring myself to deal with ore more jar)
 * So, if you change it here also change it at Unavco
 *
 * @author  Jeff McWhirter
 */
public class GeoUtils {

    /**
     *  semimajor axis of Earth WGS 84 (m)
     */
    public static final double WGS84_A = 6378137.0;

    /**
     *  semimajor axis of Earth WGS 84 (m) squared
     */

    public static final double WGS84_A_2 = WGS84_A * WGS84_A;

    /**
     *  semiminor axis of Earth WGS 84 (m)
     */
    public static final double WGS84_B = 6356752.3142451793;

    /**
     *  semiminor axis of Earth WGS 84 (m) squared
     */
    public static final double WGS84_B_2 = WGS84_B * WGS84_B;

    /** _more_ */
    public static final double WGS84_E_2 = (WGS84_A_2 - WGS84_B_2)
                                           / WGS84_A_2;

    /** _more_ */
    public static final double DEG2RAD = Math.PI / 180.0;


    /** _more_          */
    private static long GPS_TIME_OFFSET = 0;

    /** _more_ */
    public static final Calendar GPS_DATE = new GregorianCalendar(1980, 0, 6);

    /** _more_ */
    public static final int MS_PER_DAY = 1000 * 60 * 60 * 24;


    /**
     * _more_
     *
     * @param date _more_
     *
     * @return _more_
     */
    public static String date2GPS(Calendar date) {
        long elapsedTime = date.getTimeInMillis()
                           - GPS_DATE.getTimeInMillis();
        double elapsedDays = elapsedTime / MS_PER_DAY;
        double fullDays    = Math.floor(elapsedDays);
        /*
        double partialDays = elapsedDays - fullDays;
        double hours = partialDays * 24;
        */

        return Double.toString(fullDays);
    }

    /**
     * _more_
     *
     * @param gpsDays _more_
     *
     * @return _more_
     */
    public static Calendar gps2Date(int gpsDays) {
        Calendar returnDate = (Calendar) GPS_DATE.clone();
        returnDate.add(Calendar.DATE, gpsDays);

        return returnDate;
    }


    /**
     * _more_
     *
     * @return _more_
     */
    public static long getGpsTimeOffset() {
        if (GPS_TIME_OFFSET == 0) {
            GregorianCalendar cal =
                new GregorianCalendar(TimeZone.getTimeZone("UTC"));
            cal.set(1980, 1, 6, 0, 0, 0);
            GPS_TIME_OFFSET = cal.getTimeInMillis();
        }
        return GPS_TIME_OFFSET;
    }

    /**
     * _more_
     *
     * @param gpsTime _more_
     *
     * @return _more_
     */
    public static long convertGpsTime(long gpsTime) {
        return getGpsTimeOffset() + gpsTime;
    }

    /**
     * Taken from the C WGS84_xyz_to_geo  in postion.c
     *
     * @param x _more_
     * @param y _more_
     * @param r _more_
     * @param z _more_
     *
     * @return _more_
     */
    public static double[] wgs84XYZToLatLonAlt(double x, double y, double z) {
        return wgs84XYZToLatLonAlt(x, y, z, null);
    }


    /**
     * Taken from the C WGS84_xyz_to_geo  in postion.c
     *
     * @param x _more_
     * @param y _more_
     * @param r _more_
     * @param z _more_
     * @param result _more_
     *
     * @return _more_
     */
    public static double[] wgs84XYZToLatLonAlt(double x, double y, double z,
            double[] result) {
        double lat, lon, alt;
        double cos_lat, sin_lat, last_lat, p, N;
        double r = Math.sqrt(x * x + y * y + z * z);

        if (r != 0) {
            lat = Math.asin(z / r);
        } else {
            lat = 0;
        }
        lon = Math.atan2(y, x);

        /* this accounts for the shape of the WGS 84 ellipsoid and a height h above (or below) it
           note: the initial latitude correction is based on an empirical approximation, which is
           good to several thousandths of a minute in latitude for most elevations; each iteration
           improves the precision by about a factor of 1000, requiring about 5 iterations to converge */

        lat += 3.35842e-3 * Math.sin(2. * lat) + 5.82e-6 * Math.sin(4. * lat);
        p   = Math.sqrt(x * x + y * y);
        int loop = 0;
        do {
            last_lat = lat;
            cos_lat  = Math.cos(lat);
            sin_lat  = Math.sin(lat);
            N = WGS84_A_2
                / Math.sqrt(WGS84_A_2 * cos_lat * cos_lat
                            + WGS84_B_2 * sin_lat * sin_lat);
            if (cos_lat != 0) {
                alt = p / cos_lat - N;
            } else {
                alt = z - N * WGS84_B_2 / WGS84_A_2;
            }
            if (p != 0) {
                lat = Math.atan(z / (p * (1. - WGS84_E_2 * (N / (N + alt)))));
            } else {
                lat = (z < 0.)
                      ? -Math.PI / 2.
                      : Math.PI / 2.;
            }
        } while ((last_lat - lat) != 0 && (loop++ < 10));


        if (result == null) {
            result = new double[3];
        }
        result[0] = Math.toDegrees(lat);
        result[1] = Math.toDegrees(lon);
        result[2] = alt;
        return result;
    }

    /**
     * Normalize the longitude to lie between +/-180
     * @param lon east latitude in degrees
     * @return normalized lon
     */
    static public double normalizeLongitude(double lon) {
        if ((lon < -180.) || (lon > 180.)) {
            return Math.IEEEremainder(lon, 360.0);
        } else {
            return lon;
        }
    }








    /**
     * _more_
     *
     * @param args _more_
     */
    public static void main(String[] args) {
        double[][] xyz = {
            { -2307792.824, -4160678.918, 4235698.873 }
        };
        double[]   result;
        for (int i = 0; i < xyz.length; i++) {
            result = wgs84XYZToLatLonAlt(xyz[i][0], xyz[i][1], xyz[i][2]);
            //            result = wgs84XYZToLatLonAlt(xyz[i][1], xyz[i][0], xyz[i][2]);
            System.out.println(result[0] + "/" + result[1] + "/" + result[2]);
        }

    }


}
