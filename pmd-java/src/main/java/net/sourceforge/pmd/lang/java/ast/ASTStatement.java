/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Represents a code statement.
 *
 * <pre class="grammar">
 *
 * Statement ::= {@link ASTAssertStatement AssertStatement}
 *             | {@link ASTBlock Block}
 *             | {@link ASTBreakStatement BreakStatement}
 *             | {@link ASTContinueStatement ContinueStatement}
 *             | {@link ASTDoStatement DoStatement}
 *             | {@link ASTEmptyStatement EmptyStatement}
 *             | {@link ASTExplicitConstructorInvocation ExplicitConstructorInvocation}
 *             | {@link ASTExpressionStatement ExpressionStatement}
 *             | {@link ASTForeachStatement ForeachStatement}
 *             | {@link ASTForStatement ForStatement}
 *             | {@link ASTIfStatement IfStatement}
 *             | {@link ASTLabeledStatement LabeledStatement}
 *             | {@link ASTLocalClassStatement LocalClassStatement}
 *             | {@link ASTLocalVariableDeclaration LocalVariableDeclaration}
 *             | {@link ASTReturnStatement ReturnStatement}
 *             | {@link ASTStatementExpressionList StatementExpressionList}
 *             | {@link ASTSwitchStatement SwitchStatement}
 *             | {@link ASTSynchronizedStatement SynchronizedStatement}
 *             | {@link ASTThrowStatement ThrowStatement}
 *             | {@link ASTTryStatement TryStatement}
 *             | {@link ASTWhileStatement WhileStatement}
 *             | {@link ASTYieldStatement YieldStatement}
 *
 * </pre>
 *
 *
 */
public interface ASTStatement extends JavaNode {
}
