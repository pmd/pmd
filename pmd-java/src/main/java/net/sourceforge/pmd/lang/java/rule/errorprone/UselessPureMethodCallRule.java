/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Reports usages of pure methods where the result is ignored.
 *
 * @since 7.17.0
 */
public class UselessPureMethodCallRule extends AbstractJavaRulechainRule {

    public UselessPureMethodCallRule() {
        super(ASTExpressionStatement.class);
    }

    @Override
    public RuleContext visit(ASTExpressionStatement node, RuleContext data) {
        if (node.getExpr() instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) node.getExpr();
            if (JavaRuleUtil.isKnownPure(methodCall)) {
                data.addViolation(node, methodCall.getMethodName());
            }
        }
        return null;
    }
}
