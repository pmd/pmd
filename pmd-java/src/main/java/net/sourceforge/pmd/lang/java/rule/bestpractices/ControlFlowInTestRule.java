/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.29.0
 */
public class ControlFlowInTestRule extends AbstractJavaRulechainRule {

    public ControlFlowInTestRule() {
        // Inspect method declarations only.
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (method.getBody() == null
                || !TestFrameworksUtil.isTestMethod(method)) {
            return null;
        }

        RuleContext context = (RuleContext) data;

        // Report each conditional at its own location.
        method.getBody().descendants(ASTIfStatement.class)
                .forEach(context::addViolation);

        return null;
    }
}
