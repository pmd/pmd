package net.sourceforge.pmd.util.viewer.gui;

import net.sourceforge.pmd.ast.ParseException;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.model.ViewerModelEvent;
import net.sourceforge.pmd.util.viewer.model.ViewerModelListener;
import net.sourceforge.pmd.util.viewer.util.NLS;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * viewer's main frame
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class MainFrame
  extends JFrame
  implements ActionListener, ActionCommands, ViewerModelListener
{
  private ViewerModel     model;
  private SourceCodePanel sourcePanel;
  private ASTPanel        astPanel;
  private XPathPanel      xPathPanel;
  private JButton         compileBtn;
  private JButton         evalBtn;

  /**
   * constructs and shows the frame
   */
  public MainFrame(  )
  {
    super( NLS.nls( "MAIN.FRAME.TITLE" ) );

    init(  );
  }

  private void init(  )
  {
    model = new ViewerModel(  );

    model.addViewerModelListener( this );

    sourcePanel   = new SourceCodePanel( model );
    astPanel      = new ASTPanel( model );
    xPathPanel    = new XPathPanel( model );

    getContentPane(  ).setLayout( new BorderLayout(  ) );

    JSplitPane editingPane =
      new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, sourcePanel, astPanel );
    editingPane.setResizeWeight( 0.5d );

    JPanel interactionsPane = new JPanel( new BorderLayout(  ) );

    interactionsPane.add( xPathPanel, BorderLayout.SOUTH );
    interactionsPane.add( editingPane, BorderLayout.CENTER );

    getContentPane(  ).add( interactionsPane, BorderLayout.CENTER );

    compileBtn = new JButton( NLS.nls( "MAIN.FRAME.COMPILE_BUTTON.TITLE" ) );
    compileBtn.setActionCommand( COMPILE_ACTION );
    compileBtn.addActionListener( this );

    evalBtn = new JButton( NLS.nls( "MAIN.FRAME.EVALUATE_BUTTON.TITLE" ) );
    evalBtn.setActionCommand( EVALUATE_ACTION );
    evalBtn.addActionListener( this );
    evalBtn.setEnabled( false );

    JPanel btnPane = new JPanel( new FlowLayout( FlowLayout.LEFT ) );

    btnPane.add( compileBtn );
    btnPane.add( evalBtn );

    getContentPane(  ).add( btnPane, BorderLayout.SOUTH );

    setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );

    pack(  );
    setSize( 800, 600 );

    setVisible( true );
  }

  /**
   * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  public void actionPerformed( ActionEvent e )
  {
    String command = e.getActionCommand(  );

    if ( command.equals( COMPILE_ACTION ) )
    {
      try
      {
        model.commitSource( sourcePanel.getSourceCode(  ) );
      }
      catch ( ParseException exc )
      {
        new ParseExceptionHandler( this, exc );
      }
    }
    else if ( command.equals( EVALUATE_ACTION ) )
    {
      try
      {
        model.evaluateXPathExpression( xPathPanel.getXPathExpression(  ), this );
      }
      catch ( Exception exc )
      {
        new ParseExceptionHandler( this, exc );
      }
    }
  }

  /**
   * @see org.gruschko.pmd.viewer.model.ViewerModelListener#viewerModelChanged(org.gruschko.pmd.viewer.model.ViewerModelEvent)
   */
  public void viewerModelChanged( ViewerModelEvent e )
  {
    evalBtn.setEnabled( model.hasCompiledTree(  ) );
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
 * Revision 1.2  2003/09/24 00:40:35  bgr
 * evaluation results browsing added
 *
 * Revision 1.1  2003/09/22 05:21:54  bgr
 * initial commit
 *
 */
