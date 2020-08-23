/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a shift expression on a numeric value. This has a precedence greater than {@link
 * ASTRelationalExpression}, and lower than {@link ASTAdditiveExpression}.
 *
 * <pre class="grammar">
 *
 * ShiftExpression ::=  {@linkplain ASTShiftExpression ShiftExpression} ( ( "&lt;&lt;"  | "&gt;&gt;" | "&gt;&gt;&gt;" ) {@linkplain ASTAdditiveExpression AdditiveExpression} )+
 *
 * </pre>
 *
 * <p>Note that the children of this node are not necessarily {@link ASTAdditiveExpression},
 * rather, they are expressions with an operator precedence greater or equal to AdditiveExpression.
 *
 * <p>The first child may be another ShiftExpression only
 * if its operator is different. For example, if parentheses represent
 * nesting:
 * <table summary="Nesting examples">
 * <tr><th></th><th>Parses as</th></tr>
 *     <tr><td>{@code 1 >> 2 >> 3}</td><td>{@code (1 >> 2 >> 3)}</td></tr>
 *     <tr><td>{@code 1 >> 2 << 3}</td><td>{@code ((1 >> 2) << 3)}</td></tr>
 *     <tr><td>{@code 1 >> 2 << 3 + 4}</td><td>{@code ((1 >> 2) << (3 + 4))}</td></tr>
 *     <tr><td>{@code 1 >> 2 << 3 << 4}</td><td>{@code ((1 >> 2) << 3 << 4)}</td></tr>
 *     <tr><td>{@code 1 >> 2 << 3 << 4 >> 5}</td><td>{@code (((1 >> 2) << 3 << 4) >> 5)}</td></tr>
 * </table>
 *
 * @deprecated Replaced with {@link ASTInfixExpression}
 */
@Deprecated
public final class ASTShiftExpression extends AbstractJavaExpr implements ASTExpression {
    ASTShiftExpression(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        throw new UnsupportedOperationException("Node was removed from grammar");
    }

}
