package net.sourceforge.pmd.util.viewer.gui.menu;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;

import javax.swing.*;


/**
 * context sensetive menu for the AST Panel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class ASTNodePopupMenu
  extends JPopupMenu
{
  private ViewerModel model;
  private SimpleNode  node;

  public ASTNodePopupMenu( ViewerModel model, SimpleNode node )
  {
    this.model   = model;
    this.node    = node;

    init(  );
  }

  private void init(  )
  {
    add( new SimpleNodeSubMenu( model, node ) );

    addSeparator(  );

    add( new AttributesSubMenu( model, node ) );
  }
}


/*
 * $Log$
 * Revision 1.4  2004/04/15 18:21:58  tomcopeland
 * Cleaned up imports with new version of IDEA; fixed some deprecated Ant junx
 *
 * Revision 1.3  2003/09/23 20:51:06  tomcopeland
 * Cleaned up imports
 *
 * Revision 1.2  2003/09/23 20:34:33  tomcopeland
 * Fixed some stuff PMD found
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
