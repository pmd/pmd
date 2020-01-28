/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

@Deprecated
@InternalApi
public abstract class AbstractJavaNode extends AbstractJjtreeNode<JavaNode> implements JavaNode {

    private Scope scope;
    private Comment comment;
    private ASTCompilationUnit root;

    @InternalApi
    @Deprecated
    public AbstractJavaNode(int id) {
        super(id);
    }

    @Override
    public Object childrenAccept(JavaParserVisitor visitor, Object data) {
        for (Node child : children()) {
            ((JavaNode) child).jjtAccept(visitor, data);
        }

        return data;
    }


    @Override
    public <T> void childrenAccept(SideEffectingVisitor<T> visitor, T data) {
        for (Node child : children()) {
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
            root = getParent().getRoot();
        }
        return root;
    }

    @Override
    public String getXPathNodeName() {
        return JavaParserImplTreeConstants.jjtNodeName[id];
    }
}
