/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.javacc.AbstractJjtreeNode;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.symboltable.Scope;

abstract class AbstractJavaNode extends AbstractJjtreeNode<AbstractJavaNode, JavaNode> implements JavaNode {

    private Scope scope;
    protected JSymbolTable symbolTable;
    private ASTCompilationUnit root;

    AbstractJavaNode(int id) {
        super(id);
    }

    /**
     * Temporary hack so that classes and methods are reported on their
     * identifier token and not the first annotation. Changes about text
     * documents make that more general, in a future PR.
     */
    protected @Nullable JavaccToken getPreferredReportLocation() {
        return null;
    }


    @Override
    public void jjtClose() {
        super.jjtClose();
        if (this instanceof LeftRecursiveNode && getNumChildren() > 0) {
            fitTokensToChildren(0);
        }
    }
    // override those to make them accessible in this package

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

    @Override
    protected void addChild(AbstractJavaNode child, int index) {
        super.addChild(child, index);
    }

    @Override // override to make it accessible to tests that build nodes (which have been removed on java-grammar)
    protected void insertChild(AbstractJavaNode child, int index) {
        super.insertChild(child, index);
    }

    @Override
    protected void removeChildAtIndex(int childIndex) {
        super.removeChildAtIndex(childIndex);
    }

    @Override
    protected void setImage(String image) {
        super.setImage(image);
    }


    @Override
    protected void setFirstToken(JavaccToken token) {
        super.setFirstToken(token);
    }

    @Override
    protected void setLastToken(JavaccToken token) {
        super.setLastToken(token);
    }

    @Override
    protected void setChild(AbstractJavaNode child, int index) {
        super.setChild(child, index);
    }

    void setSymbolTable(JSymbolTable table) {
        this.symbolTable = table;
    }

    @Override
    @NonNull
    public JSymbolTable getSymbolTable() {
        if (symbolTable == null) {
            return getParent().getSymbolTable();
        }
        return symbolTable;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return getRoot().getTypeSystem();
    }

    @Override
    public Scope getScope() {
        if (scope == null && getParent() != null) {
            return getParent().getScope();
        }
        return scope;
    }

    void setScope(Scope scope) {
        this.scope = scope;
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


    /**
     * Shift the start and end tokens by the given offsets.
     * @throws IllegalStateException if the right shift identifies
     * a token that is left of this node
     */
    void shiftTokens(int leftShift, int rightShift) {
        if (leftShift != 0) {
            setFirstToken(findTokenSiblingInThisNode(getFirstToken(), leftShift));
        }
        if (rightShift != 0) {
            setLastToken(findTokenSiblingInThisNode(getLastToken(), rightShift));
        }
    }

    private JavaccToken findTokenSiblingInThisNode(JavaccToken token, int shift) {
        if (shift == 0) {
            return token;
        } else if (shift < 0) {
            // expects a positive shift
            return TokenUtils.nthPrevious(getFirstToken(), token, -shift);
        } else {
            return TokenUtils.nthFollower(token, shift);
        }
    }


    void copyTextCoordinates(AbstractJavaNode copy) {
        setFirstToken(copy.getFirstToken());
        setLastToken(copy.getLastToken());
    }

    @Override
    public final String getXPathNodeName() {
        return JavaParserImplTreeConstants.jjtNodeName[id];
    }
}
