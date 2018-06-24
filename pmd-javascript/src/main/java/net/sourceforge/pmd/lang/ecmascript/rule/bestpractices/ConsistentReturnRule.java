/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;

public class ConsistentReturnRule extends AbstractEcmascriptRule {

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
