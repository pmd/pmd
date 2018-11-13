/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitor;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class JavaRuleChainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        JavaParserVisitor javaParserVistor = new JavaParserVisitorAdapter() {
            // Perform a visitation of the AST to index nodes which need
            // visiting by type
            @Override
            public Object visit(JavaNode node, Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };

        for (final Node node : nodes) {
            javaParserVistor.visit((ASTCompilationUnit) node, ctx);
        }
    }

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        // Rule better either be a JavaParserVisitor, or a XPathRule
        if (rule instanceof XPathRule) {
            ((XPathRule) rule).evaluate(node, ctx);
        } else {
            ((JavaNode) node).jjtAccept((JavaParserVisitor) rule, ctx);
        }
    }
}
