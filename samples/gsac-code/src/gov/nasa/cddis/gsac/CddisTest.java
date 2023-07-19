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


import ucar.unidata.util.IOUtil;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;


/**
 * Tester
 */
public class CddisTest implements Runnable {
    //Change these to point to your server sites

    /** _more_ */
    static String[] urls = { "http://${host}/gsacws/site/search/sites.kml?output=site.kml&limit=1000&site.code=p12*",
                             "http://${host}/gsacws/site/search/sites.csv?output=site.csv&limit=1000&site.code=p12*",
                             "http://${host}/gsacws/site/search?limit=1000&site.code=p*" };

    /** _more_ */
    static int cnt = 0;

    /** _more_ */
    static int NUM_THREADS = 10;

    /** _more_ */
    private String host;

    /** _more_ */
    private static long startTime;

    /**
     * _more_
     *
     * @param host _more_
     */
    public CddisTest(String host) {
        this.host = host;
    }

    /**
     * _more_
     */
    public void run() {
        for (int i = 0; i < 1000; i++) {
            for (String url : urls) {
                try {
                    url = url.replace("${host}", host);
                    IOUtil.readContents(url, CddisTest.class);
                } catch (Exception exc) {
                    System.err.println("Error:" + exc);
                    return;
                }
                cnt++;
                if ((cnt % 10) == 0) {
                    long now = System.currentTimeMillis();
                    System.err.println("cnt:" + cnt + " time:"
                                       + (now - startTime) / 1000 + "s");
                }
            }
        }
    }

    /**
     * _more_
     *
     * @param args _more_
     *
     * @throws Exception _more_
     */
    public static void main(String[] args) throws Exception {
        startTime = System.currentTimeMillis();
        String host = "localhost:8080";
        if (args.length > 0) {
            host = args[0];
        }
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(new CddisTest(host));
        }
        executor.awaitTermination(1000000, TimeUnit.SECONDS);
        System.err.println("Done");
    }

}
