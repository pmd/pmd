/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.document.Chars;

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
    /**
     * Return the text of the literal in the source file. Note that
     * {@link #getText()} may include parentheses.
     */
    Chars getLiteralText();
}
