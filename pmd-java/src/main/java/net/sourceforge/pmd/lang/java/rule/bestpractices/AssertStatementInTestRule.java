/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTAssertStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.25.0
 */
public class AssertStatementInTestRule extends AbstractJavaRulechainRule {

    public AssertStatementInTestRule() {
        super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (TestFrameworksUtil.isTestMethod(method)
                || TestFrameworksUtil.isTestConfigurationMethod(method)) {
            method.descendants(ASTAssertStatement.class).forEach(
                node -> ctx.addViolation(node));
        }
        return null;
    }
}
