/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

@Deprecated
@InternalApi
public abstract class AbstractJavaNode extends AbstractJjtreeNode<JavaNode> implements JavaNode {

    protected JavaParser parser;
    private Scope scope;
    private Comment comment;

    @InternalApi
    @Deprecated
    public AbstractJavaNode(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public AbstractJavaNode(JavaParser parser, int id) {
        super(id);
        this.parser = parser;
    }

    @Override
    public void jjtOpen() {
        if (beginLine == -1 && parser.token.next != null) {
            beginLine = parser.token.next.beginLine;
            beginColumn = parser.token.next.beginColumn;
        }
    }

    @Override
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

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                ((JavaNode) children[i]).jjtAccept(visitor, data);
            }
        }
        return data;
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return ((JavaNode) parent).getScope();
        }
        return scope;
    }

    @InternalApi
    @Deprecated
    @Override
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @InternalApi
    @Deprecated
    public void comment(Comment theComment) {
        comment = theComment;
    }

    public Comment comment() {
        return comment;
    }


    @Override
    public final String getXPathNodeName() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }
}
