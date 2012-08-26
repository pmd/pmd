package net.sourceforge.pmd.lang.plsql.rule;

import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTinput;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.rule.AbstractRuleChainVisitor;
import net.sourceforge.pmd.lang.rule.XPathRule;

public class PLSQLRuleChainVisitor extends AbstractRuleChainVisitor {

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
			plsqlParserVistor.visit((ASTinput)nodes.get(i), ctx);
		}
	}

	protected void visit(Rule rule, Node node, RuleContext ctx) {
		// Rule better either be a PLSQLParserVisitor, or a XPathRule
		if (rule instanceof XPathRule) {
			((XPathRule)rule).evaluate(node, ctx);
		} else {
			((SimpleNode)node).jjtAccept((PLSQLParserVisitor)rule, ctx);
		}
	}
}
