/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.ast.ASTConditionalExpression;
import net.sourceforge.pmd.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * if (x != y) { diff(); } else { same(); } and<br>
 * (!x ? diff() : same());.
 * <p/>
 * XPath can handle the easy cases, e.g.:<pre>
 *    //IfStatement[
 *      Statement[2]
 *      and Expression[
 *        EqualityExpression[@Image="!="] or
 *        UnaryExpressionNotPlusMinus[@Image="!"]]]
 * </pre>
 * but "&amp;&amp;" and "||" are difficult, since we need a match
 * for <i>all</i> children instead of just one.  This can be done by
 * using a double-negative, e.g.:<pre>
 *    not(*[not(<i>matchme</i>)])
 * </pre>
 * Still, XPath is unable to handle arbitrarily nested cases, since it
 * lacks recursion, e.g.:<pre>
 *   if (((x != !y)) || !(x)) { diff(); } else { same(); }
 * </pre>
 */
public class ConfusingTernary extends AbstractRule {

    public Object visit(ASTIfStatement node, Object data) {
        // look for "if (match) ..; else .."
        if (node.jjtGetNumChildren() == 3) {
            SimpleNode inode = (SimpleNode) node.jjtGetChild(0);
            if (inode instanceof ASTExpression &&
                    inode.jjtGetNumChildren() == 1) {
                SimpleNode jnode = (SimpleNode) inode.jjtGetChild(0);
                if (isMatch(jnode)) {
                    addViolation(data, node);
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTConditionalExpression node, Object data) {
        // look for "match ? .. : .."
        if (node.jjtGetNumChildren() > 0) {
            SimpleNode inode = (SimpleNode) node.jjtGetChild(0);
            if (isMatch(inode)) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }

    // recursive!
    private static boolean isMatch(SimpleNode node) {
        return
                isUnaryNot(node) ||
                isNotEquals(node) ||
                isConditionalWithAllMatches(node) ||
                isParenthesisAroundMatch(node);
    }

    private static boolean isUnaryNot(SimpleNode node) {
        // look for "!x"
        return
                node instanceof ASTUnaryExpressionNotPlusMinus &&
                "!".equals(node.getImage());
    }

    private static boolean isNotEquals(SimpleNode node) {
        // look for "x != y"
        return
                node instanceof ASTEqualityExpression &&
                "!=".equals(node.getImage());
    }

    private static boolean isConditionalWithAllMatches(SimpleNode node) {
        // look for "match && match" or "match || match"
        if (!(node instanceof ASTConditionalAndExpression) &&
                !(node instanceof ASTConditionalOrExpression)) {
            return false;
        }
        int i_max = node.jjtGetNumChildren();
        if (i_max <= 0) {
            return false;
        }
        for (int i = 0; i < i_max; i++) {
            SimpleNode inode = (SimpleNode) node.jjtGetChild(i);
            // recurse!
            if (!isMatch(inode)) {
                return false;
            }
        }
        // all match
        return true;
    }

    private static boolean isParenthesisAroundMatch(SimpleNode node) {
        // look for "(match)"
        if (!(node instanceof ASTPrimaryExpression) ||
                (node.jjtGetNumChildren() != 1)) {
            return false;
        }
        SimpleNode inode = (SimpleNode) node.jjtGetChild(0);
        if (!(inode instanceof ASTPrimaryPrefix) ||
                (inode.jjtGetNumChildren() != 1)) {
            return false;
        }
        SimpleNode jnode = (SimpleNode) inode.jjtGetChild(0);
        if (!(jnode instanceof ASTExpression) ||
                (jnode.jjtGetNumChildren() != 1)) {
            return false;
        }
        SimpleNode knode = (SimpleNode) jnode.jjtGetChild(0);
        // recurse!
        return isMatch(knode);
    }
}
