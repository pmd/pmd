/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.basic;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.apex.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

public class ConsistentReturnRule extends AbstractApexRule {

    public ConsistentReturnRule() {
	addRuleChainVisit(ASTFunctionNode.class);
    }

    @Override
    public Object visit(ASTFunctionNode functionNode, Object data) {
	List<ASTReturnStatement> returnStatements = functionNode.findDescendantsOfType(ASTReturnStatement.class);
	Boolean hasResult = null;
	for (ASTReturnStatement returnStatement : returnStatements) {
	    // Return for this function?
	    if (functionNode == returnStatement.getFirstParentOfType(ASTFunctionNode.class)) {
		if (hasResult == null) {
		    hasResult = Boolean.valueOf(returnStatement.hasResult());
		} else {
		    // Return has different result from previous return?
		    if (hasResult.booleanValue() != returnStatement.hasResult()) {
			addViolation(data, functionNode);
			break;
		    }
		}
	    }
	}
	return data;
    }
}
