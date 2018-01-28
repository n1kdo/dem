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

import java.awt.Point; /* used for generic x,y coordinates */

/**
 * a class that provides useful constants and calculations for GIS.
 */

public class GISCalculations
{
    /* A useful constant for degrees to radians */
    private final static double DEGREES_TO_RADIANS_FACTOR = Math.PI / 180.0;

    /* number of arc-seconds in 360 degrees */
    public final static int ARC_SECONDS_360_DEGREES = 1296000;

    /* number of arc-seconds in 90 degrees, used for north-south calculations
     * from the equator to a given point. */
    public final static int ARC_SECONDS_90_DEGREES = 324000;

    /* distance from the equator to geographic pole in meters. */
    public final static int METERS_EQUATOR_TO_POLE = 10000800;
    
    /* radius of the earth in meters */
    public final static int RADIUS_OF_EARTH = 6378000;

    /* circumference of earth in meters */
    public final static int CIRCUMFERENCE_OF_EARTH = 40074159;

    /**
     * Calculate the radius of the earth (in meters) at this latitude.
     * Takes into account the fact that the earth is not completely spherical.
     * @param latitude the latitude in arc-seconds.
     * @return the radius at that latitude.
     */
    public final static int getEarthRadius(int latitude)
    {
	double latitudeRadians = degreesToRadians(latitude / 3600.0);
	int r = (int) (RADIUS_OF_EARTH - (21000 * Math.sin(latitudeRadians)));
	return r;
    } /* getEarthRadius() */

    /**
     * calculate the circumference of the earth around a certain parallel.
     * @param latitude the latitude of the parallel to get the circumference for.
     * @return the circumference at that latitude, in meters.
     */
    public final static int getCircumferenceAtLatitude(int latitude)
    {
	double latitudeRadians = degreesToRadians(latitude / 3600.0);
	int r = (int) (RADIUS_OF_EARTH - (21000 * Math.sin(latitudeRadians)));
	int c = (int) (r * Math.cos(latitudeRadians) * 2.0 * Math.PI);
	return c; 
    } /* getCircumferenceAtLatitude() */

    /**
     * get the delta-z of earth curvature at a given distance and latitude.
     * @param d the distance in arc-seconds.
     * @param r the radius of the earth to compute with.
     * @return the delta-z of the earth's curvature at that point.
     */
    public final static short getEarthCurveDrop(int d, int r)
    {
 	double radians = degreesToRadians(d / 3600.0);
	return (short) (r * Math.cos(radians) - r);
    } /* getEarthCurveDrop() */

    /**
     * convert degrees to radians.
     * @param degrees arc, in degrees, to convert to radians.
     * @return radians of arc in degrees passed as parameter.
     */
    public final static double degreesToRadians(double degrees)
    {
	return (degrees * DEGREES_TO_RADIANS_FACTOR);
    } /* degreesToRadians() */

    /**
     * convert number of miles into map resolution units.
     * @param miles number of miles to convert.
     * @param mapResolution number of arc-seconds in one unit of map resolution.
     * @return number if map-resolution ticks (pixels) in the specified number of miles.
     */
    public final static int milesToMapResolution(int miles, int mapResolution)
    {
	float metersPerUnit = CIRCUMFERENCE_OF_EARTH / (float) (ARC_SECONDS_360_DEGREES) * mapResolution;
	float rDistance = miles * 1609.344f; /* meters in number of miles */
	return (int) (rDistance / metersPerUnit);
    } /* milesToMapResolution */

        /**
     * calculate a new point based on the supplied parameters.
     * @param x the x coordinate for the origin.
     * @param y the y coordinate for the origin.
     * @param angle the angle of the vector to the new point.
     * @param distance the distance (in points) to the new point.
     * @return the calculated point.
     */
    public final static Point addVector(int x, int y, float angle, int distance)
    {
	double angleRadians = degreesToRadians(angle);
	int newX = x + (int) (Math.sin(angleRadians) * distance);
	int newY = y + (int) (Math.cos(angleRadians) * distance);
	return new Point(newX, newY);
    } /* addVector() */

} /* GISCalculations */
