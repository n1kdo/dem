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

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * a class that provides a latitude/longitude dialog box.
 */
public class CoordinateDialog extends Dialog implements ActionListener
{
    TextField txt_latDeg;
    TextField txt_latMin;
    TextField txt_latSec;
    TextField txt_lonDeg;
    TextField txt_lonMin;
    TextField txt_lonSec;
    Checkbox cbo_east;
    Checkbox cbo_west;
    Checkbox cbo_north;
    Checkbox cbo_south;
    Button btn_ok;

    /**
     * create a new CoordinateDialog.
     * @param parent the Frame that owns the application.
     * @param title text to show in the title bar of the dialog.
     * @param caption text to display inside the dialog box.
     */
    public CoordinateDialog(Frame parent, String title, String caption)
    {
	super(parent, title, true);
	GroupLayout layout = new GroupLayout(1,((caption == null) ? 2 : 3));
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	layout.setAnchor(GroupLayout.CENTER);

	CheckboxGroup cbg_ns = new CheckboxGroup();
	CheckboxGroup cbg_ew = new CheckboxGroup();

	if (caption != null)
	{
	    add(new Label(caption));
	}
	
	Panel pnl_coords = new Panel(new GroupLayout(6,3));
	pnl_coords.add(new Label(""));
	pnl_coords.add(new Label("Deg"));
	pnl_coords.add(new Label("Min"));
	pnl_coords.add(new Label("Sec"));
	pnl_coords.add(new Label(""));
	pnl_coords.add(new Label(""));

	pnl_coords.add(new Label("Latitude", Label.RIGHT));
	pnl_coords.add(txt_latDeg = new TextField(4));
	pnl_coords.add(txt_latMin = new TextField(2));
	pnl_coords.add(txt_latSec = new TextField(2));
	pnl_coords.add(cbo_north = new Checkbox("N", true, cbg_ns));
	pnl_coords.add(cbo_south = new Checkbox("S", false, cbg_ns));
	
	pnl_coords.add(new Label("Longitude", Label.RIGHT));
	pnl_coords.add(txt_lonDeg = new TextField(4));
	pnl_coords.add(txt_lonMin = new TextField(2));
	pnl_coords.add(txt_lonSec = new TextField(2));
	pnl_coords.add(cbo_west = new Checkbox("W", true, cbg_ew));
	pnl_coords.add(cbo_east = new Checkbox("E", false, cbg_ew));
	layout.setAnchor(GroupLayout.NORTH);
	add(pnl_coords);
	layout.setAnchor(GroupLayout.SOUTH);
	add(btn_ok = new Button("OK"));
	btn_ok.addActionListener(this);
	int width = 300;
	int height = 200;
	Rectangle parentRect = parent.getBounds();
	int xOffset = parentRect.x + (parentRect.width - width) / 2;
	int yOffset = parentRect.y + (parentRect.height - height) / 2;
	setBounds(xOffset, yOffset, width, height);
    } /* CoordinateDialog() */

    /**
     * manage action of the OK button.
     * @param e the ActionEvent from the OK button.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	setVisible(false);
    } /* actionPerformed() */

    /**
     * set the coordinates shown in the dialog.
     * @param gc the coordinates to set the dialog to.
     */
    public void setCoordinates(GeoCoordinate gc)
    {
	int lat = gc.getLatitude();
	if (lat < 0)
	{
	    lat = -lat;
	    cbo_south.setState(true);
	} /* if lat < 0 */
	else
	{
	    cbo_north.setState(true);
	} /* if lat < 0 */

	int lat_deg = lat / 3600;
	lat = lat - lat_deg * 3600;
	int lat_min = lat / 60;
	lat = lat - lat_min * 60;
	txt_latDeg.setText(Integer.toString(lat_deg));
	txt_latMin.setText(Integer.toString(lat_min));
	txt_latSec.setText(Integer.toString(lat));

	int lon = gc.getLongitude();
	if (lon < 0)
	{
	    lon = -lon;
	    cbo_west.setState(true);
	} /* if lon < 0 */
	else
	{
	    cbo_east.setState(true);
	} /* if lon < 0 */

	int lon_deg = lon / 3600;
	lon = lon - lon_deg * 3600;
	int lon_min = lon / 60;
	lon = lon - lon_min * 60;
	txt_lonDeg.setText(Integer.toString(lon_deg));
	txt_lonMin.setText(Integer.toString(lon_min));
	txt_lonSec.setText(Integer.toString(lon));
    } /* setCoordinates */
    
    /**
     * get the GeoCoordinate object that represents the latitude/longitude
     * entered in the dialog box.
     * @return the GeoCoordinate object representing the latitude/longitude
     * of the coordinates entered in the dialog.
     */
    public GeoCoordinate getCoordinates()
    {
	int latDeg = Util.safeStringToInt(txt_latDeg.getText());
	if (cbo_south.getState())
	{ /* southern latitude */
	    latDeg = 0 - latDeg;
	} /* if cbo_south.getState() */
	int latMin = Math.max(0,Util.safeStringToInt(txt_latMin.getText())); 
	int latSec = Math.max(0,Util.safeStringToInt(txt_latSec.getText())); 
	int lonDeg = Util.safeStringToInt(txt_lonDeg.getText()); 
	if (cbo_west.getState())
	{ /* western longitude */
	    lonDeg = 0 - lonDeg;
	} /* if cbo_west.getState() */
	int lonMin = Math.max(0,Util.safeStringToInt(txt_lonMin.getText())); 
	int lonSec = Math.max(0,Util.safeStringToInt(txt_lonSec.getText()));
	return new GeoCoordinate(latDeg, latMin, latSec, lonDeg, lonMin, lonSec);
    } /* getCoordinates */
	
} /* class CoordinateDialog */

