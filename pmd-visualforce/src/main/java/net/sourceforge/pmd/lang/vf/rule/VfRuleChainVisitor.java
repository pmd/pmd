/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.vf.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.vf.ast.VfNode;
import net.sourceforge.pmd.lang.vf.ast.VfParserVisitor;
import net.sourceforge.pmd.lang.vf.ast.VfParserVisitorAdapter;

public class VfRuleChainVisitor extends AbstractRuleChainVisitor {

    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        VfParserVisitor jspParserVisitor = new VfParserVisitorAdapter();

        for (int i = 0; i < nodes.size(); i++) {
            jspParserVisitor.visit((ASTCompilationUnit) nodes.get(i), ctx);
        }
    }

    protected void visit(Rule rule, Node node, RuleContext ctx) {
        // Rule better either be a JspParserVisitor, or a XPathRule
        if (rule instanceof VfParserVisitor) {
            ((VfNode) node).jjtAccept((VfParserVisitor) rule, ctx);
        } else {
            ((XPathRule) rule).evaluate(node, ctx);
        }
    }
}
