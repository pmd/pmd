/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

public abstract class AbstractJavaNode extends AbstractNode implements JavaNode {

    protected JavaParser parser;
    private Scope scope;
    private Comment comment;
    
    public AbstractJavaNode(int id) {
        super(id);
    }

    public AbstractJavaNode(JavaParser parser, int id) {
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
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Accept the visitor. *
     */
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((JavaNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }

    public Scope getScope() {
	if (scope == null) {
	    return ((JavaNode)parent).getScope();
	}
	return scope;
    }

    public void setScope(Scope scope) {
	this.scope = scope;
    }

    public void comment(Comment theComment) {
    	comment = theComment;
    }
    
    public Comment comment() {
    	return comment;
    }
    
    public String toString() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
