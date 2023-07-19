package OSGRS.DataManagement;

import OSGRS.dataType.*;

/*

	Copyright (C) 2007 SNAPlab

	http://www.gmat.unsw.edu.au/snap/

	This file is part of OSGRS.

	OSGRS is free software; you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation; either version 2 of the License, or
	(at your option) any later version.

	OSGRS is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with OSGRS; if not, write to the Free Software
	Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

 */

/**
 *	This is the interface which all datasources will implement 
 *	in order to be function with the datasource manager
 *	@author Manosh Fernando, Nam Hoang
 */

public interface DataSource 
{
	public SatelliteEphemeris[] getNavModel();
	
	public IonUTCModel getIonUTCModel();
	
	public RawIonUTCModel getRawIonUTCModel();
	
	public ReferenceTime[] getReferenceTime();
	
	public RawAlmanac[] getRawAlmanac();
	
	public SatellitesInView getSatellitesInViewOfReciever();
	
	public GPSAlmanac getGPSAlmanac();
	
	public long getAlmanacPeriod();
	
	public long getInitialDelay();
	
	public long getIonUTCPeriod();
	
	public long getNavigationModelPeriod();
	
	public long getReferenceTimePeriod();
}