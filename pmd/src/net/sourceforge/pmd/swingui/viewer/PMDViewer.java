package net.sourceforge.pmd.swingui.viewer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class PMDViewer extends JFrame
{

    private DirectoryTree m_directoryTree;

    /**
     *******************************************************************************
     *
     */
    public PMDViewer()
    {
        super("PMD Viewer");

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
            selectSourceFileLabel.setText("Select a source file and its analysis results will appear below.");
        }

        //
        // Create the directory tree that will go into the split pane's top panel on the left.
        //
        m_directoryTree = new DirectoryTree(this);

        //
        // Create a scroll pane for the file list.
        //
        JScrollPane directoryTreeScrollPane = new JScrollPane(m_directoryTree);

        {
            directoryTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            directoryTreeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            directoryTreeScrollPane.getViewport().setBackground(Color.white);
            directoryTreeScrollPane.setAutoscrolls(true);
            directoryTreeScrollPane.setBorder(BorderFactory.createEtchedBorder());
        }

        //
        // Create the file list that will go into the split pane's top panel on the right.
        //
        SourceFileList sourceFileList = new SourceFileList();
        {
            sourceFileList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
            sourceFileList.setBackground(Color.white);
            sourceFileList.setDirectoryTree(m_directoryTree);
        }

        //
        // Create a scroll pane for the file list.
        //
        JScrollPane fileListScrollPane = new JScrollPane(sourceFileList);

        {
            fileListScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            fileListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            fileListScrollPane.getViewport().setBackground(Color.white);
            fileListScrollPane.setAutoscrolls(true);
            fileListScrollPane.setBorder(BorderFactory.createEtchedBorder());
        }

        //
        // Create a split pane for the directory tree and file list.
        //
        JSplitPane directoryAndFileSplitPane = new JSplitPane();

        {
            directoryAndFileSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
            directoryAndFileSplitPane.setDividerLocation(0.5);
            directoryAndFileSplitPane.setDividerSize(5);
            directoryAndFileSplitPane.setLeftComponent(directoryTreeScrollPane);
            directoryAndFileSplitPane.setRightComponent(fileListScrollPane);
        }

        //
        // The editor pane where the results are stored.  An editor pane is used so that
        // the user can enter notes and copy the results.
        //
        ResultsEditorPane resultsEditorPane = new ResultsEditorPane(this);

        {
            resultsEditorPane.setSelectionColor(Color.blue);
            resultsEditorPane.setSourceFileList(sourceFileList);
        }

        //
        // The scroll pane that contains the editor pane.
        //
        JScrollPane resultsScrollPane = new JScrollPane(resultsEditorPane);

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
            mainSplitPane.setDividerLocation(0.75);
            mainSplitPane.setDividerSize(5);
            mainSplitPane.setTopComponent(directoryAndFileSplitPane);
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
     * @param args
     */
    public void setupFiles()
    {
        m_directoryTree.setupFiles();
    }

    /**
     *********************************************************************************
     *
     * @param args
     */
    public static void main(String[] args)
    {
        try
        {
            // Setup the User Interface based on this computer's operating system.
            // This must be done before calling Java and Swing classes that call the GUI.
            String useLookAndFeel = UIManager.getSystemLookAndFeelClassName();

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
}