/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class PLSQLRuleChainVisitor extends AbstractRuleChainVisitor {
    private static final Logger LOGGER = Logger.getLogger(PLSQLRuleChainVisitor.class.getName());
    private static final String CLASS_NAME = PLSQLRuleChainVisitor.class.getName();

    @Override
    protected void indexNodes(List<Node> nodes, RuleContext ctx) {
        LOGGER.entering(CLASS_NAME, "indexNodes");
        PLSQLParserVisitor plsqlParserVistor = new PLSQLParserVisitorAdapter() {
            // Perform a visitation of the AST to index nodes which need
            // visiting by type
            @Override
            public Object visit(PLSQLNode node, Object data) {
                indexNode(node);
                return super.visit(node, data);
            }
        };

        for (int i = 0; i < nodes.size(); i++) {
            plsqlParserVistor.visit((ASTInput) nodes.get(i), ctx);
        }
        LOGGER.exiting(CLASS_NAME, "indexNodes");
    }

    @Override
    protected void visit(Rule rule, Node node, RuleContext ctx) {
        LOGGER.entering(CLASS_NAME, "visit");
        // Rule better either be a PLSQLParserVisitor, or a XPathRule#
        if (LOGGER.isLoggable(Level.FINE)) {
            LOGGER.fine("Rule=" + rule);
            LOGGER.fine("Node=" + node);
            LOGGER.fine("RuleContext=" + ctx);
            LOGGER.fine("Rule Classname=" + rule.getClass().getCanonicalName());
            LOGGER.fine("Rule Name=" + rule.getName());
        }
        if (rule instanceof XPathRule) {
            ((XPathRule) rule).evaluate(node, ctx);
        } else {
            ((PLSQLNode) node).jjtAccept((PLSQLParserVisitor) rule, ctx);
        }
        LOGGER.exiting(CLASS_NAME, "visit");
    }
}
