package net.sourceforge.pmd.util.designer;

import javax.swing.*;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class JSmartPanel extends JPanel {

    private GridBagConstraints constraints = new GridBagConstraints();

    public JSmartPanel() {
        super(new GridBagLayout());
    }

    public void add(Component comp, int gridx, int gridy, int gridwidth, int gridheight, double weightx, double weighty, int anchor, int fill, Insets insets) {
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.anchor = anchor;
        constraints.fill = fill;
        constraints.insets = insets;
        add(comp, constraints);
    }
}

