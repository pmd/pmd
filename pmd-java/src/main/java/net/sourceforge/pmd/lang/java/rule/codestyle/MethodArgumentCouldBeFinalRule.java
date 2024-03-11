/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class MethodArgumentCouldBeFinalRule extends AbstractJavaRulechainRule {

    public MethodArgumentCouldBeFinalRule() {
        super(ASTExecutableDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration meth, Object data) {
        if (meth.getBody() == null) {
            return data;
        }
        lookForViolation(meth, data);
        return data;
    }

    @Override
    public Object visit(ASTConstructorDeclaration constructor, Object data) {
        lookForViolation(constructor, data);
        return data;
    }

    private void lookForViolation(ASTExecutableDeclaration node, Object data) {
        checkForFinal((RuleContext) data, node.getFormalParameters().toStream().map(ASTFormalParameter::getVarId));
    }

    static void checkForFinal(RuleContext ruleContext, NodeStream<ASTVariableId> variables) {
        outer:
        for (ASTVariableId var : variables) {
            if (var.isFinal()) {
                continue;
            }
            boolean used = false;
            for (ASTNamedReferenceExpr usage : var.getLocalUsages()) {
                used = true;
                if (usage.getAccessType() == AccessType.WRITE) {
                    continue outer;
                }
            }
            if (used) {
                ruleContext.addViolation(var, var.getName());
            }
        }
    }

}
