/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

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
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

/**
 * if (x != y) { diff(); } else { same(); } and<br>
 * (!x ? diff() : same());.
 * <p/>
 * XPath can handle the easy cases, e.g.:
 * 
 * <pre>
 *    //IfStatement[
 *      Statement[2]
 *      and Expression[
 *        EqualityExpression[@Image="!="] or
 *        UnaryExpressionNotPlusMinus[@Image="!"]]]
 * </pre>
 * 
 * but "&amp;&amp;" and "||" are difficult, since we need a match for <i>all</i>
 * children instead of just one. This can be done by using a double-negative,
 * e.g.:
 * 
 * <pre>
 *    not(*[not(<i>matchme</i>)])
 * </pre>
 * 
 * Still, XPath is unable to handle arbitrarily nested cases, since it lacks
 * recursion, e.g.:
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
    private static BooleanProperty ignoreElseIfProperty = new BooleanProperty("ignoreElseIf",
            "Ignore conditions with an else-if case",
            Boolean.FALSE, 0);

    public ConfusingTernaryRule() {
        super();
        definePropertyDescriptor(ignoreElseIfProperty);
    }

    public Object visit(ASTIfStatement node, Object data) {
        // look for "if (match) ..; else .."
        if (node.jjtGetNumChildren() == 3) {
            Node inode = node.jjtGetChild(0);
            if (inode instanceof ASTExpression && inode.jjtGetNumChildren() == 1) {
                Node jnode = inode.jjtGetChild(0);
                if (isMatch(jnode)) {

                    if (!getProperty(ignoreElseIfProperty)
                            || (
                                !(node.jjtGetChild(2).jjtGetChild(0) instanceof ASTIfStatement)
                                &&
                                !(node.jjtGetParent().jjtGetParent() instanceof ASTIfStatement)
                               )
                        ) {
                        addViolation(data, node);
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        // look for "match ? .. : .."
        if (node.jjtGetNumChildren() > 0) {
            Node inode = node.jjtGetChild(0);
            if (isMatch(inode)) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }

    // recursive!
    private static boolean isMatch(Node node) {
        return
                isUnaryNot(node) ||
                isNotEquals(node) ||
                isConditionalWithAllMatches(node) ||
                isParenthesisAroundMatch(node);
    }

    private static boolean isUnaryNot(Node node) {
        // look for "!x"
        return
                node instanceof ASTUnaryExpressionNotPlusMinus &&
                "!".equals(node.getImage());
    }

    private static boolean isNotEquals(Node node) {
        // look for "x != y"
        return
                node instanceof ASTEqualityExpression &&
                "!=".equals(node.getImage());
    }

    private static boolean isConditionalWithAllMatches(Node node) {
        // look for "match && match" or "match || match"
        if (!(node instanceof ASTConditionalAndExpression) &&
                !(node instanceof ASTConditionalOrExpression)) {
            return false;
        }
        int n = node.jjtGetNumChildren();
        if (n <= 0) {
            return false;
        }
        for (int i = 0; i < n; i++) {
            Node inode = node.jjtGetChild(i);
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
        if (!(node instanceof ASTPrimaryExpression) ||
                (node.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node inode = node.jjtGetChild(0);
        if (!(inode instanceof ASTPrimaryPrefix) ||
                (inode.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node jnode = inode.jjtGetChild(0);
        if (!(jnode instanceof ASTExpression) ||
                (jnode.jjtGetNumChildren() != 1)) {
            return false;
        }
        Node knode = jnode.jjtGetChild(0);
        // recurse!
        return isMatch(knode);
    }
}
