/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A lexical literal. This is an expression that is represented by exactly
 * one token. This interface is implemented by several nodes.
 *
 * <pre class="grammar">
 *
 * Literal ::= {@link ASTNumericLiteral NumericLiteral}
 *           | {@link ASTStringLiteral StringLiteral}
 *           | {@link ASTCharLiteral CharLiteral}
 *           | {@link ASTBooleanLiteral BooleanLiteral}
 *           | {@link ASTNullLiteral NullLiteral}
 *
 * </pre>
 */
public interface ASTLiteral extends ASTPrimaryExpression {

    // Those methods are deprecated as they're not so useful, and introduce
    // unwanted XPath attributes

    /**
     * Returns true if this is a {@linkplain ASTStringLiteral string literal}.
     */
    @Deprecated
    default boolean isStringLiteral() {
        return this instanceof ASTStringLiteral;
    }

    /**
     * Returns true if this is a {@linkplain ASTCharLiteral character literal}.
     */
    @Deprecated
    default boolean isCharLiteral() {
        return this instanceof ASTCharLiteral;
    }


    /**
     * Returns true if this is the {@linkplain ASTNullLiteral null literal}.
     */
    @Deprecated
    default boolean isNullLiteral() {
        return this instanceof ASTNullLiteral;
    }


    /**
     * Returns true if this is a {@linkplain ASTBooleanLiteral boolean literal}.
     */
    @Deprecated
    default boolean isBooleanLiteral() {
        return this instanceof ASTBooleanLiteral;
    }


    /**
     * Returns true if this is a {@linkplain ASTNumericLiteral numeric literal}
     * of any kind.
     */
    @Deprecated
    default boolean isNumericLiteral() {
        return this instanceof ASTNumericLiteral;
    }


    /**
     * Returns true if this is an {@linkplain ASTNumericLiteral integer literal}.
     */
    @Deprecated
    default boolean isIntLiteral() {
        return false;
    }


    /**
     * Returns true if this is a {@linkplain ASTNumericLiteral long integer literal}.
     */
    @Deprecated
    default boolean isLongLiteral() {
        return false;
    }


    /**
     * Returns true if this is a {@linkplain ASTNumericLiteral float literal}.
     */
    @Deprecated
    default boolean isFloatLiteral() {
        return false;
    }


    /**
     * Returns true if this is a {@linkplain ASTNumericLiteral double literal}.
     */
    @Deprecated
    default boolean isDoubleLiteral() {
        return false;
    }

}
