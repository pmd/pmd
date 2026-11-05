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
 * @deprecated since 7.27.0. Use UnusedReturnValueRule instead.
 */
@Deprecated
public class UselessPureMethodCallRule extends AbstractJavaRulechainRule {

    public UselessPureMethodCallRule() {
        super(ASTExpressionStatement.class);
    }

    @Override
    public Object visit(ASTExpressionStatement node, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (node.getExpr() instanceof ASTMethodCall) {
            ASTMethodCall methodCall = (ASTMethodCall) node.getExpr();
            if (JavaRuleUtil.isKnownPure(methodCall)) {
                ctx.addViolation(node, methodCall.getMethodName());
            }
        }
        return null;
    }
}
