/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;

/**
 * Represents an expression, in the most general sense.
 * This corresponds to the <a href="https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-Expression">Expression</a>
 * of the JLS.
 *
 * <p>From 7.0.0 on, this is an interface which all expression nodes
 * implement.
 *
 * <p>Expressions are required to be constant in some parts of the grammar
 * (in {@link ASTSwitchLabel SwitchLabel}, {@link ASTAnnotation Annotation},
 * {@link ASTDefaultValue DefaultValue}). A <i>constant expression</i> is
 * represented as a normal expression subtree, which does not feature any
 * {@link ASTMethodReference MethodReference}, {@link ASTLambdaExpression LambdaExpression}
 * or {@link ASTAssignmentExpression AssignmentExpression}.
 *
 *
 * <pre class="grammar">
 *
 * (: In increasing precedence order :)
 * Expression ::= {@link ASTAssignmentExpression AssignmentExpression}
 *              | {@link ASTConditionalExpression ConditionalExpression}
 *              | {@link ASTLambdaExpression LambdaExpression}
 *              | {@link ASTInfixExpression InfixExpression}
 *              | {@link ASTUnaryExpression PrefixExpression} | {@link ASTCastExpression CastExpression}
 *              | {@link ASTUnaryExpression PostfixExpression}
 *              | {@link ASTSwitchExpression SwitchExpression}
 *              | {@link ASTPrimaryExpression PrimaryExpression}
 *
 * </pre>
 */
public interface ASTExpression
    extends JavaNode,
            TypeNode,
            ASTMemberValue,
            ASTSwitchArrowRHS {

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
    int getParenthesisDepth();


    /**
     * Returns true if this expression has at least one level of parentheses.
     * The specific depth can be fetched with {@link #getParenthesisDepth()}.
     */
    default boolean isParenthesized() {
        return getParenthesisDepth() > 0;
    }

    @Override
    default @Nullable Object getConstValue() {
        return null;
    }


    /** Returns true if this expression is a compile-time constant, and is inlined. */
    default boolean isCompileTimeConstant() {
        return getConstValue() != null;
    }

    /**
     * Returns the type expected by the context. This type may determine
     * an implicit conversion of this value to that type (eg a boxing
     * conversion, widening numeric conversion, or widening reference
     * conversion).
     *
     * <p>There are many different cases.
     * For example, in {@code arr['c']}, {@link #getTypeMirror()} would
     * return {@code char} for the char literal, but the context type
     * is {@code int} since it's used as an array index. Hence, a widening
     * conversion occurs. Similarly, the context type of an expression
     * in a return statement is the return type of the method, etc.
     *
     * <p>If the context is undefined, then the returned object will answer
     * true to {@link ExprContext#isMissing()}. This is completely normal
     * and needs to be accounted for by rules. For instance, it occurs
     * if this expression is used as a statement.
     *
     * <p>Note that conversions are a language-level construct only.
     * Converting from a type to another may not actually require any
     * concrete operation at runtime. For instance, converting a
     * {@code char} to an {@code int} is a noop at runtime, because chars
     * are anyway treated as ints by the JVM (within stack frames). A
     * boxing conversion will however in general translate to a call to
     * e.g. {@link Integer#valueOf(int)}.
     *
     * <p>Not all contexts allow all kinds of conversions. See
     * {@link ExprContext}.
     */
    @Experimental
    default @NonNull ExprContext getConversionContext() {
        return getRoot().getLazyTypeResolver().getConversionContextForExternalUse(this);
    }

}
