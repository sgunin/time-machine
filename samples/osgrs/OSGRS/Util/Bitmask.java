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

package OSGRS.Util;

/**
 * A class for easy retrieval of individual bits in a bitmask.
 *
 * Often data is read in the form of bytes, but many times
 * there is a need to test and extract individual bits from
 * a series of bytes.
 *
 * @author Lars Samuelsson
 * @author Nam Hoang - added the getBinary method at 20/12/06 to match the GPS ICD subframe data
 * with the MSB on opposite side.
 */
public class Bitmask 
{
	private byte[] bits;
	private byte[] bytes;
	/**
	 * Creates a bitmask from the given array of bytes.
	 *
	 * @param bytes The byte array to convert to bits
	 */
	public Bitmask(byte[] bytes) {
		this.bytes = bytes;
		int len = bytes.length * 8;
		bits = new byte[len];
		for(int i = 0; i < bytes.length; i++) {
			for(int k = 7; k >= 0; k--) {
				bits[--len] = (byte) ((bytes[i] >>> k) & 0x01);
			}
		}
	}

	/**
	 * Extracts a series of bits as an int.
	 *
	 * The returned int will be the bits from the
	 * bitmask shifted to the rightmost position.
	 * The numbering of the bits are in accordance
	 * with low-endian representation.
	 * <P>
	 * Consider the bitmask: <BR>
	 * 1110011010 <BR>
	 * 9876543210 <BR>
	 * <P>
	 * get(5, 8) would produce the value 1100
	 *
	 * @param from First bit to extract from (from the right)
	 * @param to   Last bit to extract to (from the right)
	 * @return     The extracted bits shifted to the rightmost
	 *             position
	 */
	public int get(int from, int to) {
		int dec = 0;
		int pow = 1;
		for(int i = from; i <= to; i++) {
			dec += bits[i] * pow;
			pow *= 2;
		}
		return dec;
	}

	/**
	 * This method returns the byte array in binnary form
	 * reversing the order of the bits, MSB leftmost
	 * Added by Nam Hoang 21/12/06
	 * @param from
	 * @param to
	 * @return the
	 */
	public String getBinary(int from, int to) {
		int dec = 0;
		int pow = 1;
		String dbgBinaryString="";

		boolean start = false;

		int[] tmpBinaryArray = new int[(to-(from-1))];

		int binaryPosition = tmpBinaryArray.length -1;

		for(int i = from; i <= to; i++) {

			dbgBinaryString += bits[i];

			tmpBinaryArray[binaryPosition] = bits[i];

			binaryPosition--;

			dec += bits[i] * pow;
			pow *= 2;
		}
//		System.out.println(dbgBinaryString);

		String tmpBinaryArrayContents = "";
		for(int i = 0 ;i<tmpBinaryArray.length; i++)
		{
			tmpBinaryArrayContents += tmpBinaryArray[i];
		}

		return tmpBinaryArrayContents;
	}

	/**
	 * Extracts an individual bit.
	 *
	 * @param index The index of the bit to extract
	 *              (from the right)
	 * @return      The extracted bit (0 or 1)
	 */
	public byte get(int index) {
		return bits[index];
	}
	/**
	 * Gets the bits as bytes.
	 *
	 * @return The bits as a byte array
	 */
	public byte[] getBytes() {
		return bytes;
	}
	/**
	 * Returns a string representation of this Bitmask.
	 *
	 * The bits will be printed out as a series of
	 * zeros and ones with a space between each
	 * byte.
	 *
	 * @return A bitmask viewed as a string
	 */
	public String toString() {
		String mask = new String();
		String space = new String();
		for(int i = bits.length - 1; i >= 0; i--) {
			space = "";
			if((i + 1) % 8 == 0 && i < bits.length - 1)
				space = " ";
			mask += space + bits[i];
		}
		return mask;
	}
	/**
	 * Utility method for assembling a matrix of bytes
	 * into an array of bytes.
	 *
	 * The assembly is done row-wise in the matrix,
	 * ie the resulting array will start with
	 * matrix[0][] followed by matrix[1][] etc.
	 *
	 * @param matrix A byte matrix
	 * @return       The matrix assembled into an array
	 */
	public static byte[] assemble(byte[][] matrix, int rows) {
		int len = 0;
		for(int i = 0; i < rows; i++)
			len += matrix[i].length;
		byte[] assembled = new byte[len];
		int off = 0;
		for(int i = 0; i < rows; i++) {
			System.arraycopy(matrix[i], 0, assembled, off, matrix[i].length);
			off += matrix[i].length;
		}
		return assembled;
	}
}

