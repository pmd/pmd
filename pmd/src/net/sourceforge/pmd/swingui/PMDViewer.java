package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
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
    private JLabel m_selectSourceFileLabel;
    private JScrollPane m_directoryTreeScrollPane;
    private DirectoryTable m_directoryTable;
    private JScrollPane m_directoryTableScrollPane;
    private JSplitPane m_directorySplitPane;
    private ResultsViewer m_resultsViewer;
    private JScrollPane m_resultsViewerScrollPane;
    private JSplitPane m_mainSplitPane;
    private static PMDViewer m_pmdViewer;

    /**
     *******************************************************************************
     *
     */
    private PMDViewer()
    {
        super("PMD Viewer");

        m_pmdViewer = this;

        int windowWidth = 1200;
        int windowHeight = 1000;
        int windowMargin = 10;
        Dimension screenSize = getToolkit().getScreenSize();

        if (windowWidth >= screenSize.width)
        {
            windowWidth = screenSize.width - 10;
        }

        if (windowHeight >= screenSize.height)
        {
            windowHeight = screenSize.height - 20;
        }

        int windowLocationX = (screenSize.width - windowWidth) / 2;
        int windowLocationY = (screenSize.height - windowHeight) / 2;

        setLocation(windowLocationX, windowLocationY);
        setSize(windowWidth, windowHeight);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMenuBar();
        createSelectSourceFileLabel();
        createDirectoryTreeScrollPane();
        createDirectoryTableScrollPane();
        createDirectorySplitPane();
        createResultsViewer();
        createResultsViewerScrollPane();
        createMainSplitPane();
        getContentPane().add(createContentPanel(windowMargin));
    }

    /**
     *********************************************************************************
     *
     */
    private void createMenuBar()
    {
       JMenuBar menuBar = new JMenuBar();

       setJMenuBar(menuBar);
       menuBar.add(new FileMenu(menuBar));
       menuBar.add(new EditMenu(menuBar));
       menuBar.add(new ViewMenu(menuBar));
       menuBar.add(new HelpMenu(menuBar));
    }

    /**
     *********************************************************************************
     *
     */
    private void createSelectSourceFileLabel()
    {
        m_selectSourceFileLabel = new JLabel();

        m_selectSourceFileLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        m_selectSourceFileLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        m_selectSourceFileLabel.setText("Select a source file to view its analysis results below.");
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTreeScrollPane()
    {
        Color background;

        m_directoryTree = new DirectoryTree();
        m_directoryTreeScrollPane = new JScrollPane(m_directoryTree);
        background = UIManager.getColor("pmdTreeBackground");

        m_directoryTreeScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_directoryTreeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        m_directoryTreeScrollPane.getViewport().setBackground(background);
        m_directoryTreeScrollPane.setAutoscrolls(true);
        m_directoryTreeScrollPane.setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTableScrollPane()
    {
        Color background;

        m_directoryTable = new DirectoryTable(m_directoryTree);
        m_directoryTableScrollPane = new JScrollPane(m_directoryTable);
        background = UIManager.getColor("pmdTableBackground");

        m_directoryTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_directoryTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        m_directoryTableScrollPane.getViewport().setBackground(background);
        m_directoryTableScrollPane.setAutoscrolls(true);
        m_directoryTableScrollPane.setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectorySplitPane()
    {
        m_directorySplitPane = new JSplitPane();

        m_directorySplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        m_directorySplitPane.setResizeWeight(0.5);
        m_directorySplitPane.setDividerSize(5);
        m_directorySplitPane.setLeftComponent(m_directoryTreeScrollPane);
        m_directorySplitPane.setRightComponent(m_directoryTableScrollPane);
    }

    /**
     *********************************************************************************
     *
     */
    private void createResultsViewer()
    {
        m_resultsViewer = new ResultsViewer(m_directoryTable);

        m_resultsViewer.setSelectionColor(Color.blue);
    }

    /**
     *********************************************************************************
     *
     */
    private void createResultsViewerScrollPane()
    {
        m_resultsViewerScrollPane = new JScrollPane(m_resultsViewer);

        m_resultsViewerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        m_resultsViewerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        m_resultsViewerScrollPane.getViewport().setBackground(Color.white);
        m_resultsViewerScrollPane.setAutoscrolls(true);
        m_resultsViewerScrollPane.setBorder(BorderFactory.createEtchedBorder());
    }

    /**
     *********************************************************************************
     *
     */
    private void createMainSplitPane()
    {
        m_mainSplitPane = new JSplitPane();

        m_mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        m_mainSplitPane.setResizeWeight(0.2);
        m_mainSplitPane.setDividerSize(5);
        m_mainSplitPane.setTopComponent(m_directorySplitPane);
        m_mainSplitPane.setBottomComponent(m_resultsViewerScrollPane);
    }

    /**
     *********************************************************************************
     *
     */
    private JPanel createContentPanel(int margin)
    {
        JPanel contentPanel = new JPanel(new BorderLayout());

        Border outsideBorder = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
        Border insideBorder = BorderFactory.createEmptyBorder(margin, margin, margin, margin);
        Border compoundBorder = BorderFactory.createCompoundBorder(outsideBorder, insideBorder);

        contentPanel.setBorder(compoundBorder);
        contentPanel.add(m_selectSourceFileLabel, BorderLayout.NORTH);
        contentPanel.add(m_mainSplitPane,  BorderLayout.CENTER);

        return contentPanel;
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
            //String useLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            String useLookAndFeel = "net.sourceforge.pmd.swingui.PMDLookAndFeel";

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

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class FileMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private FileMenu(JMenuBar menuBar)
        {
            super("File");

            setMnemonic('F');

            Icon icon;
            JMenuItem menuItem;

            //
            // Save menu item
            //
            icon = UIManager.getIcon("Save");
            menuItem = new JMenuItem("Save", icon);
            menuItem.addActionListener((ActionListener) new SaveActionListener());
            menuItem.setMnemonic('S');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Save As menu item
            //
            icon = UIManager.getIcon("SaveAs");
            menuItem = new JMenuItem("Save As...", icon);
            menuItem.addActionListener((ActionListener) new SaveAsActionListener());
            menuItem.setMnemonic('v');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Print menu item
            //
            icon = UIManager.getIcon("Print");
            menuItem = new JMenuItem("Print...", icon);
            menuItem.addActionListener((ActionListener) new PrintActionListener());
            menuItem.setMnemonic('P');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Page Setup menu item
            //
            menuItem = new JMenuItem("Page Setup...");
            menuItem.addActionListener((ActionListener) new PageSetupActionListener());
            menuItem.setMnemonic('L');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Exit menu item
            //
            menuItem = new JMenuItem("Exit...");
            menuItem.addActionListener((ActionListener) new ExitActionListener());
            menuItem.setMnemonic('x');
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class EditMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private EditMenu(JMenuBar menuBar)
        {
            super("Edit");

            setMnemonic('E');

            Icon icon;
            JMenuItem menuItem;

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("Copy");
            menuItem = new JMenuItem("Copy Results", icon);
            menuItem.addActionListener((ActionListener) new CopyResultsActionListener());
            menuItem.setMnemonic('C');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Rule Properties menu item
            //
            icon = UIManager.getIcon("Edit");
            menuItem = new JMenuItem("Rule Properties...", icon);
            menuItem.addActionListener((ActionListener) new EditRulePropertiesActionListener());
            menuItem.setMnemonic('R');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Preferences menu item
            //
            icon = UIManager.getIcon("Edit");
            menuItem = new JMenuItem("Preferences...", icon);
            menuItem.addActionListener((ActionListener) new EditPreferencesActionListener());
            menuItem.setMnemonic('f');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class ViewMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private ViewMenu(JMenuBar menuBar)
        {
            super("View");

            setMnemonic('V');

            Icon icon;
            JMenuItem menuItem;

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("View");
            menuItem = new JMenuItem("Rules", icon);
            menuItem.addActionListener((ActionListener) new ViewRulesActionListener());
            menuItem.setMnemonic('U');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HelpMenu extends JMenu
    {

        /**
         ********************************************************************
         *
         * @param menuBar
         */
        private HelpMenu(JMenuBar menuBar)
        {
            super("Help");

            setMnemonic('H');

            Icon icon;
            JMenuItem menuItem;

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("QuestionMark");
            menuItem = new JMenuItem("Online Help", icon);
            menuItem.addActionListener((ActionListener) new HelpActionListener());
            menuItem.setMnemonic('H');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Rule Properties menu item
            //
            menuItem = new JMenuItem("About...");
            menuItem.addActionListener((ActionListener) new AboutActionListener());
            menuItem.setMnemonic('A');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
            add(menuItem);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SaveActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SaveAsActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PrintActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PageSetupActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class ExitActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class CopyResultsActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class EditRulePropertiesActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class EditPreferencesActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class ViewRulesActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HelpActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class AboutActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
        }
    }
}