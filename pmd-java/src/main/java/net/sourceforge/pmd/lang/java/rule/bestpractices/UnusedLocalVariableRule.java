/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPatternList;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class UnusedLocalVariableRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTLocalVariableDeclaration.class, ASTTypePattern.class);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration decl, Object data) {
        for (ASTVariableId varId : decl.getVarIds()) {
            if (JavaAstUtils.isNeverUsed(varId)
                && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())) {
                asCtx(data).addViolation(varId, varId.getName());
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTTypePattern pattern, Object data) {
        ASTVariableId varId = pattern.getVarId();
        if (JavaAstUtils.isNeverUsed(varId)
                && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())
                && !neededForSwitchOrRcord(pattern)) {
            asCtx(data).addViolation(varId, varId.getName());
        }
        return data;
    }

    private boolean neededForSwitchOrRcord(ASTTypePattern pattern) {
        JavaNode parent = pattern.getParent();
        return (parent instanceof ASTSwitchLabel || parent instanceof ASTPatternList)
            && pattern.getLanguageVersion().compareToVersion("22") < 0;
    }

}
