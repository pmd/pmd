/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a boolean OR-expression. This has a precedence greater than {@link ASTConditionalExpression},
 * and lower than {@link ASTConditionalAndExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTConditionalAndExpression},
 * rather, they are expressions with an operator precedence greater or equal to ConditionalAndExpression.
 *
 *
 * <pre class="grammar">
 *
 * ConditionalOrExpression ::=  {@linkplain ASTConditionalAndExpression ConditionalAndExpression} ( "||" {@linkplain ASTConditionalAndExpression ConditionalAndExpression} )+
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTConditionalOrExpression extends AbstractJavaExpr implements ASTExpression {
    ASTConditionalOrExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }
}
