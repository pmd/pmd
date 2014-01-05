/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.viewer.gui.menu;

import java.text.MessageFormat;

import javax.swing.JMenu;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;
import net.sourceforge.pmd.util.viewer.model.AttributeToolkit;
import net.sourceforge.pmd.util.viewer.model.ViewerModel;
import net.sourceforge.pmd.util.viewer.util.NLS;


/**
 * contains menu items for the predicate creation
 *
 * @author Boris Gruschko ( boris at gruschko.org )
 * @version $Id$
 */
public class AttributesSubMenu
        extends JMenu {
    private ViewerModel model;
    private Node node;

    public AttributesSubMenu(ViewerModel model, Node node) {
        super(MessageFormat.format(NLS.nls("AST.MENU.ATTRIBUTES"), node.toString()));
        this.model = model;
        this.node = node;
        init();
    }

    private void init() {
        AttributeAxisIterator i = new AttributeAxisIterator(node);
        while (i.hasNext()) {
            Attribute attribute = i.next();
            add(new XPathFragmentAddingItem(attribute.getName() + " = " + attribute.getValue(), model,
                    AttributeToolkit.constructPredicate(attribute)));
        }
    }
}
