/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.xml.rule;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class XmlRuleChainVisitor extends AbstractRuleChainVisitor {

    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
	// Visit Nodes in DFS order
	Stack<Node> stack = new Stack<Node>();
	stack.addAll(nodes);
	Collections.reverse(stack);
	while (!stack.isEmpty()) {
	    Node node = stack.pop();
	    indexNode(node);
	    if (node.jjtGetNumChildren() > 0) {
		for (int i = node.jjtGetNumChildren() - 1; i >= 0; i--) {
		    stack.push(node.jjtGetChild(i));
		}
	    }
	}
    }

    protected void visit(Rule rule, Node node, RuleContext ctx) {
	// Rule better be a XPathRule
	((XPathRule) rule).evaluate(node, ctx);
    }
}
