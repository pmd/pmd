package net.sourceforge.pmd.swingui;

import net.sourceforge.pmd.PMDDirectory;
import net.sourceforge.pmd.PMDException;
import net.sourceforge.pmd.swingui.event.ListenerList;
import net.sourceforge.pmd.swingui.event.SetupFilesEvent;
import net.sourceforge.pmd.swingui.event.SetupFilesEventListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

/**
 *
 * @author Donald A. Leckie
 * @since August 17, 2002
 * @version $Revision$, $Date$
 */
public class PMDViewer extends JFrame {

    private int m_disabledCounter;
    private boolean m_firstLayout = true;
    private TabbedPane m_tabbedPane;
    private GlassPaneMouseListener m_glassPaneMouseListener;
    private static PMDViewer m_pmdViewer;

    /**
     *******************************************************************************
     *
     */
    private PMDViewer() {
        super("PMD Java Source Code Analyzer");

        Dimension windowSize;
        Dimension screenSize;

        m_pmdViewer = this;
        windowSize = ComponentFactory.adjustWindowSize(1200, 1000);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        if (windowSize.width >= screenSize.width) {
            windowSize.width = screenSize.width - 10;
        }

        if (windowSize.height >= screenSize.height) {
            windowSize.height = screenSize.height - 20;
        }

        int windowLocationX = (screenSize.width - windowSize.width) / 2;
        int windowLocationY = (screenSize.height - windowSize.height) / 2;

        setSize(windowSize);
        setLocation(windowLocationX, windowLocationY);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            PMDDirectory.open(Preferences.getPreferences().getCurrentPathToPMD());
        } catch (PMDException pmdException) {
            String message = pmdException.getMessage();
            Exception exception = pmdException.getReason();
            MessageDialog.show(this, message, exception);
        }

        m_tabbedPane = new TabbedPane();
        getContentPane().add(m_tabbedPane);

        ImageIcon image = (ImageIcon) UIManager.get("pmdLogoImage");
        setIconImage(image.getImage());

        m_glassPaneMouseListener = new GlassPaneMouseListener();
        ListenerList.addListener((SetupFilesEventListener) new SetupFilesEventHandler());
    }

    /**
     *********************************************************************************
     *
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        if (visible && m_firstLayout) {
            m_tabbedPane.adjustSplitPaneDividerLocation();
            m_firstLayout = false;
        }
    }

    /**
     *********************************************************************************
     *
     * @return
     */
    public static final PMDViewer getViewer() {
        return m_pmdViewer;
    }

    /**
     *********************************************************************************
     *
     */
    public static final void run() {
        try {
            LoadRootDirectories loadRootDirectories = new LoadRootDirectories();
            loadRootDirectories.start();

            // Setup the User Interface based on this computer's operating system.
            // This must be done before calling Java and Swing classes that call the GUI.
            String useLookAndFeel = "net.sourceforge.pmd.swingui.PMDLookAndFeel";

            UIManager.setLookAndFeel(useLookAndFeel);

            PMDViewer pmdViewer = new PMDViewer();
            pmdViewer.setVisible(true);
            SetupFilesEvent.notifySetFileList(pmdViewer, loadRootDirectories.getDirectories());

        } catch (Exception exception) {
            exception.printStackTrace();
        } catch (Error error) {
            error.printStackTrace();
        }

        return;
    }

    /**
     *********************************************************************************
     *
     * @param enable
     */
    protected void setEnableViewer(boolean enable) {
        if (enable) {
            m_disabledCounter--;

            if (m_disabledCounter == 0) {
                Component glassPane = getGlassPane();
                glassPane.setVisible(false);
                glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                glassPane.removeMouseListener(m_glassPaneMouseListener);
                m_tabbedPane.setEnabled(true);
                JMenuBar menuBar = getJMenuBar();
                int menuCount = menuBar.getMenuCount();

                for (int n = 0; n < menuCount; n++) {
                    menuBar.getMenu(n).setEnabled(true);
                }
            }
        } else {
            if (m_disabledCounter == 0) {
                Component glassPane = getGlassPane();
                glassPane.setVisible(true);
                glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                glassPane.addMouseListener(m_glassPaneMouseListener);
                m_tabbedPane.setEnabled(false);
                JMenuBar menuBar = getJMenuBar();
                int menuCount = menuBar.getMenuCount();

                for (int n = 0; n < menuCount; n++) {
                    menuBar.getMenu(n).setEnabled(false);
                }
            }

            m_disabledCounter++;
        }
    }

    /**
     *********************************************************************************
     *
     * @param args
     */
    public static void main(String[] args) {
        run();
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class GlassPaneMouseListener extends MouseAdapter {

        /**
         *****************************************************************************
         *
         * Invoked when the mouse button has been clicked (pressed
         * and released) on a component.
         */
        public void mouseClicked(MouseEvent event) {
            event.consume();
        }

        /**
         *****************************************************************************
         *
         * Invoked when a mouse button has been pressed on a component.
         */
        public void mousePressed(MouseEvent event) {
            event.consume();
        }

        /**
         *****************************************************************************
         *
         * Invoked when a mouse button has been released on a component.
         */
        public void mouseReleased(MouseEvent event) {
            event.consume();
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class SetupFilesEventHandler implements SetupFilesEventListener {

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void startSetup(SetupFilesEvent event) {
            PMDViewer.this.setEnableViewer(false);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void stopSetup(SetupFilesEvent event) {
            PMDViewer.this.setEnableViewer(true);
        }

        /**
         ****************************************************************************
         *
         * @param event
         */
        public void setFileList(SetupFilesEvent event) {
        }
    }

    /**
     *********************************************************************************
     *********************************************************************************
     *********************************************************************************
     */
    private class TabbedPane extends JTabbedPane implements ChangeListener {
        private AnalysisViewer m_analysisViewer;
        private SearchViewer m_searchViewer;
        private RulesEditor m_rulesEditor;
        private PreferencesEditor m_preferencesEditor;
        private Component m_currentTab;

        /**
         *****************************************************************************
         *
         */
        private TabbedPane() {
            super();

            try {
                m_analysisViewer = new AnalysisViewer();
                m_searchViewer = new SearchViewer();
                m_rulesEditor = new RulesEditor();
                m_preferencesEditor = new PreferencesEditor();

                setFont(UIManager.getFont("tabFont"));
                addTab("Analysis Viewer", m_analysisViewer);
                addTab("Search Viewer", m_searchViewer);
                addTab("Rules Editor", m_rulesEditor);
                addTab("Preferences Editor", m_preferencesEditor);
                addChangeListener(this);
            } catch (PMDException pmdException) {
                String message = pmdException.getMessage();
                Exception exception = pmdException.getReason();
                MessageDialog.show(m_pmdViewer, message, exception);
            }
        }

        /**
         *****************************************************************************
         *
         */
        private void adjustSplitPaneDividerLocation() {
            m_analysisViewer.adjustSplitPaneDividerLocation();
            m_searchViewer.adjustSplitPaneDividerLocation();
            m_rulesEditor.adjustSplitPaneDividerLocation();
            m_preferencesEditor.adjustSplitPaneDividerLocation();
        }

        /**
         *******************************************************************************
         *
         * @param event
         */
        public void stateChanged(ChangeEvent event) {
            if (m_currentTab == m_rulesEditor) {
                m_rulesEditor.saveData();
            }

            m_currentTab = getSelectedComponent();

            if (m_currentTab == m_analysisViewer) {
                m_analysisViewer.setMenuBar();
                m_analysisViewer.analyze();
            } else if (m_currentTab == m_searchViewer) {
                m_searchViewer.setMenuBar();
                m_searchViewer.analyze();
            } else if (m_currentTab == m_rulesEditor) {
                m_rulesEditor.setMenuBar();
            } else if (m_currentTab == m_preferencesEditor) {
                m_preferencesEditor.setMenuBar();
            }
        }
    }
}


/**
 *********************************************************************************
 *********************************************************************************
 *********************************************************************************
 */
class LoadRootDirectories extends Thread {
    private File[] m_fileSystemRoots;

    /**
     ************************************************************************
     *
     */
    protected LoadRootDirectories() {
        super("Load Root Directories");
    }

    /**
     ***************************************************************************
     *
     */
    public void run() {
        setup();
        process();
        cleanup();
    }

    /**
     ************************************************************************
     *
     */
    protected void setup() {
    }

    /**
     ************************************************************************
     *
     */
    protected void process() {
        m_fileSystemRoots = File.listRoots();
    }

    /**
     ************************************************************************
     *
     */
    protected void cleanup() {
    }

    /**
     ************************************************************************
     *
     * @return
     */
    protected File[] getDirectories() {
        return m_fileSystemRoots;
    }
}