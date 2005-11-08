package net.sourceforge.pmd.bluej;

import bluej.extensions.BlueJ;
import bluej.extensions.PreferenceGenerator;

import javax.swing.*;

public class Preferences implements PreferenceGenerator {

    private JPanel myPanel;
    private JTextField color;
    private BlueJ bluej;
    public static final String PROFILE_LABEL="Favorite-Colour";

    // Construct the panel, and initialise it from any stored values
    public Preferences(BlueJ bluej) {
        this.bluej = bluej;
        myPanel = new JPanel();
        myPanel.add (new JLabel ("Favorite Colour"));
        color = new JTextField (40);
        myPanel.add (color);
        // Load the default value
        loadValues();
    }

    public JPanel getPanel ()  { return myPanel; }

    public void saveValues () {
        bluej.setExtensionPropertyString(PROFILE_LABEL, color.getText());
    }

    public void loadValues () {
        color.setText(bluej.getExtensionPropertyString(PROFILE_LABEL,""));
    }
}
