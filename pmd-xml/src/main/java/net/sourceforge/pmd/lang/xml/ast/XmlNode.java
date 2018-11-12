/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AttributeNode;

/**
 * This interface represents all XML AST nodes. They are essentially thin
 * wrappers around the underlying DOM nodes.
 */
public interface XmlNode extends Node, AttributeNode {
    String BEGIN_LINE = "pmd:beginLine";
    String BEGIN_COLUMN = "pmd:beginColumn";
    String END_LINE = "pmd:endLine";
    String END_COLUMN = "pmd:endColumn";

    /**
     * Provide access to the underlying DOM node.
     *
     * @return The DOM node.
     */
    org.w3c.dom.Node getNode();
}
