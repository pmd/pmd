/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

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
        for (ASTFormalParameter param : node.getFormalParameters()) {
            ASTVariableId varId = param.getVarId();
            if (!param.isFinal()
                && !JavaAstUtils.isNeverUsed(varId)
                && JavaAstUtils.isEffectivelyFinal(varId)) {
                asCtx(data).addViolation(varId, varId.getName());
            }
        }
    }

}
