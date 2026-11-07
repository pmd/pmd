/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @deprecated since 7.27.0. Use UnusedReturnValueRule instead.
 */
@Deprecated
public class CheckSkipResultRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher SKIP_METHOD = InvocationMatcher.parse("java.io.InputStream#skip(long)");

    public CheckSkipResultRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        RuleContext ctx = (RuleContext) data;
        if (SKIP_METHOD.matchesCall(call) && !isResultUsed(call)) {
            ctx.addViolation(call);
        }
        return null;
    }

    private boolean isResultUsed(ASTMethodCall call) {
        return !(call.getParent() instanceof ASTExpressionStatement);
    }
}
