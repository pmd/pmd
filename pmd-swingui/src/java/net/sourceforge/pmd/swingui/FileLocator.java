package net.sourceforge.pmd.swingui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * This is a file locator.  On the graphics end, it provides a listing of files
 * as well as list modification controls.  On the programming end, it provides
 * a means for retrieving the list of selected files.
 *
 * @author Brant Gurganus
 * @version 0.1
 * @since 0.1
 */
public class FileLocator extends TitledPanel {
    /**
     * This is the resource bundle for this UI component.
     */
    private static final ResourceBundle UI_STRINGS = ResourceBundle
        .getBundle("net.sourceforge.pmd.swingui.l10n.FileLocator");
        
    private static final Logger LOGGER = Logger.getLogger(FileLocator.class
        .getName(),
        "net.sourceforge.pmd.swingui.l10n.Logging");
        
    /**
     * This is the location list.
     */
    private JList locationList;
    
    /**
     * This is the location list scroller.
     */
    private JScrollPane locationScroller;
    
    /**
     * These are the chosen locations.
     */
    private Vector locations;
    
    /**
     * This is the reset button.
     */
    private JButton resetButton;
    
    /**
     * This is the reset action.
     */
    private AbstractAction resetAction;
    
    /**
     * This is the add button.
     */
    private JButton addButton;
    
    /**
     * This is the add action.
     */
    private AbstractAction addAction;
    
    /**
     * This is the control panel.
     */
    private JPanel controlPanel;
    
    /**
     * Creates a file locator with default title.
     */
    public FileLocator() {
        this(UI_STRINGS.getString("title"));
    }
    
    /**
     * Creates a file locator titled with <code>title</code>.
     *
     * @param title title for file locator
     */
    public FileLocator(final String title) {
        super(title);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        locationScrollerInit();
        add(locationScroller);
        controlPanelInit();
        add(controlPanel);
    }
    
    /**
     * Initializes the location list scroller.
     */
    private void locationScrollerInit() {
        LOGGER.entering(getClass().getName(), "locationScrollerInit");
        assert locationScroller == null :
            "locationScroller already initialized.";
        locationListInit();
        locationScroller = new JScrollPane(locationList);
        LOGGER.exiting(getClass().getName(), "locationScrollerInit");
    }
    
    /**
     * Initializes the location list.
     *
     * @todo determine if an initial capacity or increment should be used
     */
    private void locationListInit() {
        LOGGER.entering(getClass().getName(), "locationListInit");
        assert locationList == null : "locationList already initalized.";
        locations = new Vector();
        locationList = new JList(locations);
        LOGGER.exiting(getClass().getName(), "locationListInit");
    }
    
    /**
     * Initializes the control panel.
     */
    private void controlPanelInit() {
        LOGGER.entering(getClass().getName(), "controlPanelInit");
        assert controlPanel == null : "controlPanel already initialized.";
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.X_AXIS));
        resetButtonInit();
        controlPanel.add(resetButton);
        addButtonInit();
        controlPanel.add(addButton);
        LOGGER.exiting(getClass().getName(), "controlPanelInit");
    }
    
    /**
     * Initializes the add button.
     */
    private void addButtonInit() {
        LOGGER.entering(getClass().getName(), "addButtonInit");
        assert addButton == null : "addButton already initialized.";
        addActionInit();
        addButton = new JButton(addAction);
        LOGGER.exiting(getClass().getName(), "addButtonInit");
    }
    
    /**
     * Initializes the add action.
     */
    private void addActionInit() {
        LOGGER.entering(getClass().getName(), "addActionInit");
        assert addAction == null : "addAction already initialized.";
        addAction = new AbstractAction(UI_STRINGS.getString("add")) {
            public void actionPerformed(ActionEvent e) {
                LOGGER.entering(getClass().getName(), "actionPerformed", e);
                assert e.getActionCommand().equals(getValue(ACTION_COMMAND_KEY))
                    : "event source unexpected";
                promptForFiles();
                LOGGER.exiting(getClass().getName(), "actionPerformed");
            }
        };
        addAction.putValue(addAction.ACTION_COMMAND_KEY, "add");
        addAction.putValue(addAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(UI_STRINGS
                .getString("add.accelerator")));
        addAction.putValue(addAction.MNEMONIC_KEY, Integer
            .valueOf(PMDViewer.translateKey(UI_STRINGS
                .getString("add.mnemonic"))));
        addAction.putValue(addAction.SHORT_DESCRIPTION, UI_STRINGS
            .getString("add.tooltip"));
        addAction.putValue(addAction.LONG_DESCRIPTION, UI_STRINGS
            .getString("add.description"));
        LOGGER.exiting(getClass().getName(), "addActionInit");
    }
    
    /**
     * Prompts for files.
     */
    private void promptForFiles() {
        LOGGER.entering(getClass().getName(), "promptForFiles");
        assert locations != null : "locations not initialized.";
        assert locationList != null : "locationList not initialized.";
        assert resetAction != null : "resetAction not initialized.";
        final JFileChooser fileChooser = new JFileChooser();
        final JavaFileFilter filter = new JavaFileFilter();
        fileChooser.setFileFilter(filter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setAcceptAllFileFilterUsed(false);
        final int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            final File selectedFile = fileChooser.getSelectedFile();
            locations.add(selectedFile);
            locationList.setListData(locations);
            resetAction.setEnabled(true);
        }
        LOGGER.exiting(getClass().getName(), "promptForFiles");
    }
    
    /**
     * Initializes the reset button.
     */
    private void resetButtonInit() {
        LOGGER.entering(getClass().getName(), "resetButtonInit");
        assert resetButton == null : "resetButton already initialized.";
        resetActionInit();
        resetButton = new JButton(resetAction);
        LOGGER.exiting(getClass().getName(), "resetButtonInit");
    }
    
    /**
     * Initializes the reset action.
     */
    private void resetActionInit() {
        LOGGER.entering(getClass().getName(), "resetActionInit");
        assert resetAction == null : "resetAction already initialized.";
        resetAction = new AbstractAction(UI_STRINGS.getString("reset")) {
            public void actionPerformed(ActionEvent e) {
                LOGGER.entering(getClass().getName(), "actionPerformed", e);
                assert e.getActionCommand().equals(getValue(ACTION_COMMAND_KEY))
                    : "event source unexpected";
                resetList();
                LOGGER.exiting(getClass().getName(), "actionPerformed");
            }
        };
        resetAction.putValue(resetAction.ACTION_COMMAND_KEY, "reset");
        resetAction.putValue(resetAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(UI_STRINGS
                .getString("reset.accelerator")));
        resetAction.putValue(resetAction.MNEMONIC_KEY, Integer
            .valueOf(PMDViewer.translateKey(UI_STRINGS
                .getString("reset.mnemonic"))));
        resetAction.putValue(resetAction.SHORT_DESCRIPTION, UI_STRINGS
            .getString("reset.tooltip"));
        resetAction.putValue(resetAction.LONG_DESCRIPTION, UI_STRINGS
            .getString("reset.description"));
        resetAction.setEnabled(false);
        LOGGER.exiting(getClass().getName(), "resetActionInit");
    }
    
    /**
     * Gets the list of files.
     *
     * @return copy of list of files
     */
    public Vector getList() {
        LOGGER.entering(getClass().getName(), "getList");
        final Vector returnedList = (Vector) locations.clone();
        LOGGER.exiting(getClass().getName(), "getList", returnedList);
        return returnedList;
    }
    
    /**
     * Resets the list.
     */
    private void resetList() {
        LOGGER.entering(getClass().getName(), "resetList");
        assert locations != null : "locations is not initialized.";
        assert locationList != null : "locationList is not initialized.";
        assert resetAction != null : "resetAction is not initialized.";
        locations.removeAllElements();
        locationList.setListData(locations);
        resetAction.setEnabled(false);
        LOGGER.exiting(getClass().getName(), "resetList");
    }
}