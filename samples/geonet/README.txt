/*****************************************************************\
  
                              GeoNet

\*****************************************************************/

   v0.1.3 beta

1. Introduction
   ============
   GeoNet is a software to perform rigorous least squares adjustment
   of topographic (Total station, GNSS and levels) observations 
   directly in a global reference frame.
   It is developed in MATLAB and it is aimed at providing a tool
   useful for studying the integration of various surveying
   instruments.

2. Requirements
   ============
   goGPS has been developed and tested in MATLAB 7.6+ environments,
   on both Windows and UNIX. The following elements are needed
   in order to use goGPS:

   - a computer with Windows or a UNIX-based operating system
   - a MATLAB 7.6+ installation

3. Run the software
   ================
   Set Matlab current folder to the one where all the files are
   downloaded and run the script launchGeonet.m
   
4. Attachment
   ==========
   There are format files of Leica GeoOffice (*.frt) to export
   observations and import them directly in GeoNet in the folder
   \LGO_frt:
   - Geonet_GPS_bl.FRT --> export GPS observed baselines
   - Geonet_GPS_sp.FRT --> export GPS observed points
   - Geonet_ref.FRT --> export all points coordinates (useful for
     RTK reference stations)
   - TPS_export.FRT --> export TPS observations 