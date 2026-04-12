/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit3Class;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class JUnitStaticSuiteRule extends AbstractJavaRulechainRule {

    public JUnitStaticSuiteRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public RuleContext visit(ASTClassDeclaration node, RuleContext data) {
        if (isJUnit3Class(node)) {
            ASTMethodDeclaration suiteMethod = node.getDeclarations(ASTMethodDeclaration.class)
                                                   .filter(it -> "suite".equals(it.getName()) && it.getArity() == 0)
                                                   .first();
            if (suiteMethod != null
                && (suiteMethod.getVisibility() != Visibility.V_PUBLIC || !suiteMethod.isStatic())) {
                data.addViolation(suiteMethod);
            }
        }
        return null;
    }
}
