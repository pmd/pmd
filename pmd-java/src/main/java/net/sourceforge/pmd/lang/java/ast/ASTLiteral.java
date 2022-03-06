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

}
