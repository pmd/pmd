/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit3Class;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

public class JUnitStaticSuiteRule extends AbstractJavaRulechainRule {

    public JUnitStaticSuiteRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        if (isJUnit3Class(node)) {
            ASTMethodDeclaration suiteMethod = node.getDeclarations(ASTMethodDeclaration.class)
                                                   .filter(it -> "suite".equals(it.getName()) && it.getArity() == 0)
                                                   .first();
            if (suiteMethod != null
                && (suiteMethod.getVisibility() != Visibility.V_PUBLIC || !suiteMethod.isStatic())) {
                asCtx(data).addViolation(suiteMethod);
            }
        }
        return null;
    }
}
