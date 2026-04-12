/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.reporting.RuleContext;

public class CheckSkipResultRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher SKIP_METHOD = InvocationMatcher.parse("java.io.InputStream#skip(long)");

    public CheckSkipResultRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall call, RuleContext data) {
        if (SKIP_METHOD.matchesCall(call) && !isResultUsed(call)) {
            data.addViolation(call);
        }
        return null;
    }

    private boolean isResultUsed(ASTMethodCall call) {
        return !(call.getParent() instanceof ASTExpressionStatement);
    }
}
