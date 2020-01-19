/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface JspNode extends Node {

    /**
     * Accept the visitor. *
     */
    Object jjtAccept(JspParserVisitor visitor, Object data);


    /**
     * Accept the visitor. *
     * @deprecated This method is not useful, the logic for combining
     *     children values should be present on the visitor, not the node
     */
    @Deprecated
    Object childrenAccept(JspParserVisitor visitor, Object data);


    @Override
    JspNode getChild(int index);


    @Override
    JspNode getParent();


    @Override
    Iterable<? extends JspNode> children();
}
