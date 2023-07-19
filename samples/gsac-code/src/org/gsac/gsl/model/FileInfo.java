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

package org.gsac.gsl.model;


import org.gsac.gsl.util.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.net.URL;
import java.net.URLConnection;

import java.util.Date;


/**
 */
public class FileInfo {

    /** _more_ */
    public static final int CLASS_FILE = 0;

    /** _more_ */
    public static final int TYPE_NONFILE = 1;

    /** _more_ */
    private int type = CLASS_FILE;

    /** _more_ */
    private File localFile;

    /** _more_ */
    private String url="";

    /** _more_ */
    private long fileSize = 0;

    /** MD5 check sum value of the datafile itself. */
    private String md5="";

    /** originator_url_domain; is empty str "" by default, not null. */
    // the domain in the URL (ftp or http) where a datafile came from originally.
    // used to help prevent endless circular copies of datafiles between GSACs, and to retain the name of the source of the data.
    private String originator_url_domain="";

    /**  TRF the terrestrial reference frame for the datafile's data. */
    private String TRF="";

    /** _more_ */
    private String station4charId="";

    /**  sample rate in seconds of instrument making this geodesy data file */
    private float sample_interval=0.0f;

    /**
     * ctor
     */
    public FileInfo() {}

    /**
     * _more_
     *
     * @param url _more_
     */
    public FileInfo(String url) {
        this.url = url;
    }


    /**
     * _more_
     *
     * @param url _more_
     * @param fileSize _more_
     * @param md5 _more_
     */
    public FileInfo(String url, long fileSize, String md5) {
        this(url, fileSize, md5, CLASS_FILE);
    }

    /**
     * _more_
     *
     * @param url _more_
     * @param fileSize _more_
     * @param md5 _more_
     */
    public FileInfo(String url, long fileSize, String md5, String station4charId) {
        this(url, fileSize, md5, CLASS_FILE);
        this.station4charId = station4charId;
    }

    /**
     * _more_
     *
     * @param url _more_
     * @param fileSize _more_
     * @param md5 _more_
     * @param type _more_
     */
    public FileInfo(String url, long fileSize, String md5, int type) {
        this(url, fileSize, md5, type, null);
    }



    /**
     * _more_
     *
     * @param url _more_
     * @param fileSize _more_
     * @param md5 _more_
     * @param type _more_
     * @param localFile _more_
     */
    public FileInfo(String url, long fileSize, String md5, int type, File localFile) {
        this.url       = url;
        this.fileSize  = fileSize;
        this.md5       = md5;
        this.type      = type;
        this.localFile = localFile;
    }


    /**
     * _more_
     *
     * @param url _more_
     * @param fileSize _more_
     * @param md5 _more_
     * @param type _more_
     * @param localFile _more_
     * @param  _more_
     */
    public FileInfo(String url, long fileSize, String md5, int type, File localFile, String station4charId) {
        this.url       = url;
        this.fileSize  = fileSize;
        this.md5       = md5;
        this.type      = type;
        this.localFile = localFile;
        this.station4charId = station4charId;
    }

    /**
     * _more_
     *
     * @return _more_
     */
    public String toString() {
        return this.url;
    }

    /**
     * _more_
     *
     * @return _more_
     *
     * @throws Exception On badness
     */
    public InputStream getInputStream() throws Exception {
        if (type != CLASS_FILE) {
            return null;
        }
        if (localFile != null) {
            return new FileInputStream(localFile);
        }
        URLConnection connection = new URL(url).openConnection();

        return connection.getInputStream();
    }






    /**
     *  Set the Url property.
     *
     *  @param value The new value for Url
     */
    public void setUrl(String value) {
        url = value;
    }

    /**
     *  Get the Url property.
     *
     *  @return The Url
     */
    public String getUrl() {
        return url;
    }

    /**
     *  Set the FileSize property.
     *
     *  @param value The new value for FileSize
     */
    public void setFileSize(long value) {
        fileSize = value;
    }

    /**
     *  Get the FileSize property.
     *
     *  @return The FileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * Set the sample_interval property.
     *
     * @param value The new value for 
     */
    public void  setSampleInterval(float val )  {
       sample_interval=val;
    }

    /**
     *  Get the sample_interval property.
     *
     *  @return the 
     */
    public float getSampleInterval() {
        return sample_interval;
    }


    /**
     *  Set the originator_url_domain property.
     *
     *  @param value The new value for originator_url_domain
     */
    public void setOriginator_url_domain(String value) {
        originator_url_domain = value;
    }
    /**
     *  Get the originator_url_domain property.
     *
     *  @return originator_url_domain 
     */
    public String getOriginator_url_domain() {
        return originator_url_domain;
    }

    /**
     *  Set the MD5 property.
     *
     *  @param value The new value for Md5
     */
    public void setMd5(String value) {
        md5 = value;
    }
    /**
     *  Get the MD5 property.
     *
     *  @return The Md5
     */
    public String getMd5() {
        return md5;
    }

    /**
     *  Set the TRF property.
     *
     *  @param value The new value for TRF the terrestrial reference frame for the datafile's data. 
     */
    public void setTRF(String value) {
        TRF = value;
    }
    /**
     *  Get the TRF property.
     *
     *  @return the TRF
     */
    public String getTRF() {
        return TRF;
    }

    /**
     *  Set the station4charId property.
     *
     *  @param value The new value for station4charId 
     */
    public void setStation4charId(String id) {
        station4charId = id;
    }
    /**
     *  Get the 
     *
     *  @return The station4charId
     */
    public String getStation4charId() {
        return station4charId;
    }

    /**
     *  Set the Type property.
     *
     *  @param value The new value for type
     */
    public void setType(int value) {
        type = value;
    }

    /**
     *  Get the Type property.
     *
     *  @return The Type
     */
    public int getType() {
        return type;
    }


    /**
     *  Set the LocalFile property.
     *
     *  @param value The new value for LocalFile
     */
    public void setLocalFile(File value) {
        localFile = value;
    }

    /**
     *  Get the LocalFile property.
     *
     *  @return The LocalFile
     */
    public File getLocalFile() {
        return localFile;
    }


}
