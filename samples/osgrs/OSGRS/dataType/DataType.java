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


package OSGRS.dataType;

/**
 * This interface contains the blueprint for implementing data types.
 *
 */

public interface DataType
{
    /**
     * This method should be implemented to check parameters of a data type.
     * @throws IllegalArgumentException - IllegalArgumentException should be thrown when a value is out of range.
     */
    public void checkRange() throws IllegalArgumentException;


    /**
     * Output data information in a string format.
     * @return the string that represents an instance of the data model.
     */
    public String toString();

}
