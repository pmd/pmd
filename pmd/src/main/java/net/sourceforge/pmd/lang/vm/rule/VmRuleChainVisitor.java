package net.sourceforge.pmd.lang.vm.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.vm.ast.ASTprocess;
import net.sourceforge.pmd.lang.vm.ast.SimpleNode;
import net.sourceforge.pmd.lang.vm.ast.VmParserVisitor;
import net.sourceforge.pmd.lang.vm.ast.VmParserVisitorAdapter;

public class VmRuleChainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void indexNodes(final List<Node> nodes, final RuleContext ctx) {
        final VmParserVisitor vmParserVisitor = new VmParserVisitorAdapter() {
            // Perform a visitation of the AST to index nodes which need
            // visiting by type
            @Override
            public Object visit(final SimpleNode node, final Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };

        for (int i = 0; i < nodes.size(); i++) {
            vmParserVisitor.visit((ASTprocess) nodes.get(i), ctx);
        }
    }

    @Override
    protected void visit(final Rule rule, final Node node, final RuleContext ctx) {
        // Rule better either be a VmParserVisitor, or a XPathRule
        if (rule instanceof VmParserVisitor) {
            ((SimpleNode) node).jjtAccept((VmParserVisitor) rule, ctx);
        }
        else {
            ((XPathRule) rule).evaluate(node, ctx);
        }
    }
}
