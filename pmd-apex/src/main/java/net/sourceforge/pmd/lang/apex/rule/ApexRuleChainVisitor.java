/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.apex.ast.ApexNode;
import net.sourceforge.pmd.lang.apex.ast.ApexParserVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class ApexRuleChainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        Stack<Node> stack = new Stack<>();
        stack.addAll(nodes);
        Collections.reverse(stack);
        while (!stack.isEmpty()) {
            Node node = stack.pop();
            indexNode(node);
            if (node.getNumChildren() > 0) {
                for (int i = node.getNumChildren() - 1; i >= 0; i--) {
                    stack.push(node.getChild(i));
                }
            }
        }
    }

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        if (rule instanceof XPathRule) {
            ((XPathRule) rule).evaluate(node, ctx);
        } else {
            ((ApexNode<?>) node).jjtAccept((ApexParserVisitor) rule, ctx);
        }
    }
}
