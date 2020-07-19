/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a multiplication, division, or modulo operation on
 * two or more values. This has a precedence greater than {@link ASTAdditiveExpression},
 * and lower than {@linkplain ASTUnaryExpression UnaryExpression}.
 *
 * <pre class="grammar">
 *
 * MultiplicativeExpression ::= {@linkplain ASTMultiplicativeExpression MultiplicativeExpression} ( ( "*" | "/" | "%" ) {@linkplain ASTUnaryExpression UnaryExpression} )+
 *
 * </pre>
 *
 * <p>Note that the children of this node are not necessarily {@link ASTUnaryExpression}s,
 * rather, they are expressions with an operator precedence greater or equal to UnaryExpression.
 *
 * <p>The first child may be another MultiplicativeExpression only
 * if its operator is different. For example, if parentheses represent
 * nesting:
 * <table summary="Nesting examples">
 * <tr><th></th><th>Parses as</th></tr>
 *     <tr><td>{@code 1 * 2 * 3}</td><td>{@code (1 * 2 * 3)}</td></tr>
 *     <tr><td>{@code 1 * 2 / 3}</td><td>{@code ((1 * 2) / 3)}</td></tr>
 *     <tr><td>{@code 1 * 2 / 3 / 4}</td><td>{@code ((1 * 2) / 3 / 4)}</td></tr>
 * </table>
 *
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTMultiplicativeExpression extends AbstractJavaExpr {


    ASTMultiplicativeExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns the image of the operator, i.e. "*", "/" or "%".
     */
    public String getOperator() {
        return getImage();
    }
}
