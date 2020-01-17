/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class AntlrRuleChainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        if (rule instanceof AntlrBaseRule) {
            AntlrBaseRule rule1 = (AntlrBaseRule) rule;
            ((AntlrBaseNode) node).accept(rule1.buildVisitor(ctx));
        } else {
            ((XPathRule) rule).evaluate(node, ctx);
        }
    }

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        for (final Node node : nodes) {
            indexSubtree(node);
        }
    }

    private void indexSubtree(Node node) {
        indexNode(node);
        for (Node child : node.children()) {
            indexSubtree(child);
        }
    }
}
