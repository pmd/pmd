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
 * PrimaryExpression ::= {@linkplain ASTLiteral Literal}
 *                     | {@linkplain ASTClassLiteral ClassLiteral}
 *                     | {@linkplain ASTMethodCall MethodCall}
 *                     | {@linkplain ASTFieldAccess FieldAccess}
 *                     | {@linkplain ASTConstructorCall ConstructorCall}
 *                     | {@linkplain ASTArrayAllocation ArrayAllocation}
 *                     | {@linkplain ASTArrayAccess ArrayAccess}
 *                     | {@linkplain ASTVariableReference VariableReference}
 *                     | {@linkplain ASTParenthesizedExpression ParenthesizedExpression}
 *                     | {@linkplain ASTMethodReference MethodReference}
 *                     | {@linkplain ASTThisExpression ThisExpression}
 *                     | {@linkplain ASTSuperExpression SuperExpression}
 *
 *
 * </pre>
 *
 *
 */
public interface ASTPrimaryExpression extends ASTExpression {

}
