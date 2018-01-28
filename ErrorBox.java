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
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * a class that provides a dialog box for displaying error messages.
 */
public class ErrorBox extends Dialog implements ActionListener
{
    MultiLineLabel label;
    Button btn_ok;

    /**
     * create a new ErrorBox.
     * @param parent the Frame that owns the application.
     * @param title text to show in the title bar of the dialog.
     * @param caption text to display inside the dialog box.
     */
    public ErrorBox(Frame parent, String title, String caption)
    {
	super(parent, title, true);
	GroupLayout layout = new GroupLayout(1,2);
	setLayout(layout);
	layout.setInsets(5,5,5,5);
	layout.setAnchor(GroupLayout.CENTER);
	add(label = new MultiLineLabel(caption));
	add(btn_ok = new Button("OK"));
	btn_ok.addActionListener(this);
	doLayout();
	Dimension labelSize = label.getPreferredSize();
	int width = labelSize.width + 40;
	int height = labelSize.height + 80;
	Rectangle parentRect = parent.getBounds();
	int xOffset = parentRect.x + (parentRect.width - width) / 2;
	int yOffset = parentRect.y + (parentRect.height - height) / 2;
	setBounds(xOffset, yOffset, width, height);
	parent.getToolkit().beep();
	setVisible(true);
    } /* ErrorBox() */

    /**
     * manage action of the OK button.
     * @param e the ActionEvent from the OK button.
     */
    public void actionPerformed(ActionEvent e)
    {
	String arg = e.getActionCommand();
	setVisible(false);
    } /* actionPerformed() */

} /* class ErrorBox */

