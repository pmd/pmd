package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.gui.menu.ASTNodePopupMenu;
import net.sourceforge.pmd.util.viewer.model.ASTModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;


/**
 * tree panel GUI
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class ASTPanel
  extends JPanel
  implements ViewerModelListener, TreeSelectionListener
{
  private ViewerModel model;
  private JTree       tree;

  /**
   * constructs the panel
   *
   * @param model model to attach the panel to
   */
  public ASTPanel( ViewerModel model )
  {
    this.model = model;

    init(  );
  }

  private void init(  )
  {
    model.addViewerModelListener( this );

    setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(  ), NLS.nls( "AST.PANEL.TITLE" ) ) );

    setLayout( new BorderLayout(  ) );

    tree = new JTree( (TreeNode)null );

    tree.addTreeSelectionListener( this );

    tree.addMouseListener(
      new MouseAdapter(  )
      {
        public void mouseReleased( MouseEvent e )
        {
          if ( e.isPopupTrigger(  ) )
          {
            TreePath path =
              tree.getClosestPathForLocation( e.getX(  ), e.getY(  ) );
            tree.setSelectionPath( path );

            JPopupMenu menu =
              new ASTNodePopupMenu(
                model, (SimpleNode)path.getLastPathComponent(  ) );

            menu.show( tree, e.getX(  ), e.getY(  ) );
          }
        }
      } );

    add( new JScrollPane( tree ), BorderLayout.CENTER );
  }

  /**
   * @see org.gruschko.pmd.viewer.model.ViewerModelListener#viewerModelChanged(org.gruschko.pmd.viewer.model.ViewerModelEvent)
   */
  public void viewerModelChanged( ViewerModelEvent e )
  {
    switch ( e.getReason(  ) )
    {
      case ViewerModelEvent.CODE_RECOMPILED :
        tree.setModel( new ASTModel( model.getRootNode(  ) ) );

        break;

      case ViewerModelEvent.NODE_SELECTED :

        if ( e.getSource(  ) != this )
        {
          LinkedList list = new LinkedList(  );

          for (
            Node node = (Node)e.getParameter(  ); node != null;
              node = node.jjtGetParent(  ) )
            list.addFirst( node );

          TreePath path = new TreePath( list.toArray(  ) );

          tree.setSelectionPath( path );

          tree.scrollPathToVisible( path );
        }

        break;
    }
  }

  /**
   * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
   */
  public void valueChanged( TreeSelectionEvent e )
  {
    model.selectNode(
      (SimpleNode)e.getNewLeadSelectionPath(  ).getLastPathComponent(  ), this );
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
 * Revision 1.3  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.2  2003/09/23 07:52:16  bgr
 * menus added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
