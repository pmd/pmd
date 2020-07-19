/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Tags those {@link ASTExpression expressions} that are categorised as primary
 * by the JLS.
 *
 * <pre class="grammar">
 *
 * PrimaryExpression ::= {@link ASTAssignableExpr AssignableExpr}
 *                     | {@link ASTLiteral Literal}
 *                     | {@link ASTClassLiteral ClassLiteral}
 *                     | {@link ASTMethodCall MethodCall}
 *                     | {@link ASTConstructorCall ConstructorCall}
 *                     | {@link ASTArrayAllocation ArrayAllocation}
 *                     | {@link ASTMethodReference MethodReference}
 *                     | {@link ASTThisExpression ThisExpression}
 *                     | {@link ASTSuperExpression SuperExpression}
 *
 *                     | {@link ASTAmbiguousName AmbiguousName}
 *                     | {@link ASTTypeExpression TypeExpression}
 *
 * </pre>
 *
 *
 */
public interface ASTPrimaryExpression extends ASTExpression {
}
