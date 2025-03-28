/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.internal;

import net.sourceforge.pmd.lang.ast.Node;

import net.sf.saxon.tree.wrapper.AbstractNodeWrapper;

/**
 * A function that returns the current file name.
 *
 * @author Clément Fournier
 */
public final class XPathElementToNodeHelper {

    public static final String PMD_NODE_USER_DATA = "pmd.node";

    private XPathElementToNodeHelper() {

    }

    static Node itemToNode(Object item) {
        if (item instanceof Node node) {
            return node;
        } else if (item instanceof AstElementNode node) {
            return itemToNode(node.getUnderlyingNode());
        } else if (item instanceof AbstractNodeWrapper wrapper) {
            return itemToNode(wrapper.getUnderlyingNode());
        } else if (item instanceof org.w3c.dom.Node node) {
            return itemToNode(node.getUserData(PMD_NODE_USER_DATA));
        }
        return null;
    }
}
