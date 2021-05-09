/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 * @author Unknown,
 * @author Romain PELISSE, belaran@gmail.com, fix for bug 1808110
 */
public class PreserveStackTraceRule extends AbstractJavaRulechainRule {

    private static final InvocationMatcher INIT_CAUSE = InvocationMatcher.parse("java.lang.Throwable#initCause(_)");
    private static final InvocationMatcher FILL_IN_STACKTRACE = InvocationMatcher.parse("java.lang.Throwable#fillInStackTrace()");

    public PreserveStackTraceRule() {
        super(ASTCatchClause.class);
    }

    @Override
    public Object visit(ASTCatchClause catchStmt, Object data) {
        ASTVariableDeclaratorId exceptionParam = catchStmt.getParameter().getVarId();

        // Inspect all the throw stmt inside the catch stmt
        for (ASTThrowStatement throwStatement : catchStmt.getBody().descendants(ASTThrowStatement.class)) {
            ASTExpression thrownExpr = throwStatement.getExpr();

            if (!exprConsumesException(exceptionParam, thrownExpr, true)) {
                addViolation(data, thrownExpr);
            }
        }
        return null;
    }

    private static boolean exprConsumesException(ASTVariableDeclaratorId exceptionParam, ASTExpression expr, boolean mayBeSelf) {
        if (expr instanceof ASTConstructorCall) {
            // new Exception(e)
            return isCtorOk(exceptionParam, (ASTConstructorCall) expr);

        } else if (expr instanceof ASTMethodCall) {

            return isMethodOk(exceptionParam, (ASTMethodCall) expr);

        } else if (expr instanceof ASTCastExpression) {

            ASTExpression innermost = JavaRuleUtil.peelCasts(expr);
            return exprConsumesException(exceptionParam, innermost, mayBeSelf);

        } else if (expr instanceof ASTVariableAccess) {
            // fixme sloppy, should be reaching definition not necessarily initializer.
            //  For now we're assuming the var is effectively final.
            ASTVariableDeclaratorId decl = ((ASTVariableAccess) expr).getReferencedSym().tryGetNode();

            if (decl == null) {
                return false;
            } else if (decl == exceptionParam) {
                return mayBeSelf;
            } else if (decl.getInitializer() != null && exprConsumesException(exceptionParam, decl.getInitializer(), mayBeSelf)) {
                return true;
            }

            for (ASTNamedReferenceExpr usage : decl.getLocalUsages()) {
                if (JavaRuleUtil.followingCallChain(usage).any(it -> consumesExceptionNonRecursive(exceptionParam, it))) {
                    return true;
                }
            }

            return false;
        } else {
            // we don't know
            return true;
        }
    }

    private static boolean isCtorOk(ASTVariableDeclaratorId exceptionParam, ASTConstructorCall ctorCall) {
        return ctorCall.isAnonymousClass() && callsInitCauseInAnonInitializer(exceptionParam, ctorCall)
            || hasReferenceAsArgument(ctorCall, exceptionParam);
    }

    private static boolean consumesExceptionNonRecursive(ASTVariableDeclaratorId exceptionParam, ASTExpression expr) {
        if (expr instanceof ASTConstructorCall) {
            ASTConstructorCall ctorCall = (ASTConstructorCall) expr;
            if (ctorCall.isAnonymousClass() && callsInitCauseInAnonInitializer(exceptionParam, ctorCall)) {
                return true;
            }
        }
        return expr instanceof InvocationNode && hasReferenceAsArgument((InvocationNode) expr, exceptionParam);
    }

    private static boolean isMethodOk(ASTVariableDeclaratorId exceptionParam, ASTMethodCall call) {
        if (hasReferenceAsArgument(call, exceptionParam)) {
            return true;
        }
        ASTExpression qualifier = call.getQualifier();
        if (qualifier == null) {
            return false;
        }
        boolean mayBeSelf = FILL_IN_STACKTRACE.matchesCall(call);
        return exprConsumesException(exceptionParam, qualifier, mayBeSelf);
    }

    private static boolean callsInitCauseInAnonInitializer(ASTVariableDeclaratorId exceptionParam, ASTConstructorCall ctorCall) {
        return ctorCall.getAnonymousClassDeclaration().getDeclarations().map(NodeStream.asInstanceOf(ASTFieldDeclaration.class, ASTInitializer.class))
                       .descendants().filterIs(ASTMethodCall.class)
                       .any(it -> isInitCauseWithTargetInArg(exceptionParam, it));
    }

    private static boolean isInitCauseWithTargetInArg(ASTVariableDeclaratorId exceptionSym, JavaNode expr) {
        if (INIT_CAUSE.matchesCall(expr)) {
            ASTMethodCall initCauseCall = (ASTMethodCall) expr;
            return hasReferenceAsArgument(initCauseCall, exceptionSym);
        }
        return false;
    }

    private static boolean hasReferenceAsArgument(InvocationNode thrownExpr, @NonNull ASTVariableDeclaratorId toFind) {
        for (ASTExpression arg : thrownExpr.getArguments()) {
            if (JavaRuleUtil.isReferenceToVar(arg, toFind.getSymbol())) {
                return true;
            }
        }
        return false;
    }

}
