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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * class that provides and manages the User Interface for the DEM application.
 */
public class DEMmain extends Frame implements ActionListener, Runnable
{
    /**
     * constant that specifies that the action thread load a USGS DEM file.
     */
    public static final int LOAD_USGS = 0;
    /**
     * constant that specifies that the action thread load a binary DEM file.
     */
    public static final int LOAD_BINARY = 1;
    /**
     * constant that specifies that the action thread save a binary DEM file.
     */
    public static final int SAVE_BINARY = 2;
    /**
     * constant that specifies that the action thread display the loaded DEM.
     */
    public static final int VIEW = 3;
    /**
     * constant that specifies that the action thread extract and load DEM data
     * from a binary DEM file.
     */
    public static final int EXTRACT = 4;
    /**
     * constant that specifies that the action thread merge 2 adjacent binary
     * DEM files into a new binary DEM file.
     */
    public static final int MERGE = 5;
    /**
     * constant that specifies that the action thread merge 2 adjacent binary
     * DEM files into a new binary DEM file.
     */
    public static final int COVERAGE = 6;
    /**
     * constant that specifies that the action thread print the DEM.
     */
    public static final int PRINT = 7;
    /**
     * constant that specifies that the action thread calculate AHAAT.
     */
    public static final int AHAAT = 8;

    /**
     * constant that specifies the verion number.
     */
    private static String msVersion = "1.01";
    
    private static final int X_SIZE = 800;
    private static final int Y_SIZE = 600;

    static final String APP_NAME = "DEM";

    String demFileName;
    int threadAction;

    DEM dem;
    Map map;
    StatusBar statusBar;
    Menu fileMenu;
    ScrollPane sp;
    AntennaLocationDialog antennaLocationDialog;
    
    /**
     * create a new DEMmain object complete with UI.
     */
    public DEMmain()
    {
	super("DEM");
	setFont(new Font("Dialog", 0, 12));
	setBackground(Color.lightGray);
	setSize(X_SIZE, Y_SIZE);
       	setLayout(new BorderLayout());

	/* create this so it will have persistence */
	antennaLocationDialog = new AntennaLocationDialog(this);

	MenuBar mb = new MenuBar();
	fileMenu = new Menu("File");
	addMenuItem(fileMenu, "Open USGS DEM", "FileOpenUSGS");
	addMenuItem(fileMenu, "Open DEM", "FileOpen");
	addMenuItem(fileMenu, "Save DEM", "FileSave");
	addMenuItem(fileMenu, "View DEM", "FileView");
	addMenuItem(fileMenu, "Merge 2 DEMs", "FileMerge");
	addMenuItem(fileMenu, "Extract Region", "FileExtract");
	addMenuItem(fileMenu, "View Coverage", "FileCoverage");
	addMenuItem(fileMenu, "Calculate AHAAT", "FileAHAAT");
	addMenuItem(fileMenu, "Print Image", "FilePrint");
	addMenuItem(fileMenu, "About", "FileAbout");
	addMenuItem(fileMenu, "Exit", "FileExit");
	mb.add(fileMenu);
	
	setMenuBar(mb);

	statusBar = new StatusBar(this);
	sp = new ScrollPane(ScrollPane.SCROLLBARS_AS_NEEDED);
	map = new Map(this);
	sp.add(map);
	add(sp, "Center");
	add(statusBar, "South");
	setVisible(true);
    } /* EAmain constructor */

    /**
     * add a menu item to a menu.  Set the listener up.
     * @param menu the menu to add to.
     * @param itemName the name of the new menu item.
     * @param actionCommand the text for the action command.
     */
    void addMenuItem(Menu menu, String itemName, String actionCommand)
    {
	MenuItem item = new MenuItem(itemName);
	item.setActionCommand(actionCommand);
	item.addActionListener(this);
	menu.add(item);
    } /* addMenuItem() */

    /**
     * add a menu item to a menu.  Set the listener up.
     * @param menu the menu to add to.
     * @param itemName the name of the new menu item.
     */
    void addMenuItem(Menu menu, String itemName)
    {
	MenuItem item = new MenuItem(itemName);
	item.addActionListener(this);
	menu.add(item);
    } /* addMenuItem() */
    
    /**
     * manage actions.
     * @param e the ActionEvent to manage.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	if (arg.equals("FileExit"))
	{
	    System.exit(0);
	} /* if arg.equals("Exit") */
	    
	if (arg.equals("FileOpenUSGS"))
	{
	    String newFileName = getFileName("Open USGS DEM", FileDialog.LOAD);
	    if (newFileName != null)
	    {
		demFileName = newFileName;
		dem = new DEM(demFileName, this);
		startAction(LOAD_USGS);
	    } /* if demFileName != null */
	    return;
	} /* if arg.equals("FileOpenUSGS") */
	    
	if (arg.equals("FileOpen"))
	{
	    String newFileName = getFileName("Open DEM", FileDialog.LOAD);
	    if (newFileName != null)
	    {
		demFileName = newFileName;
		dem = new DEM(demFileName, this);
		startAction(LOAD_BINARY);
	    } /* if demFileName != null */
	    return;
	} /* if arg.equals("FileOpen") */
	    
	if (arg.equals("FileSave"))
	{
	    String newFileName = getFileName("Save DEM", FileDialog.SAVE);
	    if (newFileName != null)
	    {
		demFileName = newFileName;
		startAction(SAVE_BINARY);
	    } /* if demFileName != null */
	    return;
	} /* if arg.equals("FileSave") */
	
	if (arg.equals("FileView"))
	{
	    startAction(VIEW);
	    return;
	} /* if arg.equals("FileView") */
	    
	if (arg.equals("FileExtract"))
	{
	    startAction(EXTRACT);
	    return;
	} /* if arg.equals("FileExtract") */
	    
	if (arg.equals("FileMerge"))
	{
	    startAction(MERGE);
	    return;
	} /* if arg.equals("FileMerge") */
	    
	if (arg.equals("FileCoverage"))
	{
	    startAction(COVERAGE);
	    return;
	} /* if arg.equals("FileCoverage") */
	    
	if (arg.equals("FileAHAAT"))
	{
	    startAction(AHAAT);
	    return;
	} /* if arg.equals("FileCoverage") */
	    
	if (arg.equals("FilePrint"))
	{
	    startAction(PRINT);
	    return;
	} /* if arg.equals("FilePrint") */
	    
	if (arg.equals("FileAbout"))
	{
	    String sCopyright =
		"DEM version " + msVersion + "\n\n" +
		"Copyright (C) 1998, 1999 Jeffrey B. Otterson\n\n" +
		"DEM comes with ABSOLUTELY NO WARRANTY; for details see the\n" +
		"file \"copying\" that comes with this software.  This is free software,\n" +
		"and you are welcome to redistribute it under the conditions\n" +
		"described in \"copying\".";
		ErrorBox errorBox = new ErrorBox(this,
					     "About DEM",
					     sCopyright);
	    return;
	} /* if arg.equals("FilePrint") */
	    
        System.out.println("unhandled action command: " + arg);
    } /* actionPerformed() */

    /**
     * start up a thread for the intended action.
     * @param action the action code for the new thread.
     */
    public void startAction(int action)
    {
	threadAction = action;
	Thread actionThread = new Thread(this);
	actionThread.start();
    } /* startAction() */

    /**
     * main method is provided to create the initial DEMmain instance when
     * started from the command line.
     * @param args command line arguments.
     */
    public static void main(String args[])
    {
	DEMmain demmain = new DEMmain();
    } /* main() */

    /**
     * thread to perform detached operations while the UI can still be updated.
     */
    public void run()
    {
	setWait(true);
	switch (threadAction)
	{
	    case LOAD_USGS:
		this.setTitle(APP_NAME + " (loading USGS)");
		if (dem.readUSGS())
		{
		    this.setTitle(APP_NAME + " " + demFileName);
		    antennaLocationDialog.setDEM(dem);
		} /* if dem.readDEM(demFileName) */
		else
		{
		    demFileName = "";
		} /* if dem.readDEM(demFileName) */
		break;
		
	    case LOAD_BINARY:
		this.setTitle(APP_NAME + " (loading)");
		if (dem.read())
		{
		    this.setTitle(APP_NAME + " " + demFileName);
		    antennaLocationDialog.setDEM(dem);
		} /* if dem.readBinaryDEM(demFileName) */
		else
		{
		    demFileName = "";
		} /* if dem.readDEM(demFileName) */
		break;
		
	    case SAVE_BINARY:
		this.setTitle(APP_NAME + " (saving)");
		dem.write(demFileName);
		break;
		
	    case VIEW:
		this.setTitle(APP_NAME + " (drawing)");
		if (dem != null)
		{
		    map.setDEM(dem);
		} /* if dem != null*/
		else
		{
		    ErrorBox errorBox = new ErrorBox(this,
						     "Error!",
						     "no DEM data loaded");
		} /* if dem != null*/
		break;

	    case EXTRACT:
		this.setTitle(APP_NAME + " (extracting)");
		ExtractDialog extractDialog = new ExtractDialog(this);
		extractDialog.dialogAction();
		break;
		
	    case MERGE:
		this.setTitle(APP_NAME + " (merging)");
		MergeDialog mergeDialog = new MergeDialog(this);
		mergeDialog.dialogAction();
		break;
		
	    case COVERAGE:
		this.setTitle(APP_NAME + " (calculating coverage)");
		if (dem != null)
		{
		    antennaLocationDialog.show();
		    antennaLocationDialog.dialogAction();
		} /* if dem != null*/
		else
		{
		    ErrorBox errorBox = new ErrorBox(this,
						     "Error!",
						     "No DEM data loaded.");
		} /* if dem != null*/
		break;

	    case PRINT:
		this.setTitle(APP_NAME + " (printing)");
		if (dem != null)
		{
		    print();
		} /* if dem != null*/
		else
		{
		    ErrorBox errorBox = new ErrorBox(this,
						     "Error!",
						     "no DEM data loaded");
		} /* if dem != null*/
		break;

	    case AHAAT:
		this.setTitle(APP_NAME + " (calculating AHAAT)");
		if (dem != null)
		{
		    AHAATDialog ahaatDialog = new AHAATDialog(this);
		    ahaatDialog.show();
		    ahaatDialog.dialogAction();
		    ahaatDialog = null;
		} /* if dem != null*/
		else
		{
		    ErrorBox errorBox = new ErrorBox(this,
						     "Error!",
						     "No DEM data loaded.");
		} /* if dem != null*/
		break;

	} /* switch */
	if (demFileName == null)
	{
	    this.setTitle(APP_NAME);
	} /* if demFileName == null */
	else
	{
	    this.setTitle(APP_NAME + " " + demFileName);
	} /* if demFileName == null */
	setWait(false);
    } /* run() */

    void print()
    {
	PrintJob pj = getToolkit().getPrintJob(this, "output", null);
	if (pj != null)
	{
	    Graphics g = pj.getGraphics();
	    map.paint(g);
	    g.dispose();
	    pj.end();
	} /* if pj != null */
    } /* print() */
    
    /**
     * disable the menus and set the wait cursor.
     * @param state true to disable, false to enable.
     */
    void setWait(boolean state)
    {
	if (state)
	{ /* set the working state */
	    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	    fileMenu.setEnabled(false);
	} /* if state */
	else
   	{ /* set the available state */
	    setCursor(Cursor.getDefaultCursor());
	    fileMenu.setEnabled(true);
	} /* if state */
    } /* setWait() */

    private String getFileName(String caption, int flag)
    {
	FileDialog fileDialog = new FileDialog(this, caption, flag);
	fileDialog.show();
	if (fileDialog.getFile() == null)
	    return null;
	else
	    return(fileDialog.getDirectory() + fileDialog.getFile());
    } /* getFileName() */
} /* class DEMmain */
