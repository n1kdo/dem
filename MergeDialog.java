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
 * a class that provides a dialog to enter data for merging DEMs.
 */
public class MergeDialog extends Dialog implements ActionListener
{
    private final String longString = "                                                                      ";
    String inFile1;
    String inFile2;
    String outFile;
    Label lbl_inFile1;
    Label lbl_inFile2;
    Label lbl_outFile;
    DEMmain demMain;
    boolean actionOK;
    
    /**
     * create a new MergeDialog.
     * @param demMain the Frame that owns the application.
     */
    public MergeDialog(DEMmain demMain)
    {
	super(demMain, "Merge DEMs", true);
	this.demMain = demMain;
	Button btn_set_inFile1;
	Button btn_set_inFile2;
	Button btn_set_outFile;
	Button btn_ok;
	Button btn_cancel;
	GroupLayout layout = new GroupLayout(1,2);
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	inFile1 = inFile2 = outFile = "";
	actionOK = false;
	
	layout.setAnchor(GroupLayout.CENTER);
	Panel pnl_fileNames = new Panel(new GroupLayout(3,5));

	pnl_fileNames.add(new Label(""));
	pnl_fileNames.add(new Label("Select the files to merge from."));
	pnl_fileNames.add(new Label(""));

	pnl_fileNames.add(new Label("South/West File"));
	pnl_fileNames.add(lbl_inFile1 = new Label(longString));
	lbl_inFile1.setBackground(Color.white);
	pnl_fileNames.add(btn_set_inFile1 = new Button("Set"));

	pnl_fileNames.add(new Label("North/East File"));
	pnl_fileNames.add(lbl_inFile2 = new Label(longString));
	lbl_inFile2.setBackground(Color.white);
	pnl_fileNames.add(btn_set_inFile2 = new Button("Set"));

	pnl_fileNames.add(new Label(""));
	pnl_fileNames.add(new Label("Select the output file."));
	pnl_fileNames.add(new Label(""));
	
	pnl_fileNames.add(new Label("Output File"));
	pnl_fileNames.add(lbl_outFile = new Label(longString));
	lbl_outFile.setBackground(Color.white);
	pnl_fileNames.add(btn_set_outFile = new Button("Set"));
	
	add(pnl_fileNames);

	Panel pnl_buttons = new Panel(new GroupLayout(2,1));
	pnl_buttons.add(btn_cancel = new Button("Cancel"));
	pnl_buttons.add(btn_ok = new Button("OK"));
	layout.setAnchor(GroupLayout.CENTER);
	layout.setInsets(5,15,5,5);
	add(pnl_buttons);
	
	btn_set_inFile1.addActionListener(this);
	btn_set_inFile1.setActionCommand("Set File 1");
	btn_set_inFile2.addActionListener(this);
	btn_set_inFile2.setActionCommand("Set File 2");
	btn_set_outFile.addActionListener(this);
	btn_set_outFile.setActionCommand("Set Out File");
	btn_ok.addActionListener(this);
	btn_cancel.addActionListener(this);

	setSize(400, 220);
	setVisible(true);
    } /* MergeDialog() */

    /**
     * manage button presses.
     * @param e the ActionEvent from the button.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	if (arg.equals("OK"))
	{
	    if ((inFile1.length() == 0) ||
		(inFile2.length() == 0) ||
		(outFile.length() == 0))
	    {
		new ErrorBox(demMain,
			     "Error!",
			     "Not all filenames are specified!");
		return;
	    } /* if inFile1.length() == 0 ...*/
	    actionOK = true;
	    setVisible(false);
	} /* if arg.equals("OK") */

	if (arg.equals("Cancel"))
	{
	    actionOK = false;
	    setVisible(false);
	    return;
	} /* if arg.equals("Cancel") */

	if (arg.equals("Set File 1"))
	{
	    inFile1 = getFileName("Southern/Western DEM file", FileDialog.LOAD);
	    lbl_inFile1.setText(inFile1);
	} /* if arg.equals("Set File 1") */
	
	if (arg.equals("Set File 2"))
	{
	    inFile2 = getFileName("Northern/Eastern DEM file", FileDialog.LOAD);
	    lbl_inFile2.setText(inFile2);
	} /* if arg.equals("Set File 2") */
	
	if (arg.equals("Set Out File"))
	{
	    outFile = getFileName("Output File", FileDialog.SAVE);
	    lbl_outFile.setText(outFile);
	} /* if arg.equals("Set Out File") */
	
    } /* actionPerformed() */

    String getFileName(String title, int action)
    {
	FileDialog fileDialog = new FileDialog(demMain,
					       title,
					       action);
	fileDialog.show();
	if (fileDialog.getFile() == null)
	    return ("");
	else 
	    return (fileDialog.getDirectory() + fileDialog.getFile());
    } /* getFileName */

    /**
     * process this dialog's action.  Done this way to not hog the
     * AWT thread that responds to OK button press.
     */
    void dialogAction()
    {
	if (actionOK)
	{
	    DEM.merge(inFile1, inFile2, outFile, demMain);
	} /* if actionOK */
    } /* dialogAction() */

} /* class MergeDialog */

