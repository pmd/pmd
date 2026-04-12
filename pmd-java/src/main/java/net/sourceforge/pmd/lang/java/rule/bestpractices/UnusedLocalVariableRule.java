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
import net.sourceforge.pmd.reporting.RuleContext;

public class UnusedLocalVariableRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTLocalVariableDeclaration.class, ASTTypePattern.class);
    }

    @Override
    public RuleContext visit(ASTLocalVariableDeclaration decl, RuleContext data) {
        for (ASTVariableId varId : decl.getVarIds()) {
            if (JavaAstUtils.isNeverUsed(varId)
                && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())) {
                data.addViolation(varId, varId.getName());
            }
        }
        return data;
    }

    @Override
    public RuleContext visit(ASTTypePattern pattern, RuleContext data) {
        ASTVariableId varId = pattern.getVarId();
        if (JavaAstUtils.isNeverUsed(varId)
                && !JavaRuleUtil.isExplicitUnusedVarName(varId.getName())
                && !neededForSwitchOrRecord(pattern)) {
            data.addViolation(varId, varId.getName());
        }
        return data;
    }

    private boolean neededForSwitchOrRecord(ASTTypePattern pattern) {
        JavaNode parent = pattern.getParent();
        return (parent instanceof ASTSwitchLabel || parent instanceof ASTPatternList)
            && pattern.getLanguageVersion().compareToVersion("22") < 0;
    }

}
