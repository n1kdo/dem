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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.IndexColorModel;
import java.awt.image.MemoryImageSource;

/**
 * a class that displays a color coded map of a DEM.
 */
public class Map extends Canvas implements MouseListener, MouseMotionListener
{
    private final static int NUM_COLORS = 24;
    private final static int TOTAL_COLORS = NUM_COLORS + NUM_COLORS + 3;
    private final static int BLACK = NUM_COLORS + NUM_COLORS;
    private final static int WHITE = BLACK + 1;
    private final static int MARKER = BLACK + 2;
    Image image;
    IndexColorModel colorModel;
    DEM dem;
    DEMmain demMain;
    StatusBar statusBar;
    int width;
    int height;
    GeoCoordinate swCorner;
    int lat;
    int lon;
    int xResolution;
    int yResolution;
    boolean coordsDisplayed = false;

    /**
     * create a new Map with the specified StatusBar used for status information.
     * @param sb the StatusBar to update during computation of the map.
     */
    public Map(DEMmain demMain)
    {
	super();
	this.demMain = demMain;
        statusBar = demMain.statusBar;
        width = 0;
        height = 0;
	setSize(width, height);
        byte reds[] = new byte[TOTAL_COLORS];
        byte blues[] = new byte[TOTAL_COLORS];
        byte greens[] = new byte[TOTAL_COLORS];

	int colorBase = 0x44;
	int colorWhitener = 0;
	int colorIncrement = 0x11;
	int whitenerIncrement = 0x11; 

	int shadowColorBase = 0x33;
	int shadowColorWhitener = 0;
	int shadowColorIncrement = 0x11;
	int shadowWhitenerIncrement = 0x11; 
	int i, j;
	for (i = 0; i < NUM_COLORS; i++)
	{
	    //System.out.println("i="+i+" base="+colorBase+" whitener="+colorWhitener);
	    j = i + NUM_COLORS;
	    reds[i] = (byte) colorWhitener;
	    blues[i] = (byte) colorWhitener;
	    greens[i] = (byte) colorBase;
	    
	    reds[j] = (byte) shadowColorBase;
	    blues[j] = (byte) shadowColorWhitener;
	    greens[j] = (byte) shadowColorBase;
	    if (colorBase < 0xff)
		colorBase = colorBase + colorIncrement;
	    else
		colorWhitener = colorWhitener + whitenerIncrement;

	    if (shadowColorBase < 0xff)
		shadowColorBase = shadowColorBase + shadowColorIncrement;
	    else
		shadowColorWhitener = shadowColorWhitener + shadowWhitenerIncrement;
	} /* for i */
	reds[BLACK] = 0;
	blues[BLACK] = 0;
	greens[BLACK] = 0;
	reds[WHITE] = (byte) 255;
	blues[WHITE] = (byte) 255;
	greens[WHITE] = (byte) 255;

	reds[MARKER] = (byte) 0xff;
	blues[MARKER] = (byte) 0;
	greens[MARKER] = (byte) 0;
        colorModel = new IndexColorModel(8, TOTAL_COLORS, reds, greens, blues);
	addMouseListener(this);
	addMouseMotionListener(this);
    } /* Map constructor */

    /**
     * Assign a DEM object to the Map as a data source for images.
     * @param dem the DEM to assign to the MAP.
     */
    public void setDEM(DEM dem)
    {
	this.dem = dem;
        width = dem.getColumns();
        height = dem.getRows();
	setSize(width, height);
	GeoCoordinate swCorner;
	swCorner = dem.getCorner(DEM.SW_CORNER);
	lat = swCorner.getLatitude();
	lon = swCorner.getLongitude();
	xResolution = dem.getXResolution();
	yResolution = dem.getYResolution();
        mapImage();
	getParent().validate();
        repaint();
    } /* setDEM() */

    /**
     * calculate the radio horizon based on supplied parameters.
     * @param dem the DEM to plot against.
     * @param location the transmitter antenna location
     * @param amsl the height of the antenna above sea level.
     * @param receiverHeight the height of the receiver antenna above ground.
     * @param degreeIncrement the angular increment to vector around the transmitter.
     * @param distanceIncrement the distance increment to vector around the transmitter.
     * @param kMTick the grid distance to draw on the map.
     */
    public void plotCoverage(DEM dem,
			     GeoCoordinate location,
			     short amsl,
			     short receiverHeight,
			     float degreeIncrement,
			     int distanceIncrement,
                             int kMTick)
    {
        int i, j;
	this.dem = dem;
        width = dem.getColumns();
        height = dem.getRows();
	setSize(width, height);

	GeoCoordinate swCorner;
	swCorner = dem.getCorner(DEM.SW_CORNER);
	lat = swCorner.getLatitude();
	lon = swCorner.getLongitude();
	xResolution = dem.getXResolution();
	yResolution = dem.getYResolution();
	image = null;
	byte pixels[] = generateImagePixels();
	
	int latitude = location.getLatitude();
	int x = (location.getLongitude() - lon) / xResolution;
	int y = (latitude  - lat) / yResolution;

	/* draw the center marker */
	byte mc = (byte) MARKER;
 	for (i=-1;i<=1;i++)
	{
	    updatePixel(pixels, x+i, y-3, mc);
	    updatePixel(pixels, x+i, y+3, mc);
	    updatePixel(pixels, x-3, y+i, mc);
	    updatePixel(pixels, x+3, y+i, mc);
	} /* for i */

	for (i=-2;i<=2;i++)
	{
	    updatePixel(pixels, x+i, y-2, mc);
	    updatePixel(pixels, x+i, y+2, mc);
	    updatePixel(pixels, x-2, y+i, mc);
	    updatePixel(pixels, x+2, y+i, mc);
	} /* for i */
	updatePixel(pixels, x-1, y-1, mc);
	updatePixel(pixels, x-1, y+1, mc);
	updatePixel(pixels, x+1, y-1, mc);
	updatePixel(pixels, x+1, y+1, mc);
	
        float degrees;
        int distance;
        int maxDistance = (int) Math.sqrt((double)(width * width + height * height));
        int pctDone = 0;
        short elevation;
	boolean vectorOK;
	Point point;
	int lx, ly;
	short elevations[];
	short earthCurveDeltaZ[];
	int maxElevations = maxDistance / distanceIncrement;
	elevations = new short[maxElevations];
	earthCurveDeltaZ = new short[maxElevations];
	int numElevations;
	float slope;
	boolean occluded;
	short lineElev;
	int r = GISCalculations.getEarthRadius(latitude) * 4 / 3;
	
	numElevations = 0;
	statusBar.setMessage("calculating curvature...");
	for (distance = distanceIncrement; distance < maxDistance; distance += distanceIncrement)
	{
            pctDone = distance * 100 / maxDistance;
            statusBar.updateProgress(pctDone);
	    earthCurveDeltaZ[numElevations++] = GISCalculations.getEarthCurveDrop(distance, r);	    
	} /* for distance */

	for (degrees = (float) 0.0; degrees < 360.0; degrees += degreeIncrement)
        {
            pctDone = (int) (degrees / 360.0 * 100);
            statusBar.updateProgress(pctDone);
	    statusBar.setMessage("working on vector "+degrees);
	    numElevations = 0;
	    vectorOK = true;
	    elevations[numElevations++] = (short) amsl;
	    for (distance = distanceIncrement; (distance < maxDistance) && vectorOK; distance += distanceIncrement)
	    {
		point = addVector(x, y, degrees, distance);
		if (point == null)
		{
		    vectorOK = false;
		} /* if lx... */
		else
		{ /* valid spot on the map */
		    lx = point.x;
		    ly = point.y;
		    /* check for visibility here */
		    elevation = (short) (dem.getElevation(lx,ly) - earthCurveDeltaZ[numElevations]);
		    elevations[numElevations] = elevation;
		    slope = (float) ((elevation + receiverHeight) - amsl) / (float) numElevations;
		    occluded = false;
		    j = numElevations - 1;
		    for (i = 1; (i < j) && (!occluded); i++)
		    {
			lineElev = (short) ((i * slope) + amsl);
			if (lineElev < elevations[i])
			{
			    occluded = true;
			} /* if lineElev < elevations[i] */
		    } /* for i */
		    
		    if (occluded)
		    {
			dimPixel(pixels, lx, ly);
		    } /* if occluded */
		    numElevations++;
		} /* if lx... */
	    } /* for distance */
        } /* for degrees */

	if (kMTick != 0)
	{ /* add tick marks */
	    double pixelsPerKMLat = (GISCalculations.ARC_SECONDS_90_DEGREES / yResolution) /
		10000.8;
	    int c = GISCalculations.getCircumferenceAtLatitude(latitude);
	    double pixelsPerKMLon = (GISCalculations.ARC_SECONDS_360_DEGREES / xResolution) /
		(GISCalculations.getCircumferenceAtLatitude(latitude) / 1000.0);
	    
	    int kMLat;
	    int startKMLat = 0;
	    int stopKMLat = 0;
	    int kMLon;
	    int startKMLon = 0;
	    int stopKMLon = 0;
	    int tx = x;
	    int ty = y;
	    while (ty > 0)
	    {
		ty = y + (int) (startKMLat * pixelsPerKMLat);
		startKMLat -= kMTick;
	    } /* while ty > 0 */
	    startKMLat += kMTick;
	    while (ty < height)
	    {
		ty = y + (int) (stopKMLat * pixelsPerKMLat);
		stopKMLat += kMTick;
	    } /* while ty < height */
	    stopKMLat -= kMTick;
	    while (tx > 0)
	    {
		tx = x + (int) (startKMLon * pixelsPerKMLon);
		startKMLon -= kMTick;
	    } /* while tx > 0 */
	    startKMLon += kMTick;
	    while (tx < width)
	    {
		tx = x + (int) (stopKMLon * pixelsPerKMLon);
		stopKMLon += kMTick;
	    } /* while tx < width */
	    stopKMLon -= kMTick;
	    for (kMLat = startKMLat; kMLat <= stopKMLat; kMLat += kMTick)
	    {
                ty = y + (int) (kMLat * pixelsPerKMLat);
                for (kMLon = startKMLon; kMLon <= stopKMLon; kMLon += kMTick)
		{
		    if ((kMLat != 0) || (kMLon != 0))
		    {
			tx = x + (int) (kMLon * pixelsPerKMLon);
			for (i = -2; i < 3; i++)
			{
			    updatePixel(pixels, tx+i, ty, (byte) MARKER);
			    updatePixel(pixels, tx, ty+i, (byte) MARKER);
			} /* for i */
		    } /* if kMLat != 0 && kMLon != 0 */
		} /* for kMLon */
	    } /* for kMLat */
	} /* if ... */

        image = createImage(new MemoryImageSource(width,
						  height,
						  colorModel,
						  pixels,
						  0,
						  width));
	if (statusBar != null)
	{
	    statusBar.updateProgress(0);
	    statusBar.setMessage("");
	} /* if statusBar != null */
	setSize(width, height);
	getParent().validate();
        repaint();
    } /* plotCoverage() */

    /**
     * change the value of a pixel in an array of pixels intended for MemoryImageSource.
     * @param pixels the pixel array to modify.
     * @param x the x offset from the SW corner.
     * @param y the y offset from the SW corner.
     * @param newPixel the new value for the pixel.
     */
    void updatePixel(byte[] pixels, int x, int y, byte newPixel)
    {
	if ((x < 0) || (x >= width) || (y < 0) || (y >= height))
	    return;
        y = height - y - 1;
	int index;
	index = x + (y * width);
	pixels[index] = newPixel;
    } /* updatePixel() */

    /**
     * dim a pixel.
     * @param pixels the pixel array to modify.
     * @param x the x offset from the SW corner.
     * @param y the y offset from the SW corner.
     */
    void dimPixel(byte[] pixels, int x, int y)
    {
	if ((x < 0) || (x >= width) || (y < 0) || (y >= height))
	    return;
        y = height - y - 1;
	int index;
	index = x + (y * width);
	int pix = pixels[index];
	if (pix < NUM_COLORS)
	{
	    pixels[index] = (byte) (pix + NUM_COLORS);
	} /* if pix < NUM_COLORS */
    } /* updatePixel() */

    /**
     * calculate a new point based on the supplied parameters.
     * @param x the x coordinate for the origin.
     * @param y the y coordinate for the origin.
     * @param angle the angle of the vector to the new point.
     * @param distance the distance (in points) to the new point.
     * @return the calculated point, or null if it is not on the map.
     */
    public Point addVector(int x, int y, float angle, int distance)
    {
	double angleRadians = GISCalculations.degreesToRadians(angle);
	int newX = x + (int) (Math.sin(angleRadians) * distance);
	int newY = y + (int) (Math.cos(angleRadians) * distance);
	if ((newX >= 0) &&
	    (newX < width) &&
	    (newY >= 0) &&
	    (newY < height))
	    return new Point(newX, newY);
	else
	{
	    return null;
	}
    } /* addVector() */

    /**
     * display the map image.
     */
    public void mapImage()
    {
        if (dem != null)
        {
	    image = null;
	    byte pixels[] = generateImagePixels();
            image = createImage(new MemoryImageSource(width, height, colorModel, pixels, 0, width));
        } /* if dem != null */
	if (statusBar != null)
	{
	    statusBar.updateProgress(0);
	    statusBar.setMessage("");
	} /* if statusBar != null */
	setSize(width, height);
    } /* mapImage() */

    /**
     * generate a pixels array for the image.
     */
    private byte[] generateImagePixels()
    {
	byte pixels[] = new byte[width*height];
	int index = 0;
	
	short elevation;
	short minimumElevation = dem.getMinimumElevation();
	int interval = (dem.getMaximumElevation() - minimumElevation) / NUM_COLORS + 1;
	int i, j, k;
	int x, y;
	int pctDone;
	
	if (statusBar != null)
	{
	    statusBar.setMessage("Creating image,  please wait...");
	} /* if statusBar != null */
	
	for (j =0; j < height; j++)
	{
	    if (statusBar != null)
	    {
		pctDone = j * 100 / height;
		statusBar.updateProgress(pctDone);
	    } /* if statusBar != null */
	    y = height - j - 1;
	    for (i =0; i < width; i++)
	    {
		x = i;
		elevation = dem.getElevation(x,y);
		k = (elevation - minimumElevation) / interval;
		if ((k >= NUM_COLORS) || (k < 0))
		{
		    System.err.println("("+x+","+y+")"+
				       " k = "+ k +
				       " elev = " + elevation +
				       " min = "+ dem.getMinimumElevation() +
				       " max = " + dem.getMaximumElevation());
		    k = BLACK;
		} /* if k >= NUM_COLORS */
		pixels[index++] = (byte) k;
	    } /* for i */
	} /* for j */
	return pixels;
    } /* generateImagePixels */
    
    /**
     * draw the map image into the graphics object.
     * @param g the graphics object to receive the image for display.
     */
    public void paint(Graphics g)
    {
        if (image != null)
        {
            g.drawImage(image,0,0,this);
        } /* if dem != null */
    } /* paint */

    /**
     * process mousePressed events.
     * @param e the MouseEvent to process.
     */
    public void mousePressed(MouseEvent e)
    {
	if ((image != null) && (statusBar != null))
	{
	    int x = e.getX();
	    int y = height - e.getY() - 1;
	    if ((x <= width) &&
		(y >= 0) &&
		(y <= height))
	    {
		int longitude = lon + (x * xResolution);
		int latitude  = lat + (y * yResolution);
		demMain.antennaLocationDialog.setCoordinates(new GeoCoordinate(latitude, longitude));
		if (e.getModifiers() == InputEvent.BUTTON3_MASK)
		{
		    demMain.startAction(demMain.COVERAGE);
		} /* if e.getModifiers() == */
	    } /* if x <= width */
	} /* if image != null */
    } /* mousePressed() */

    /**
     * process mouseReleased events.
     * @param e the MouseEvent to process.
     */
    public void mouseReleased(MouseEvent e)
    {
    } /* mouseReleased() */

    /**
     * process mouseClicked events.
     * @param e the MouseEvent to process.
     */
    public void mouseClicked(MouseEvent e)
    {
    } /* mouseClicked() */

    /**
     * process mouseEntered events.
     * @param e the MouseEvent to process.
     */
    public void mouseEntered(MouseEvent e)
    {
    } /* mouseEntered() */

    /**
     * process mouseExited events.  remove the coordinate status message.
     * @param e the MouseEvent to process.
     */
    public void mouseExited(MouseEvent e)
    {
	if ((image != null) && (statusBar != null) && coordsDisplayed)
	{
	    statusBar.setMessage("");
	    coordsDisplayed = false;
	} /* if image != null */
    } /* mouseExited() */

    /**
     * process mouseDragged events.
     * @param e the MouseEvent to process.
     */
    public void mouseDragged(MouseEvent e)
    {
    } /* mouseDragged() */

    /**
     * process mouseMoved events. display the coordinates pointed at by the mouse.
     * @param e the MouseEvent to process.
     */
    public void mouseMoved(MouseEvent e)
    {
	if ((image != null) && (statusBar != null))
	{
	    int x = e.getX();
	    int y = height - e.getY() - 1;
	    if ((x <= width) &&
		(y >= 0) &&
		(y <= height))
	    {
		int longitude = lon + (x * xResolution);
		int latitude  = lat + (y * yResolution);
		statusBar.setMessage("" + dem.getElevation(x,y) + " "+GeoCoordinate.toString(latitude,longitude));
		coordsDisplayed = true;
	    } /* if x <= width */
	    else
	    {
		statusBar.setMessage("");
		coordsDisplayed = false;
	    } /* if x <= width */
	} /* if image != null */
    } /* mouseDragged() */

    /**
     * Gets the preferred size of the Map.
     * @return A dimension object indicating this Map's preferred size.
     */
    public Dimension getPreferredSize()
    {
	return getMinimumSize();
    } /* getPreferredSize() */

    /**
     * Gets the minimum size of the Map.
     * @return A dimension object indicating this Map's minimum size.
     */
    public java.awt.Dimension getMinimumSize()
    {
	return new Dimension(width, height);
    } /* getMinimumSize() */

} /* class Map */

