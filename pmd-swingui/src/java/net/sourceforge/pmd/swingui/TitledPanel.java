package net.sourceforge.pmd.swingui;

import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * This is a titled panel.
 *
 * @author Brant Gurganus
 * @version 0.1
 * @since 0.1
 */
public class TitledPanel extends JPanel {
    /**
     * Creates the titled panel.
     *
     * @param title title of panel
     */
    public TitledPanel(String title) {
        super();
        setBorder(new TitledBorder(title));
    }
}