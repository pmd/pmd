/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A unary operator, either prefix or postfix. This is used by {@link ASTUnaryExpression UnaryExpression}
 * to abstract over the syntactic form of the operator.
 *
 * <pre class="grammar">
 *
 * UnaryOp ::= {@link PrefixOp} | {@link PostfixOp}
 *
 * </pre>
 *
 */
public interface UnaryOp {

    /**
     * Returns true if this operator is pure, ie the evaluation of
     * the unary expression doesn't produce side-effects. Only increment
     * and decrement operators are impure.
     *
     * <p>This can be used to fetch all increment or decrement operations,
     * regardless of whether they're postfix or prefix. E.g.
     * <pre>{@code
     *  node.findDescendantsOfType(ASTUnaryExpression.class).stream().anyMatch(it -> !it.getOperator().isPure())
     * }</pre>
     *
     * TODO update example for node streams
     */
    boolean isPure();


    /**
     * A prefix operator for {@link ASTPrefixExpression}.
     *
     * <pre class="grammar">
     *
     * PrefixOp ::= "+" | "-" | "~" | "!" | "++" | "--"
     *
     * </pre>
     *
     * @see BinaryOp
     * @see AssignmentOp
     */
    enum PrefixOp implements UnaryOp {
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
        PRE_DECREMENT("--");

        private final String code;

        PrefixOp(String code) {
            this.code = code;
        }

        @Override
        public boolean isPure() {
            return this != PRE_INCREMENT && this != PRE_DECREMENT;
        }

        @Override
        public String toString() {
            return this.code;
        }

    }

    /**
     * A postfix operator for {@link ASTPostfixExpression}.
     *
     * <pre class="grammar">
     *
     * PostfixOp ::= "++" | "--"
     *
     * </pre>
     *
     * @see BinaryOp
     * @see AssignmentOp
     */
    enum PostfixOp implements UnaryOp {
        /** Postfix increment operator {@code "++"}. */
        POST_INCREMENT("++"),
        /** Postfix decrement operator {@code "--"}. */
        POST_DECREMENT("--");

        private final String code;

        PostfixOp(String code) {
            this.code = code;
        }

        @Override
        public boolean isPure() {
            return false;
        }

        @Override
        public String toString() {
            return this.code;
        }
    }
}
