/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
// stolen from XPath Explorer (http://www.xpathexplorer.com)
package net.sourceforge.pmd.cpd;

import javax.swing.*;
import java.awt.*;

public class GridBagHelper {

    GridBagLayout gridbag;
    Container container;
    GridBagConstraints c;
    int x = 0;
    int y = 0;
    int labelAlignment = SwingConstants.RIGHT;
    double[] weights;

    public GridBagHelper(Container container, double[] weights) {
        this.container = container;
        this.weights = weights;

        gridbag = new GridBagLayout();
        container.setLayout(gridbag);

        c = new GridBagConstraints();
        c.insets = new Insets(2, 2, 2, 2);
        c.anchor = GridBagConstraints.EAST;
        c.fill = GridBagConstraints.HORIZONTAL;
    }

    public void add(Component component) {
        add(component, 1);
    }

    public void add(Component component, int width) {
        c.gridx = x;
        c.gridy = y;
        c.weightx = weights[x];
        c.gridwidth = width;
        gridbag.setConstraints(component, c);
        container.add(component);
        x += width;
    }

    public void nextRow() {
        y++;
        x = 0;
    }

    public void addLabel(String label) {
        add(new JLabel(label, labelAlignment));
    }

}

