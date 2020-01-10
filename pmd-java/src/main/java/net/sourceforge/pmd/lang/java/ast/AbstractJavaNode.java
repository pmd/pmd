/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.symboltable.Scope;

@Deprecated
@InternalApi
public abstract class AbstractJavaNode extends AbstractNode implements JavaNode {

    protected JavaParser parser;
    private Scope scope;
    private Comment comment;
    private ASTCompilationUnit root;
    private CharSequence text;

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
    public int getBeginLine() {
        return jjtGetFirstToken().getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return jjtGetFirstToken().getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return jjtGetLastToken().getEndLine();
    }

    @Override
    public int getEndColumn() {
        return jjtGetLastToken().getEndColumn();
    }


    @Override
    public JavaNode jjtGetParent() {
        return (JavaNode) super.jjtGetParent();
    }

    @Override
    public JavaNode jjtGetChild(int index) {
        return (JavaNode) super.jjtGetChild(index);
    }


    /**
     * Accept the visitor. *
     */
    @Override
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        for (Node child : children) {
            ((JavaNode) child).jjtAccept(visitor, data);
        }

        return data;
    }


    @Override
    public <T> void childrenAccept(SideEffectingVisitor<T> visitor, T data) {
        for (Node child : children) {
            ((JavaNode) child).jjtAccept(visitor, data);
        }

    }


    @Override
    public Scope getScope() {
        if (scope == null) {
            return ((JavaNode) parent).getScope();
        }
        return scope;
    }

    @Override
    public CharSequence getText() {
        if (text == null) {
            text = getRoot().getText().subSequence(getStartOffset(), getEndOffset());
        }
        return text;
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
    @NonNull
    public ASTCompilationUnit getRoot() {
        // storing a reference on each node ensures that each path is roamed
        // at most once.
        if (root == null) {
            root = jjtGetParent().getRoot();
        }
        return root;
    }

    @Override
    public JavaccToken jjtGetFirstToken() {
        return (JavaccToken) firstToken;
    }

    @Override
    public JavaccToken jjtGetLastToken() {
        return (JavaccToken) lastToken;
    }

    @Override
    public String getXPathNodeName() {
        return JavaParserTreeConstants.jjtNodeName[id];
    }


    /**
     * The toString of Java nodes is only meant for debugging purposes
     * as it's pretty expensive.
     */
    @Override
    public String toString() {
        return "|" + getXPathNodeName() + "|" + getStartOffset() + "," + getEndOffset() + "|" + getText();
    }

    private int getStartOffset() {
        return this.jjtGetFirstToken().getStartInDocument();
    }


    private int getEndOffset() {
        return this.jjtGetLastToken().getEndInDocument();
    }
}
