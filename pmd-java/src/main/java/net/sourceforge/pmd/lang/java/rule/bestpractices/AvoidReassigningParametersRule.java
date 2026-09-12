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
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        lookForViolations(node, ctx);
        return null;
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        lookForViolations(node, ctx);
        return null;
    }

    private void lookForViolations(ASTExecutableDeclaration node, RuleContext ctx) {
        for (ASTFormalParameter formal : node.getFormalParameters()) {
            ASTVariableId varId = formal.getVarId();
            for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                if (usage.getAccessType() == AccessType.WRITE) {
                    ctx.addViolation(usage, varId.getName());
                    // only the first assignment should be reported
                    break;
                }
            }
        }
    }

}
