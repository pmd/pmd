package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.swingui.event.DirectoryTableEvent;
import net.sourceforge.pmd.swingui.event.DirectoryTableEventListener;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.HTMLAnalysisResultsEventListener;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.StatusBarEvent;
import net.sourceforge.pmd.swingui.event.StatusBarEventListener;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEvent;
import net.sourceforge.pmd.swingui.event.TextAnalysisResultsEventListener;

import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Donald A. Leckie
 * @since December 25, 2002
 * @version $Revision$, $Date$
 */
class AnalysisViewer extends JPanel
{

    private DirectoryTree m_directoryTree;
    private JLabel m_message;
    private JPanel m_statusBar;
    private JPanel m_directoryTreePanel;
    private DirectoryTable m_directoryTable;
    private JPanel m_directoryTablePanel;
    private JSplitPane m_directorySplitPane;
    private ResultsViewer m_resultsViewer;
    private JPanel m_resultsViewerPanel;
    private JSplitPane m_mainSplitPane;
    private StatusArea m_statusArea;
    private JMenuBar m_menuBar;
    private PMDClipboard m_clipboardOwner = new PMDClipboard();
    private JMenuItem m_saveMenuItem;
    private JMenuItem m_saveAsMenuItem;
    private JMenuItem m_printAnalysisMenuItem;
    private JMenuItem m_copyHTMLResultsMenuItem;
    private JMenuItem m_copyTextResultsMenuItem;

    /**
     **********************************************************************************
     *
     */
    protected AnalysisViewer()
    {
        super(new BorderLayout());

        createStatusBar(10);
        createDirectoryTreePanel();
        createDirectoryTablePanel();
        createDirectorySplitPane();
        createResultsViewer();
        createResultsViewerScrollPane();
        createMainSplitPane();
        createMenuBar();
        setMenuBar();
        add(createContentPanel(10));
        ListenerList.addListener((StatusBarEventListener) new StatusBarEventHandler());
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
    private void createDirectoryTreePanel()
    {
        Color background;
        JScrollPane scrollPane;

        m_directoryTree = new DirectoryTree("File Directories");
        scrollPane = ComponentFactory.createScrollPane(m_directoryTree);
        background = UIManager.getColor("pmdTreeBackground");
        scrollPane.getViewport().setBackground(background);
        m_directoryTreePanel = new JPanel(new BorderLayout());
        m_directoryTreePanel.setBorder(createTitledBorder(" Directory "));
        m_directoryTreePanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     *********************************************************************************
     *
     */
    private void createDirectoryTablePanel()
    {
        Color background;
        JScrollPane scrollPane;

        m_directoryTable = new DirectoryTable(m_directoryTree, ".java");
        scrollPane = ComponentFactory.createScrollPane(m_directoryTable);
        background = UIManager.getColor("pmdTableBackground");
        scrollPane.getViewport().setBackground(background);
        m_directoryTablePanel = new JPanel(new BorderLayout());
        m_directoryTablePanel.setBorder(createTitledBorder(" Java Source Code "));
        m_directoryTablePanel.add(scrollPane, BorderLayout.CENTER);
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
        m_directorySplitPane.setLeftComponent(m_directoryTreePanel);
        m_directorySplitPane.setRightComponent(m_directoryTablePanel);
    }

    /**
     *********************************************************************************
     *
     */
    private void createResultsViewer()
    {
        m_resultsViewer = new ResultsViewer();

        m_resultsViewer.setSelectionColor(Color.blue);
    }

    /**
     *********************************************************************************
     *
     */
    private void createResultsViewerScrollPane()
    {
        JScrollPane scrollPane;

        scrollPane = ComponentFactory.createScrollPane(m_resultsViewer);
        m_resultsViewerPanel = new JPanel(new BorderLayout());
        m_resultsViewerPanel.setBorder(createTitledBorder(" Analysis Results "));
        m_resultsViewerPanel.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     *********************************************************************************
     *
     */
    private void createMainSplitPane()
    {
        m_mainSplitPane = new JSplitPane();

        m_mainSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        m_mainSplitPane.setResizeWeight(0.5);
        m_mainSplitPane.setDividerSize(5);
        m_mainSplitPane.setTopComponent(m_directorySplitPane);
        m_mainSplitPane.setBottomComponent(m_resultsViewerPanel);
    }

    /**
     *********************************************************************************
     *
     * @param margin
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
     * @param title
     */
    private TitledBorder createTitledBorder(String title)
    {
        EtchedBorder etchedBorder;
        TitledBorder titledBorder;
        Font font;

        etchedBorder = new EtchedBorder(EtchedBorder.RAISED);
        titledBorder = new TitledBorder(etchedBorder, title);
        font = titledBorder.getTitleFont();
        font = new Font(font.getName(), Font.BOLD, font.getSize());
        titledBorder.setTitleFont(font);

        return titledBorder;
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
     * @param message The message to be displayed in the status area.
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
     */
    protected void adjustSplitPaneDividerLocation()
    {
        m_mainSplitPane.setDividerLocation(0.4);
        m_directorySplitPane.setDividerLocation(0.5);
    }

    /**
     *********************************************************************************
     *
     */
    private void createMenuBar()
    {
       m_menuBar = new JMenuBar();

       m_menuBar.add(new FileMenu());
       m_menuBar.add(new EditMenu());
       m_menuBar.add(new HelpMenu());
    }

    /**
     *********************************************************************************
     *
     */
    protected void setMenuBar()
    {
        PMDViewer.getViewer().setJMenuBar(m_menuBar);
    }

    /**
     ********************************************************************************
     *
     */
    protected void analyze()
    {
        m_resultsViewer.analyze();
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
    private class StatusBarEventHandler implements StatusBarEventListener
    {

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void startAnimation(StatusBarEvent event)
        {
            m_statusArea.startAction();
            m_message.setText("");
            SwingUtilities.invokeLater(new Repaint(m_message));
        }

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void showMessage(StatusBarEvent event)
        {
            m_message.setText(event.getMessage());
            SwingUtilities.invokeLater(new Repaint(m_message));
        }

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void stopAnimation(StatusBarEvent event)
        {
            setDefaultMessage();
            SwingUtilities.invokeLater(new Repaint(m_message));
            m_statusArea.stopAction();
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

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class PrintAnalysisActionListener implements ActionListener
    {

        public void actionPerformed(ActionEvent event)
        {
            MessageDialog.show(PMDViewer.getViewer(), "Printing not available yet.");
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
            MessageDialog.show(PMDViewer.getViewer(), "Page setup not available yet.");
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
    private class CopyHTMLResultsActionListener implements ActionListener, HTMLAnalysisResultsEventListener
    {
        private String m_htmlText;

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            HTMLAnalysisResultsEvent.notifyRequestHTMLText(this);

            if ((m_htmlText != null) && (m_htmlText.length() > 0))
            {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection contents = new StringSelection(m_htmlText);
                clipboard.setContents(contents, m_clipboardOwner);
            }
        }

        /**
         ******************************************************************************
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
        }

        /**
         ******************************************************************************
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
            m_htmlText = event.getHTMLText();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class CopyTextResultsActionListener implements ActionListener, TextAnalysisResultsEventListener
    {
        private String m_text;

        public void actionPerformed(ActionEvent event)
        {
            TextAnalysisResultsEvent.notifyRequestText(this);

            if ((m_text != null) && (m_text.length() > 0))
            {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                StringSelection contents = new StringSelection(m_text);
                clipboard.setContents(contents, m_clipboardOwner);
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestTextAnalysisResults(TextAnalysisResultsEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedTextAnalysisResults(TextAnalysisResultsEvent event)
        {
            m_text = event.getText();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class FileMenuMouseListener extends MouseAdapter
                                        implements HTMLAnalysisResultsEventListener
    {

        private String m_htmlText;

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void mouseEntered(MouseEvent event)
        {
            try
            {
                ListenerList.addListener((HTMLAnalysisResultsEventListener) this);
                HTMLAnalysisResultsEvent.notifyRequestHTMLText(this);
                boolean enable = (m_htmlText.length() > 0);
                m_saveMenuItem.setEnabled(enable);
                m_saveAsMenuItem.setEnabled(enable);
                m_printAnalysisMenuItem.setEnabled(enable);
            }
            finally
            {
                ListenerList.removeListener((HTMLAnalysisResultsEventListener) this);
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
            m_htmlText = event.getHTMLText();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class EditMenuMouseListener extends MouseAdapter
                                        implements HTMLAnalysisResultsEventListener
    {

        String m_htmlText;

        /**
         *****************************************************************************
         *
         * @param event
         */
        public void mouseEntered(MouseEvent event)
        {
            try
            {
                ListenerList.addListener((HTMLAnalysisResultsEventListener) this);
                HTMLAnalysisResultsEvent.notifyRequestHTMLText(this);
                boolean enable = (m_htmlText.length() > 0);
                m_copyHTMLResultsMenuItem.setEnabled(enable);
                m_copyTextResultsMenuItem.setEnabled(enable);
            }
            finally
            {
                ListenerList.removeListener((HTMLAnalysisResultsEventListener) this);
            }
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
            m_htmlText = event.getHTMLText();
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
            m_saveMenuItem = new JMenuItem("Save Analysis Results", icon);
            m_saveMenuItem.addActionListener((ActionListener) new SaveActionListener());
            m_saveMenuItem.setMnemonic('S');
            m_saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK));
            add(m_saveMenuItem);

            //
            // Save As menu item
            //
            icon = UIManager.getIcon("saveAs");
            m_saveAsMenuItem = new JMenuItem("Save Analysis Results As...", icon);
            m_saveAsMenuItem.addActionListener((ActionListener) new SaveAsActionListener());
            m_saveAsMenuItem.setMnemonic('A');
            m_saveAsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK));
            add(m_saveAsMenuItem);

            //
            // Separator
            //
            add(new JSeparator());

            //
            // Print Analysis menu item
            //
            icon = UIManager.getIcon("print");
            m_printAnalysisMenuItem = new JMenuItem("Print Analysis...", icon);
            m_printAnalysisMenuItem.addActionListener((ActionListener) new PrintAnalysisActionListener());
            m_printAnalysisMenuItem.setMnemonic('P');
            m_printAnalysisMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK));
            add(m_printAnalysisMenuItem);

            //
            // Page Setup menu item
            //
            menuItem = new JMenuItem("Page Setup...");
            menuItem.addActionListener((ActionListener) new PageSetupActionListener());
            menuItem.setMnemonic('U');
            menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK));
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
            menuItem.setMnemonic('X');
            add(menuItem);

            addMouseListener(new FileMenuMouseListener());
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
         */
        private EditMenu()
        {
            super("Edit");

            setMnemonic('E');

            Icon icon;

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("copy");
            m_copyHTMLResultsMenuItem = new JMenuItem("Copy Results as HTML", icon);
            m_copyHTMLResultsMenuItem.addActionListener((ActionListener) new CopyHTMLResultsActionListener());
            m_copyHTMLResultsMenuItem.setMnemonic('C');
            m_copyHTMLResultsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK));
            m_copyHTMLResultsMenuItem.setEnabled(false);
            add(m_copyHTMLResultsMenuItem);

            //
            // Copy Results menu item
            //
            icon = UIManager.getIcon("copy");
            m_copyTextResultsMenuItem = new JMenuItem("Copy Results as Text", icon);
            m_copyTextResultsMenuItem.addActionListener((ActionListener) new CopyTextResultsActionListener());
            m_copyTextResultsMenuItem.setMnemonic('Y');
            m_copyTextResultsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK));
            m_copyTextResultsMenuItem.setEnabled(false);
            add(m_copyTextResultsMenuItem);

            addMouseListener(new EditMenuMouseListener());
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SaveSaveAs implements HTMLAnalysisResultsEventListener
    {

        private String m_htmlText;

        /**
         ****************************************************************************
         *
         * @param outputFile
         */
        private void perform(File outputFile)
        {
            if (outputFile != null)
            {
                try
                {
                    ListenerList.addListener((HTMLAnalysisResultsEventListener) this);
                    HTMLAnalysisResultsEvent.notifyRequestHTMLText(this);
                    FileWriter writer = null;

                    if (outputFile.exists())
                    {
                        outputFile.delete();
                    }

                    try
                    {
                        writer = new FileWriter(outputFile);
                        writer.write(m_htmlText);
                        String message = "Saved results to file \"" + outputFile.getPath() + "\".";
                        MessageDialog.show(PMDViewer.getViewer(), message);
                    }
                    catch (IOException ioException)
                    {
                        String message = "Could not save Analysis results to a file.";
                        PMDException pmdException = new PMDException(message, ioException);
                        pmdException.fillInStackTrace();
                        throw pmdException;
                    }
                    finally
                    {
                        try
                        {
                            writer.close();
                        }
                        catch (IOException exception)
                        {
                        }
                    }
                }
                catch (PMDException pmdException)
                {
                    String message = pmdException.getMessage();
                    Exception exception = pmdException.getOriginalException();
                    MessageDialog.show(PMDViewer.getViewer(), message, exception);
                }
                finally
                {
                    ListenerList.removeListener((HTMLAnalysisResultsEventListener) this);
                }
            }
        }

        /**
         ****************************************************************************
         *
         * Implements AnalyzeFileEventListener
         *
         * @param event
         */
        public void requestHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * Implements AnalyzeFileEventListener
         *
         * @param event
         */
        public void returnedHTMLAnalysisResults(HTMLAnalysisResultsEvent event)
        {
            m_htmlText = event.getHTMLText();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SaveActionListener implements ActionListener, DirectoryTableEventListener
    {

        private File m_selectedFile;

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void actionPerformed(ActionEvent event)
        {
            try
            {
                ListenerList.addListener((DirectoryTableEventListener) this);
                DirectoryTableEvent.notifyRequestFileSelected(this);

                if (m_selectedFile != null)
                {
                    String fileName = m_selectedFile.getName();
                    int index = fileName.lastIndexOf('.');

                    if (index >= 0)
                    {
                        fileName = fileName.substring(0, index);
                    }

                    String directory = Preferences.getPreferences().getAnalysisResultsPath();
                    String path = directory + File.separator + fileName + ".html";
                    File outputFile = new File(path);
                    (new SaveSaveAs()).perform(outputFile);
                }
            }
            catch (PMDException pmdException)
            {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getOriginalException();
                MessageDialog.show(PMDViewer.getViewer(), message, exception);
            }
            finally
            {
                ListenerList.removeListener((DirectoryTableEventListener) this);
            }
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void requestSelectedFile(DirectoryTableEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void fileSelectionChanged(DirectoryTableEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void fileSelected(DirectoryTableEvent event)
        {
            m_selectedFile = event.getSelectedFile();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SaveAsActionListener implements ActionListener, DirectoryTableEventListener
    {

        private File m_selectedFile;

        public void actionPerformed(ActionEvent event)
        {
            try
            {
                ListenerList.addListener((DirectoryTableEventListener) this);
                DirectoryTableEvent.notifyRequestFileSelected(this);

                if (m_selectedFile != null)
                {
                    String fileName = m_selectedFile.getName();
                    int index = fileName.lastIndexOf('.');

                    if (index >= 0)
                    {
                        fileName = fileName.substring(0, index);
                    }

                    String path = Preferences.getPreferences().getAnalysisResultsPath();
                    File lastSavedDirectory = new File(path);
                    File selectedFile = new File(path + File.separator + fileName + ".html");
                    JFileChooser fileChooser = new JFileChooser(lastSavedDirectory);
                    fileChooser.addChoosableFileFilter(new HTMLFileFilter());
                    fileChooser.setSelectedFile(selectedFile);
                    int result = fileChooser.showSaveDialog(PMDViewer.getViewer());

                    if (result == JFileChooser.APPROVE_OPTION)
                    {
                        (new SaveSaveAs()).perform(fileChooser.getSelectedFile());
                    }
                }
            }
            catch (PMDException pmdException)
            {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getOriginalException();
                MessageDialog.show(PMDViewer.getViewer(), message, exception);
            }
            finally
            {
                ListenerList.removeListener((DirectoryTableEventListener) this);
            }
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void requestSelectedFile(DirectoryTableEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void fileSelectionChanged(DirectoryTableEvent event)
        {
        }

        /**
         ****************************************************************************
         *
         * Implements DirectoryTableEventListener
         *
         * @param event
         */
        public void fileSelected(DirectoryTableEvent event)
        {
            m_selectedFile = event.getSelectedFile();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class HTMLFileFilter extends FileFilter
    {

        /**
         *****************************************************************************
         * @param file
         * @return
         */
        public boolean accept(File file)
        {
            String fileName = file.getName();

            return fileName.endsWith(".html") || fileName.endsWith(".htm");
        }

        /**
         ******************************************************************************
         */
        public String getDescription()
        {
            return "HTML Anaysis Result File (*.html, *.htm)";
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
}