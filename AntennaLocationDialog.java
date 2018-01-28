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
import java.awt.Rectangle;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * a class that provides a dialog box for placing the transmitting antenna
 * on the DEM, and setting the parameters for shadowing calculations.
 */
public class AntennaLocationDialog extends Dialog implements ActionListener
{
    GeoCoordinate gc_location;
    Label lbl_location;
    TextField txt_heightAboveGround;
    TextField txt_heightAboveSeaLevel;
    TextField txt_receiveHeight;
    TextField txt_angleIncrement;
    TextField txt_distanceIncrement;
    TextField txt_tickDistance;
    DEM dem;
    DEMmain demMain;
    boolean actionOK;
    
    /**
     * create a new AntennaLocationDialog.
     * @param demMain the Frame that owns the application.
     */
    public AntennaLocationDialog(DEMmain demMain)
    {
	super(demMain, "Enter Antenna Location", true);
	this.demMain = demMain;
	Button btn_setLocation;
	Button btn_ok;
	Button btn_cancel;
	GroupLayout layout = new GroupLayout(1,4);
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	layout.setAnchor(GroupLayout.CENTER);
	gc_location = new GeoCoordinate(0,0);

	Panel pnl_location = new Panel(new GroupLayout(3,1));
	pnl_location.add(new Label("Location"));
	pnl_location.add(lbl_location = new Label(gc_location.toString()));
	lbl_location.setBackground(Color.white);
	pnl_location.add(btn_setLocation = new Button("Set Location"));
	add(pnl_location);

	Panel pnl_heights = new Panel(new GroupLayout(2,6));
	pnl_heights.add(new Label("Height Above Ground (meters)"));
	pnl_heights.add(txt_heightAboveGround = new TextField(4));
	pnl_heights.add(new Label("Ground Height Above Sea Level(meters)"));
	pnl_heights.add(txt_heightAboveSeaLevel = new TextField(4));
	pnl_heights.add(new Label("Receive Antenna Height Above Ground (meters)"));
	pnl_heights.add(txt_receiveHeight = new TextField(4));
	pnl_heights.add(new Label("Analysis Angle Increment (degrees)"));
	pnl_heights.add(txt_angleIncrement = new TextField(4));
	pnl_heights.add(new Label("Analysis Distance Increment (points)"));
	pnl_heights.add(txt_distanceIncrement = new TextField(3));
	pnl_heights.add(new Label("Tick Distance (KM)"));
	pnl_heights.add(txt_tickDistance = new TextField(3));
	add(pnl_heights);

	Panel pnl_buttons = new Panel(new GroupLayout(2,1));
	pnl_buttons.add(btn_cancel = new Button("Cancel"));
	pnl_buttons.add(btn_ok = new Button("OK"));
	add(pnl_buttons);
	
	btn_setLocation.addActionListener(this);
	btn_ok.addActionListener(this);
	btn_cancel.addActionListener(this);

	txt_receiveHeight.setText("2");
	txt_angleIncrement.setText("1.0");
	txt_distanceIncrement.setText("1");
	txt_tickDistance.setText("10");

	int width = 400;
	int height = 350;
	Rectangle parentRect = demMain.getBounds();
	int xOffset = parentRect.x + (parentRect.width - width) / 2;
	int yOffset = parentRect.y + (parentRect.height - height) / 2;
	setBounds(xOffset, yOffset, width, height);
    } /* AntennaLocationDialog() */

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
	    coordDialog.setCoordinates(gc_location);
	    coordDialog.show();
	    gc_location = coordDialog.getCoordinates();
	    lbl_location.setText(gc_location.toString());
	    if (dem != null)
	    {
		txt_heightAboveSeaLevel.setText(Integer.toString(dem.getElevation(gc_location)));
	    } /* if dem != null */
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
     * get receive antenna height above ground.
     * @return the height.
     */
    public int getReceiveHeight()
    {
	return Util.safeStringToInt(txt_receiveHeight.getText());
    } /* getReceiveHeight */
	
    /**
     * get the angle increment.
     * @return the angle increment entered in the dialog.
     */
    public float getAngleIncrement()
    {
	return Util.safeStringToFloat(txt_angleIncrement.getText());
    } /* getAngleIncrement */
	
    /**
     * get the distance increment.
     * @return the distance increment entered in the dialog.
     */
    public int getDistanceIncrement()
    {
	return Util.safeStringToInt(txt_distanceIncrement.getText());
    } /* getDistanceIncrement */

    /**
     * get the tick distance.
     * @return the tick distance entered in the dialog.
     */
    public int getTickDistance()
    {
	return Util.safeStringToInt(txt_tickDistance.getText());
    } /* getTickIncrement */


    /**
     * process this dialog's action.  Done this way to not hog the
     * AWT thread that responds to OK button press.
     */
    void dialogAction()
    {
	if (actionOK)
	{
	    demMain.map.plotCoverage(demMain.dem,
				     getCoordinates(),
				     (short) (getHeightAboveGround() + getHeightAboveSeaLevel()),
				     (short) getReceiveHeight(),
				     getAngleIncrement(),
				     getDistanceIncrement(),
				     getTickDistance());
	} /* if actionOK */
    } /* dialogAction() */

} /* class AntennaLocationDialog */

