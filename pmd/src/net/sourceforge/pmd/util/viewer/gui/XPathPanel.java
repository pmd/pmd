package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.*;
import java.awt.*;


/**
 * Panel for the XPath entry and editing
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class XPathPanel
  extends JTabbedPane
  implements ViewerModelListener
{
  private ViewerModel model;
  private JTextArea   xPathArea;

  /**
   * Constructs the panel
   *
   * @param model model to refer to
   */
  public XPathPanel( ViewerModel model )
  {
    super( JTabbedPane.BOTTOM );

    this.model = model;

    init(  );
  }

  private void init(  )
  {
    model.addViewerModelListener( this );

    xPathArea = new JTextArea(  );

    setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(  ), NLS.nls( "XPATH.PANEL.TITLE" ) ) );

    add( new JScrollPane( xPathArea ), NLS.nls( "XPATH.PANEL.EXPRESSION" ) );
    add( new EvaluationResultsPanel( model ), NLS.nls( "XPATH.PANEL.RESULTS" ) );

    setPreferredSize( new Dimension( -1, 200 ) );
  }

  /**
   * retrieves the XPath expression typed into the text area
   *
   * @return XPath expression
   */
  public String getXPathExpression(  )
  {
    return xPathArea.getText(  );
  }

  /**
   * @see org.gruschko.pmd.viewer.model.ViewerModelListener#viewerModelChanged(org.gruschko.pmd.viewer.model.ViewerModelEvent)
   */
  public void viewerModelChanged( ViewerModelEvent e )
  {
    switch ( e.getReason(  ) )
    {
      case ViewerModelEvent.PATH_EXPRESSION_APPENDED :

        if ( e.getSource(  ) != this )
        {
          xPathArea.append( (String)e.getParameter(  ) );
        }

        setSelectedIndex( 0 );

        break;

      case ViewerModelEvent.CODE_RECOMPILED :
        setSelectedIndex( 0 );

        break;
    }
  }
}


/*
 * $Log$
 * Revision 1.3  2004/04/15 18:21:58  tomcopeland
 * Cleaned up imports with new version of IDEA; fixed some deprecated Ant junx
 *
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
