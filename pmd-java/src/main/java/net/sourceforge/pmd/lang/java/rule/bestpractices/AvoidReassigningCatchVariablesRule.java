/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTCatchParameter;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class AvoidReassigningCatchVariablesRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTCatchParameter.class);
    }

    @Override
    public Object visit(ASTCatchParameter catchParam, Object data) {
        ASTVariableDeclaratorId caughtExceptionId = catchParam.getVarId();
        for (ASTNamedReferenceExpr usage : caughtExceptionId.getUsages()) {
            if (usage.getAccessType() == AccessType.WRITE) {
                addViolation(data, usage, caughtExceptionId.getName());
            }

        }
        return data;
    }
}
