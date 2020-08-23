/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.xpath.internal.DeprecatedAttribute;

/**
 * Represents an addition operation on two or more values, or string concatenation.
 * This has a precedence greater than {@link ASTShiftExpression}, and lower
 * than {@link ASTMultiplicativeExpression}.
 *
 *
 * <pre class="grammar">
 *
 * AdditiveExpression ::= {@linkplain ASTAdditiveExpression AdditiveExpression} ( ( "+" | "-" ) {@linkplain ASTMultiplicativeExpression MultiplicativeExpression} )+
 *
 * </pre>
 *
 * <p>Note that the children of this node are not necessarily {@link ASTMultiplicativeExpression},
 * rather, they are expressions with an operator precedence greater or equal to MultiplicativeExpression.
 *
 * <p>The first child may be another AdditiveExpression only
 * if its operator is different. For example, if parentheses represent
 * nesting:
 * <table summary="Nesting examples">
 * <tr><th></th><th>Parses as</th></tr>
 *     <tr><td>{@code 1 + 2 + 3}</td><td>{@code (1 + 2 + 3)}</td></tr>
 *     <tr><td>{@code 1 + 2 / 3}</td><td>{@code (1 + (2 / 3))}</td></tr>
 *     <tr><td>{@code 1 + 2 - 3 / 4}</td><td>{@code ((1 + 2) - 3)}</td></tr>
 *     <tr><td>{@code 1 + 2 - 3 - 4}</td><td>{@code ((1 + 2) - 3 - 4)}</td></tr>
 *     <tr><td>{@code 1 + 2 - 3 - 4 + 5}</td><td>{@code (((1 + 2) - 3 - 4) + 5)}</td></tr>
 * </table>
 *
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTAdditiveExpression extends AbstractJavaExpr {

    ASTAdditiveExpression(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }

    /**
     * @deprecated Use {@link #getOperator()}
     */
    @Override
    @Deprecated
    @DeprecatedAttribute(replaceWith = "@Operator")
    public String getImage() {
        return getOperator();
    }

    /**
     * Returns the image of the operator, i.e. "+" or "-".
     */
    public String getOperator() {
        return super.getImage();
    }
}
