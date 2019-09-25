/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.scala.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;
import net.sourceforge.pmd.lang.scala.ast.ASTSource;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitorAdapter;

/**
 * A Rule Chain visitor for Scala.
 */
public class ScalaRuleChainVisitor extends AbstractRuleChainVisitor {

    @SuppressWarnings("unchecked")
    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        // Rule better either be a ScalaParserVisitor, or a XPathRule
        if (rule instanceof XPathRule) {
            ((XPathRule) rule).evaluate(node, ctx);
        } else {
            ((ScalaNode<?>) node).accept((ScalaParserVisitorAdapter<RuleContext, ?>) rule, ctx);
        }
    }

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        ScalaParserVisitor<RuleContext, Void> visitor = new ScalaParserVisitorAdapter<RuleContext, Void>() {
            @Override
            public Void visit(ScalaNode<?> node, RuleContext data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };
        for (final Node node : nodes) {
            visitor.visit((ASTSource) node, ctx);
        }
    }

}
