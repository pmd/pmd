/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.apex.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

/**
 * This is an Apex RuleViolation. It knows how to try to extract the following
 * extra information from the violation node:
 * <ul>
 * <li>Package name</li>
 * <li>Class name</li>
 * <li>Method name</li>
 * <li>Variable name</li>
 * <li>Suppression indicator</li>
 * </ul>
 * @param <T>
 */
public class ApexRuleViolation<T> extends ParametricRuleViolation<Node> {

    public ApexRuleViolation(Rule rule, RuleContext ctx, Node node, String message, int beginLine, int endLine) {
        this(rule, ctx, node, message);

        setLines(beginLine, endLine);
    }

    public ApexRuleViolation(Rule rule, RuleContext ctx, Node node, String message) {
        super(rule, ctx, node, message);

        if (node != null) {
            if (!suppressed) {
                suppressed = isSupressed(node, getRule());
            }
        }
    }

    /**
     * Check for suppression on this node, on parents, and on contained types
     * for ASTCompilationUnit
     * 
     * @param node
     */
    public static boolean isSupressed(Node node, Rule rule) {
        boolean result = suppresses(node, rule);

        if (!result) {
            Node parent = node.jjtGetParent();
            while (!result && parent != null) {
                result = suppresses(parent, rule);
                parent = parent.jjtGetParent();
            }
        }
        
        return result;
    }

    private static boolean suppresses(final Node node, Rule rule) {
        return node instanceof CanSuppressWarnings
                && ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(rule);
    }
}
