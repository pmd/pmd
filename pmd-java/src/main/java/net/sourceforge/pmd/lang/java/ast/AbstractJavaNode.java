/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.symboltable.Scope;

@Deprecated
@InternalApi
public abstract class AbstractJavaNode extends AbstractJjtreeNode<AbstractJavaNode, JavaNode> implements JavaNode {

    private Scope scope;
    private Comment comment;
    private ASTCompilationUnit root;

    @InternalApi
    @Deprecated
    public AbstractJavaNode(int id) {
        super(id);
    }

    @Override
    public Scope getScope() {
        if (scope == null) {
            return getParent().getScope();
        }
        return scope;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof JavaVisitor) {
            return this.acceptVisitor((JavaVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    protected abstract <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data);

    // override those to make them accessible in this package

    @Override // override to make it accessible to tests that build nodes (which have been removed on java-grammar)
    protected void addChild(AbstractJavaNode child, int index) {
        super.addChild(child, index);
    }

    @Override // override to make it accessible to tests that build nodes (which have been removed on java-grammar)
    protected void insertChild(AbstractJavaNode child, int index) {
        super.insertChild(child, index);
    }

    @Override // override to make it accessible to parser
    protected void setImage(String image) {
        super.setImage(image);
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
