package net.sourceforge.pmd.swingui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;

/**
 * This is the PMD Locator.
 */
class PMDLocator extends JPanel {
    /**
     * This is the resource bundle for this UI component.
     */
    private ResourceBundle UI_STRINGS =
        ResourceBundle.getBundle("net.sourceforge.pmd.swingui.l10n.PMDLocator");
    
    /**
     * These are the preferences for this component.
     */
    private Preferences PREFS =
        Preferences.userNodeForPackage(PMDLocator.class);
        
    private Logger LOGGER =
        Logger.getLogger("net.sourceforge.pmd.swingui.PMDLocator",
            "net.sourceforge.pmd.swingui.l10n.Logging");
        
    /**
     * This is the label.
     */
    private JLabel locationLabel;
    
    /**
     * This is the location field.
     */
    private JTextField locationField;
    
    /**
     * This is the location browsing button.
     */
    private JButton locationButton;
    
    /**
     * This is the browse action.
     */
    private AbstractAction browseAction;
    
    /**
     * This creates the PMD Locator.
     */
    public PMDLocator() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new TitledBorder(UI_STRINGS.getString("title")));
        locationFieldInit();
        locationLabelInit();
        locationButtonInit();
        add(locationLabel);
        add(Box.createHorizontalStrut(25));
        add(locationField);
        add(Box.createHorizontalStrut(25));
        add(locationButton);
    }
    
    /**
     * Initializes the location button.
     */
    private void locationButtonInit() {
        LOGGER.entering(getClass().getName(), "locationButtonInit");
        assert locationButton == null : "locationButton already initialized.";
        browseActionInit();
        locationButton = new JButton(browseAction);
        LOGGER.exiting(getClass().getName(), "locationButtonInit");
    }
    
    /**
     * Initializes the browse action.
     */
    private void browseActionInit() {
        LOGGER.entering(getClass().getName(), "browseActionInit");
        assert browseAction == null : "browseAction already initialized.";
        browseAction = new AbstractAction(UI_STRINGS.getString("browse")) {
            public void actionPerformed(ActionEvent e) {
                LOGGER.entering(getClass().getName(), "actionPerformed", e);
                assert e.getActionCommand().equals(getValue(ACTION_COMMAND_KEY))
                    : "event source unexpected";
                promptLocation();
                LOGGER.exiting(getClass().getName(), "actionPerformed");
            }
        };
        browseAction.putValue(browseAction.ACTION_COMMAND_KEY, "browse");
        browseAction.putValue(browseAction.ACCELERATOR_KEY,
            KeyStroke.getKeyStroke(UI_STRINGS
                .getString("browse.accelerator")));
        browseAction.putValue(browseAction.MNEMONIC_KEY, Integer
            .valueOf(PMDViewer.translateKey(UI_STRINGS
                .getString("browse.mnemonic"))));
        browseAction.putValue(browseAction.SHORT_DESCRIPTION, UI_STRINGS
            .getString("browse.tooltip"));
        browseAction.putValue(browseAction.LONG_DESCRIPTION, UI_STRINGS
            .getString("browse.description"));
        LOGGER.exiting(getClass().getName(), "browseActionInit");
    }
    
    /**
     * Gets the location of PMD.
     */
    private void promptLocation() {
        LOGGER.entering(getClass().getName(), "promptLocation");
        assert locationField != null : "locationField not initialized.";
        final JFileChooser fileChooser =
            new JFileChooser(locationField.getText());
        final JARFileFilter filter = new JARFileFilter();
        fileChooser.setFileFilter(filter);
        fileChooser.setAcceptAllFileFilterUsed(false);
        final int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            try {
                final File selectedFile = fileChooser.getSelectedFile();
                final String filePath = selectedFile.getCanonicalPath();
                PREFS.put("pmdLocation", filePath);
                locationField.setText(filePath);
            } catch (IOException ioException) {
                LOGGER.severe("ioException");
            }
        }
        LOGGER.exiting(getClass().getName(), "promptLocation");
    }
    
    /**
     * Initializes the location label.
     */
    private void locationLabelInit() {
        LOGGER.entering(getClass().getName(), "locationLabelInit");
        assert locationLabel == null : "locationLabel already initialized.";
        assert locationField != null : "locationField not yet initialized.";
        locationLabel = new JLabel(UI_STRINGS.getString("locationLabel"));
        locationLabel.setLabelFor(locationField);
        locationLabel.setDisplayedMnemonic(PMDViewer.translateKey(UI_STRINGS
            .getString("locationLabel.mnemonic")));
        LOGGER.exiting(getClass().getName(), "locationLabelInit");
    }
    
    /**
     * Initializes the location field.
     */
    private void locationFieldInit() {
        LOGGER.entering(getClass().getName(), "locationFieldInit");
        assert locationField == null : "locationField already initialized.";
        locationField = new JTextField(PREFS.get("pmdLocation", ""));
        locationField.setEditable(false);
        LOGGER.exiting(getClass().getName(), "locationFieldInit");
    }
}