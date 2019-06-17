/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents an expression, in the most general sense.
 * This corresponds roughly to the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-AssignmentExpression">AssignmentExpression</a>
 * of the JLS. One difference though, is that this production
 * also matches lambda expressions, contrary to the JLS.
 *
 * <p>From 7.0.0 on, this is an interface which all expression nodes
 * implement.
 *
 * <pre class="grammar">
 *
 * (: In increasing precedence order :)
 * Expression ::= {@link ASTLambdaExpression LambdaExpression}
 *              | {@link ASTAssignmentExpression AssignmentExpression}
 *              | {@link ASTConditionalExpression ConditionalExpression}
 *              | {@link ASTConditionalOrExpression ConditionalOrExpression}
 *              | {@link ASTConditionalAndExpression ConditionalAndExpression}
 *              | {@link ASTInclusiveOrExpression InclusiveOrExpression}
 *              | {@link ASTExclusiveOrExpression ExclusiveOrExpression}
 *              | {@link ASTAndExpression AndExpression}
 *              | {@link ASTEqualityExpression AndExpression}
 *              | {@link ASTRelationalExpression RelationalExpression} | {@link ASTInstanceOfExpression InstanceOfExpression}
 *              | {@link ASTShiftExpression ShiftExpression}
 *              | {@link ASTAdditiveExpression AdditiveExpression}
 *              | {@link ASTMultiplicativeExpression MultiplicativeExpression}
 *              | {@link ASTUnaryExpression UnaryExpression} | {@link ASTPreIncrementExpression PreIncrementExpression} | {@link ASTPreDecrementExpression PreDecrementExpression} | {@link ASTCastExpression CastExpression}
 *              | {@link ASTPostfixExpression PostfixExpression}
 *              | {@link ASTSwitchExpression PostfixExpression}
 *              | {@link ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public interface ASTExpression extends JavaNode, TypeNode, ASTMemberValue {

    /**
     * Always returns true. This is to allow XPath queries
     * to query like {@code /*[@Expression=true()]} to match
     * any expression, but is useless in Java code.
     */
    default boolean isExpression() {
        return true;
    }


    /**
     * Returns the number of parenthesis levels around this expression.
     * If this method returns 0, then no parentheses are present.
     *
     * <p>E.g. the expression {@code (a + b)} is parsed as an AdditiveExpression
     * whose parenthesisDepth is 1, and in {@code ((a + b))} it's 2.
     *
     * <p>This is to avoid the parentheses interfering with analysis.
     * Parentheses already influence parsing by breaking the natural
     * precedence of operators. It would mostly hide false positives
     * to make a ParenthesizedExpr node, because it would make semantically
     * equivalent nodes have a very different representation.
     *
     * <p>On the other hand, when a rule explicitly cares about parentheses,
     * then this attribute may be used to find out whether parentheses
     * were mentioned, so no information is lost.
     */
    default int getParenthesisDepth() {
        return 0;
    }


    /**
     * Returns true if this expression has at least one level of parentheses.
     * The specific depth can be fetched with {@link #getParenthesisDepth()}.
     */
    default boolean isParenthesized() {
        return getParenthesisDepth() > 0;
    }

}
