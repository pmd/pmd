/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A switch expression, as introduced in Java 12. This node only occurs
 * in the contexts where an expression is expected. In particular,
 * switch constructs occurring in statement position are parsed as a
 * {@linkplain ASTSwitchStatement SwitchStatement}, and not a
 * {@link ASTSwitchExpression SwitchExpression} within a
 * {@link ASTExpressionStatement ExpressionStatement}. That is because
 * switch statements are not required to be exhaustive, contrary
 * to switch expressions.
 *
 * <p>Their syntax is identical though, and described on {@link ASTSwitchLike}.
 */
public final class ASTSwitchExpression extends AbstractJavaExpr
    implements ASTExpression,
               ASTSwitchLike {

    ASTSwitchExpression(int id) {
        super(id);
    }



    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
