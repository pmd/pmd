package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDDirectory;
import net.sourceforge.pmd.PMDException;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class PMDViewer extends JFrame implements JobThreadListener
{

    private Preferences m_preferences;
    private PMDDirectory m_pmdDirectory;
    private DirectoryTree m_directoryTree;
    private JLabel m_message;
    private JPanel m_statusBar;
    private JScrollPane m_directoryTreeScrollPane;
    private DirectoryTable m_directoryTable;
    private JScrollPane m_directoryTableScrollPane;
    private JSplitPane m_directorySplitPane;
    private ResultsViewer m_resultsViewer;
    private JScrollPane m_resultsViewerScrollPane;
    private JSplitPane m_mainSplitPane;
    private StatusArea m_statusArea;
    private PMDClipboard m_clipboardOwner = new PMDClipboard();
    private List m_ruleSetChangeListeners = new ArrayList();
    private int m_disabledCounter;
    private GlassPaneMouseListener m_glassPaneMouseListener = new GlassPaneMouseListener();

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
        createStatusBar(windowMargin);
        createDirectoryTreeScrollPane();
        createDirectoryTableScrollPane();
        createDirectorySplitPane();
        createResultsViewer();
        createResultsViewerScrollPane();
        createMainSplitPane();
        getContentPane().add(createContentPanel(windowMargin));

        ImageIcon image = (ImageIcon) UIManager.get("pmdLogoImage");
        setIconImage(image.getImage());

        try
        {
            m_preferences = new Preferences();
            m_preferences.load();
            m_pmdDirectory = new PMDDirectory(m_preferences.getCurrentPathToPMD());
        }
        catch (PMDException pmdException)
        {
            String message = pmdException.getMessage();
            Exception exception = pmdException.getOriginalException();

            MessageDialog.show(this, message, exception);
        }
    }

    /**
     *********************************************************************************
     *
     */
    private void createMenuBar()
    {
       JMenuBar menuBar = new JMenuBar();

       setJMenuBar(menuBar);
       menuBar.add(new FileMenu());
       menuBar.add(new EditMenu());
       menuBar.add(new HelpMenu());
    }

    /**
     *********************************************************************************
     *
     * @param windowMargin
     */
    private void createStatusBar(int windowMargin)
    {
        EmptyBorder emptyBorder;

        //
        // Status Bar
        //
        m_statusBar = new JPanel(new BorderLayout());
        emptyBorder = new EmptyBorder(0, 0, windowMargin, 0);
        m_statusBar.setBorder(emptyBorder);

        //
        // Status Bar Components Border
        //
        BevelBorder componentBorder = new BevelBorder(BevelBorder.LOWERED);

        //
        // Status Indicator
        //
        m_statusArea = new StatusArea(componentBorder);
        m_statusBar.add(m_statusArea, BorderLayout.WEST);

        //
        // Message Area
        //
        JPanel messageArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        messageArea.setOpaque(true);
        messageArea.setBackground(UIManager.getColor("pmdMessageAreaBackground"));
        messageArea.setBorder(componentBorder);
        m_statusBar.add(messageArea, BorderLayout.CENTER);

        //
        // Message
        //
        m_message = new JLabel();
        m_message.setFont(new Font("Dialog", Font.BOLD, 12));
        m_message.setBackground(UIManager.getColor("pmdMessageAreaBackground"));
        m_message.setForeground(UIManager.getColor("pmdBlue"));
        setDefaultMessage();
        messageArea.add(m_message);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTreeScrollPane()
    {
        Color background;

        m_directoryTree = new DirectoryTree("File Directories");
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

        m_directoryTable = new DirectoryTable(m_directoryTree, ".java");
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
        m_resultsViewer.setParentScrollPane(m_resultsViewerScrollPane);
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
        contentPanel.add(m_statusBar, BorderLayout.NORTH);
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
        return m_preferences;
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    protected PMDDirectory getPMDDirectory()
    {
        return m_pmdDirectory;
    }

    /**
     *********************************************************************************
     *
     */
    protected void setDefaultMessage()
    {
        setMessage("Select a source file to view its analysis below.");
    }

    /**
     *********************************************************************************
     *
     */
    protected void setMessage(String message)
    {
        if (message == null)
        {
            message = "";
        }

        m_message.setText(message);
    }

    /**
     *********************************************************************************
     *
     * @param rootDirectories
     */
    private void setupFiles(File[] rootDirectories)
    {
        m_directoryTree.setupFiles(this, rootDirectories);
    }

    /**
     *********************************************************************************
     *
     * @param enable
     */
    protected void setEnableViewer(boolean enable)
    {
        if (enable)
        {
            m_disabledCounter--;

            if (m_disabledCounter == 0)
            {
                Component glassPane = getGlassPane();
                JMenuBar menuBar = getJMenuBar();
                MenuElement[] menuElements = menuBar.getSubElements();

                glassPane.setVisible(false);
                glassPane.removeMouseListener(m_glassPaneMouseListener);
                glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                menuBar.setEnabled(true);

                for (int n = 0; n < menuElements.length; n++)
                {
                    if (menuElements[n] instanceof JMenu)
                    {
                        ((JMenu) menuElements[n]).setEnabled(true);
                    }
                }
            }
        }
        else
        {
            if (m_disabledCounter == 0)
            {
                Component glassPane = getGlassPane();
                JMenuBar menuBar = getJMenuBar();
                MenuElement[] menuElements = menuBar.getSubElements();

                glassPane.setVisible(true);
                glassPane.addMouseListener(m_glassPaneMouseListener);
                glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                menuBar.setEnabled(false);

                for (int n = 0; n < menuElements.length; n++)
                {
                    if (menuElements[n] instanceof JMenu)
                    {
                        ((JMenu) menuElements[n]).setEnabled(false);
                    }
                }
            }

            m_disabledCounter++;
        }
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void addRuleSetChangeListener(ChangeListener listener)
    {
        if ((listener != null) && (m_ruleSetChangeListeners.contains(listener) == false))
        {
            m_ruleSetChangeListeners.add(listener);
        }
    }

    /**
     *********************************************************************************
     *
     * @param listener
     */
    protected void removeRuleSetChangeListener(ChangeListener listener)
    {
        if (listener != null)
        {
            m_ruleSetChangeListeners.remove(listener);
        }
    }

    /**
     *********************************************************************************
     *
     */
    private void notifyRuleSetChangeListeners()
    {
        Iterator iterator = m_ruleSetChangeListeners.iterator();
        ChangeEvent changeEvent = new ChangeEvent(this);

        while (iterator.hasNext())
        {
            ((ChangeListener) iterator.next()).stateChanged(changeEvent);
        }
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadStarted(JobThreadEvent event)
    {
        m_statusArea.startAction();
        m_message.setText(event.getMessage());
        SwingUtilities.invokeLater(new Repaint(m_message));
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadFinished(JobThreadEvent event)
    {
        setDefaultMessage();
        SwingUtilities.invokeLater(new Repaint(m_message));
        m_statusArea.stopAction();
    }

    /**
     *******************************************************************************
     *
     * @param event
     */
    public void jobThreadStatus(JobThreadEvent event)
    {
        m_message.setText(event.getMessage());
        SwingUtilities.invokeLater(new Repaint(m_message));
    }

    /**
     *********************************************************************************
     *
     */
    public static final void run()
    {
        try
        {
            LoadRootDirectories loadRootDirectories = new LoadRootDirectories();
            loadRootDirectories.start();

            // Setup the User Interface based on this computer's operating system.
            // This must be done before calling Java and Swing classes that call the GUI.
            //String useLookAndFeel = UIManager.getSystemLookAndFeelClassName();
            String useLookAndFeel = "net.sourceforge.pmd.swingui.PMDLookAndFeel";

            UIManager.setLookAndFeel(useLookAndFeel);

            PMDViewer pmdViewer = new PMDViewer();
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
    private class StatusArea extends JPanel
    {
        private StatusActionThread m_actionThread;
        private Color m_inactiveBackground;
        private Color m_activeBackground;
        private Color m_actionColor;
        private int m_direction;
        private int m_indicatorCurrentPosition;
        private final int POSITION_INCREMENT = 5;
        private final int START_MOVING = 0;
        private final int MOVE_FORWARD = 1;
        private final int MOVE_BACKWARD = 2;

        /**
         ****************************************************************************
         *
         * @param border
         */
        private StatusArea(Border border)
        {
            super(null);

            m_inactiveBackground = Color.gray;
            m_activeBackground = UIManager.getColor("pmdStatusAreaBackground");
            m_actionColor = Color.red;

            setOpaque(true);
            setBackground(m_inactiveBackground);
            setBorder(border);

            Dimension size = new Dimension(160, 20);

            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setPreferredSize(size);
        }

        /**
         ****************************************************************************
         *
         */
        private void startAction()
        {
            if (m_actionThread == null)
            {
                setBackground(m_activeBackground);
                m_direction = START_MOVING;
                m_actionThread = new StatusActionThread(this);
                m_actionThread.start();
            }
        }

        /**
         ****************************************************************************
         *
         */
        private void stopAction()
        {
            if (m_actionThread != null)
            {
                m_actionThread.stopAction();
                m_actionThread = null;
                setBackground(m_inactiveBackground);
                repaint();
            }
        }

        /**
         ****************************************************************************
         *
         * @param graphics
         */
        public void paint(Graphics graphics)
        {
            super.paint(graphics);

            if (getBackground() == m_activeBackground)
            {
                Rectangle totalArea;
                Insets insets;
                int indicatorWidth;
                int indicatorHeight;
                int indicatorY;
                int indicatorX;
                int totalAreaRight;

                totalArea = getBounds();
                insets = getInsets();
                totalArea.x += insets.left;
                totalArea.y += insets.top;
                totalArea.width -= (insets.left + insets.right);
                totalArea.height -= (insets.top + insets.bottom);
                totalAreaRight = totalArea.x + totalArea.width;
                indicatorWidth = totalArea.width / 3;
                indicatorHeight = totalArea.height;
                indicatorY = totalArea.y;

                if (m_direction == MOVE_FORWARD)
                {
                    m_indicatorCurrentPosition += POSITION_INCREMENT;

                    if (m_indicatorCurrentPosition >= totalAreaRight)
                    {
                        m_indicatorCurrentPosition = totalAreaRight - POSITION_INCREMENT;
                        m_direction = MOVE_BACKWARD;
                    }
                }
                else if (m_direction == MOVE_BACKWARD)
                {
                    m_indicatorCurrentPosition -= POSITION_INCREMENT;

                    if (m_indicatorCurrentPosition < totalArea.x)
                    {
                        m_indicatorCurrentPosition = totalArea.x + POSITION_INCREMENT;
                        m_direction = MOVE_FORWARD;
                    }
                }
                else
                {
                    m_indicatorCurrentPosition = totalArea.x + POSITION_INCREMENT;
                    m_direction = MOVE_FORWARD;
                }

                indicatorX = m_indicatorCurrentPosition;

                Rectangle oldClip = graphics.getClipBounds();
                Color oldColor = graphics.getColor();

                graphics.setColor(m_activeBackground);
                graphics.setClip(totalArea.x, totalArea.y, totalArea.width, totalArea.height);
                graphics.clipRect(totalArea.x, totalArea.y, totalArea.width, totalArea.height);
                graphics.fillRect(totalArea.x, totalArea.y, totalArea.width, totalArea.height);

                if (m_direction == MOVE_FORWARD)
                {
                    int stopX = indicatorX - indicatorWidth;

                    if (stopX < totalArea.x)
                    {
                        stopX = totalArea.x;
                    }

                    int y1 = indicatorY;
                    int y2 = y1 + indicatorHeight;
                    Color color = m_actionColor;

                    for (int x = indicatorX; x > stopX; x--)
                    {
                        graphics.setColor(color);
                        graphics.drawLine(x, y1, x, y2);
                        color = brighter(color);
                    }
                }
                else
                {
                    int stopX = indicatorX + indicatorWidth;

                    if (stopX > totalAreaRight)
                    {
                        stopX = totalAreaRight;
                    }

                    int y1 = indicatorY;
                    int y2 = indicatorY + indicatorHeight;
                    Color color = m_actionColor;

                    for (int x = indicatorX; x < stopX; x++)
                    {
                        graphics.setColor(color);
                        graphics.drawLine(x, y1, x, y2);
                        color = brighter(color);
                    }
                }

                graphics.setColor(oldColor);

                if (oldClip != null)
                {
                    graphics.clipRect(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
                    graphics.setClip(oldClip.x, oldClip.y, oldClip.width, oldClip.height);
                }
            }
        }

        /**
         ****************************************************************************
         *
         * @param color
         *
         * @return
         */
        private Color brighter(Color color)
        {
            int red;
            int green;
            int blue;

            red = color.getRed() + 5;
            green = color.getGreen() + 5;
            blue = color.getBlue() + 5;

            if (red > 255)
            {
                red = 255;
            }

            if (green > 255)
            {
                green = 255;
            }

            if (blue > 255)
            {
                blue = 255;
            }

            return new Color(red, green, blue);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class StatusActionThread extends Thread
    {
        private StatusArea m_statusArea;
        private boolean m_stopAction;
        private int m_doNothing;
        private final long ELAPSED_TIME = 25;

        /**
         ****************************************************************************
         *
         * @param statusArea
         */
        private StatusActionThread(StatusArea statusArea)
        {
            super("Status Action");

            m_statusArea = statusArea;
        }

        /**
         ****************************************************************************
         *
         */
        public void run()
        {
            while (m_stopAction == false)
            {
                m_statusArea.repaint();

                try
                {
                    sleep(ELAPSED_TIME);
                }
                catch (InterruptedException exception)
                {
                    m_doNothing++;
                }
            }
        }

        /**
         ****************************************************************************
         *
         */
        private void stopAction()
        {
            m_stopAction = true;
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class GlassPaneMouseListener implements MouseListener
    {

        /**
         * Invoked when the mouse has been clicked on a component.
         */
        public void mouseClicked(MouseEvent event)
        {
            event.consume();
        }

        /**
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(MouseEvent event)
        {
            event.consume();
        }

        /**
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent event)
        {
            event.consume();
        }

        /**
         * Invoked when the mouse enters a component.
         */
        public void mouseEntered(MouseEvent event)
        {
        }

        /**
         * Invoked when the mouse exits a component.
         */
        public void mouseExited(MouseEvent event)
        {
        }
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
        private FileMenu()
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
        private EditMenu()
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
        private HelpMenu()
        {
            super("Help");

            setMnemonic('H');

            Icon icon;
            JMenuItem menuItem;

            //
            // Online Help menu item
            //
            icon = UIManager.getIcon("help");
            menuItem = new JMenuItem("Online Help", icon);
            menuItem.addActionListener(new HelpActionListener());
            menuItem.setMnemonic('H');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, KeyEvent.CTRL_MASK));
            add(menuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // About menu item
            //
            menuItem = new JMenuItem("About...");
            menuItem.addActionListener(new AboutActionListener());
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
                setEnableViewer(false);
                (new RulesEditor(PMDViewer.this)).setVisible(true);
                notifyRuleSetChangeListeners();
                setEnableViewer(true);
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
            setEnableViewer(false);
            (new PreferencesEditor(PMDViewer.this)).setVisible(true);
            setEnableViewer(true);
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
            setEnableViewer(false);
            (new AboutPMD(PMDViewer.this)).setVisible(true);
            setEnableViewer(true);
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class Repaint implements Runnable
    {
        private Component m_component;

        /**
         *****************************************************************************
         *
         * @param component
         */
        private Repaint(Component component)
        {
            m_component = component;
        }

        /**
         *****************************************************************************
         *
         */
        public void run()
        {
            m_component.repaint();
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
    protected void setup()
    {
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
     */
    protected void cleanup()
    {
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
