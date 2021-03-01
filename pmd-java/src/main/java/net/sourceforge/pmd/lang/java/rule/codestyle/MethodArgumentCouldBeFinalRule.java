/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public class MethodArgumentCouldBeFinalRule extends AbstractJavaRulechainRule {

    public MethodArgumentCouldBeFinalRule() {
        super(ASTMethodOrConstructorDeclaration.class);
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

    private void lookForViolation(ASTMethodOrConstructorDeclaration node, Object data) {
        checkForFinal((RuleContext) data, this, node.getFormalParameters().toStream().map(ASTFormalParameter::getVarId));
    }

    static void checkForFinal(RuleContext ruleContext, AbstractRule rule, NodeStream<ASTVariableDeclaratorId> variables) {
        outer:
        for (ASTVariableDeclaratorId var : variables) {
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
                rule.addViolation(ruleContext, var, var.getName());
            }
        }
    }

}
