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

import java.awt.Insets;
import java.awt.Container;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

/** 
 * A class that provides simplified access to <tt>GridBagLayout</tt>.
 * It allows simplified creation of Containers containing automatically
 * aligned AWT components. To use, create a new <tt>Container</tt> or
 * subclass (like <tt>Panel</tt>), and set it's layout manager to be a
 * new GroupLayout(width, height).  Then add the components to the container.
 * Components are populated by rows.  Trickier placement of components may
 * require the use of the setInsets, setAnchor, and setFill methods prior
 * to adding the control to the container.<p>
 *
 * Known Bugs: Over- or under-population of the container is not detected and 
 * may cause the container to be laid out in some wierd way. 
 *
 * @author Jeffrey B. Otterson
 * @see java.awt.GridBagLayout
 * @see java.awt.GridBagConstraints
 */

public class GroupLayout extends GridBagLayout
{
    private int m_n_columns;
    private int m_n_rows;
    private int m_n_col_count;
    private int m_n_row_count;

    private Insets m_insets_edge;

    private GridBagConstraints m_gbc_default;

    /**
     * Put the component in the center of it's display area.
     */
    public final static int CENTER    = GridBagConstraints.CENTER;
    /**
     * Put the component in the top of it's display area, centered horizontally
     */
    public final static int NORTH     = GridBagConstraints.NORTH;
    /**
     * Put the component in the top-right corner of it's display area
     */
    public final static int NORTHEAST = GridBagConstraints.NORTHEAST;
    /**
     * Put the component in the right side it's display area, centered vertically
     */
    public final static int EAST      = GridBagConstraints.EAST;
    /**
     * Put the component in the bottom-right corner of it's display area
     */
    public final static int SOUTHEAST = GridBagConstraints.SOUTHEAST;
    /**
     * Put the component in the bottom of it's display area, centered horizontally
     */
    public final static int SOUTH     = GridBagConstraints.SOUTH;
    /**
     * Put the component in the bottom-left corner of it's display area
     */
    public final static int SOUTHWEST = GridBagConstraints.SOUTHWEST;
    /**
     * Put the component in the left side it's display area, centered vertically
     */
    public final static int WEST      = GridBagConstraints.WEST;
    /**
     * Put the component in the top-left corner of it's display area
     */
    public final static int NORTHWEST = GridBagConstraints.NORTHWEST;

    /**
     * Do not resize the component.
     */
    public final static int NONE       = GridBagConstraints.NONE;
    /**
     * Resize the component vertically but not horizontally.
     */
    public final static int VERTICAL   = GridBagConstraints.VERTICAL;
    /**
     * Resize the component horizontally but not vertically.
     */
    public final static int HORIZONTAL = GridBagConstraints.HORIZONTAL;
    /**
     * Resize the component both horizontally and vertically.
     */
    public final static int BOTH       = GridBagConstraints.BOTH;
    /**
     * Default value for constraint adjustment methods.
     * Set anchoring to CENTER,  fill to NONE, or insets to default values.
     */
    public final static int DEFAULT = -1;
    
    private final static int DEFAULT_INSETS_TOP    = 0;
    private final static int DEFAULT_INSETS_LEFT   = 0;
    private final static int DEFAULT_INSETS_RIGHT  = 5;
    private final static int DEFAULT_INSETS_BOTTOM = 5;

    /** 
     * Create a GroupLayout with the given dimensions
     *
     * @param n_columns the width of the grid in columns
     * @param n_rows the height of the grid in rows
     */
    public GroupLayout(int n_columns, int n_rows)
    {
	super();
	m_n_columns = n_columns;
	m_n_rows = n_rows;
	m_gbc_default = new GridBagConstraints();
	m_gbc_default.anchor = GridBagConstraints.NORTHWEST;
	m_gbc_default.gridheight = 1;
	m_insets_edge = new Insets(0,0,0,0);
	setInsets();
    } /* GroupLayout() constructor */

    /**
     * set the default anchor for subsequent components added to this GroupLayout 
     * @param anchoring the new default value for the GridBagConstraints <i>anchor</i> 
     * @see java.awt.GridBagConstraints
     */
    public void setAnchor(int anchor)
    {
	if (m_gbc_default != null)
	    m_gbc_default.anchor = (anchor == DEFAULT) ? CENTER : anchor;
    } /* setAnchor() */

    /**
     * set the default fill for subsequent components added to this GroupLayout
     * @param fill the new default value for the GridBagConstraints <i>fill</i> 
     * @see java.awt.GridBagConstraints
     */
    public void setFill(int fill)
    {
	if (m_gbc_default != null)
	    m_gbc_default.fill = (fill == DEFAULT) ? NONE : fill;
    } /* setFill() */

    /** 
     * set the GroupLayout's default insets to the default values
     */
    public void setInsets()
    {
	setInsets(DEFAULT, DEFAULT, DEFAULT, DEFAULT);
    } /* setInsets() */

    /** 
     * set the GroupLayout's edge insets to the specified values
     * @param insets the insets to use
     */
    public void setEdgeInsets(Insets insets)
    {
	m_insets_edge.top    = insets.top;
	m_insets_edge.left   = insets.left;
	m_insets_edge.right  = insets.right;
	m_insets_edge.bottom = insets.bottom;
    } /* setEdgeInsets() */

    /** 
     * set the GroupLayout's edge insets to the specified values
     * @param t the top    inset
     * @param l the left   inset
     * @param r the right  inset
     * @param b the bottom inset
     */
    public void setEdgeInsets(int l, int t, int r, int b)
    {
	m_insets_edge.top    = t;
	m_insets_edge.left   = l;
	m_insets_edge.right  = r;
	m_insets_edge.bottom = b;
    } /* setEdgeInsets() */

    /** 
     * set the GroupLayout's default insets to the specified values
     * @param t the top    inset
     * @param l the left   inset
     * @param r the right  inset
     * @param b the bottom inset
     */
    public void setInsets(int l, int t, int r, int b)
    {
	m_gbc_default.insets.top    = (t == DEFAULT) ? DEFAULT_INSETS_TOP    : t;
	m_gbc_default.insets.left   = (l == DEFAULT) ? DEFAULT_INSETS_LEFT   : l;
	m_gbc_default.insets.right  = (r == DEFAULT) ? DEFAULT_INSETS_RIGHT  : r;
	m_gbc_default.insets.bottom = (b == DEFAULT) ? DEFAULT_INSETS_BOTTOM : b;
    } /* setInsets() */

    /** 
     * Add's the specified Component to the Layout, optionally using the specified constraint
     * if the constraints are not specified, they are automatically created.
     * @param comp the component to be added
     * @param constr the specified constraints
     * @see java.awt.GridBagLayout#addLayoutComponent
     */
    public void addLayoutComponent(Component comp, Object constr)
    {
	if (constr != null)
	{
	    super.addLayoutComponent(comp, constr);
	} /* if constraints != null */
	else
	{
	    GridBagConstraints constraints = (GridBagConstraints) m_gbc_default.clone();
	    int i = m_n_columns - m_n_col_count;
	    if (i == 0)
	    {
		m_n_col_count = 0;
		i = m_n_columns;
		m_n_row_count++;
	    } /* if i == 0 */
	    
	    constraints.gridy = m_n_row_count;
	    if (m_n_row_count == 0) /* first row */
	    {
		constraints.insets.top = m_insets_edge.top;
	    } /* if m_n_row_count = 0 */

	    if (m_n_row_count + 1 == m_n_rows)
	    {
		constraints.insets.bottom = m_insets_edge.bottom;
	} /* if m_n_row_count + 1 = m_n_rows */

	if (m_n_col_count == 0)
	{
	    constraints.gridx = 0;
	    constraints.insets.left = m_insets_edge.left;
	} /* if m_n_col_count == 0 */
	else
	    constraints.gridx = GridBagConstraints.RELATIVE;

	switch (i)
	{
	    case 1: //last component in row 
		constraints.gridwidth = GridBagConstraints.REMAINDER;
		constraints.insets.right = m_insets_edge.right;
		break;
	    case 2: //2nd to last component in row
		constraints.gridwidth = GridBagConstraints.RELATIVE;
		break;
	    default:
		constraints.gridwidth = 1;
		break;
	} /* switch i */
	super.addLayoutComponent(comp, constraints);
	m_n_col_count++;
	} /* if constraints != null */
    } /* addLayoutComponent */
} /* class GroupLayout */
