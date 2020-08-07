/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class AvoidReassigningCatchVariablesRule extends AbstractJavaRule {

    public AvoidReassigningCatchVariablesRule() {
        addRuleChainVisit(ASTCatchClause.class);
    }

    @Override
    public Object visit(ASTCatchClause catchStatement, Object data) {
        ASTVariableDeclaratorId caughtExceptionId = catchStatement.getParameter().getVarId();
        String caughtExceptionVar = caughtExceptionId.getName();
        for (NameOccurrence usage : caughtExceptionId.oldGetUsages()) {
            JavaNode operation = getOperationOfUsage(usage);
            if (isAssignment(operation)) {
                String assignedVar = getAssignedVariableName(operation);
                if (caughtExceptionVar.equals(assignedVar)) {
                    addViolation(data, operation, caughtExceptionVar);
                }
            }
        }
        return data;
    }

    private JavaNode getOperationOfUsage(NameOccurrence usage) {
        return usage.getLocation()
                .getFirstParentOfType(ASTPrimaryExpression.class)
                .getParent();
    }

    private boolean isAssignment(JavaNode operation) {
        return operation.hasDescendantOfType(ASTAssignmentOperator.class);
    }

    private String getAssignedVariableName(JavaNode operation) {
        return operation.getFirstDescendantOfType(ASTName.class).getImage();
    }
}
