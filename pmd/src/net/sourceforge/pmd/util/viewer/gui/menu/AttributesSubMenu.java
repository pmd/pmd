package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.Attribute;
import net.sourceforge.pmd.jaxen.AttributeAxisIterator;
import net.sourceforge.pmd.util.viewer.model.AttributeToolkit;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.util.NLS;

import java.text.MessageFormat;

import javax.swing.JMenu;


/**
 * contains menu items for the predicate creation
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class AttributesSubMenu
  extends JMenu
{
  private ViewerModel model;
  private SimpleNode  node;

  public AttributesSubMenu( ViewerModel model, SimpleNode node )
  {
    super( 
      MessageFormat.format( 
        NLS.nls( "AST.MENU.ATTRIBUTES" ), new Object[] { node.toString(  ) } ) );

    this.model   = model;
    this.node    = node;

    init(  );
  }

  private void init(  )
  {
    AttributeAxisIterator i = new AttributeAxisIterator( node );

    for ( ; i.hasNext(  ); )
    {
      Attribute attribute = (Attribute)i.next(  );

      add( 
        new XPathFragmentAddingItem( 
          attribute.getName(  ) + " = " + attribute.getValue(  ), model,
          AttributeToolkit.constructPredicate( attribute ) ) );
    }
  }
}


/*
 * $Log$
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
