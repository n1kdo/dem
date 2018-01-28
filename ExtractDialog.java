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
import java.awt.Color;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * a class that provides a dialog for data entry for extracting
 * a smaller DEM from a larger DEM.
 */
public class ExtractDialog extends Dialog implements ActionListener
{
    private final String longString = "                                                                      ";
    GeoCoordinate gc_swLocation;
    GeoCoordinate gc_neLocation;
    String fileName;
    Label lbl_swLocation;
    Label lbl_neLocation;
    Label lbl_fileName;
    DEMmain demMain;
    boolean actionOK;
    
    /**
     * create a new ExtractDialog.
     * @param demMain the Frame that owns the application.
     */
    public ExtractDialog(DEMmain demMain)
    {
	super(demMain, "Extract Smaller DEM", true);
	this.demMain = demMain;
	Button btn_set_swLocation;
	Button btn_set_neLocation;
	Button btn_set_fileName;
	Button btn_ok;
	Button btn_cancel;
	GroupLayout layout = new GroupLayout(1,4);
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	gc_swLocation = new GeoCoordinate(0,0);
	gc_neLocation = new GeoCoordinate(0,0);
	fileName = "";
	actionOK = false;

	layout.setAnchor(GroupLayout.WEST);
	add(new Label("Set the filename to extract the new DEM from."));
	Panel pnl_file = new Panel(new GroupLayout(3,1));
	pnl_file.add(new Label("File Name"));
	pnl_file.add(lbl_fileName = new Label(longString));
	lbl_fileName.setBackground(Color.white);
	pnl_file.add(btn_set_fileName = new Button("Set"));
	layout.setAnchor(GroupLayout.CENTER);
	add(pnl_file);

	layout.setAnchor(GroupLayout.WEST);
	add(new Label("Set the corners of the region to extract from the DEM."));
	Panel pnl_location = new Panel(new GroupLayout(3,2));
	pnl_location.add(new Label("SW Corner"));
	pnl_location.add(lbl_swLocation = new Label(longString));
	lbl_swLocation.setBackground(Color.white);
	pnl_location.add(btn_set_swLocation = new Button("Set"));
	pnl_location.add(new Label("NE Corner"));
	pnl_location.add(lbl_neLocation = new Label(longString));
	lbl_neLocation.setBackground(Color.white);
	pnl_location.add(btn_set_neLocation = new Button("Set"));
	add(pnl_location);

	Panel pnl_buttons = new Panel(new GroupLayout(2,1));
	pnl_buttons.add(btn_cancel = new Button("Cancel"));
	pnl_buttons.add(btn_ok = new Button("OK"));
	layout.setAnchor(GroupLayout.CENTER);
	layout.setInsets(5,25,5,5);
	add(pnl_buttons);
	
	btn_set_fileName.addActionListener(this);
	btn_set_fileName.setActionCommand("Set File");
	btn_set_swLocation.addActionListener(this);
	btn_set_swLocation.setActionCommand("Set SW");
	btn_set_neLocation.addActionListener(this);
	btn_set_neLocation.setActionCommand("Set NE");
	btn_ok.addActionListener(this);
	btn_cancel.addActionListener(this);
	setSize(400, 250);
	setVisible(true);
    } /* ExtractDialog() */

    /**
     * manage button presses.
     * @param e the ActionEvent from the button.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	if (arg.equals("OK"))
	{
	    if (fileName.length() == 0)
	    {
		new ErrorBox(demMain, "error!", "No file specified!");
		return;
	    } /* if fileName.length() > 0 */

	    if (!reasonableRegion())
	    {
		new ErrorBox(demMain,
			     "Error!",
			     "Unreasonable extraction region specified!");
		return;
	    } /* if swLat > neLat... */
	    actionOK = true;
	    setVisible(false);
	} /* if arg.equals("OK") */

	if (arg.equals("Cancel"))
	{
	    actionOK = false;
	    setVisible(false);
	    return;
	} /* if arg.equals("Cancel") */

	if (arg.equals("Set File"))
	{
	    FileDialog fileDialog = new FileDialog(demMain,
						   "Select file",
						   FileDialog.LOAD);
	    fileDialog.show();
	    if (fileDialog.getFile() == null)
		return;
	    fileName = fileDialog.getDirectory() + fileDialog.getFile();
	    lbl_fileName.setText(fileName);
	    return;
	} /* if arg.equals("Set File") */
	
	if (arg.equals("Set SW"))
	{
	    CoordinateDialog coordDialog = new CoordinateDialog(demMain,
								"Extract Region from DEM",
								"Enter SW corner of region to extract");
	    coordDialog.setCoordinates(gc_swLocation);
	    coordDialog.show();
	    gc_swLocation = coordDialog.getCoordinates();
	    lbl_swLocation.setText(gc_swLocation.toString());
	    if (!reasonableRegion())
	    gc_neLocation = new GeoCoordinate(gc_swLocation);
	    lbl_neLocation.setText(gc_neLocation.toString());
	    return;
	} /* if arg.equals("Set SW") */
	
	if (arg.equals("Set NE"))
	{
	    CoordinateDialog coordDialog = new CoordinateDialog(demMain,
								"Extract Region from DEM",
								"Enter NE corner of region to extract");
	    coordDialog.setCoordinates(gc_neLocation);
	    coordDialog.show();
	    gc_neLocation = coordDialog.getCoordinates();
	    lbl_neLocation.setText(gc_neLocation.toString());
	    return;
	} /* if arg.equals("Set NE") */
    } /* actionPerformed() */

    boolean reasonableRegion()
    {
	int maxDelta = 36000; /* 10 degrees */
	int deltaLat = gc_neLocation.getLatitude()  - gc_swLocation.getLatitude();
	int deltaLon = gc_neLocation.getLongitude() - gc_swLocation.getLongitude();
	System.err.println("deltaLat="+deltaLat);
	System.err.println("deltaLon="+deltaLon);
	return ((deltaLat >= 0) &&
		(deltaLat <= maxDelta) &&
		(deltaLon >= 0) &&
		(deltaLon <= maxDelta));
    } /* reasonableRegion() */

    /**
     * process this dialog's action.  Done this way to not hog the
     * AWT thread that responds to OK button press.
     */
    void dialogAction()
    {
	if (actionOK)
	{
	    DEM dem = new DEM(fileName, demMain);
	    if (dem.read(gc_swLocation, gc_neLocation))
	    {
		demMain.setTitle(demMain.APP_NAME + " Extracted from " + fileName);
		demMain.dem = dem;
		demMain.antennaLocationDialog.setDEM(dem);
	    } /* if dem.read() */
	} /* if actionOK */
    } /* dialogAction() */
    
} /* class ExtractDialog */

