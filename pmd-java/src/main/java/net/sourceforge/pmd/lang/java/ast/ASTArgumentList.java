/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.ast.ASTList.ASTMaybeEmptyListOf;

/**
 * The argument list of a {@linkplain ASTMethodCall method}, {@linkplain ASTConstructorCall constructor call},
 * or {@linkplain ASTExplicitConstructorInvocation explicit constructor invocation}.
 *
 * <pre class="grammar">
 *
 * ArgumentList ::= "(" ( {@link ASTExpression Expression} ( "," {@link ASTExpression Expression})* )? ")"
 *
 * </pre>
 */
public final class ASTArgumentList extends ASTMaybeEmptyListOf<ASTExpression> {

    ASTArgumentList(int id) {
        super(id, ASTExpression.class);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the number of arguments.
     *
     * @return the number of arguments.
     */
    public int size() {
        return this.getNumChildren();
    }
}
