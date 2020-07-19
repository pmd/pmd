/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a boolean XOR-expression. This has a precedence greater than {@link ASTInclusiveOrExpression},
 * and lower than {@link ASTAndExpression}.
 *
 * <p>Note that the children of this node are not necessarily {@link ASTAndExpression},
 * rather, they are expressions with an operator precedence greater or equal to AndExpression.
 *
 *
 * <pre class="grammar">
 *
 * ExclusiveOrExpression ::=  {@linkplain ASTAndExpression AndExpression} ( "^" {@linkplain ASTAndExpression AndExpression} )+
 *
 * </pre>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTExclusiveOrExpression extends AbstractJavaExpr implements ASTExpression {

    ASTExclusiveOrExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
