package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface JspNode extends Node {
    /**
     * Accept the visitor. *
     */
    Object jjtAccept(JspParserVisitor visitor, Object data);

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(JspParserVisitor visitor, Object data);
}
