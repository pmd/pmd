package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.BorderLayout;
import java.awt.Color;


/**
 * source code panel
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class SourceCodePanel
  extends JPanel
  implements ViewerModelListener
{
  private ViewerModel model;
  private JTextArea   sourceCodeArea;

  public SourceCodePanel( ViewerModel model )
  {
    this.model = model;

    init(  );
  }

  private void init(  )
  {
    model.addViewerModelListener( this );

    setBorder(
      BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(  ), NLS.nls( "SOURCE.PANEL.TITLE" ) ) );

    setLayout( new BorderLayout(  ) );

    sourceCodeArea = new JTextArea(  );

    add( new JScrollPane( sourceCodeArea ), BorderLayout.CENTER );
  }

  /**
   * retrieves the string representation of the source code
   *
   * @return source code's string representation
   */
  public String getSourceCode(  )
  {
    return sourceCodeArea.getText(  );
  }

  /**
   * @see org.gruschko.pmd.viewer.model.ViewerModelListener#viewerModelChanged(org.gruschko.pmd.viewer.model.ViewerModelEvent)
   */
  public void viewerModelChanged( ViewerModelEvent e )
  {
    if ( e.getReason(  ) == ViewerModelEvent.NODE_SELECTED )
    {
      final SimpleNode node = (SimpleNode)e.getParameter(  );

      SwingUtilities.invokeLater(
        new Runnable(  )
        {
          public void run(  )
          {
            try
            {
              sourceCodeArea.getHighlighter(  ).removeAllHighlights(  );

              if ( node == null )
              {
                return;
              }

              int startOffset =
                ( sourceCodeArea.getLineStartOffset( node.getBeginLine(  ) - 1 ) +
                node.getBeginColumn(  ) ) - 1;

              int end =
                ( sourceCodeArea.getLineStartOffset( node.getEndLine(  ) - 1 ) +
                node.getEndColumn(  ) );

              sourceCodeArea.getHighlighter(  ).addHighlight(
                startOffset, end,
                new DefaultHighlighter.DefaultHighlightPainter(
                  new Color( 79, 237, 111 ) ) );

              sourceCodeArea.moveCaretPosition( startOffset );
            }
            catch ( BadLocationException exc )
            {
              throw new IllegalStateException( exc.getMessage(  ) );
            }
          }
        } );
    }
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
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
