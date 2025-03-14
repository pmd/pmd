/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A unary operator, either prefix or postfix. This is used by {@link ASTUnaryExpression UnaryExpression}
 * to abstract over the syntactic form of the operator.
 *
 * <pre class="grammar">
 *
 * UnaryOp ::= PrefixOp | PostfixOp
 *
 * PrefixOp ::= "+" | "-" | "~" | "!" | "++" | "--"
 *
 * PostfixOp ::= "++" | "--"
 *
 *  </pre>
 *
 * @see BinaryOp
 * @see AssignmentOp
 */
public enum UnaryOp implements InternalInterfaces.OperatorLike {
    /** Unary numeric promotion operator {@code "+"}. */
    UNARY_PLUS("+"),
    /** Arithmetic negation operation {@code "-"}. */
    UNARY_MINUS("-"),
    /** Bitwise complement operator {@code "~"}. */
    COMPLEMENT("~"),
    /** Logical complement operator {@code "!"}. */
    NEGATION("!"),

    /** Prefix increment operator {@code "++"}. */
    PRE_INCREMENT("++"),
    /** Prefix decrement operator {@code "--"}. */
    PRE_DECREMENT("--"),

    /** Postfix increment operator {@code "++"}. */
    POST_INCREMENT("++"),
    /** Postfix decrement operator {@code "--"}. */
    POST_DECREMENT("--");


    private final String code;

    UnaryOp(String code) {
        this.code = code;
    }

    /**
     * Returns true if this operator is pure, ie the evaluation of
     * the unary expression doesn't produce side-effects. Only increment
     * and decrement operators are impure.
     *
     * <p>This can be used to fetch all increment or decrement operations,
     * regardless of whether they're postfix or prefix. E.g.
     * <pre>{@code
     *  node.descendants(ASTUnaryExpression.class)
     *      .filterNot(it -> it.getOperator().isPure())
     * }</pre>
     */
    public boolean isPure() {
        return this.ordinal() < PRE_INCREMENT.ordinal();
    }

    /** Returns true if this is one of {@link #PRE_INCREMENT} or {@link #POST_INCREMENT}. */
    public boolean isIncrement() {
        return this == PRE_INCREMENT || this == POST_INCREMENT;
    }

    /** Returns true if this is one of {@link #PRE_DECREMENT} or {@link #POST_DECREMENT}. */
    public boolean isDecrement() {
        return this == PRE_DECREMENT || this == POST_DECREMENT;
    }


    /** Returns true if this is a prefix operator. */
    public boolean isPrefix() {
        return this.ordinal() < POST_INCREMENT.ordinal();
    }

    /** Returns true if this is a postfix operator. */
    public boolean isPostfix() {
        return !isPrefix();
    }


    @Override
    public String getToken() {
        return code;
    }

    @Override
    public String toString() {
        return this.code;
    }

    /**
     * Tests if the node is an {@link ASTUnaryExpression} with one of the given operators.
     */
    public static boolean isUnaryExprWithOperator(@Nullable JavaNode e, Set<UnaryOp> operators) {
        if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            return operators.contains(unary.getOperator());
        }
        return false;
    }

    /**
     * Tests if the node is an {@link ASTUnaryExpression} with the given operator.
     */
    public static boolean isUnaryExprWithOperator(@Nullable JavaNode e, UnaryOp operator) {
        if (e instanceof ASTUnaryExpression) {
            ASTUnaryExpression unary = (ASTUnaryExpression) e;
            return operator == unary.getOperator();
        }
        return false;
    }
}
