/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;

import net.sourceforge.pmd.util.viewer.model.ViewerModel;


/**
 * adds the given path fragment to the XPath expression upon action
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class XPathFragmentAddingItem extends JMenuItem implements ActionListener {
    private ViewerModel model;
    private String fragment;

    /**
     * constructs the item
     *
     * @param caption  menu item's caption
     * @param model    model to refer to
     * @param fragment XPath expression fragment to be added upon action
     */
    public XPathFragmentAddingItem(String caption, ViewerModel model, String fragment) {
        super(caption);
        this.model = model;
        this.fragment = fragment;
        addActionListener(this);
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        model.appendToXPathExpression(fragment, this);
    }
}
