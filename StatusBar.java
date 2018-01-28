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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;

/**
 * an object that provides and manages a TextField on the left and a
 * ProgressIndicator on the right.  It is intended to form the bottom
 * part of an application's window.
 */
public class StatusBar extends java.awt.Panel
{
    Label statusLabel;
    ProgressIndicator progressIndicator;
    Frame frame;

    /**
     * create a new StatusBar for the specified Frame.
     * @param f the frame of the application.
     */
    public StatusBar(Frame f)
    {
	super(new GridLayout(1,2));
	frame = f;
	add(statusLabel = new Label(""));
	add(progressIndicator = new ProgressIndicator());
    } /* StatusBar() */

    /**
     * create a new StatusBar.
     */
    public StatusBar()
    {
	this(null);
    } /* StatusBar() */

    /**
     * update the progress indicator with the indicated percentage.
     * @param value the percent complete to use in the progress indicator.
     */
    public void updateProgress(int value)
    {
	progressIndicator.setValue(value);
    } /* updateProgress() */

    /**
     * set the message in the left half of the status bar.
     * @param message the text to place in the left half of the status bar.
     */
    public void setMessage(String message)
    {
	statusLabel.setText(message);
    } /* setMessage() */

    /**
     * set the title of the application's frame.
     * @param message the text to place in the top of the applications frame.
     */
    public void setTitle(String message)
    {
	if (frame != null)
	    frame.setTitle(message);
    } /* setTitle() */
} /* class StatusBar */
