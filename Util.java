/*
DEM -- A Geographic Information System for Line-Of-Sight Radio Communications.
Copyright (C) 1998, 1999 Jeffrey B. Otterson

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.

For more information, to submit bugs, software changes, etc., please contact

  Jeff Otterson / N1KDO
  3543 Tritt Springs Way
  Marietta, GA 30062
  otterson@mindspring.com

*/

/**
 * This class provides miscellaneous utility functions.
 * All the methods are static; this class should never be instantiated.
 */
public class Util
{
    private final static boolean DEBUG = false;

    /**
     * dump a byte array to the console for debugging.
     * @param buf the array to dump.
     * @param l the number of bytes to dump.
     */
    static void dumpBuf(byte[] buf, int l)
    {
        int lines = l / 16;
        lines += (l % 16 == 0) ? 0 : 1;

        int i, j, k;
        byte b;

        System.out.println("       00 01 02 03 04 05 06 07  08 09 0a 0b 0c 0d 0e 0f");
        System.out.println("       -- -- -- -- -- -- -- --  -- -- -- -- -- -- -- --    ----------------");
        for (i=0; i < lines; i++)
        {
            k = i * 16;
            printHexInt16(k);
            System.out.print(" | ");
            for (j=0; (j < 16); j++, k++)
            {
                if (k < l)
                {
                    b = buf[k];
                    printHexByte(b);
                } /* if k < l */
                else
                {
                    System.out.print("  ");
                } /* if k < l */
                System.out.print((j == 7) ? "  " : " ");
            } /* for j */

            k = i * 16;
            System.out.print("   ");
            for (j=0; (j < 16) && (k < l); j++, k++)
            {
                b = buf[k];
                if ((b < 32) || (b > 126))
                    b = '.';
                System.out.write(b);
            } /* for j */
            System.out.println();
        } /* for i */
        System.out.println();
    } /* dumpBuf() */

    /**
     * output a byte's hex value to the console.  Used by dumpBuf.
     * @param b the byte to dump.
     */
    static void printHexByte(byte b)
    {
        byte nibble;
        nibble = (byte) ((b & 0xf0) >>> 4);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
        nibble = (byte) (b & 0x0f);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
    } /* printHexByte() */

    /**
     * output a 16-bit integer's hex value to the console.  Used by dumpBuf.
     * @param i the integer to dump.
     */
    static void printHexInt16(int i)
    {
        i = i & 0x0000ffff;
        byte nibble;
        nibble = (byte) ((i & 0x0000f000) >>> 12);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
        nibble = (byte) ((i & 0x00000f00) >>> 8);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
        nibble = (byte) ((i & 0x000000f0) >>> 4);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
        nibble = (byte) (i & 0x0000000f);
        System.out.write(nibble < 10 ? nibble + '0' : nibble - 10 + 'a');
    } /* printHexInt16() */

    /**
     * convert a string to an int.
     * will return 0 for bad values, will not throw an exception.
     * @param s - the string to convert to an int.
     * @return the integer value of the string, or 0 if not convertable.
     */
    public static int safeStringToInt(String s)
    {
	int i;
	try
	{
	    i = Integer.parseInt(s.trim());
	} /* try */
	catch (NumberFormatException e)
	{
	    if (DEBUG)
		System.err.println("SafeStringToInt: " + e);
	    i = 0;
	} /* catch */
	return i;
    } /* safeStringToInt() */

    /**
     * convert a string to an float.
     * will return 0 for bad values, will not throw an exception.
     * @param s - the string to convert to an int.
     * @return the float value of the string, or 0 if not convertable.
     */
    public static float safeStringToFloat(String s)
    {
	float f;
	try
	{
	    f = Float.valueOf(s.trim()).floatValue();
	} /* try */
	catch (NumberFormatException e)
	{
	    if (DEBUG)
		System.err.println("SafeStringToFloat: " + e);
	    f = (float) 0.0;
	} /* catch */
	return f;
    } /* safeStringToFloat() */

} /* class Util */
