/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.basic;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class ConsistentReturnRule extends AbstractApexRule {

	public ConsistentReturnRule() {
		addRuleChainVisit(ASTMethod.class);
	}

	@Override
	public Object visit(ASTMethod methodNode, Object data) {
		List<ASTReturnStatement> returnStatements = methodNode
				.findDescendantsOfType(ASTReturnStatement.class);

		Boolean hasResult = null;

		for (ASTReturnStatement returnStatement : returnStatements) {
			// Return for this function?
			if (methodNode == returnStatement
					.getFirstParentOfType(ASTMethod.class)) {
				if (hasResult == null) {
					hasResult = Boolean
							.valueOf(returnStatement.getNode().isReturnable());
				}
				else {
					// Return has different result from previous return?
					if (hasResult.booleanValue() != returnStatement.getNode()
							.isReturnable()) {
						addViolation(data, methodNode);
						break;
					}
				}
			}
		}
		return data;
	}
}
