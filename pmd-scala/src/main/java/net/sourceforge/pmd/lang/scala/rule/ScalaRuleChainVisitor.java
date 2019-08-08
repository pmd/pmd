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
import net.sourceforge.pmd.lang.scala.ast.ASTSourceNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaNode;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitor;
import net.sourceforge.pmd.lang.scala.ast.ScalaParserVisitorAdapter;
import net.sourceforge.pmd.lang.scala.ast.ScalaWrapperNode;

/**
 * A Rule Chain visitor for Scala.
 */
public class ScalaRuleChainVisitor extends AbstractRuleChainVisitor {

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        // Rule better either be a JavaParserVisitor, or a XPathRule
        if (rule instanceof XPathRule) {
            ((XPathRule) rule).evaluate(node, ctx);
        } else {
            ((ScalaWrapperNode) node).accept((ScalaParserVisitorAdapter) rule, ctx);
        }
    }

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        ScalaParserVisitor visitor = new ScalaParserVisitorAdapter() {
            @Override
            public Object visit(ScalaNode node, Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };
        for (final Node node : nodes) {
            visitor.visit((ASTSourceNode) node, ctx);
        }
    }

}
