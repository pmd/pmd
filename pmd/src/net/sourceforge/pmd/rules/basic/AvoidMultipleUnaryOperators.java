package net.sourceforge.pmd.rules.basic;

import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTUnaryExpression;
import net.sourceforge.pmd.ast.ASTUnaryExpressionNotPlusMinus;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

public class AvoidMultipleUnaryOperators extends AbstractJavaRule {

	public AvoidMultipleUnaryOperators() {
		super.addRuleChainVisit("UnaryExpression");
		super.addRuleChainVisit("UnaryExpressionNotPlusMinus");
	}

	@Override
	public Object visit(ASTUnaryExpression node, Object data) {
		checkUnaryDescendent(node, data);
		return data;
	}

	@Override
	public Object visit(ASTUnaryExpressionNotPlusMinus node, Object data) {
		checkUnaryDescendent(node, data);
		return data;
	}

	private void checkUnaryDescendent(SimpleNode node, Object data) {
		boolean match = false;
		if (node.jjtGetNumChildren() == 1) {
			Node child = node.jjtGetChild(0);
			if (child instanceof ASTUnaryExpression || child instanceof ASTUnaryExpressionNotPlusMinus) {
				match = true;
			} else if (child instanceof ASTPrimaryExpression) {
				Node primaryExpression = child;
				// Skip down PrimaryExpression/PrimaryPrefix/Expression chains created by parentheses
				while (true) {
					if (primaryExpression.jjtGetNumChildren() == 1
							&& primaryExpression.jjtGetChild(0) instanceof ASTPrimaryPrefix
							&& primaryExpression.jjtGetChild(0).jjtGetNumChildren() == 1
							&& primaryExpression.jjtGetChild(0).jjtGetChild(0) instanceof ASTExpression
							&& primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetNumChildren() == 1) {
						Node candidate = primaryExpression.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0);
						if (candidate instanceof ASTUnaryExpression
								|| candidate instanceof ASTUnaryExpressionNotPlusMinus) {
							match = true;
							break;
						} else if (candidate instanceof ASTPrimaryExpression) {
							primaryExpression = candidate;
							continue;
						} else {
							break;
						}
					} else {
						break;
					}
				}
			}
		}

		if (match) {
			addViolation(data, node);
		}
	}
}
