/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ReturnScopeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Rule that verifies, that the return values of next(), previous(), first(), last()
 * calls to a java.sql.ResultSet are actually verified.
 */
public class CheckResultSetRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher.CompoundInvocationMatcher RESULTSET_CHANGE_POSITION_METHODS = InvocationMatcher.parseAll(
            "java.sql.ResultSet#next()",
            "java.sql.ResultSet#previous()",
            "java.sql.ResultSet#first()",
            "java.sql.ResultSet#last()"
    );
    private static final InvocationMatcher MOCKITO_VERIFY = InvocationMatcher.parse("org.mockito.Mockito#verify(_*)");

    public CheckResultSetRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public RuleContext visit(ASTMethodCall call, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (shouldCheckResult(call) && !isResultUsed(call)) {
            ctx.addViolation(call);
        }

        return ctx;
    }

    private boolean shouldCheckResult(ASTMethodCall call) {
        return RESULTSET_CHANGE_POSITION_METHODS.anyMatch(call)
                && !MOCKITO_VERIFY.matchesCall(call.getQualifier());
    }

    private boolean isResultUsed(ASTMethodCall call) {
        if (call.getParent() instanceof ASTExpressionStatement) {
            // the result is not used at all.
            return false;
        }

        final ASTVariableDeclarator variableDeclarator = call.ancestors()
                .takeWhile(n -> !(n instanceof ReturnScopeNode))
                .first(ASTVariableDeclarator.class);

        if (variableDeclarator == null) {
            // the result is not assigned to a variable, so it is used directly.
            return true;
        }

        final List<ASTAssignableExpr.ASTNamedReferenceExpr> usages = variableDeclarator.getVarId().getLocalUsages();
        // check that the first usage of the variable is a read access, which means that the result of the method call is used.
        return !usages.isEmpty() && usages.get(0).getAccessType() == ASTAssignableExpr.AccessType.READ;
    }
}
