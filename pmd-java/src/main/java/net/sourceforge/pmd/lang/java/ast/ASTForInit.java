/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * The initialization clause of a {@linkplain ASTForStatement for loop}.
 * Note: ForInit nodes are necessary in the tree to differentiate them
 * from the update clause. They just confer a contextual role to their
 * child.
 *
 * <pre class="grammar">
 *
 * ForInit ::= {@link ASTLocalVariableDeclaration LocalVariableDeclaration}
 *           | {@link ASTStatementExpressionList StatementExpressionList}
 *
 * </pre>
 */
public final class ASTForInit extends AbstractJavaNode {

    ASTForInit(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /** Returns the statement nested within this node. */
    public ASTStatement getStatement() {
        return (ASTStatement) getFirstChild();
    }

}
