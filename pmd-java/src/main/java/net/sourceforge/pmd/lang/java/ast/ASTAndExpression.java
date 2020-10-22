/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a non-shortcut boolean AND-expression.
 * This has a precedence greater than {@link ASTExclusiveOrExpression},
 * and lower than {@link ASTEqualityExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTEqualityExpression},
 * rather, they are expressions with an operator precedence greater or equal to EqualityExpression.
 *
 *
 * <pre class="grammar">
 *
 * AndExpression ::=  {@linkplain ASTEqualityExpression EqualityExpression} ( "&" {@linkplain ASTEqualityExpression EqualityExpression} )+
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTAndExpression extends AbstractJavaExpr implements ASTExpression {

    ASTAndExpression(int id) {
        super(id);
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
