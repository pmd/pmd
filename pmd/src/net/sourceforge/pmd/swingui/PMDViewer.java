package net.sourceforge.pmd.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.Font;
import java.io.File;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

import net.sourceforge.pmd.PMDException;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class PMDViewer extends JFrame
{

    private Preferences m_preferences;
    private DirectoryTree m_directoryTree;
    private JLabel m_selectSourceFileLabel;
    private JScrollPane m_directoryTreeScrollPane;
    private DirectoryTable m_directoryTable;
    private JScrollPane m_directoryTableScrollPane;
    private JSplitPane m_directorySplitPane;
    private ResultsViewer m_resultsViewer;
    private JScrollPane m_resultsViewerScrollPane;
    private JSplitPane m_mainSplitPane;
    private PMDClipboard m_clipboardOwner = new PMDClipboard();

    /**
     *******************************************************************************
     *
     */
    private PMDViewer()
    {
        super("PMD Viewer");

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

        ImageIcon image = Utilities.getImageIcon("icons/pmdLogo.jpg");
        setIconImage(image.getImage());
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
        m_selectSourceFileLabel.setBorder(new EmptyBorder(0, 0, 5, 0));
        m_selectSourceFileLabel.setText("Select a source file to view its analysis below.");
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTreeScrollPane()
    {
        Color background;

        m_directoryTree = new DirectoryTree(this);
        m_directoryTreeScrollPane = ComponentFactory.createScrollPane(m_directoryTree);
        background = UIManager.getColor("pmdTreeBackground");

        m_directoryTreeScrollPane.getViewport().setBackground(background);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTableScrollPane()
    {
        Color background;

        m_directoryTable = new DirectoryTable(m_directoryTree);
        m_directoryTableScrollPane = ComponentFactory.createScrollPane(m_directoryTable);
        background = UIManager.getColor("pmdTableBackground");

        m_directoryTableScrollPane.getViewport().setBackground(background);
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
        m_resultsViewer = new ResultsViewer(this, m_directoryTable);

        m_resultsViewer.setSelectionColor(Color.blue);
    }

    /**
     *********************************************************************************
     *
     */
    private void createResultsViewerScrollPane()
    {
        m_resultsViewerScrollPane = ComponentFactory.createScrollPane(m_resultsViewer);
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

        EtchedBorder outsideBorder = new EtchedBorder(EtchedBorder.RAISED);
        EmptyBorder insideBorder = new EmptyBorder(margin, margin, margin, margin);
        CompoundBorder compoundBorder = new CompoundBorder(outsideBorder, insideBorder);

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
    protected Preferences getPreferences()
    {
        if (m_preferences == null)
        {
            m_preferences = new Preferences(this);

            m_preferences.load(this);
        }

        return m_preferences;
    }

    /**
     *********************************************************************************
     *
     */
    private void setupFiles(File[] rootDirectories)
    {
        m_directoryTree.setupFiles(this, rootDirectories);
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
            LoadRootDirectories loadRootDirectories = new LoadRootDirectories();
            loadRootDirectories.start();
            pmdViewer.setVisible(true);
            pmdViewer.setupFiles(loadRootDirectories.getDirectories());
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
            icon = UIManager.getIcon("save");
            menuItem = new JMenuItem("Save", icon);
            menuItem.addActionListener((ActionListener) new SaveActionListener());
            menuItem.setMnemonic('S');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Save As menu item
            //
            icon = UIManager.getIcon("saveAs");
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
            icon = UIManager.getIcon("print");
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
            icon = UIManager.getIcon("copy");
            menuItem = new JMenuItem("Copy Results as HTML", icon);
            menuItem.addActionListener((ActionListener) new CopyHTMLResultsActionListener());
            menuItem.setMnemonic('C');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("copy");
            menuItem = new JMenuItem("Copy Results as Text", icon);
            menuItem.addActionListener((ActionListener) new CopyTextResultsActionListener());
            menuItem.setMnemonic('Y');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Preferences menu item
            //
            icon = UIManager.getIcon("edit");
            menuItem = new JMenuItem("Preferences...", icon);
            menuItem.addActionListener((ActionListener) new EditPreferencesActionListener());
            menuItem.setMnemonic('f');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Rule Properties menu item
            //
            icon = UIManager.getIcon("edit");
            menuItem = new JMenuItem("Rules...", icon);
            menuItem.addActionListener((ActionListener) new EditRulesActionListener());
            menuItem.setMnemonic('R');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK));
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
            icon = UIManager.getIcon("help");
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
            MessageDialog.show(PMDViewer.this, "What should we save?  The results?");
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
            MessageDialog.show(PMDViewer.this, "What should we save?  The results?");
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
            MessageDialog.show(PMDViewer.this, "Printing not available yet.");
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
            MessageDialog.show(PMDViewer.this, "Page setup not available yet.");
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
            System.exit(0);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class CopyHTMLResultsActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            String htmlText = m_resultsViewer.getHTMLText();
            Clipboard clipboard = PMDViewer.this.getToolkit().getSystemClipboard();
            StringSelection contents = new StringSelection(htmlText);
            clipboard.setContents(contents, m_clipboardOwner);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class CopyTextResultsActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            String text = m_resultsViewer.getPlainText();
            Clipboard clipboard = PMDViewer.this.getToolkit().getSystemClipboard();
            StringSelection contents = new StringSelection(text);
            clipboard.setContents(contents, m_clipboardOwner);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PMDClipboard implements ClipboardOwner
    {

        /**
         ************************************************************************
         *
         */
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class EditRulesActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            try
            {
                (new RulesEditor(PMDViewer.this)).setVisible(true);
            }
            catch (PMDException pmdException)
            {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getOriginalException();

                MessageDialog.show(PMDViewer.this, message, exception);
            }
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
            (new PreferencesEditor(PMDViewer.this)).setVisible(true);
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
            MessageDialog.show(PMDViewer.this, "Online Help not available yet.");
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
            (new AboutPMD(PMDViewer.this)).setVisible(true);
        }
    }
}


/**
*********************************************************************************
*********************************************************************************
*********************************************************************************
*/
class LoadRootDirectories extends JobThread
{
    private File[] m_fileSystemRoots;

    /**
     ************************************************************************
     *
     */
    protected LoadRootDirectories()
    {
        super("Load Root Directories");
    }

    /**
     ************************************************************************
     *
     */
    protected void process()
    {
        m_fileSystemRoots = File.listRoots();
    }

    /**
     ************************************************************************
     *
     * @return
     */
    protected File[] getDirectories()
    {
        return m_fileSystemRoots;
    }
}
