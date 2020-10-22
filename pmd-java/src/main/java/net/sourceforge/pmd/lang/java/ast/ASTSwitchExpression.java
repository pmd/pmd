/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

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

    /**
     * Returns a stream of all expressions which can be the value of this
     * switch. Eg in the following, the yield expressions are marked by a
     * comment.
     * <pre>{@code
     *
     * switch (foo) {
     *    case 1  -> 1;         // <- <1>
     *    case 2  -> 2;         // <- <2>
     *    default -> {
     *        int i = foo * 2;
     *        yield i * foo;    // <- <i * foo>
     *    }
     * }
     *
     * }</pre>
     *
     */
    public NodeStream<ASTExpression> getYieldExpressions() {

        return NodeStream.forkJoin(
            getBranches(),
            br -> br.descendants(ASTYieldStatement.class)
                    .filter(it -> it.getYieldTarget() == this)
                    .map(ASTYieldStatement::getExpr),
            br -> br.asStream()
                    .filterIs(ASTSwitchArrowBranch.class)
                    .map(ASTSwitchArrowBranch::getRightHandSide)
                    .filterIs(ASTExpression.class)
        );
    }
}
