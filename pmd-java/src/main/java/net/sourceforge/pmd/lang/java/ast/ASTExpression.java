/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import java.util.Objects;
import net.sourceforge.pmd.lang.java.types.ast.ExprContext;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
public interface ASTExpression extends TypeNode, ASTMemberValue, ASTSwitchArrowRHS {

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

    /**
     * {@inheritDoc}
     *
     * <p>Note that even if this is null, the constant value may be known anyway but
     * technically not be a CT constant, because it uses non-static (final) fields
     * for instance. See {@link #getConstFoldingResult()} and {@link ConstResult} to
     * get a constant value in this case.
     *
     * @see #getConstFoldingResult() for more precise results.
     */
    @Override
    default @Nullable Object getConstValue() {
        ConstResult res = getConstFoldingResult();
        return res.isCompileTimeConstant() ? res.getValue() : null;
    }

    /**
     * Returns the result of constant folding on this expression. This may find a
     * constant value for more than strict compile-time constants. See {@link ConstResult}.
     *
     * @since 7.12.0
     */
    default @NonNull ConstResult getConstFoldingResult() {
        return ConstResult.NO_CONST_VALUE;
    }

    /**
     * Returns true if this expression is a compile-time constant, and is inlined.
     *
     * @see #getConstFoldingResult()
     */
    default boolean isCompileTimeConstant() {
        return getConstFoldingResult().isCompileTimeConstant();
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
    default @NonNull ExprContext getConversionContext() {
        return getRoot().getLazyTypeResolver().getConversionContextForExternalUse(this);
    }

    /**
     * Result of constant folding an expression. This can be in one of three states:
     * <ul>
     * <li>No constant value: constant folding failed, meaning, the value of the expression is not known at compile time.
     * <li>Has compile-time constant value: there is a constant value, and it is a compile-time constant in the sense of the JLS.
     * Such constants are inlined in class files. One restriction on them is that they only use literals or CT-constant
     * fields (which must be static final and have a CT-constant initializer), but not final variables or non-static fields
     * for instance.
     * <li>Has value, not compile-time constant: we could compute a constant value, but it is not CT-constant in the sense
     * of the JLS. Maybe it uses the constant initializer of a final local variable for instance.
     * </ul>
     *
     * @since 7.12.0
     */
    final class ConstResult {
        private final boolean isCompileTimeConstant;
        private final @Nullable Object value;

        static final ConstResult NO_CONST_VALUE = new ConstResult(false, null);
        static final ConstResult BOOL_TRUE = ctConst(true);
        static final ConstResult BOOL_FALSE = ctConst(false);

        ConstResult(boolean isCompileTimeConstant, @Nullable Object value) {
            this.isCompileTimeConstant = isCompileTimeConstant && value != null;
            this.value = value;
        }

        static @NonNull ConstResult ctConst(@NonNull Object result) {
            return new ConstResult(true, Objects.requireNonNull(result));
        }

        static @NonNull ConstResult ctConstIfNotNull(@Nullable Object result) {
            if (result == null) {
                return NO_CONST_VALUE;
            }
            return new ConstResult(true, Objects.requireNonNull(result));
        }

        /**
         * If true, this value is a compile-time constant in the sense of the JLS. See class description.
         */
        public boolean isCompileTimeConstant() {
            return isCompileTimeConstant;
        }

        /**
         * If true, a constant value could be computed. It is not necessarily a CT-constant. See class description.
         */
        public boolean hasValue() {
            return value != null;
        }

        /**
         * Get the value, or null if no value could be computed. The value has one of the primitive wrapper types,
         * or String type.
         */
        public @Nullable Object getValue() {
            return value;
        }
    }
}
