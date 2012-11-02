package net.sourceforge.pmd.lang.plsql.rule;

import java.util.List;
import java.util.logging.Logger;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class PLSQLRuleChainVisitor extends AbstractRuleChainVisitor {
   private final static Logger LOGGER = Logger.getLogger(PLSQLRuleChainVisitor.class.getName()); 

	protected void indexNodes(List<Node> nodes, RuleContext ctx) {
		PLSQLParserVisitor plsqlParserVistor = new PLSQLParserVisitorAdapter() {
			// Perform a visitation of the AST to index nodes which need
			// visiting by type
			public Object visit(SimpleNode node, Object data) {
				indexNode(node);
				return super.visit(node, data);
			}
		};

		for (int i = 0; i < nodes.size(); i++) {
			plsqlParserVistor.visit((ASTInput)nodes.get(i), ctx);
		}
	}

	protected void visit(Rule rule, Node node, RuleContext ctx) {
		// Rule better either be a PLSQLParserVisitor, or a XPathRule
		LOGGER.finest("Rule="+rule);
		LOGGER.finest("Node="+node);
		LOGGER.finest("RuleContext="+ctx);
		LOGGER.finest("Rule Classname="+rule.getClass().getCanonicalName());
		if (rule instanceof XPathRule) {
			((XPathRule)rule).evaluate(node, ctx);
		} else {
			((SimpleNode)node).jjtAccept((PLSQLParserVisitor)rule, ctx);
		}
	}
}
