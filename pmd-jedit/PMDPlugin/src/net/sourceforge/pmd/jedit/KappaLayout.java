
package net.sourceforge.pmd.jedit;

import java.awt.LayoutManager2;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.io.Serializable;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * KappaLayout, a Java layout manager.<br>
 * Copyright (C) 2000, Dale Anson<br>
 *<br>
 * This library is free software; you can redistribute it and/or<br>
 * modify it under the terms of the GNU Lesser General Public<br>
 * License as published by the Free Software Foundation; either<br>
 * version 2.1 of the License, or (at your option) any later version.<br>
 *<br>
 * This library is distributed in the hope that it will be useful,<br>
 * but WITHOUT ANY WARRANTY; without even the implied warranty of<br>
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU<br>
 * Lesser General Public License for more details.<br>
 *<br>
 * You should have received a copy of the GNU Lesser General Public<br>
 * License along with this library; if not, write to the Free Software<br>
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA<br>
 * <p>
 * KappaLayout -- similar to others, but this one's simpler and easier to use.<br>
 * Example use:<br>
 * This will put a button on a panel in the top of its cell, stretched to
 * fill the cell width, with a 3 pixel pad:<br>
 * <code>
 * Panel p = new Panel(new KappaLayout());
 * Button b = new Button("OK");
 * p.add(b, "0, 0, 1, 2, 2, w, 3");
 * </code>
 * <br>
 * The constraints string has this layout:<br>
 * "x, y, w, h, a, s, p"<br>
 * defined as follows:<br>
 * <ul>
 * <li>'x' is the column to put the component, default is 0<br>
 * <li>'y' is the row to put the component, default is 0<br>
 * <li>'w' is the width of the component in columns (column span), default is 1.
 * Can also be R or r, which means the component will span the remaining cells
 * in the row.<br>
 * <li>'h' is the height of the component in rows (row span), default is 1.
 * Can also be R or r, which means the component will span the remaining cells
 * in the column.<br>
 * <li>'a' is the alignment within the cell. 'a' can be a value between 0 and 8,
 * inclusive, (default is 0) and causes the alignment of the component within the cell to follow
 * this pattern:<br>
 * 8 1 2<br>
 * 7 0 3<br>
 * 6 5 4<br>, or<br>
 * 0 horizontal center, vertical center,<br>
 * 1 horizontal center, vertical top,<br>
 * 2 horizontal right, vertical top,<br>
 * 3 horizontal right, vertical center,<br>
 * 4 horizontal right, vertical bottom,<br>
 * 5 horizontal center, vertical bottom,<br>
 * 6 horizontal left, vertical bottom,<br>
 * 7 horizontal left, vertical center,<br>
 * 8 horizontal left, vertical top.<br>
 * <p>
 * By popular request, the alignment constraint can also be represented as:<br>
 * NW N NE<br>
 * &nbsp;W 0 E<br>
 * SW S SE<br>
 * which are compass directions for alignment within the cell.
 * <li>'s' is the stretch value. 's' can have these values:<br>
 * 'w' stretch to fill cell width<br>
 * 'h' stretch to fill cell height<br>
 * 'wh' or 'hw' stretch to fill both cell width and cell height<br>
 * '0' (character 'zero') no stretch (default)
 * <li>'p' is the amount of padding to put around the component. This much blank
 * space will be applied on all sides of the component, default is 0.
 * </ul>
 * Parameters may be omitted (default  values will be used), e.g.,
 * <code> p.add(new Button("OK), "1,4,,,w,");</code><br>
 * which means put the button at column 1, row 4, default 1 column wide, default
 * 1 row tall, stretch to fit width of column, no padding. <br>
 * Spaces in the parameter string are ignored, so these are identical:<br>
 * <code> p.add(new Button("OK), "1,4,,,w,");</code><br>
 * <code> p.add(new Button("OK), " 1, 4,   , , w");</code><p>
 * Rather than use a constraints string, a Constraints object may be used
 * directly, similar to how GridBag uses a GridBagConstraint. E.g,<br>
 * <code>
 * Panel p = new Panel();<br>
 * KappaLayout tl = new KappaLayout();<br>
 * p.setLayout(tl);<br>
 * KappaLayout.Constraints con = tl.getConstraint();<br>
 * con.x = 1;<br>
 * con.y = 2;<br>
 * con.w = 2;<br>
 * con.h = 2;<br>
 * con.s = "wh";<br>
 * panel.add(new Button("OK"), con);<br>
 * con.x = 3;<br>
 * panel.add(new Button("Cancel"), con);<br>
 * </code><br>
 * Note that the same Constraints can be reused, thereby reducing the number of
 * objects created.<p>
 * @author Dale Anson
 */

public class KappaLayout implements LayoutManager2, Serializable {
   // overall preferred width of components in container
   protected int _preferred_width = 0;

   // overall preferred height of components in container
   protected int _preferred_height = 0;

   protected boolean _size_unknown = true;

   protected int _col_count = 0;    // number of columns in the layout
   protected int _row_count = 0;    // number of rows in the layout

   // storage for component constraints
   // key is the component, value is a Constraints
   protected Hashtable _constraints = new Hashtable();

   // model of the table -- key is a Point with Point.x
   // representing the column, Point.y representing the row. As most layouts
   // are sparse, this is an efficient way to represent the table without
   // creating excess objects. The value is the dimension of the component.
   protected Hashtable _table = null;

   // a Dimension with no width and no height, used repeatedly to represent
   // empty cells.
   protected Dimension _0dim = new Dimension( 0, 0 );

   // model of the component layout in the table -- key is a Point with Point.x
   // representing the column, Point.y representing the row. The value is a
   // reference to the component.
   protected Hashtable _components = null;

   // in both _col_widths and _row_heights, if the number is negative, the column or
   // row will be treated as having a fixed width or height. During layout, the negative
   // numbers will be treated as positive numbers -- negative width and negative height
   // being meaningless concepts.
   protected int[] _col_widths;    // stores a width per column which is the widest preferred width of components in each column
   protected int[] _row_heights;   // stores a height per row which is the tallest preferred height of components in each row

   // storage for columns and rows that want to be the same size. Each element
   // in these vectors is an int[], with each item in the array being a column
   // or row number.
   protected Vector _same_width_cols;
   protected Vector _same_height_rows;

   // if true, spread extra space equally between components in both directions,
   // doesn't stretch the components, just the space between them
   protected boolean _stretch = false;

   // by user request, the following are alternate compass directions for
   // the alignment constraint. Center is still 0.
   /**
    * For alignment constraint (a), align component to North in cell.
    */
   public static final int N = 1;  // north
   /**
    * For alignment constraint (a), align component to NorthEast in cell.
    */
   public static final int NE = 2;  // northeast
   /**
    * For alignment constraint (a), align component to East in cell.
    */
   public static final int E = 3;  // east
   /**
    * For alignment constraint (a), align component to SouthEast in cell.
    */
   public static final int SE = 4;  // southeast
   /**
    * For alignment constraint (a), align component to South in cell.
    */
   public static final int S = 5;  // south
   /**
    * For alignment constraint (a), align component to SouthWest in cell.
    */
   public static final int SW = 6;  // southwest
   /**
    * For alignment constraint (a), align component to West in cell.
    */
   public static final int W = 7;  // west
   /**
    * For alignment constraint (a), align component to NorthWest in cell.
    */
   public static final int NW = 8;  // northwest

   /**
    * Convenience setting for width (w) or height (h), causes component to use
    * Remaining cells.
    */
   public static final int R = Integer.MAX_VALUE;


   /**
    * Default constructor, no stretching.
    */
   public KappaLayout() {
      this( false );
   }

   /**
    * Constructor, allows stretching.
    * @param s if true, stretches layout to fill container by adding extra space
    * between components, does not stretch the components, just the space between them.
    */
   public KappaLayout( boolean s ) {
      _stretch = s;
   }

   /**
    * Useful for debugging a layout. Call after components are showing to make
    * sure all data is available.
    * @return a String with lots of useful info about the layout.
    */
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append( "-------------------------------\n" );
      sb.append( getClass().getName() + ":\n" );
      sb.append( "columns=" + _col_count );
      sb.append( ", rows=" + _row_count );
      sb.append( ", cells=" + ( _col_count * _row_count ) + "\n" );
      sb.append( "preferred width=" + _preferred_width );
      sb.append( ", preferred height=" + _preferred_height + "\n" );
      if ( _col_widths != null ) {
         sb.append( "column widths (left to right):" );
         for ( int i = 0; i < _col_widths.length; i++ )
            sb.append( _col_widths[ i ] + "," );
      }
      if ( _row_heights != null ) {
         sb.append( "\nrow heights (top to bottom):" );
         for ( int i = 0; i < _row_heights.length; i++ )
            sb.append( _row_heights[ i ] + "," );
      }
      if ( _constraints != null ) {
         sb.append( "\ncomponent count=" + _constraints.size() );
         sb.append( "\ncomponents (no order):\n" );
         Enumeration en = _constraints.keys();
         while ( en.hasMoreElements() )
            sb.append( en.nextElement() + "\n" );
      }
      sb.append( "-------------------------------\n" );
      return sb.toString();
   }

   /**
    * Required by LayoutManager, simply calls <code>addLayoutComponent(Component, Object)</code>.
    */
   public void addLayoutComponent( String n, Component c ) {
      addLayoutComponent( c, n );
   }

   /**
    * Required by LayoutManager.
    */
   public void removeLayoutComponent( Component c ) {
      if (c == null)
         return;
      synchronized ( c.getTreeLock() ) {
         if ( _constraints != null ) {
            _constraints.remove( c );
            if (_components == null)
               return;
            Enumeration keys = _components.keys();
            while(keys.hasMoreElements()) {
               Object key = keys.nextElement();
               Object value = _constraints.get(key);
               if (value == null)
                  continue;
               if (value.equals(c)) {
                  _components.remove(key);
                  break;
               }
            }
            keys = _table.keys();
            while(keys.hasMoreElements()) {
               Object key = keys.nextElement();
               Object value = _constraints.get(key);
               if (value == null)
                  continue;
               if (value.equals(c)) {
                  _table.remove(key);
                  break;
               }
            }
            _size_unknown = true;
            calculateDimensions();
         }
      }
   }

   /**
    * Required by LayoutManager.
    */
   public Dimension preferredLayoutSize( Container parent ) {
      synchronized ( parent.getTreeLock() ) {
         Dimension dim = new Dimension( 0, 0 );
         _size_unknown = true;
         calculateDimensions();
         Insets insets = parent.getInsets();
         dim.width = _preferred_width + insets.left + insets.right;
         dim.height = _preferred_height + insets.top + insets.bottom;
         return dim;
      }
   }

   /**
    * Required by LayoutManager.
    * @return <code>preferredLayoutSize(parent)</code>
    */
   public Dimension minimumLayoutSize( Container parent ) {
      synchronized ( parent.getTreeLock() ) {
         return preferredLayoutSize( parent );
      }
   }

   /**
    * Required by LayoutManager, does all the real layout work.
    */
   public void layoutContainer( Container parent ) {
      synchronized ( parent.getTreeLock() ) {
         Insets insets = parent.getInsets();
         int max_width = parent.getSize().width - ( insets.left + insets.right );
         int max_height = parent.getSize().height - ( insets.top + insets.bottom );
         int x = insets.left;    // x and y location to put component in pixels
         int y = insets.top;
         int xfill = 0;          // how much extra space to put between components
         int yfill = 0;          // when stretching to fill entire container

         // make sure preferred size is known, a side effect is that countColumns
         // and countRows are automatically called.
         calculateDimensions();

         // if necessary, calculate the amount of padding to add between the
         // components to fill the container
         if ( _stretch ) {
            if ( max_width > _preferred_width && _col_count > 1 ) {
               xfill = ( max_width - _preferred_width ) / ( _col_count - 1 );
            }
            if ( max_height > _preferred_height && _row_count > 1 ) {
               yfill = ( max_height - _preferred_height ) / ( _row_count - 1 );
            }
         }

         // do the layout. Components are handled by columns, top to bottom,
         // left to right. i is current column, j is current row.
         Point cell = new Point();
         for ( int i = 0; i < _col_count; i++ ) {
            cell.x = i;
            y = insets.top;
            if ( i > 0 ) {
               x += Math.abs( _col_widths[ i - 1 ] );
               if ( _stretch && i < _col_count ) {
                  x += xfill;
               }
            }

            for ( int j = 0; j < _row_count; j++ ) {
               cell.y = j;
               if ( j > 0 ) {
                  y += Math.abs( _row_heights[ j - 1 ] );
                  if ( _stretch && j < _row_count ) {
                     y += yfill;
                  }
               }
               Component c = ( Component ) _components.get( cell );
               if ( c != null && c.isVisible() ) {
                  Dimension d = c.getPreferredSize();
                  Constraints q = ( Constraints ) _constraints.get( c );

                  // calculate width of spanned columns
                  int sum_cols = 0;
                  if ( q.w == R ) {
                     for ( int n = i; n < _col_count; n++ ) {
                        sum_cols += Math.abs( _col_widths[ n ] );
                     }
                  }
                  else {
                     for ( int n = i; n < i + q.w; n++ ) {
                        sum_cols += Math.abs( _col_widths[ n ] );
                     }
                  }

                  // calculate height of spanned rows
                  int sum_rows = 0;
                  if ( q.h == R ) {
                     for ( int n = i; n < _row_count; n++ ) {
                        sum_rows += Math.abs( _row_heights[ n ] );
                     }
                  }
                  else {
                     for ( int n = j; n < j + q.h; n++ ) {
                        sum_rows += Math.abs( _row_heights[ n ] );
                     }
                  }

                  // stretch and pad if required
                  if ( q.s.indexOf( "w" ) != -1 ) {
                     d.width = sum_cols - q.p * 2;
                  }
                  if ( q.s.indexOf( "h" ) != -1 ) {
                     d.height = sum_rows - q.p * 2;
                  }

                  // calculate adjustment
                  int x_adj = sum_cols - d.width;  // max amount to put component at right edge of spanned cell(s)
                  int y_adj = sum_rows - d.height; // max amount to put component at bottom edge of spanned cell(s)

                  // in each case, add the correction for the cell, then subtract
                  // the correction after applying it.  This prevents the corrections
                  // from improperly accumulating across cells. Padding must be handled
                  // explicitly for each case.
                  // Alignment follows this pattern within the spanned cells:
                  // 8 1 2    new pattern: NW N NE
                  // 7 0 3                  W 0 E
                  // 6 5 4                 SW S SE
                  switch ( q.a ) {
                     case N:      // top center
                        x += x_adj / 2 ;
                        y += q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj / 2;
                        y -= q.p;
                        break;
                     case NE:     // top right
                        x += x_adj - q.p;
                        y += q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj - q.p;
                        y -= q.p;
                        break;
                     case E:      // center right
                        x += x_adj - q.p;
                        y += y_adj / 2;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj - q.p;
                        y -= y_adj / 2;
                        break;
                     case SE:     // bottom right
                        x += x_adj - q.p;
                        y += y_adj - q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj - q.p;
                        y -= y_adj - q.p;
                        break;
                     case S:      // bottom center
                        x += x_adj / 2;
                        y += y_adj - q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj / 2;
                        y -= y_adj - q.p;
                        break;
                     case SW:     // bottom left
                        x += q.p;
                        y += y_adj - q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= q.p;
                        y -= y_adj - q.p;
                        break;
                     case W:      // center left
                        x += q.p;
                        y += y_adj / 2;
                        c.setBounds( x, y, d.width, d.height );
                        x -= q.p;
                        y -= y_adj / 2;
                        break;
                     case NW:     // top left
                        x += q.p;
                        y += q.p;
                        c.setBounds( x, y, d.width, d.height );
                        x -= q.p;
                        y -= q.p;
                        break;
                     case 0:      // dead center
                     default:
                        x += x_adj / 2;
                        y += y_adj / 2;
                        c.setBounds( x, y, d.width, d.height );
                        x -= x_adj / 2;
                        y -= y_adj / 2;
                        break;
                  }
               }
            }
         }
      }
   }

   /**
    * Required by LayoutManager2. Will NOT add component if either component or
    * constraints are null, or if constraints is not a String or
    * Kappa/LambdaLayout.Constraint.
    */
   public void addLayoutComponent( Component comp, Object constraint ) {
      synchronized ( comp.getTreeLock() ) {
         if ( comp == null ) {
            throw new IllegalArgumentException( "No component." );
         }
         if ( constraint == null ) {
            throw new IllegalArgumentException( "No constraint." );
         }

         if ( constraint instanceof Constraints ) {
            // clone and store a Constraint so user can reuse his original
            Constraints q = ( Constraints ) constraint;
            _constraints.put( comp, q.clone() );

            // if component is a rigid strut, set the column and/or row size
            if ( comp instanceof Strut ) {
               Strut strut = ( Strut ) comp;
               if ( strut.isRigid() ) {
                  Dimension d = strut.getSize();
                  if ( d.width > 0 ) {
                     setColumnWidth( q.x, d.width );
                  }
                  if ( d.height > 0 ) {
                     setRowHeight( q.y, d.height );
                  }
               }
            }
            _size_unknown = true;

            // that's all that needs to be done for this case (Constraint and
            // Component), so return now
            return ;
         }

         // already dealt with Constraint, so check for constraint
         // String, if not a String, bail out
         if ( !( constraint instanceof String ) )
            throw new IllegalArgumentException( "Illegal constraint object." );

         // parse constraint string into tokens. There may be as few as 0 tokens,
         // or as many as 7.
         Vector tokens = new Vector();
         String token;
         String c = constraint.toString();
         while ( c.length() > 0 ) {
            int comma = c.indexOf( ',' );

            // find a token
            if ( comma != -1 ) {
               token = c.substring( 0, comma );
               c = c.substring( comma + 1 );
            }
            else {
               token = c;
               c = "";
            }

            // clean it up
            if ( token != null )
               token = token.trim();

            // if there's something left, store it, otherwise, mark missing
            // token with -1 (example of constraint string with missing tokens:
            // "1,1,,,,w", width, height, alignment are missing)
            if ( token != null && token.length() > 0 )
               tokens.addElement( token );
            else
               tokens.addElement( "-1" );
         }

         // turn tokens into a Constraints. Default Constraints values are used
         // for missing or non-specified values
         Constraints q = new Constraints();
         Enumeration en = tokens.elements();

         // get column
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString();
            try {
               q.x = Integer.parseInt( token );
               if ( q.x < 0 )
                  q.x = 0;
            }
            catch ( Exception e ) {
               q.x = 0;
            }
         }

         // get row
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString();
            try {
               q.y = Integer.parseInt( token );
               if ( q.y < 0 )
                  q.y = 0;
            }
            catch ( Exception e ) {
               q.y = 0;
            }
         }

         // get column span (width)
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString();
            if ( token.equalsIgnoreCase( "R" ) ) {
               q.w = R;
            }
            else {
               try {
                  q.w = Integer.parseInt( token );
                  if ( q.w < 1 )
                     q.w = 1;
               }
               catch ( Exception e ) {
                  q.w = 1;
               }
            }
         }

         // get row span (height)
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString();
            if ( token.equalsIgnoreCase( "R" ) ) {
               q.h = R;
            }
            else {
               try {
                  q.h = Integer.parseInt( token );
                  if ( q.h < 1 )
                     q.h = 1;
               }
               catch ( Exception e ) {
                  q.h = 1;
               }
            }
         }

         // get alignment
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString().trim();
            if ( token.equalsIgnoreCase( "N" ) || token.equals( "1" ) )
               q.a = N;
            else if ( token.equalsIgnoreCase( "NE" ) || token.equals( "2" ) )
               q.a = NE;
            else if ( token.equalsIgnoreCase( "E" ) || token.equals( "3" ) )
               q.a = E;
            else if ( token.equalsIgnoreCase( "SE" ) || token.equals( "4" ) )
               q.a = SE;
            else if ( token.equalsIgnoreCase( "S" ) || token.equals( "5" ) )
               q.a = S;
            else if ( token.equalsIgnoreCase( "SW" ) || token.equals( "6" ) )
               q.a = SW;
            else if ( token.equalsIgnoreCase( "W" ) || token.equals( "7" ) )
               q.a = W;
            else if ( token.equalsIgnoreCase( "NW" ) || token.equals( "8" ) )
               q.a = NW;
            else
               q.a = 0;
         }

         // get component stretch
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString().trim().toLowerCase();
            if ( token.equals( "w" ) || token.equals( "h" ) || token.equals( "wh" ) || token.equals( "hw" ) ) {
               q.s = token;
            }
            else
               q.s = "0";
         }

         // get component padding
         if ( en.hasMoreElements() ) {
            token = en.nextElement().toString();
            try {
               q.p = Integer.parseInt( token );
               if ( q.p < 0 )
                  q.p = 0;
            }
            catch ( Exception e ) {
               q.p = 0;
            }
         }

         // save the component and its constraints for later use
         _constraints.put( comp, q );

         // if component is a rigid strut, set the column and/or row size
         if ( comp instanceof Strut ) {
            Strut strut = ( Strut ) comp;
            if ( strut.isRigid() ) {
               Dimension d = strut.getSize();
               if ( d.width > 0 ) {
                  setColumnWidth( q.x, d.width );
               }
               if ( d.height > 0 ) {
                  setRowHeight( q.y, d.height );
               }
            }
         }
         _size_unknown = true;
      }
   }

   /**
    * Required by LayoutManager2.
    * @return <code>preferredLayoutSize(parent)</code>
    */
   public Dimension maximumLayoutSize( Container c ) {
      synchronized ( c.getTreeLock() ) {
         return preferredLayoutSize( c );
      }
   }

   /**
    * Required by LayoutManager2.
    * @return 0.5f
    */
   public float getLayoutAlignmentX( Container c ) {
      return 0.5f;   // default to centered.
   }

   /**
    * Required by LayoutManager2.
    * @return 0.5f
    */
   public float getLayoutAlignmentY( Container c ) {
      return 0.5f;   // default to centered.
   }

   /**
    * Required by LayoutManager2.
    */
   public void invalidateLayout( Container c ) {
      /* I would think this is the right thing to do, but doing this causes
      the layout to be wrong every time.  Also causes the stretch option to
      fail.
      _size_unknown = true;
      */
   }

   /**
    * Calculate preferred size and other dimensions.
    */
   protected void calculateDimensions() {
      if ( !_size_unknown )
         return ;
      _preferred_width = 0;
      _preferred_height = 0;
      Dimension dim = null;

      // count columns and rows
      countColumns();
      countRows();

      // set up table and component maps
      if ( _table == null )
         _table = new Hashtable( 23, 0.75f );
      else
         _table.clear();
      if ( _components == null )
         _components = new Hashtable( 23, 0.75f );
      else
         _components.clear();

      // set up storage for max col width and max row height.  These arrays
      // have an entry per column and row and will hold the largest width or
      // height of components in each column or row respectively.
      int[] temp;
      if ( _col_widths != null ) {
         // if column count has changed, need to preserve existing column widths
         // in case one or more remaining columns has been set to a fixed width.
         temp = new int[ _col_widths.length ];
         System.arraycopy( _col_widths, 0, temp, 0, _col_widths.length );
         _col_widths = new int[ _col_count ];
         System.arraycopy( temp, 0, _col_widths, 0, Math.min( temp.length, _col_widths.length ) );
      }
      else {
         _col_widths = new int[ _col_count ];
      }
      if ( _row_heights != null ) {
         // if row count has changed, need to preserve existing row heights
         // in case one or more remaining rows has been set to a fixed height.
         temp = new int[ _row_heights.length ];
         System.arraycopy( _row_heights, 0, temp, 0, _row_heights.length );
         _row_heights = new int[ _row_count ];
         System.arraycopy( temp, 0, _row_heights, 0, Math.min( temp.length, _row_heights.length ) );
      }
      else {
         _row_heights = new int[ _row_count ];
      }

      // get the constraints
      Enumeration en = _constraints.keys();
      while ( en.hasMoreElements() ) {
         Component c = ( Component ) en.nextElement();
         Constraints q = ( Constraints ) _constraints.get( c );
         if ( q.w == R )
            q.w = _col_count - q.x;
         if ( q.h == R )
            q.h = _row_count - q.y;

         // store the component in its (x, y) location
         _components.put( new Point( q.x, q.y ), c );

         // as components can span columns and rows, store the maximum dimension
         // of the component that could be in the spanned cells. Note that it
         // may happen that none of the component is actually in the cell.
         dim = new Dimension(c.getPreferredSize().width, c.getPreferredSize().height);
         dim.width += q.p * 2;   // adjust for padding if necessary
         dim.height += q.p * 2;
         dim.width /= q.w;
         dim.height /= q.h;
         for ( int i = q.x; i < q.x + q.w; i++ ) {
            for ( int j = q.y; j < q.y + q.h; j++ ) {
               _table.put( new Point( i, j ), dim );
            }
         }
      }

      // calculate preferred width
      int col_width = 0;
      for ( int i = 0; i < _col_count; i++ ) {
         for ( int j = 0; j < _row_count; j++ ) {
            Dimension p = ( Dimension ) _table.get( new Point( i, j ) );
            if ( p == null )
               p = _0dim;
            col_width = Math.max( p.width, col_width );
         }

         // store max width of each column
         if ( _col_widths[ i ] >= 0 ) {
            _col_widths[ i ] = col_width;
            _preferred_width += col_width;
         }
         else {
            _preferred_width += Math.abs( _col_widths[ i ] );
         }
         col_width = 0;
      }

      // adjust for same width columns
      if ( _same_width_cols != null ) {
         en = _same_width_cols.elements();
         while ( en.hasMoreElements() ) {
            int[] same = ( int[] ) en.nextElement();
            // find widest column of this group
            int widest = same[ 0 ];
            for ( int i = 0; i < same.length; i++ ) {
               if ( same[ i ] < _col_widths.length )
                  widest = Math.max( widest, _col_widths[ same[ i ] ] );
            }
            // now set all columns to this widest width
            for ( int i = 0; i < same.length; i++ ) {
               if ( same[ i ] < _col_widths.length ) {
                  _preferred_width += widest - _col_widths[ same[ i ] ];
                  _col_widths[ same[ i ] ] = widest;
               }
            }
         }
      }

      // calculate preferred height
      int row_height = 0;
      for ( int j = 0; j < _row_count; j++ ) {
         for ( int i = 0; i < _col_count; i++ ) {
            Dimension p = ( Dimension ) _table.get( new Point( i, j ) );
            if ( p == null )
               p = _0dim;
            row_height = Math.max( p.height, row_height );
         }

         // store max height of each row
         if ( _row_heights[ j ] >= 0 ) {
            _row_heights[ j ] = row_height;
            _preferred_height += row_height;
         }
         else {
            _preferred_height += Math.abs( _row_heights[ j ] );
         }
         row_height = 0;
      }

      // adjust for same height rows
      if ( _same_height_rows != null ) {
         en = _same_height_rows.elements();
         while ( en.hasMoreElements() ) {
            int[] same = ( int[] ) en.nextElement();
            // find tallest row of this group
            int tallest = same[ 0 ];
            for ( int i = 0; i < same.length; i++ ) {
               if ( same[ i ] < _row_heights.length )
                  tallest = Math.max( tallest, _row_heights[ same[ i ] ] );
            }
            // now set all rows to this tallest height
            for ( int i = 0; i < same.length; i++ ) {
               if ( same[ i ] < _row_heights.length ) {
                  _preferred_height += tallest - _row_heights[ same[ i ] ];
                  _row_heights[ same[ i ] ] = tallest;
               }
            }
         }
      }
      _size_unknown = false;
   }

   /**
    * Calculate number of columns in table.
    */
   private void countColumns() {
      _col_count = 0;
      Hashtable rows = new Hashtable();

      // get the constraints
      Enumeration en = _constraints.elements();
      while ( en.hasMoreElements() ) {
         Constraints q = ( Constraints ) en.nextElement();

         // figure out which columns this component spans. The BitSet represents
         // a row, and for each non-empty column in the row, a bit is set. The
         // BitSets representing each row are stored in the 'rows' Hashtable.
         BitSet row = null;
         String y = String.valueOf( q.y );
         if ( !rows.containsKey( y ) ) {
            row = new BitSet();
            rows.put( y, row );
         }
         row = ( BitSet ) rows.get( y );
         int last_col = ( q.w == R ? q.x + 1 : q.x + q.w );
         for ( int i = q.x; i < last_col; i++ )
            row.set( i );
      }

      // calculate the number of columns by going through each BitSet and
      // counting the number of set bits. The highest bit is the number of
      // columns.
      en = rows.elements();
      while ( en.hasMoreElements() ) {
         BitSet row = ( BitSet ) en.nextElement();
         for ( int i = 0; i < row.size(); i++ ) {
            if ( row.get( i ) )
               _col_count = Math.max( _col_count, i + 1 ); // add 1 as column index is 0-based
         }
      }
   }

   /**
    * Calculate number of rows in table.
    */
   private void countRows() {
      // this is done exactly in the same manner as countColumns, see the comments
      // there for details on the counting algorithm
      _row_count = 0;
      Hashtable cols = new Hashtable();

      Enumeration en = _constraints.elements();
      while ( en.hasMoreElements() ) {
         Constraints q = ( Constraints ) en.nextElement();
         BitSet col = null;
         String x = String.valueOf( q.x );
         if ( !cols.containsKey( x ) ) {
            col = new BitSet();
            cols.put( x, col );
         }
         col = ( BitSet ) cols.get( x );
         int last_row = ( q.h == R ? q.y + 1 : q.y + q.h );
         for ( int i = q.y; i < last_row; i++ ) {
            col.set( i );
         }
      }
      en = cols.elements();
      while ( en.hasMoreElements() ) {
         BitSet col = ( BitSet ) en.nextElement();
         for ( int i = 0; i < col.size(); i++ ) {
            if ( col.get( i ) ) {
               _row_count = Math.max( _row_count, i + 1 );
            }
         }
      }
   }

   /**
    * Makes two columns be the same width.  The actual width will be the larger
    * of the preferred widths of these columns.
    * @param column1 column number
    * @param column2 column number
    */
   public void makeColumnsSameWidth( int column1, int column2 ) {
      makeColumnsSameWidth( new int[] {column1, column2} );
   }

   /**
    * Makes several columns be the same width.  The actual width will be the largest
    * preferred width of these columns.
    * @param columns array of column numbers to make the same width.
    */
   public void makeColumnsSameWidth( int[] columns ) {
      if ( columns.length <= 1 )
         return ;
      for ( int i = 0; i < columns.length; i++ ) {
         if ( columns[ i ] < 0 )
            throw new IllegalArgumentException( "Column parameter must be greater than 0." );
      }
      if ( _same_width_cols == null )
         _same_width_cols = new Vector();
      _same_width_cols.addElement( columns );
      _size_unknown = true;
   }

   /**
    * Makes all columns be the same width.  The actual width will be the largest
    * preferred width of these columns.
    */
   public void makeColumnsSameWidth() {
      countColumns();
      int[] columns = new int[ _col_count ];
      for ( int i = 0; i < _col_count; i++ ) {
         columns[ i ] = i;
      }
      makeColumnsSameWidth( columns );
   }

   /**
    * Makes two rows be the same height.  The actual height will be the larger
    * of the preferred heights of these rows.
    * @param row1 row number
    * @param row2 row number
    */
   public void makeRowsSameHeight( int row1, int row2 ) {
      makeRowsSameHeight( new int[] {row1, row2} );
   }

   /**
    * Makes several rows be the same height.  The actual height will be the largest
    * preferred height of these rows.
    * @param rows  array of row numbers to make the same height.
    */
   public void makeRowsSameHeight( int[] rows ) {
      if ( rows.length <= 1 )
         return ;
      for ( int i = 0; i < rows.length; i++ ) {
         if ( rows[ i ] < 0 )
            throw new IllegalArgumentException( "Row parameter must be greater than 0." );
      }
      if ( _same_height_rows == null )  // laziness pays off now
         _same_height_rows = new Vector();
      _same_height_rows.addElement( rows );
      _size_unknown = true;
   }

   /**
    * Makes all rows be the same height.  The actual height will be the largest
    * preferred height of these rows.
    */
   public void makeRowsSameHeight() {
      countRows();
      int[] rows = new int[ _row_count ];
      for ( int i = 0; i < rows.length; i++ ) {
         rows[ i ] = i;
      }
      makeRowsSameHeight( rows );
   }

   /**
    * Sets a column to a specific width.  Use care with this method, components
    * wider than the set width will be truncated.
    * @param column column number
    * @param width width in pixels
    */
   public void setColumnWidth( int column, int width ) {
      if ( column < 0 )
         throw new IllegalArgumentException( "Column must be >= 0." );
      if ( _col_widths == null )
         _col_widths = new int[ column + 1 ];
      if ( _col_widths.length <= column ) {
         int[] tmp = new int[ _col_widths.length ];
         System.arraycopy( _col_widths, 0, tmp, 0, _col_widths.length );
         _col_widths = new int[ column + 1 ];
         System.arraycopy( tmp, 0, _col_widths, 0, tmp.length );
      }
      // store fixed width columns as a negative number
      _col_widths[ column ] = -1 * width;
      _size_unknown = true;
   }

   /**
    * Sets a row to a specific height.  Use care with this method, components
    * taller than the set height will be truncated.
    * @param row row number
    * @param height height in pixels
    */
   public void setRowHeight( int row, int height ) {
      if ( row < 0 )
         throw new IllegalArgumentException( "Row must be >= 0." );
      if ( _row_heights == null )
         _row_heights = new int[ row + 1 ];
      if ( _row_heights.length <= row ) {
         int[] tmp = new int[ _row_heights.length ];
         System.arraycopy( _row_heights, 0, tmp, 0, _row_heights.length );
         _row_heights = new int[ row + 1 ];
         System.arraycopy( tmp, 0, _row_heights, 0, tmp.length );
      }
      // store fixed height rows as a negative number
      _row_heights[ row ] = -1 * height;
      _size_unknown = true;
   }

   /**
    * Creates a Constraints for direct manipulation.
    * @return a Constraints object for direct manipulation.
    */
   public static Constraints createConstraint() {
      return new Constraints();
   }

   /**
    * Useful for holding an otherwise empty column to a minimum width.
    * @param width desired width of component
    * @return a component with some width but no height
    */
   public static Component createHorizontalStrut( int width ) {
      return new Strut( width, 0 );
   }

   /**
    * Useful for holding a column to a fixed width.
    * @param width desired width of component
    * @param rigid if true, will not stretch
    * @return a component with some width but no height
    */
   public static Component createHorizontalStrut( int width, boolean rigid ) {
      return new Strut( width, 0, rigid );
   }

   /**
    * Useful for holding an otherwise empty row to a minimum height.
    * @param height desired height of component
    * @return a component with some height but no width
    */
   public static Component createVerticalStrut( int height ) {
      return new Strut( 0, height );
   }

   /**
    * Useful for holding a  row to a fixed height.
    * @param height desired height of component
    * @param rigid if true, will not stretch
    * @return a component with some height but no width
    */
   public static Component createVerticalStrut( int height, boolean rigid ) {
      return new Strut( 0, height, rigid );
   }

   /**
    * Useful for setting an otherwise blank cell to a minimum width and height.
    * @param width desired width of component
    * @param height desired height of component
    * @return a component with some height and width
    */
   public static Component createStrut( int width, int height ) {
      return new Strut( width, height );
   }

   /**
    * Useful for setting a row and column to a fixed width and height.
    * @param width desired width of component
    * @param height desired height of component
    * @return a component with some height and width
    */
   public static Component createStrut( int width, int height, boolean rigid ) {
      return new Strut( width, height, rigid );
   }

   /**
    * Simple component that is invisible. Struts can be either rigid or non-
    * rigid. While this component could be used with other layout managers,
    * it special properties are intended for use with KappaLayout. In particular,
    * when the strut is set to rigid, it will lock the column or row (depends on
    * the orientation of the strut) to the width or height of the strut. A non-
    * rigid strut sets a minimum width or height on a column or row, but does
    * not set a maximum like a rigid strut does.
    */
   public static class Strut extends Component implements Serializable {
      private Dimension _dim;
      private boolean _rigid;

      /**
       * @param w width
       * @param h height
       */
      public Strut( int w, int h ) {
         this( w, h, false );
      }

      /**
       * @param w width
       * @param h height
       * @param rigid rigid
       */
      public Strut( int w, int h, boolean rigid ) {
         _dim = new Dimension( w, h );
         _rigid = rigid;
      }

      /**
       * Overrides <code>getPreferredSize</code> from Component.
       */
      public Dimension getPreferredSize() {
         return _dim;
      }

      /**
       * Overrides <code>getSize</code> from Component.
       */
      public Dimension getSize() {
         return _dim;
      }

      /**
       * @return true if this strut is rigid.
       */
      public boolean isRigid() {
         return _rigid;
      }

      /**
       * @param rigid if true, this strut will act as a rigid strut
       */
      public void setRigid( boolean rigid ) {
         _rigid = rigid;
      }
   }


   /**
    * This class is cloneable so that users may create and reuse a Constraints object
    * similar to how one would use a GridBagConstraints rather than the string parameters.
    */
   public static class Constraints extends Object implements Cloneable, Serializable {
      /**
       * start column
       */
      public int x = 0;

      /**
       * start row
       */
      public int y = 0;

      /**
       * # columns wide
       */
      public int w = 1;

      /**
       * # rows high
       */
      public int h = 1;

      /**
       * alignment within cell, see comments in KappaLayout
       */
      public int a = 0;

      /**
       * stretch: default is 0 (character zero, no stretch), w = width of cell, h = height of cell, wh = both width and height
       */
      public String s = "0";

      /**
       * padding, same amount of blank space on all four sides of component
       */
      public int p = 0;

      /**
       * Plain String representation of this Constraints, suitable for using as a
       * constraints string if needed.
       */
      public String toString() {
         StringBuffer sb = new StringBuffer();
         sb.append( String.valueOf( x ) + "," )
         .append( String.valueOf( y ) + "," )
         .append( String.valueOf( w ) + "," );
         if ( w == R )
            sb.append( "R," );
         else
            sb.append( String.valueOf( w ) + "," );
         if ( h == R )
            sb.append( "R," );
         else
            sb.append( String.valueOf( h ) + "," );
         sb.append( String.valueOf( a ) + "," )
         .append( String.valueOf( s ) + "," )
         .append( String.valueOf( p ) )
         .toString();
         return sb.toString();
      }


      /**
       * @return a clone of this object.
       */
      public Object clone() {
         try {
            return super.clone();
         }
         catch ( Exception e ) {
            return null;
         }
      }
   }

   /*
   public Component getDebugPanel() {
      java.awt.Panel p = new java.awt.Panel() {
         public Dimension getPreferredSize() {
            int width = 0;
            for ( int i = 0; i < _col_widths.length; i++ ) {
               width += Math.abs(_col_widths[i]);
            }

            int height = 0;
            for ( int i = 0; i < _row_heights.length; i++ ) {
               height += Math.abs(_row_heights[i]);
            }

            return new Dimension(width, height);
         }

         public void paint(Graphics g) {
            g.setColor(Color.black);
            Dimension d = getPreferredSize();

            int x = 0;
            g.drawLine(x, 0, x, d.height);
            for ( int i = 0; i < _col_widths.length; i++ ) {
               x = Math.abs(_col_widths[i]);
               g.drawLine(x, 0, x, d.height);
            }
            g.drawLine(d.width, 0, d.width, d.height);

            int y = 0;
            g.drawLine(0, y, d.width, y);
            for ( int i = 0; i < _row_heights.length; i++ ) {
               y = Math.abs(_row_heights[i]);
               g.drawLine(0, y, d.width, y);
            }
            g.drawLine(0, d.height, d.width, d.height);
         }
      };
      return p;
}
   */
}


