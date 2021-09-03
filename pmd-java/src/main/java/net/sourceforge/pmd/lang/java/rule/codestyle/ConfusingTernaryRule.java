/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpression;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.UnaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * <code>if (x != y) { diff(); } else { same(); }</code> and<br>
 * <code>(!x ? diff() : same());</code>
 *
 * <p>XPath can handle the easy cases, e.g.:</p>
 *
 * <pre>
 *    //IfStatement[
 *      Statement[2]
 *      and Expression[
 *        EqualityExpression[@Image="!="] or
 *        UnaryExpressionNotPlusMinus[@Image="!"]]]
 * </pre>
 *
 * <p>But "&amp;&amp;" and "||" are difficult, since we need a match for <i>all</i>
 * children instead of just one. This can be done by using a double-negative,
 * e.g.:</p>
 *
 * <pre>
 *    not(*[not(<i>matchme</i>)])
 * </pre>
 *
 * <p>Still, XPath is unable to handle arbitrarily nested cases, since it lacks
 * recursion, e.g.:</p>
 *
 * <pre>
 * if (((x != !y)) || !(x)) {
 *     diff();
 * } else {
 *     same();
 * }
 * </pre>
 */
public class ConfusingTernaryRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> IGNORE_ELSE_IF = booleanProperty("ignoreElseIf").desc("Ignore conditions with an else-if case").defaultValue(false).build();

    public ConfusingTernaryRule() {
        super(ASTIfStatement.class, ASTConditionalExpression.class);
        definePropertyDescriptor(IGNORE_ELSE_IF);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // look for "if (match) ..; else .."
        if (node.getNumChildren() == 3
            && isMatch(node.getCondition())) {
            if (!getProperty(IGNORE_ELSE_IF)
                || !(node.getElseBranch() instanceof ASTIfStatement)
                && !(node.getParent() instanceof ASTIfStatement)) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        // look for "match ? .. : .."
        if (isMatch(node.getCondition())) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    // recursive!
    private static boolean isMatch(ASTExpression node) {
        return isUnaryNot(node) || isNotEquals(node) || isConditionalWithAllMatches(node);
    }

    private static boolean isUnaryNot(ASTExpression node) {
        // look for "!x"
        return node instanceof ASTUnaryExpression
            && ((ASTUnaryExpression) node).getOperator().equals(UnaryOp.NEGATION);
    }

    private static boolean isNotEquals(ASTExpression node) {
        // look for "x != y"
        return node instanceof ASTInfixExpression
            && ((ASTInfixExpression) node).getOperator().equals(BinaryOp.NE);
    }

    private static boolean isConditionalWithAllMatches(ASTExpression node) {
        // look for "match && match" or "match || match"
        if (node instanceof ASTInfixExpression) {
            ASTInfixExpression infix = (ASTInfixExpression) node;
            if (infix.getOperator() != BinaryOp.CONDITIONAL_AND
                && infix.getOperator() != BinaryOp.CONDITIONAL_OR) {
                return false;
            }

            return isMatch(infix.getLeftOperand())
                && isMatch(infix.getRightOperand());
        }

        return false;
    }
}
