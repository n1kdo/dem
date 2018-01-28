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
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Point; /* used for generic x,y coordinates */
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * a class that provides a dialog box for parameters used to calculate
 * AHAAT (antenna height above average terrain.)
  */
public class AHAATDialog extends Dialog implements ActionListener
{
    GeoCoordinate gc_location;
    Label lbl_location;
    TextField txt_heightAboveGround;
    TextField txt_heightAboveSeaLevel;
    DEM dem;
    DEMmain demMain;
    boolean actionOK;
    
    /**
     * create a new AHAATDialog.
     * @param demMain the Frame that owns the application.
     */
    public AHAATDialog(DEMmain demMain)
    {
	super(demMain, "AHAAT Calculation", true);
	this.demMain = demMain;
	dem = demMain.dem;
	Button btn_setLocation;
	Button btn_ok;
	Button btn_cancel;
	GroupLayout layout = new GroupLayout(1,4);
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	layout.setAnchor(GroupLayout.CENTER);

	Panel pnl_location = new Panel(new GroupLayout(3,1));
	pnl_location.add(new Label("Location"));
	pnl_location.add(lbl_location = new Label(""));
	lbl_location.setBackground(Color.white);
	pnl_location.add(btn_setLocation = new Button("Set Location"));
	add(pnl_location);

	Panel pnl_heights = new Panel(new GroupLayout(2,4));
	pnl_heights.add(new Label("Height Above Ground (meters)"));
	pnl_heights.add(txt_heightAboveGround = new TextField(4));
	pnl_heights.add(new Label("Ground Height Above Sea Level(meters)"));
	pnl_heights.add(txt_heightAboveSeaLevel = new TextField(4));
	add(pnl_heights);

	Panel pnl_buttons = new Panel(new GroupLayout(2,1));
	pnl_buttons.add(btn_cancel = new Button("Cancel"));
	pnl_buttons.add(btn_ok = new Button("OK"));
	add(pnl_buttons);
	
	btn_setLocation.addActionListener(this);
	btn_ok.addActionListener(this);
	btn_cancel.addActionListener(this);

	setCoordinates(dem.getCenter());

	int width = 400;
	int height = 250;
	Rectangle parentRect = demMain.getBounds();
	int xOffset = parentRect.x + (parentRect.width - width) / 2;
	int yOffset = parentRect.y + (parentRect.height - height) / 2;
	setBounds(xOffset, yOffset, width, height);
    } /* AHAATDialog() */

    /**
     * manage button presses.
     * @param e the ActionEvent from the button.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	if (arg.equals("OK"))
	{
	    actionOK = true;
	    setVisible(false);
	    return;
	} /* if arg.equals("OK") */

	if (arg.equals("Cancel"))
	{
	    actionOK = false;
	    setVisible(false);
	    return;
	} /* if arg.equals("Cancel") */

	if (arg.equals("Set Location"))
	{
	    CoordinateDialog coordDialog = new CoordinateDialog(demMain, "Enter Antenna Location", null);
	    coordDialog.setCoordinates(getCoordinates());
	    coordDialog.show();
	    setCoordinates(coordDialog.getCoordinates());
	    return;
	} /* if arg.equals("OK") */
    } /* actionPerformed() */

    /**
     * get the GeoCoordinate for the specified location.
     * @return the GeoCoordinate for the latitude/longitude chosen.
     */
    public GeoCoordinate getCoordinates()
    {
	return gc_location;
    } /* getCoordinates */
	
    /**
     * set the GeoCoordinate for the specified location.
     * @param the GeoCoordinate for the latitude/longitude to set.
     */
    public void setCoordinates(GeoCoordinate gc)
    {
	gc_location = gc;
	lbl_location.setText(gc_location.toString());
	if (dem != null)
	{
	    txt_heightAboveSeaLevel.setText(Integer.toString(dem.getElevation(gc_location)));
	} /* if dem != null */
    } /* setCoordinates */
	
    /**
     * set the DEM to work against.
     * @param the DEM for the model.  Used to get default antenna elevation.
     */
    public void setDEM(DEM dem)
    {
	this.dem = dem;
	setCoordinates(dem.getCorner(DEM.SW_CORNER));
    } /* setDEM */
	
    /**
     * get the antenna height above ground.
     * @return the antenna height above ground.
     */
    public int getHeightAboveGround()
    {
	return Util.safeStringToInt(txt_heightAboveGround.getText());
    } /* getHeightAboveGround */
	
    /**
     * get the height above sea level.
     * @return the height above sea level.
     */
    public int getHeightAboveSeaLevel()
    {
	return Util.safeStringToInt(txt_heightAboveSeaLevel.getText());
    } /* getHeightAboveSeaLevel */

    /**
     * process this dialog's action.  Done this way to not hog the
     * AWT thread that responds to OK button press.
     */
    void dialogAction()
    {
	if (actionOK)
	{
	    doAHAAT(demMain.dem,
		    getCoordinates(),
		    (short) (getHeightAboveGround() + getHeightAboveSeaLevel()));
	} /* if actionOK */
    } /* dialogAction() */

    /**
     * calculate AHAAT by projecting a vector every 45 degrees and getting
     * the elevations at points on this vector at 2, 4, 6, 8, and 10 miles.
     */
    void doAHAAT(DEM dem, GeoCoordinate location, short amsl)
    {
	GeoCoordinate swCorner = dem.getCorner(DEM.SW_CORNER);
	int lat = swCorner.getLatitude();
	int lon = swCorner.getLongitude();
	int xResolution = dem.getXResolution();
	int yResolution = dem.getYResolution();

	location = getCoordinates();

	int latitude = location.getLatitude();
	int x = (location.getLongitude() - lon) / xResolution;
	int y = (latitude  - lat) / yResolution;
	int lx;
	int ly;
	int miles;
	int angle;
	int units;
	short elevation;
	Point point;
	int totalElevation = 0;
	int totalPoints = 0;
	StringBuffer report = new StringBuffer("AHAAT Calculation\n\n");
	String reportLine;
	for (angle=0; angle<360; angle += 45)
	{ /* do the 8 vectors */
	    for (miles = 2; miles <= 10; miles += 2)
	    { /* do the 5 elevation checks every 2 miles out this vector */
		units = GISCalculations.milesToMapResolution(miles, dem.getXResolution());
		point = GISCalculations.addVector(x, y, angle, units);
		
		lx = point.x;
		ly = point.y;
		if (dem.isValidLocation(lx, ly))
		{ /* location is on the DEM */
		    elevation = dem.getElevation(lx,ly);
		    System.out.println("("+angle+","+miles+":"+units+") "+lx+" "+ly+" "+elevation);
		    totalElevation += elevation;
		    totalPoints++;
		    report.append(elevation + " ");
		} /* if dem.isValidLocation... */
		else
		{ /* location is not on the DEM */
		    report.append("n/a ");
		} /* if dem.isValidLocation... */
	    } /* for miles */
	    report.append("\n");
	} /* for angle */
	int ahaat =  totalElevation / totalPoints;
	report.append("\n");
	report.append("Average Terrain is " + ahaat + " meters\n");
	report.append("Antenna height is " + amsl + " meters.\n");
	report.append("AHAAT is " + (amsl - ahaat) + " meters.");
	System.out.println(report);
	new ErrorBox(demMain, "AHAAT Analysis", report.toString());
    } /* doAHAAT() */
} /* class AHAATDialog */
