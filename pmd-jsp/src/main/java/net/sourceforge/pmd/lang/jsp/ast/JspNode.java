/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

public interface JspNode extends Node {

    /**
     * Accept the visitor.
     */
    Object jjtAccept(JspParserVisitor visitor, Object data);


    @Override
    JspNode getChild(int index);


    @Override
    JspNode getParent();


    @Override
    NodeStream<? extends JspNode> children();
}
