package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class PMDViewer extends JFrame
{

    private DirectoryTree m_directoryTree;
    private static PMDViewer m_pmdViewer;

    /**
     *******************************************************************************
     *
     */
    private PMDViewer()
    {
        super("PMD Viewer");

        m_pmdViewer = this;

        int windowWidth = 900;
        int windowHeight = 900;
        int windowMargin = 20;
        Dimension screenSize = getToolkit().getScreenSize();
        int windowLocationX = (screenSize.width - windowWidth) / 2;
        int windowLocationY = (screenSize.height - windowHeight) / 2;

        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //
        // Create the source file chooser label that will go into the split pane's top panel.
        //
        JLabel selectSourceFileLabel = new JLabel();

        {
            selectSourceFileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            selectSourceFileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
            selectSourceFileLabel.setText("Select a source file to view its analysis results below.");
        }

        //
        // Create the directory tree that will appear in the directory split pane's left panel.
        //
        m_directoryTree = new DirectoryTree();

        //
        // Create a scroll pane for the directory tree.
        //
        JScrollPane directoryTreeScrollPane = new JScrollPane(m_directoryTree);

        {
            Color background = PMDLookAndFeel.TREE_BACKGROUND_COLOR;
            directoryTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            directoryTreeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            directoryTreeScrollPane.getViewport().setBackground(background);
            directoryTreeScrollPane.setAutoscrolls(true);
            directoryTreeScrollPane.setBorder(BorderFactory.createEtchedBorder());
        }

        //
        // Create the directory table that will go into the directory split pane's right panel.
        //
        DirectoryTable directoryTable = new DirectoryTable(m_directoryTree);

        //
        // Create a scroll pane for the directory table.
        //
        JScrollPane directoryTableScrollPane = new JScrollPane(directoryTable);

        {
            Color background = PMDLookAndFeel.TABLE_BACKGROUND_COLOR;
            directoryTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            directoryTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            directoryTableScrollPane.getViewport().setBackground(background);
            directoryTableScrollPane.setAutoscrolls(true);
            directoryTableScrollPane.setBorder(BorderFactory.createEtchedBorder());
        }

        //
        // Create a split pane for the directory tree and file list.
        //
        JSplitPane directorySplitPane = new JSplitPane();

        {
            directorySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            directorySplitPane.setResizeWeight(0.5);
            directorySplitPane.setDividerSize(5);
            directorySplitPane.setLeftComponent(directoryTreeScrollPane);
            directorySplitPane.setRightComponent(directoryTableScrollPane);
        }

        //
        // The editor pane where the results are stored.  An editor pane is used so that
        // the user can enter notes and copy the results.
        //
        ResultsViewer resultsViewer = new ResultsViewer(directoryTable);

        {
            resultsViewer.setSelectionColor(Color.blue);
        }

        //
        // The scroll pane that contains the editor pane.
        //
        JScrollPane resultsScrollPane = new JScrollPane(resultsViewer);

        {
            resultsScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            resultsScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            resultsScrollPane.getViewport().setBackground(Color.white);
            resultsScrollPane.setAutoscrolls(true);
            resultsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        }

        //
        // Create the split pane that contains the top panel and bottom panels.
        //
        JSplitPane mainSplitPane = new JSplitPane();

        {
            mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
            mainSplitPane.setResizeWeight(0.5);
            mainSplitPane.setDividerSize(5);
            mainSplitPane.setTopComponent(directorySplitPane);
            mainSplitPane.setBottomComponent(resultsScrollPane);
        }

        //
        // Create the content panel that will contain the split pane.
        //
        JPanel contentPanel = new JPanel(new BorderLayout());

        {
            Border outsideBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            Border insideBorder = BorderFactory.createEmptyBorder(windowMargin,windowMargin,windowMargin,windowMargin);
            Border compoundBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);

            contentPanel.setBorder(compoundBorder);
            contentPanel.add(selectSourceFileLabel, BorderLayout.NORTH);
            contentPanel.add(mainSplitPane,  BorderLayout.CENTER);
        }

        //
        // Put the content panel into the frame's content pane.
        //
        getContentPane().add(contentPanel);
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    protected static final JFrame getWindow()
    {
        return m_pmdViewer;
    }

    /**
     *********************************************************************************
     *
     * @param args
     */
    private void setupFiles()
    {
        m_directoryTree.setupFiles();
    }

    /**
     *********************************************************************************
     *
     */
    public static final void run()
    {
        try
        {
            // Setup the User Interface based on this computer's operating system.
            // This must be done before calling Java and Swing classes that call the GUI.
            String useLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            //String useLookAndFeel = "net.sourceforge.pmd.swingui.PMDLookAndFeel";

            UIManager.setLookAndFeel(useLookAndFeel);

            PMDViewer pmdViewer = new PMDViewer();

            pmdViewer.setVisible(true);
            pmdViewer.setupFiles();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        catch (Error error)
        {
            error.printStackTrace();
        }

        return;
    }

    /**
     *********************************************************************************
     *
     * @param args
     */
    public static void main(String[] args)
    {
        run();
    }
}