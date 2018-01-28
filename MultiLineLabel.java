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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * This class provides a multi-line label.  Lines are separated with the \n character.
 * 
 * @author Jeffrey B. Otterson
 * @see java.awt.Label
 */

public class MultiLineLabel extends Component
{
    String label;
    int alignment;
    
    private static final String base = "multilinelabel";
    private static int nameCounter = 0;

    /**
     * Left alignment
     */
    public final static int LEFT = 0;
    
    /**
     * Center alignment
     */
    public final static int CENTER = 1;

    /**
     * Right alignment
     */
    public final static int RIGHT = 2;
    
    /**
     * Creates a MultiLineLabel with no label text and default alignment (left justified).
     */
    public MultiLineLabel()
    {
	this("", LEFT);
    } /* MultiLineLabel constructor */

    /**
     * Creates a MultiLineLabel with the specified label and default alignment (left justified).
     * @param label the text that makes up the label.
     */
    public MultiLineLabel(String label)
    {
	this(label, LEFT);
    } /* MultiLineLabel constructor */

    /**
     * Creates a MultiLineLabel with the specified label and aligmment.  The
     * alignment value must be one of LEFT, CENTER, or RIGHT.
     * @param label the text that makes up the label.
     * @param alignment the alignment value.
     */
    public MultiLineLabel(String label, int alignment)
    {
	this.setName(base + nameCounter++);
	this.label = label;
	this.alignment = alignment;
    } /* MultiLineLabel constructor */
    
    /**
     * Gets the text of this MultiLineLabel.
     * @return the text of this MultiLineLabel.
     * @see #setText
     */
    public String getText()
    {
	return label;
    } /* getText() */
  
    /**
     * Sets the text for this MultiLineLabel to the specified text.
     * @param label text of the MultiLineLabel.
     * @see #getText
     */
    public synchronized void setText(String label)
    {
	this.label = label;
	invalidate();
	repaint();
    } /* setLabel */
  
    /**
     * Gets the alignment of this MultiLineLabel
     * @return the current aligmnent of this MultiLineLabel
     * @see #setAlignment
     */
    public int getAlignment()
    {
	return alignment;
    } /* getAlignment() */
  
    /**
     * Sets the alignment of this MultiLineLabel to the specified alignment.
     * @param alignment the alignment value.
     * @see #getAlignment
     */
    public synchronized void setAlignment(int alignment)
    {
	this.alignment = alignment;
	invalidate();
	repaint();
    } /* setAlignment */

    /**
     * paints the MultiLineLabel
     * @param g The graphics context to use for painting.
     */
    public void paint(Graphics g)
    {
        Font f = getFont();
        if (f != null)
        {
            if (label != null)
            {
		g.setFont(f);
		FontMetrics fm = getFontMetrics(f);
		int font_height = fm.getAscent() + fm.getDescent();
		int index = 0;
		int last_index = 0;
		int num_lines = 1;
		
		/* count the lines of text */
		index = label.indexOf('\n');
		while (index != -1)
		{
		    num_lines++;
		    index = label.indexOf('\n', index + 1);
		} /* while index != -1 */

		Dimension size = getSize();
		int text_height = num_lines * font_height;
		int x;
		int y = Math.max(0, (size.height - text_height) / 2) + fm.getAscent();
		
		/* start drawing the lines of text */
		index = label.indexOf('\n');
		String line;
		while (index != -1)
		{
		    line = label.substring(last_index, index);
		    x = calculateOffset(fm.stringWidth(line), size.width);
		    g.drawString(line, x, y);
		    y += font_height;
		    last_index = index + 1;
		    index = label.indexOf('\n', last_index);
		} /* while */
		/* draw the last line of text */
		line = label.substring(last_index);
		if (line.length() > 0)
		{
		    x = calculateOffset(fm.stringWidth(line), size.width);
		    g.drawString(line, x, y);
		    //y += font_height;
		} /* if lastLine.length() > 0 */
            } /* if label != null */
        } /* if f != null */
    } /* paint */

    /**
     * calculate the offset for a line of text based on the alignment.
     * @param width the width of the line of text
     * @param wholeWidth the width of the MultiLineLabel
     * @return the calculated x offset for the line of text
     */
    private int calculateOffset(int width, int wholeWidth)
    {
	int x;
	
	switch (alignment)
	{
	    case LEFT:
		x = 0;
		break;
	    case RIGHT:
		x = wholeWidth - width;
		break;
	    case CENTER:
		x = (wholeWidth - width) / 2;
		break;
	    default:
		x = 0;
		break;
	} /* switch */
	return(x);
    } /* calculateOffset() */

    /**
     * Gets the preferred size of the MultiLineLabel.
     * @return A dimension object indicating this MultiLineLabel's preferred size.
     */
    public Dimension getPreferredSize()
    {
	Font f = getFont();
	if (f != null)
        {
            FontMetrics fm = getFontMetrics(f);
	    int fontHeight = fm.getAscent() + fm.getDescent();
            int x = 0;
	    int y = 0;
	    int index = 0;
	    int last_index = 0;
            if (label != null)
            {
		index = label.indexOf('\n');
		String line;
		while (index != -1)
		{
		    line = label.substring(last_index, index);
		    y += fontHeight;
		    x = Math.max(x, fm.stringWidth(line));
		    last_index = index + 1;
		    index = label.indexOf('\n', last_index);
		} /* while */
		line = label.substring(last_index);
		if (line.length() > 0)
		{
		    y += fontHeight;
		    x = Math.max(x, fm.stringWidth(line));
		} /* if lastLine.length() > 0 */
            } /* if label != null */
            return new Dimension(x, y);
        } /* if f != null */
	else
        {
            return new Dimension(1, 1);
        } /* if f != null */
    } /* getPreferredSize() */

    /**
     * Gets the minimum size of the MultiLineLabel.
     * @return A dimension object indicating this MultiLineLabel's minimum size.
     */
    public Dimension getMinimumSize()
    {
	return(getPreferredSize());
    } /* getMinimumSize() */

} /* class MultiLineLabel */
