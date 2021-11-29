/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResource;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;

public class UnusedLocalVariableRule extends AbstractJavaRulechainRule {

    public UnusedLocalVariableRule() {
        super(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration decl, Object data) {
        for (ASTVariableDeclaratorId varId : decl.getVarIds()) {
            if (JavaRuleUtil.isNeverUsed(varId)
                && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())
                && !(varId.ancestors().get(2) instanceof ASTResource)) {
                addViolation(data, varId, varId.getName());
            }
        }
        return data;
    }

}
