/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
// stolen from XPath Explorer (http://www.xpathexplorer.com)

package net.sourceforge.pmd.cpd;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

