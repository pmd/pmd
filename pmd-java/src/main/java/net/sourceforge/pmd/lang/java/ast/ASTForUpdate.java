/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Update clause of a {@linkplain ASTForStatement for statement}.
 *
 * <pre class="grammar">
 *
 * ForUpdate ::= {@linkplain ASTStatementExpressionList StatementExpressionList}
 *
 * </pre>
 */
public final class ASTForUpdate extends AbstractJavaNode {

    ASTForUpdate(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /** Returns the expression list nested within this node. */
    public ASTStatementExpressionList getExprList() {
        return (ASTStatementExpressionList) getChild(0);
    }
}
