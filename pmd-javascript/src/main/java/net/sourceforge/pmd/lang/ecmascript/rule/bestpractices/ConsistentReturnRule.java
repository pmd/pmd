/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ecmascript.ast.ASTFunctionNode;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class ConsistentReturnRule extends AbstractEcmascriptRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTFunctionNode.class);
    }

    @Override
    public Object visit(ASTFunctionNode functionNode, Object data) {
        Boolean hasResult = null;
        for (ASTReturnStatement returnStatement : functionNode.descendants(ASTReturnStatement.class)) {
            // Return for this function?
            if (functionNode == returnStatement.ancestors(ASTFunctionNode.class).first()) {
                if (hasResult == null) {
                    hasResult = returnStatement.hasResult();
                } else {
                    // Return has different result from previous return?
                    if (hasResult != returnStatement.hasResult()) {
                        asCtx(data).addViolation(functionNode);
                        break;
                    }
                }
            }
        }
        return data;
    }
}
