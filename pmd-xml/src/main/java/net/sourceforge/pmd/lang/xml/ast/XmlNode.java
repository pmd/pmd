/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.xml.ast;

import net.sourceforge.pmd.lang.ast.impl.GenericNode;

/**
 * This interface represents all XML AST nodes. They are essentially thin
 * wrappers around the underlying DOM nodes.
 */
public interface XmlNode extends GenericNode<XmlNode> {

    /**
     * Provide access to the underlying DOM node.
     *
     * @return The DOM node.
     */
    org.w3c.dom.Node getNode();
}
