/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class AvoidReassigningParametersRule extends AbstractJavaRulechainRule {

    public AvoidReassigningParametersRule() {
        super(ASTMethodDeclaration.class, ASTConstructorDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        lookForViolations(node, data);
        return data;
    }


    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        lookForViolations(node, data);
        return data;
    }

    private void lookForViolations(ASTMethodOrConstructorDeclaration node, Object data) {
        for (ASTFormalParameter formal : node.getFormalParameters()) {
            ASTVariableDeclaratorId varId = formal.getVarId();
            for (ASTNamedReferenceExpr usage : varId.getLocalUsages()) {
                if (usage.getAccessType() == AccessType.WRITE) {
                    addViolation(data, usage, varId.getName());
                }
            }
        }
    }

}
