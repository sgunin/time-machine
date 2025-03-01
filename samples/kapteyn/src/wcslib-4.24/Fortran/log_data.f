*=======================================================================
*
* WCSLIB 4.24 - an implementation of the FITS WCS standard.
* Copyright (C) 1995-2014, Mark Calabretta
*
* This file is part of WCSLIB.
*
* WCSLIB is free software: you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* WCSLIB is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
* FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
* License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with WCSLIB.  If not, see http://www.gnu.org/licenses.
*
* Direct correspondence concerning WCSLIB to mark@calabretta.id.au
*
* Author: Mark Calabretta, Australia Telescope National Facility, CSIRO.
* http://www.atnf.csiro.au/people/Mark.Calabretta
* $Id: log_data.f,v 4.24 2014/09/18 15:25:01 mcalabre Exp $
*=======================================================================

      BLOCK DATA LOG_BLOCK_DATA

      CHARACTER LOG_ERRMSG(0:3)*80

      COMMON /LOG_DATA/ LOG_ERRMSG

      DATA LOG_ERRMSG /
     :  'Success',
     :  ' ',
     :  'Invalid log-coordinate reference value',
     :  'One or more of x coordinates were invalid'/

      END
