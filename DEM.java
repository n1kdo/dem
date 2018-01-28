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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * class to represent a logical Digital Elevation Model.
 */
public class DEM
{
    private static final int DEM_BUFFER_SIZE = 1024;
    /**
     * constant to select the South West corner of the DEM.
     */
    public static final int SW_CORNER = 0;
    /**
     * constant to select the North West corner of the DEM.
     */
    public static final int NW_CORNER = 1;
    /**
     * constant to select the North East corner of the DEM.
     */
    public static final int NE_CORNER = 2;
    /**
     * constant to select the South East corner of the DEM.
     */
    public static final int SE_CORNER = 3;
    String fileName;
    String name;
    byte units;
    GeoCoordinate corners[];
    short minimumElevation;
    short maximumElevation;
    byte xResolution;
    byte yResolution;
    byte zResolution;
    short rows;
    short columns;
    short elevations[][];
    DEMmain demMain;
    StatusBar statusBar;
    
    /**
     * Construct a new, empty DEM object.
     */
    public DEM()
    {
	this("", null);
    } /* DEM constructor */

    /**
     * Construct a new, empty DEM object, referencing the specified parent.
     * @param fileName the DEM's fileName.
     * @param demMain the DEMmain object that owns this DEM.
     */
    public DEM(String fileName, DEMmain demMain)
    {
	this.fileName = fileName;
	this.demMain = demMain;
        statusBar = demMain.statusBar;
    } /* DEM constructor */

    /**
     * get the DEM's file name.
     * @return a string with the DEM's file name.
     */
    public String getFileName()
    {
	return fileName;
    } /* getFileName() */

    /**
     * get the units of this DEM.
     * @return the units code.
     */
    public byte getUnits()
    {
        return units;
    } /* getRows() */
    
    /**
     * get the number of rows in this DEM.
     * @return the number of rows.
     */
    public short getRows()
    {
        return rows;
    } /* getRows() */
    
    /**
     * get the number of columns in this DEM.
     * @return the number of columns.
     */
    public short getColumns()
    {
        return columns;
    } /* getColumns() */

    /**
     * get the minimum elevation of this DEM.
     * @return the minimum elevation.
     */
    public short getMinimumElevation()
    {
        return minimumElevation;
    } /* getMinimumElevation */
    
    /**
     * get the maximum elevation of this DEM.
     * @return the minimum elevation.
     */
    public short getMaximumElevation()
    {
        return maximumElevation;
    } /* getMaximumElevation */

    /**
     * return the X resolution of the DEM in arc-seconds.
     * @return the x-resolution of the DEM in arc-seconds.
     */
    public byte getXResolution()
    {
	return xResolution;
    } /* getXResolution() */

    /**
     * return the Y resolution of the DEM in arc-seconds.
     * @return the y-resolution of the DEM in arc-seconds.
     */
    public byte getYResolution()
    {
	return yResolution;
    } /* getYResolution() */

    /**
     * return the GeoCoordinate for the specified corner.
     * @param cornerNum an integer representing the corner: SW_CORNER,
     * NW_CORNER, NE_CORNER, SE_CORNER.
     * @return a GeoCoordinate for the specified corner, or null.
     */
    public GeoCoordinate getCorner(int cornerNum)
    {
	if ((cornerNum < SW_CORNER) || (cornerNum > SE_CORNER))
	    return null;
	else
	    return corners[cornerNum];
    } /* getCorner() */

    /**
     * return the GeoCoordinate for the center of the DEM.
     * WARNING: doesn't deal with DEMs that cross either 0-degree line.
     * @return a GeoCoordinate for the center of the DEM.
     */
    public GeoCoordinate getCenter()
    {
	GeoCoordinate nwCorner = getCorner(NW_CORNER);
	GeoCoordinate seCorner = getCorner(SE_CORNER);
	int lat = (nwCorner.getLatitude() + seCorner.getLatitude()) / 2;
	int lon = (nwCorner.getLongitude() + seCorner.getLongitude()) / 2;
	return new GeoCoordinate(lat, lon);
    } /* getCenter() */

    /**
     * determine if the x,y location is valid on the DEM.
     * @param x the longitudinal offset.
     * @param y the latitudinal offset.
     * @return true if the location is on the DEM.
     */
    public boolean isValidLocation(int column, int row)
    {
	return ((column >= 0) && (column < columns) && (row >= 0) && (row < rows));
    } /* validPoint */
    
    /**
     * get the elevation of a particular point in this DEM.
     * @param column the column number of the elevation. 
     * @param row the row number of the elevation.
     * @return the elevation at that point or 0.
     */
    public short getElevation(int column, int row)
    {
	if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns))
	{
	    new ErrorBox(demMain,
			 "Error!",
			 "DEM.getElevation: column="+column+" row="+row+" columns="+columns+" rows="+rows);
	    return 0;
	}
	else
	{
	    return elevations[column][row];
	}
    } /* getElevation() */
    
    /**
     * get the elevation of a particular point in this DEM.
     * @param gc the location to get the elevation for.
     * @return the elevation at that point or 0.
     */
    public short getElevation(GeoCoordinate gc)
    {
	int row    = (gc.getLatitude()  - corners[SW_CORNER].getLatitude())  / yResolution;
	int column = (gc.getLongitude() - corners[SW_CORNER].getLongitude()) / xResolution ;
	if ((row < 0) || (row >= rows) || (column < 0) || (column >= columns))
	{
	    new ErrorBox(demMain,
			 "Error!",
			 "DEM.getElevation: column="+column+" row="+row+" columns="+columns+" rows="+rows);
	    return 0;
	}
	else
	{
	    return elevations[column][row];
	}
    } /* getElevation() */
    
    /**
     * write the DEM data to a file in a binary compressed format.
     * @return true for successful save.
     */
    public boolean write()
    {
	return write(fileName);
    } /* write() */
	 
    /**
     * write the DEM data to a specified file in a binary compressed format.
     * @param demFileName the name of the file to write.
     * @return true for successful save.
     */
    public boolean write(String demFileName)
    {
        if (statusBar != null)
        {
            statusBar.setMessage("Saving "+demFileName);
        } /* if statusBar != null */
	try
	{
	    FileOutputStream fout = new FileOutputStream(demFileName);
            DeflaterOutputStream dout = new DeflaterOutputStream(fout);
	    DataOutputStream out = new DataOutputStream(dout);
	    int i, j;
	    out.writeUTF(name);
	    out.writeByte(units);
	    for(i = 0; i < 4; i++)
	    {
		out.writeInt(corners[i].getLatitude());
		out.writeInt(corners[i].getLongitude());
	    } /* for i */
	    out.writeShort(minimumElevation);
	    out.writeShort(maximumElevation);
	    out.writeByte(xResolution);
	    out.writeByte(yResolution);
	    out.writeByte(zResolution);
	    out.writeShort(rows);
	    out.writeShort(columns);
            int pctDone;
	    for (i=0; i < columns; i++)
	    {
                if (statusBar != null)
                {
                    pctDone = i * 100 / columns;
                    statusBar.updateProgress(pctDone);
                } /* if statusBar != null */
		for (j = 0; j < rows; j++)
		{
		    out.writeShort(elevations[i][j]);
		} /* for j */
	    } /* for i */
	    out.close();
            dout.close();
	    fout.close();
	} /* try */
	catch (IOException e)
	{
	    new ErrorBox(demMain,
			 "Error saving " + demFileName,
			 e.toString());
	    return false;
	}
	catch (SecurityException e)
	{
	    new ErrorBox(demMain,
			 "Error saving " + demFileName,
			 e.toString());
	    return false;
	}
        if (statusBar != null)
        {
            statusBar.setMessage("Saved  "+demFileName);
            statusBar.updateProgress(0);
        } /* if statusBar != null */
	return true;
    } /* write() */

    /**
     * read the DEM data from a file.
     * @param demFileName the name of the file to read.
     * @return true for successful read.
     */
    public boolean read(String demFileName)
    {
	fileName = demFileName;
	return read();
    } /* read() */

     /**
     * read the DEM data from a disk file.
     * @return true for successful load.
     */
    public boolean read()
    {
        if (statusBar != null)
        {
            statusBar.setMessage("Loading "+fileName);
        } /* if statusBar != null */

	try
	{
            FileInputStream fin = new FileInputStream(fileName);
            InflaterInputStream iin = new InflaterInputStream(fin);
	    DataInputStream in = new DataInputStream(iin);
	    int i, j, lat, lon;
	    name = in.readUTF();
	    units = in.readByte();
	    corners = new GeoCoordinate[8];
	    for(i = 0; i < 4; i++)
	    {
		lat = in.readInt();
		lon = in.readInt();
		corners[i] = new GeoCoordinate(lat, lon);
	    } /* for i */
	    minimumElevation = in.readShort();
	    maximumElevation = in.readShort();
	    xResolution = in.readByte();
	    yResolution = in.readByte();
	    zResolution = in.readByte();
	    rows = in.readShort();
	    columns = in.readShort();
	    elevations = new short[columns][rows];
            int pctDone;
	    short elevation;
	    for (i = 0; i < columns; i++)
	    {
                if (statusBar != null)
                {
                    pctDone = i * 100 / columns;
                    statusBar.updateProgress(pctDone);
                } /* if statusBar != null */
		for (j = 0; j < rows; j++)
		{
		    elevation = in.readShort();
		    elevations[i][j] = elevation;
		} /* for j */
	    } /* for i */
	    in.close();
            iin.close();
            fin.close();
	} /* try */
	catch (FileNotFoundException e)
	{
	    new ErrorBox(demMain,
			 "Error reading " + fileName,
			 e.toString());
	    return false;
	}
	catch (IOException e)
	{
	    new ErrorBox(demMain,
			 "Error reading " + fileName,
			 e.toString());
	    return false;
	}
	catch (SecurityException e)
	{
	    new ErrorBox(demMain,
			 "Error reading " + fileName,
			 e.toString());
	    return false;
	}
        if (statusBar != null)
        {
            statusBar.setMessage("Loaded " + fileName);
            statusBar.updateProgress(0);
        } /* if statusBar != null */
	return true;
    } /* read() */

    /**
     * extract DEM data for a given region from a file.
     * @param swCoordinate the coordinates fo the SW corner of the new DEM.
     * @param neCoordinate the coordinates fo the NE corner of the new DEM.
     * @return true for successful load.
     */
    public boolean read(GeoCoordinate swCoordinate,
			GeoCoordinate neCoordinate)
    {
	if (statusBar != null)
        {
            statusBar.setMessage("Loading "+fileName);
        } /* if statusBar != null */

	try
	{
            FileInputStream fin = new FileInputStream(fileName);
            InflaterInputStream iin = new InflaterInputStream(fin);
	    DataInputStream in = new DataInputStream(iin);
	    int i, j, lat, lon;
	    name = in.readUTF();
	    units = in.readByte();
	    corners = new GeoCoordinate[8];
	    for(i = 0; i < 4; i++)
	    {
		lat = in.readInt();
		lon = in.readInt();
		corners[i] = new GeoCoordinate(lat, lon);
	    } /* for i */
	    minimumElevation = in.readShort();
	    maximumElevation = in.readShort();
	    xResolution = in.readByte();
	    yResolution = in.readByte();
	    zResolution = in.readByte();
	    rows = in.readShort();
	    columns = in.readShort();

	    int x1, x2, y1, y2;
	    int swLat, swLon, neLat, neLon;

	    lat = corners[SW_CORNER].getLatitude();
	    lon = corners[SW_CORNER].getLongitude();
	    	    
	    swLat = swCoordinate.getLatitude();
	    swLon = swCoordinate.getLongitude();
	    neLat = neCoordinate.getLatitude();
	    neLon = neCoordinate.getLongitude();
	    
	    x1 = (swLon - lon) / xResolution;
	    x2 = (neLon - lon) / xResolution;

	    y1 = (swLat - lat) / yResolution;
	    y2 = (neLat - lat) / yResolution;

	    if ((x1 < 0) ||
		(x1 > columns) ||
		(x2 < 0) ||
		(x2 > columns) ||
		(y1 < 0) ||
		(y1 > rows) ||
		(y2 < 0) ||
		(y2 > rows))
	    {
		new ErrorBox(demMain,
			     "Error extracting from " + fileName,
			     "Coordinates are not on the DEM:\n"+
			     "SW: " + corners[SW_CORNER].toString() + "\n" +
			     "NE: " + corners[NE_CORNER].toString());
		return false;
	    } /* if x1... */

	    int x, y;
	    
	    short newColumns = (short) (x2 - x1 + 1);
	    short newRows = (short) (y2 - y1 + 1);
	    elevations = new short[newColumns][newRows];
            int pctDone;
            int minElev = 0;
            int maxElev = 0;
	    short elevation;
	    x = 0;
	    for (i = 0; i < columns; i++)
	    {
		y = 0;
		
                if (statusBar != null)
                {
                    pctDone = i * 100 / columns;
                    statusBar.updateProgress(pctDone);
                } /* if statusBar != null */
		for (j = 0; j < rows; j++)
		{
		    elevation = in.readShort();
		    if ((i >= x1) &&
			(i <= x2) &&
			(j >= y1) &&
			(j <= y2))
		    {
			if ((x == 0) &&
			    (y == 0))
			{ /* first point */
			    minElev = elevation;
			    maxElev = elevation;
			} /* if x == 0 && y == 0 */
			else
			{ /* not first point */
			    minElev = Math.min(elevation, minElev);
			    maxElev = Math.max(elevation, maxElev);
			} /* if x == 0 && y == 0 */
			elevations[x][y++] = elevation;
		    } /* if i >= x1 */
		} /* for j */
		if ((i >= x1) &&
		    (i <= x2))
		    x++;
	    } /* for i */
	    columns = newColumns;
	    rows = newRows;
            minimumElevation = (short) minElev;
            maximumElevation = (short) maxElev;
	    corners[SW_CORNER] = new GeoCoordinate(swLat, swLon);
	    corners[NW_CORNER] = new GeoCoordinate(neLat, swLon);
	    corners[NE_CORNER] = new GeoCoordinate(neLat, neLon);
	    corners[SE_CORNER] = new GeoCoordinate(swLat, neLon);
	    in.close();
            iin.close();
            fin.close();
	} /* try */
	catch (FileNotFoundException e)
	{
	    new ErrorBox(demMain,
			 "Error extracting from " + fileName,
			 e.toString());
	    return false;
	}
	catch (IOException e)
	{
	    new ErrorBox(demMain,
			 "Error extracting from " + fileName,
			 e.toString());
	    return false;
	}
	catch (SecurityException e)
	{
	    new ErrorBox(demMain,
			 "Error extracting from " + fileName,
			 e.toString());
	    return false;
	}
        if (statusBar != null)
        {
            statusBar.setMessage("Loaded " + fileName);
            statusBar.updateProgress(0);
        } /* if statusBar != null */
	return true;
    } /* read() */
    
    /**
     * read DEM data from a USGS format DEM 1 degree (1:250000) file.
     * @param demFileName the name of the file to read.
     * @return true for successful read.
     */
    public boolean readUSGS(String demFileName)
    {
	fileName = demFileName;
	return read();
    } /* readUSGS() */
    
    /**
     * read DEM data from a USGS format DEM 1 degree (1:250000) file.
     * @return true for successful read.
     */
    public boolean readUSGS()
    {
        if (statusBar != null)
            statusBar.setMessage("Loading (USGS) "+fileName);

	try
	{
	    BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName),2048);
	    byte buffer[] = new byte[1024];
	    int offset;
	    int index;
	    int increment;
	    int lat,lon;

	    /* read record type A (header) */
	    in.read(buffer, 0, 1024);
	    name = new String (buffer, 0, 40);
	    units = (byte) Util.safeStringToInt(new String(buffer,534,6));
	    
	    offset = 546;
	    increment = 24;
	    index = 0;
	    corners = new GeoCoordinate[8];
	    for (index = 0; index < 4; index++)
	    {
		lon = convertScientificToInt(new String(buffer, offset, increment));
		offset += increment;
		lat = convertScientificToInt(new String(buffer, offset, increment));
		offset += increment;
		corners[index] = new GeoCoordinate(lat, lon);
	    } /* for index */
	    minimumElevation = (short) convertScientificToInt(new String(buffer, 738, 24));
	    maximumElevation = (short) convertScientificToInt(new String(buffer, 762, 24));

	    xResolution = (byte) convertScientificToInt(new String(buffer, 816, 12));
	    yResolution = (byte) convertScientificToInt(new String(buffer, 828, 12));
	    zResolution = (byte) convertScientificToInt(new String(buffer, 840, 12));

	    int testRows = Util.safeStringToInt(new String(buffer, 852, 6));
	    if (testRows != 1)
	    {
		new ErrorBox(demMain,
			 "Error reading (USGS) " + fileName,
			 "Unsupported DEM file feature!");
		return false;
	    } /* if rows != 1  */
	    columns = (short) Util.safeStringToInt(new String(buffer, 858, 6));
	    rows = columns; // particularly rude hack for 1 degreee DEMs only.

	    /* read record type B (data "profiles") */
	    elevations = new short[columns][rows];
	    int i, j;
	    int rowNum;
	    boolean firstBlock = true;
	    int bytesRead;
	    int rowsThisBlock;
	    int pctDone;
	    /* read the profiles */
	    for (i=0; i < columns; i++)
	    {
                if (statusBar != null)
                {
                    pctDone = i * 100 / columns;
                    statusBar.updateProgress(pctDone);
                } /* if statusBar != null */
		rowNum = 0;
		while (rowNum < rows)
		{
		    bytesRead = in.read(buffer, 0, 1024);
		    if (rowNum == 0)
		    { /* first block */
			offset = 144;
			rowsThisBlock = 146;
		    } /* if rowNum == 0 */
		    else
		    { /* subsequent block */
			offset = 0;
			rowsThisBlock = 170;
		    } /* if rowNum == 0 */
		    for (j=0; j < rowsThisBlock && rowNum < rows; j++)
		    {
			elevations[i][rowNum++] = (short) Util.safeStringToInt(new String(buffer, offset, 6));
			offset += 6;
		    } /* for j */
		} /* while */	
	    } /* for i */
	    in.close();
	} /* try */
	catch (FileNotFoundException e)
	{
	    new ErrorBox(demMain,
			 "Error reading (USGS) from " + fileName,
			 e.toString());
	    return false;
	}
	catch (IOException e)
	{
	    new ErrorBox(demMain,
			 "Error reading (USGS) from " + fileName,
			 e.toString());
	    return false;
	}
	catch (SecurityException e)
	{
	    new ErrorBox(demMain,
			 "Error reading (USGS) from " + fileName,
			 e.toString());
	    return false;
	}
        if (statusBar != null)
        {
            statusBar.setMessage("Loaded (USGS) "+fileName);
            statusBar.updateProgress(0);
        } /* if statusBar != null */
	return true;
    } /* readUSGS() */
    
    /**
     * get the integer value of a number in scientific notation.
     * this version does this the hard way, with many floating point
     * math operations, and a fair amount of casting and promotion.
     * @param s the string representation of the number.
     * @return the number converted to an integer or 0 if not convertable.
     */
    public static int floatConvertScientificToInt(String s)
    {
	int o = s.indexOf('D');
	if (o == -1)
	{
	    return 0;
	} /* if o == -1 */
	else
	{
	    float value = Util.safeStringToFloat(s.substring(0,o));
	    int power = Util.safeStringToInt(s.substring(o+2));
	    value = (float) (value * Math.pow(10.0, power));
	    return (int) value;
	} /* if o == -1 */
    } /* floatConvertScientificToInt() */

    /**
     * get the integer value of a number in scientific notation.
     * this version should be faster, the number is only converted once, and
     * no floating point is used, which eliminates promotion, casting, etc.
     * @param s the string representation of the number.
     * @return the number converted to an integer or 0 if not convertable.
     */
    public static int convertScientificToInt(String s)
    {
	int o = s.indexOf('D');
 	if (o == -1)
	    o = s.indexOf('E');
 	if (o == -1)
	    return 0;
	String mant = s.substring(0,o);
	int dp = mant.indexOf('.');
	if (dp == -1)
	    return 0;
	mant = mant.substring(0,dp) + mant.substring(dp+1);
	int power = Util.safeStringToInt(s.substring(o+2));
	mant = mant.substring(0, dp+power);
	return(Util.safeStringToInt(mant));
    } /* convertScientificToInt() */

    /**
     * merge two adjacent DEMs into a new DEM.
     * @param file1 the name of the western DEM file.
     * @param file2 the name of the eastern DEM file.
     * @param resultFile the name of the resulting DEM file.
     * @param demMain the DEMmain that owns the parent frame and status bar.
     * @return true for success.
     */
    public static boolean merge(String file1,
				String file2,
				String resultFile,
				DEMmain demMain)
    {
	StatusBar statusBar = demMain.statusBar;
        if (statusBar != null)
        {
            statusBar.setMessage("Merging DEMs...");
        } /* if statusBar != null */

	try
	{
	    String name1, name2;
	    byte units1, units2;
	    GeoCoordinate corners1[], corners2[];
	    short minElev1, maxElev1, minElev2, maxElev2;
	    short minimumElevation, maximumElevation;
	    int i, j, lat, lon;
	    byte xRes1, yRes1, zRes1, xRes2, yRes2, zRes2;
	    int rows1, columns1, rows2, columns2;
	    int rows, columns;
	    boolean snFlag = false;
		
            FileInputStream fin1 = new FileInputStream(file1);
            InflaterInputStream iin1 = new InflaterInputStream(fin1);
	    DataInputStream in1 = new DataInputStream(iin1);
	    
            FileInputStream fin2 = new FileInputStream(file2);
            InflaterInputStream iin2 = new InflaterInputStream(fin2);
	    DataInputStream in2 = new DataInputStream(iin2);
	    
	    name1 = in1.readUTF();
	    units1 = in1.readByte();
	    corners1 = new GeoCoordinate[4];
	    for(i = 0; i < 4; i++)
	    {
		lat = in1.readInt();
		lon = in1.readInt();
		corners1[i] = new GeoCoordinate(lat, lon);
	    } /* for i */
	    minElev1 = in1.readShort();
	    maxElev1 = in1.readShort();
	    xRes1 = in1.readByte();
	    yRes1 = in1.readByte();
	    zRes1 = in1.readByte();
	    rows1 = in1.readShort();
	    columns1 = in1.readShort();
	    
	    name2 = in2.readUTF();
	    units2 = in2.readByte();
	    corners2 = new GeoCoordinate[4];
	    for(i = 0; i < 4; i++)
	    {
		lat = in2.readInt();
		lon = in2.readInt();
		corners2[i] = new GeoCoordinate(lat, lon);
	    } /* for i */
	    minElev2 = in2.readShort();
	    maxElev2 = in2.readShort();
	    xRes2 = in2.readByte();
	    yRes2 = in2.readByte();
	    zRes2 = in2.readByte();
	    rows2 = in2.readShort();
	    columns2 = in2.readShort();

	    if ((xRes1 != xRes2) ||
		(yRes1 != yRes2) ||
		(zRes1 != zRes2) ||
		(units1 != units2))
	    {
		    new ErrorBox(demMain,
				 "Error merging!",
				 "Files are not compatable (resolution or units):\n" +
				 file1 + "\n" +
				 file2);
		return false;
	    } /* if xRes1 != xRes2... */

	    if (!corners1[SE_CORNER].equals(corners2[SW_CORNER]) ||
		!corners1[NE_CORNER].equals(corners2[NW_CORNER]))
	    { /* not west-east */
		if (corners1[NW_CORNER].equals(corners2[SW_CORNER]) &&
		    corners1[NE_CORNER].equals(corners2[SE_CORNER]))
		{ /* it's south-north */
		    snFlag = true;
		} /* if corners1[NW_CORNER]... */
		else
		{ /* not south-north */
		    new ErrorBox(demMain,
				 "Error merging!",
				 "Files are not adjacent:\n" +
				 file1 + "\n" +
				 file2);
		    return false;
		} /* if corners1[NW_CORNER]... */
	    } /* if !corners1[...].equals(corners2[...]) */

	    minimumElevation = (short) Math.min(minElev1, minElev2);
	    maximumElevation = (short) Math.max(maxElev1, maxElev2);

	    if (snFlag)
	    {
		if (columns1 != columns2)
		{
		    statusBar.setMessage("DEM geometry mismatch");
		    return false;
		} /* if columns1 != columns2 */
		rows = rows1 + rows2 - 1;
		columns = columns1;
	    } /* if snFlag */
	    else
	    {
		if (rows1 != rows2)
		{
		    statusBar.setMessage("DEM geometry mismatch");
		    return false;
		} /* if columns1 != columns2 */
		rows = rows1;
		columns = columns1 + columns2 - 1;
	    } /* if snFlag */

	    FileOutputStream fout = new FileOutputStream(resultFile);
            DeflaterOutputStream dout = new DeflaterOutputStream(fout);
	    DataOutputStream out = new DataOutputStream(dout);
	    out.writeUTF("merged DEM");
	    out.writeByte(units1);
	    if (snFlag)
	    { /* south-north */
		out.writeInt(corners1[SW_CORNER].getLatitude());
		out.writeInt(corners1[SW_CORNER].getLongitude());
		out.writeInt(corners2[NW_CORNER].getLatitude());
		out.writeInt(corners2[NW_CORNER].getLongitude());
		out.writeInt(corners2[NE_CORNER].getLatitude());
		out.writeInt(corners2[NE_CORNER].getLongitude());
		out.writeInt(corners1[SE_CORNER].getLatitude());
		out.writeInt(corners1[SE_CORNER].getLongitude());
	    } /* if snFlag */
	    else
	    { /* west-east */
		out.writeInt(corners1[SW_CORNER].getLatitude());
		out.writeInt(corners1[SW_CORNER].getLongitude());
		out.writeInt(corners1[NW_CORNER].getLatitude());
		out.writeInt(corners1[NW_CORNER].getLongitude());
		out.writeInt(corners2[NE_CORNER].getLatitude());
		out.writeInt(corners2[NE_CORNER].getLongitude());
		out.writeInt(corners2[SE_CORNER].getLatitude());
		out.writeInt(corners2[SE_CORNER].getLongitude());
	    } /* if snFlag */

	    out.writeShort(minimumElevation);
	    out.writeShort(maximumElevation);
	    out.writeByte(xRes1);
	    out.writeByte(yRes1);
	    out.writeByte(zRes1);
	    
	    out.writeShort(rows);
	    out.writeShort(columns);
	    
            int pctDone;
	    short elevation;
	    if (snFlag)
	    { /* south-north merge */
		for (i = 0; i < columns; i++)
		{
		    if (statusBar != null)
		    {
			pctDone = i * 100 / columns;
			statusBar.updateProgress(pctDone);
		    } /* if statusBar != null */
		    for (j = 0; j < rows1; j ++)
		    {
			elevation = in1.readShort();
			out.writeShort(elevation);
		    } /* for j */
		    for (j = 0; j < rows2; j ++)
		    {
			elevation = in2.readShort();
			if (j != 0)
			    out.writeShort(elevation);
		    } /* for j */
		} /* for i */
	    } /* if snFlag */
	    else
	    { /* west-east merge */
		int endColumn = columns1 - 1;
		for (i = 0; i < endColumn ; i++)
		{
		    if (statusBar != null)
		    {
			pctDone = i * 100 / columns;
			statusBar.updateProgress(pctDone);
		    } /* if statusBar != null */
		    for (j = 0; j < rows; j++)
		    {
			elevation = in1.readShort();
			out.writeShort(elevation);
		    } /* for j */
		} /* for i */
		endColumn = columns2;
		for (i = 0; i < endColumn ; i++)
		{
		    if (statusBar != null)
		    {
			pctDone = (i + columns1 - 1) * 100 / columns;
			statusBar.updateProgress(pctDone);
		    } /* if statusBar != null */
		    for (j = 0; j < rows; j++)
		    {
			elevation = in2.readShort();
			out.writeShort(elevation);
		    } /* for j */
		} /* for i */
	    } /* if (!) snFlag */

	    in1.close();
            iin1.close();
            fin1.close();
	    in1.close();
            iin1.close();
            fin1.close();
	    out.close();
            dout.close();
	    fout.close();
	} /* try */
	catch (FileNotFoundException e)
	{
	    new ErrorBox(demMain,
			 "Error merging!",
			 e.toString());
	    return false;
	}
	catch (IOException e)
	{
	    new ErrorBox(demMain,
			 "Error merging!",
			 e.toString());
	    return false;
	}
	catch (SecurityException e)
	{
	    new ErrorBox(demMain,
			 "Error merging!",
			 e.toString());
	    return false;
	}
        if (statusBar != null)
        {
            statusBar.setMessage("merge complete...");
            statusBar.updateProgress(0);
        } /* if statusBar != null */
	return true;
    } /* merge() */
} /* class DEM */

