package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.util.viewer.model.ViewerModel;

import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * adds the given path fragment to the XPath expression upon action
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class XPathFragmentAddingItem
  extends JMenuItem
  implements ActionListener
{
  private ViewerModel model;
  private String      fragment;

  /**
   * constructs the item
   *
   * @param caption menu item's caption
   * @param model model to refer to
   * @param fragment XPath expression fragment to be added upon action
   */
  public XPathFragmentAddingItem( 
    String caption, ViewerModel model, String fragment )
  {
    super( caption );

    this.model      = model;
    this.fragment   = fragment;

    addActionListener( this );
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed( ActionEvent e )
  {
    model.appendToXPathExpression( fragment, this );
  }
}


/*
 * $Log$
 * Revision 1.2  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.1  2003/09/23 20:32:42  tomcopeland
 * Added Boris Gruschko's new AST/XPath viewer
 *
 * Revision 1.1  2003/09/24 01:33:03  bgr
 * moved to a new package
 *
 * Revision 1.1  2003/09/23 07:52:16  bgr
 * menus added
 *
 */
