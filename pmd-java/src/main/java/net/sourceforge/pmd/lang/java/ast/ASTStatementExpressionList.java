/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTNonEmptyList;

/**
 * A list of statement expressions. Statement expressions are those
 * expressions which can appear in an {@linkplain ASTExpressionStatement expression statement}.
 *
 *
 * <p>Statement expression lists occur only {@link ASTForInit} and {@link ASTForUpdate}.
 * To improve the API of {@link ASTForInit}, however, this node implements {@link ASTStatement}.
 *
 * <pre class="grammar">
 *
 * StatementExpressionList ::= {@link ASTExpression Expression} ( "," {@link ASTExpression Expression} )*
 *
 * </pre>
 */
public final class ASTStatementExpressionList extends ASTNonEmptyList<ASTExpression> implements ASTStatement {

    ASTStatementExpressionList(int id) {
        super(id, ASTExpression.class);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
