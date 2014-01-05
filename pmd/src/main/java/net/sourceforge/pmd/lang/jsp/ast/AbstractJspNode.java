/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;

public class AbstractJspNode extends AbstractNode implements JspNode {

    protected JspParser parser;

    public AbstractJspNode(int id) {
        super(id);
    }

    public AbstractJspNode(JspParser parser, int id) {
        super(id);
        this.parser = parser;
    }

    public void jjtOpen() {
	if (beginLine == -1 && parser.token.next != null) {
	    beginLine = parser.token.next.beginLine;
	    beginColumn = parser.token.next.beginColumn;
	}
    }

    public void jjtClose() {
	if (beginLine == -1 && (children == null || children.length == 0)) {
	    beginColumn = parser.token.beginColumn;
	}
	if (beginLine == -1) {
	    beginLine = parser.token.beginLine;
	}
	endLine = parser.token.endLine;
	endColumn = parser.token.endColumn;
    }

    /**
     * Accept the visitor. *
     */
    public Object jjtAccept(JspParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(JspParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((JspNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public String toString() {
        return JspParserTreeConstants.jjtNodeName[id];
    }
}
