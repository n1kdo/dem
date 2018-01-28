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
 * a class that represents geographic coordinates of latitude and longitude.
 */
public class GeoCoordinate
{
    private int longitude; /* arc-seconds */
    private int latitude; /* arc-seconds */

    /**
     * create a new GeoCoordinate.
     * @param gc Geocoordinate to copy.
     */
    public GeoCoordinate(GeoCoordinate gc)
    {
	latitude  = gc.getLatitude();
	longitude = gc.getLongitude();
    } /* GeoCoordinate constructor */

    /**
     * create a new GeoCoordinate.
     * @param lat latitude in arc-seconds.
     * @param lon longitude in arc-seconds.
     */
    public GeoCoordinate(int lat, int lon)
    {
	latitude = lat;
	longitude = lon;
    } /* GeoCoordinate constructor */

    /**
     * create a new GeoCoordinate.
     * @param latDeg latitude degrees.
     * @param latMin latitude minutes.
     * @param latSec latitude seconds.
     * @param lonDeg longitude degrees.
     * @param lonMin longitude minutes.
     * @param lonSec longitude seconds.
     */
    public GeoCoordinate(int latDeg, int latMin, int latSec,
			 int lonDeg, int lonMin, int lonSec)
    {
	if (latDeg < 0)
	{
	    latitude  = latDeg * 3600 - latMin * 60 - latSec;
	} /* if latDeg < 0 */
	else
	{
	    latitude  = latDeg * 3600 + latMin * 60 + latSec;
	} /* if latDeg < 0 */
	if (lonDeg < 0)
	{
	    longitude = lonDeg * 3600 - lonMin * 60 - lonSec;;
	} /* if lonDeg < 0 */
	else
	{
	    longitude = lonDeg * 3600 + lonMin * 60 + lonSec;;
	} /* if lonDeg < 0 */
    } /* GeoCoordinate constructor */

    /**
     * get the latitude component from the GeoCoordinate.
     * @return an integer representing the latitude of the
     * GeoCoordinate in arc-seconds.
     */
    public int getLatitude()
    {
	return latitude;
    } /* getLatitude() */

    /**
     * get the longitude component from the GeoCoordinate.
     * @return an integer representing the longitude of the
     * GeoCoordinate in arc-seconds.
     */
    public int getLongitude()
    {
	return longitude;
    } /* getLongitude() */

    /**
     * test to see if this GeoCoordinate is the same as another.
     * @param gc the GeoCoordinate to compare to.
     * @return true if the GeoCoordinates are equal.
     */
    public boolean equals(GeoCoordinate gc)
    {
	return ((latitude  == gc.getLatitude()) &&
		(longitude == gc.getLongitude()));
    } 

    /**
     * convert this GeoCoordinate to a string.
     * @return a string representing the human-readable coordinates.
     */
    public String toString()
    {
	return toString(latitude, longitude);
    } /* toString() */

    /**
     * convert a pair of geographic coordinates to a string.
     * @param latitude latitude in arc-seconds.
     * @param longitude longitude in arc-seconds.
     * @return a string representing the human-readable coordinates.
     */
    public static String toString(int latitude, int longitude)
    {
	int lon = Math.abs(longitude);
        int lon_deg = lon / 3600;
	lon -= lon_deg * 3600;
	int lon_min = lon / 60;
	lon -= lon_min * 60;
	int lat = Math.abs(latitude);
        int lat_deg = lat / 3600;
	lat -= lat_deg * 3600;
	int lat_min = lat / 60;
	lat -= lat_min * 60;
	return new String (lon_deg + "d " +
			   lon_min + "m " +
			   lon + "s " + ((longitude >= 0) ? "E, " : "W, ") +
			   lat_deg + "d " +
			   lat_min + "m " +
			   lat + "s " + ((latitude >= 0) ? "N" : "S"));
    } /* toString() */
} /* class GeoCoordinate */
