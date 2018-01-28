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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * This class provides a progress indicator, which is a horizontal bar
 * that gives a real-time indication of the progress of an operation that
 * may take a considerable abount of real time.
 * @author Jeffrey B. Otterson
 */

public class ProgressIndicator extends Canvas
{
    
    private static final String base = "ProgressIndicator";
    private static int nameCounter = 0;
    private int value;
    
    /**
     * Creates a ProgressIndicator.
     */
    public ProgressIndicator()
    {
	value = 0;
    } /* ProgressIndicator constructor */
    
    /**
     * Gets the value of this ProgressIndicator.
     * @return the value of this ProgressIndicator.
     * @see #setValue
     */
    public int getValue()
    {
	return value;
    } /* getValue() */
  
    /**
     * Sets the value for this ProgressIndicator.
     * @param value value of the ProgressIndicator.
     * @see #getValue
     */
    public synchronized void setValue(int value)
    {
	if ((value >= 0) || (value <=100))
	{
	    if (this.value != value)
	    { 
		this.value = value;
		repaint(50);
	    } /* if this.value != value */
	} /* if value >=0... */
	else
	{
	    System.err.println("ProgressIndicator.setValue("+value+"): invalid value");
	} /* if value >=0... */
    } /* setValue */

    /**
     * overrides update in component to prevent background being cleared.
     * @param g the specified context to use when updating.
     */
    public void update(Graphics g)
    {
	paint(g);
    } /* update() */
    
    /**
     * paints the ProgressIndicator
     * @param g The graphics context to use for painting.
     */
    public void paint(Graphics g)
    {
	Dimension size = getSize();
	g.setColor(Color.black);
	g.drawRect(0,0,size.width-1, size.height-1);
	int offset = (size.width-2) * value / 100;
	g.setColor(Color.blue);
	g.fillRect(1, 1, offset, size.height-2);
	g.setColor(Color.white);
	g.fillRect(offset+1, 1, size.width-2-offset-1, size.height-2);
	g.setColor(Color.black);
	if (value != 0)
	{
	    Font f = g.getFont();
	    if (f != null)
	    {
		FontMetrics fm = getFontMetrics(f);
		if (fm != null)
		{
		    String message = Integer.toString(value) + "%";
		    int messageWidth = fm.stringWidth(message);
		    int x = (size.width - messageWidth) / 2;
		    int y = Math.max(0, (size.height - (fm.getAscent() + fm.getDescent())) / 2 + fm.getAscent());
		    g.drawString(message, x, y);
		} /* if fm != null */
	    } /* if f != null */
	} /* if value != 0 */		    
    } /* paint */
} /* class ProgressIndicator */



