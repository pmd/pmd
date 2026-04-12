/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class AvoidReassigningParametersRule extends AbstractJavaRulechainRule {

    public AvoidReassigningParametersRule() {
        super(ASTMethodDeclaration.class, ASTConstructorDeclaration.class);
    }

    @Override
    public RuleContext visit(ASTMethodDeclaration node, RuleContext data) {
        lookForViolations(node, data);
        return data;
    }


    @Override
    public RuleContext visit(ASTConstructorDeclaration node, RuleContext data) {
        lookForViolations(node, data);
        return data;
    }

    private void lookForViolations(ASTExecutableDeclaration node, RuleContext data) {
        for (ASTFormalParameter formal : node.getFormalParameters()) {
            ASTVariableId varId = formal.getVarId();
            for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                if (usage.getAccessType() == AccessType.WRITE) {
                    data.addViolation(usage, varId.getName());
                    // only the first assignment should be reported
                    break;
                }
            }
        }
    }

}
