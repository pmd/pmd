package net.sourceforge.pmd.jsp.ast;

import java.util.List;

import net.sourceforge.pmd.AbstractRuleChainVisitor;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.ast.CompilationUnit;

public class JspRuleChainVisitor extends AbstractRuleChainVisitor {

    protected void indexNodes(List<CompilationUnit> astCompilationUnits, RuleContext ctx) {
        JspParserVisitor jspParserVisitor = new JspParserVisitorAdapter() {
            // Perform a visitation of the AST to index nodes which need
            // visiting by type
            public Object visit(SimpleNode node, Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };

        for (int i = 0; i < astCompilationUnits.size(); i++) {
            jspParserVisitor.visit((ASTCompilationUnit)astCompilationUnits.get(i), ctx);
        }
    }

    protected void visit(Rule rule, net.sourceforge.pmd.ast.SimpleNode node, RuleContext ctx) {
        // Rule better either be a JspParserVisitor, or a XPathRule
        if (rule instanceof JspParserVisitor) {
            ((SimpleNode) node).jjtAccept((JspParserVisitor) rule, ctx);
        } else {
            ((XPathRule) rule).evaluate(node, ctx);
        }
    }
}
