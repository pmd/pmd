package net.sourceforge.pmd.swingui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

/**
 * This is the parent frame of PMD's user interface.
 *
 * @author Brant Gurganus
 * @version 0.1
 * @since 0.1
 */
class PMDViewer extends JFrame {
    /**
     * This is the budle of localized strings.
     */
    private static final ResourceBundle UI_STRINGS =
        ResourceBundle.getBundle("net.sourceforge.pmd.swingui.l10n.PMDViewer");
    
    /**
     * This is the logger.
     */
    private static final Logger LOGGER = Logger.getLogger(
        "net.sourceforge.pmd.swingui.PMDViewer",
            "net.sourceforge.pmd.swingui.l10n.Logging");
    
    /**
     * This is the application menu bar.
     */
    private JMenuBar menuBar;
    
    /**
     * This is the file menu.
     */
    private JMenu fileMenu;
    
    /**
     * This is the exit menu item.
     */
    private JMenuItem exitItem;
    
    /**
     * This is the exit action.
     */
    private AbstractAction exitAction;
    
    /**
     * This is the content pane.
     */
    private Container contentPane;
    
    /**
     * This is the PMD locator.
     */
    private PMDLocator pmdLocator;
    
    /**
     * Creates the PMD Viewer.
     */
    public PMDViewer() {
        super(UI_STRINGS.getString("title"));
    }
    
    /**
     * Initializes the frame.
     */
    protected void frameInit() {
        LOGGER.entering(getClass().getName(), "frameInit");
        super.frameInit();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        final URL iconLocation = getClass().getResource("icon.png");
        final ImageIcon icon = new ImageIcon(iconLocation);
        setIconImage(icon.getImage());
        menuBarInit();
        setJMenuBar(menuBar);
        contentPaneInit();
        pack();
        LOGGER.exiting(getClass().getName(), "frameInit");
    }
    
    /**
     * Initializes the content pane.
     */
    private void contentPaneInit() {
        LOGGER.entering(getClass().getName(), "contentPaneInit");
        assert contentPane == null : "contentPane already initialized.";
        contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        pmdLocatorInit();
        contentPane.add(pmdLocator);
        LOGGER.exiting(getClass().getName(), "contentPaneInit");
    }
    
    /**
     * Initializes the PMD Locator.
     */
    private void pmdLocatorInit() {
        LOGGER.entering(getClass().getName(), "pmdLocatorInit");
        assert pmdLocator == null : "pmdLocator already initialized.";
        pmdLocator = new PMDLocator();
        LOGGER.exiting(getClass().getName(), "pmdLocatorInit");
    }
    
    /**
     * Initializes the menu bar.
     */
    private void menuBarInit() {
        LOGGER.entering(getClass().getName(), "menuBarInit");
        assert menuBar == null : "menuBar already initialized.";
        menuBar = new JMenuBar();
        fileMenuInit();
        menuBar.add(fileMenu);
        LOGGER.exiting(getClass().getName(), "menuBarInit");
    }
    
    /**
     * Initializes the file menu.
     */
    private void fileMenuInit() {
        LOGGER.entering(getClass().getName(), "fileMenuInit");
        assert fileMenu == null : "fileMenu already initialized.";
        fileMenu = new JMenu(UI_STRINGS.getString("menus.file"));
        fileMenu.setMnemonic(translateKey(UI_STRINGS
            .getString("menus.file.mnemonic")));
        exitItemInit();
        fileMenu.add(exitItem);
        LOGGER.exiting(getClass().getName(), "fileMenuInit");
    }
    
    /**
     * Initializes the exit menu item.
     */
    private void exitItemInit() {
        LOGGER.entering(getClass().getName(), "exitItemInit");
        assert exitItem == null : "exitItem already initialized.";
        exitActionInit();
        exitItem = new JMenuItem(exitAction);
        LOGGER.exiting(getClass().getName(), "exitItemInit");
    }
    
    /**
     * Initializes the exit action.
     */
    private void exitActionInit() {
        LOGGER.entering(getClass().getName(), "exitActionInit");
        assert exitAction == null : "exitAction already initialized.";
        exitAction = new AbstractAction(UI_STRINGS.getString("actions.exit")) {
            public void actionPerformed(ActionEvent e) {
                LOGGER.entering(getClass().getName(), "actionPerformed");
                assert e.getActionCommand().equals(getValue(ACTION_COMMAND_KEY))
                    : "Event source was unexpected.";
                LOGGER.exiting(getClass().getName(), "actionPerformed");
                System.exit(0);
            }
        };
        exitAction.putValue(exitAction.ACTION_COMMAND_KEY, "exit");
        exitAction.putValue(exitAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(UI_STRINGS
                .getString("actions.exit.accelerator")));
        exitAction.putValue(exitAction.MNEMONIC_KEY, Integer
            .valueOf(translateKey(UI_STRINGS
                .getString("actions.exit.mnemonic"))));
        exitAction.putValue(exitAction.SHORT_DESCRIPTION, UI_STRINGS
            .getString("actions.exit.tooltip"));
        exitAction.putValue(exitAction.LONG_DESCRIPTION, UI_STRINGS
            .getString("actions.exit.description"));
        LOGGER.exiting(getClass().getName(), "exitActionInit");
    }
    
    /**
     * Translates a key name to its code.  This is done by looking up the value
     * of the same named constant prepended with "VK_" in
     * {@link java.awt.event.KeyEvent}.  If the <code>name</code> is not
     * found in <code>KeyEvent</code>, an error is logged and the key code
     * returned is -1 which indicates no mnemonic according to
     * {@link javax.swing.JButton#
     *
     * @param name key name from <code>KeyEvent</code>
     * @return the value of the constant named <code>name</code>
     */
    public static int translateKey(final String name) {
        LOGGER.entering(PMDViewer.class.getName(), "translateKey", name);
        final String lookupName = "VK_" + name;
        int translationCode = -1;
        try {
            final Class keyEventClass = KeyEvent.class;
            final Field keyField = keyEventClass.getField(lookupName);
            translationCode = keyField.getInt(keyEventClass);
        } catch (NoSuchFieldException noSuchField) {
            LOGGER.severe("fieldNotFound");
        } catch (IllegalAccessException illegalAccess) {
            LOGGER.severe("illegalAccess");
        }
        LOGGER.exiting(PMDViewer.class.getName(), "translateKey",
            Integer.valueOf(translationCode));
        return translationCode;
    }
    
    /**
     * Runs the application.
     *
     * @param args command-line arguments
     */
    public static void main(final String[] args) {
        LOGGER.entering(PMDViewer.class.getName(), "main", args);
        final PMDViewer viewer = new PMDViewer();
        viewer.setVisible(true);
        LOGGER.exiting(PMDViewer.class.getName(), "main");
    }
}