/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
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
public class ConfusingTernaryRule extends AbstractJavaRule {
    private static PropertyDescriptor<Boolean> ignoreElseIfProperty = booleanProperty("ignoreElseIf").desc("Ignore conditions with an else-if case").defaultValue(false).build();

    public ConfusingTernaryRule() {
        super();
        definePropertyDescriptor(ignoreElseIfProperty);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
        // look for "if (match) ..; else .."
        if (node.getNumChildren() == 3) {
            Node inode = node.getChild(0);
            if (inode instanceof ASTExpression && inode.getNumChildren() == 1) {
                Node jnode = inode.getChild(0);
                if (isMatch(jnode)) {

                    if (!getProperty(ignoreElseIfProperty)
                            || !(node.getChild(2).getChild(0) instanceof ASTIfStatement)
                                    && !(node.getParent().getParent() instanceof ASTIfStatement)) {
                        addViolation(data, node);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        // look for "match ? .. : .."
        if (node.getNumChildren() > 0) {
            Node inode = node.getChild(0);
            if (isMatch(inode)) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }

    // recursive!
    private static boolean isMatch(Node node) {
        return isUnaryNot(node) || isNotEquals(node) || isConditionalWithAllMatches(node)
                || isParenthesisAroundMatch(node);
    }

    private static boolean isUnaryNot(Node node) {
        // look for "!x"
        return node instanceof ASTUnaryExpressionNotPlusMinus && "!".equals(node.getImage());
    }

    private static boolean isNotEquals(Node node) {
        // look for "x != y"
        return node instanceof ASTEqualityExpression && "!=".equals(node.getImage());
    }

    private static boolean isConditionalWithAllMatches(Node node) {
        // look for "match && match" or "match || match"
        if (!(node instanceof ASTConditionalAndExpression) && !(node instanceof ASTConditionalOrExpression)) {
            return false;
        }
        int n = node.getNumChildren();
        if (n <= 0) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            Node inode = node.getChild(i);
            // recurse!
            if (!isMatch(inode)) {
                return false;
            }
        }
        // all match
        return true;
    }

    private static boolean isParenthesisAroundMatch(Node node) {
        // look for "(match)"
        if (!(node instanceof ASTPrimaryExpression) || node.getNumChildren() != 1) {
            return false;
        }
        Node inode = node.getChild(0);
        if (!(inode instanceof ASTPrimaryPrefix) || inode.getNumChildren() != 1) {
            return false;
        }
        Node jnode = inode.getChild(0);
        if (!(jnode instanceof ASTExpression) || jnode.getNumChildren() != 1) {
            return false;
        }
        Node knode = jnode.getChild(0);
        // recurse!
        return isMatch(knode);
    }
}
