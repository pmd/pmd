/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
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
        ASTVariableDeclaratorId target = catchStmt.getParameter().getVarId();
        JVariableSymbol exceptionSym = target.getSymbol();

        // Inspect all the throw stmt inside the catch stmt
        for (ASTThrowStatement throwStatement : catchStmt.getBody().descendants(ASTThrowStatement.class)) {
            ASTExpression thrownExpr = throwStatement.getExpr();
            if (thrownExpr instanceof ASTConstructorCall) {
                // new Exception(e)
                if (!isCtorOk(exceptionSym, (ASTConstructorCall) thrownExpr)) {
                    addViolation(data, throwStatement);
                }
            } else if (thrownExpr instanceof ASTVariableAccess) {
                // IllegalStateException ex = new IllegalStateException();
                // ex.initCause(e);
                // throw ex;

                // id of the throw var
                ASTVariableDeclaratorId decl = ((ASTVariableAccess) thrownExpr).getReferencedSym().tryGetNode();
                if (decl == null || target == decl) {
                    continue;
                }

                // Exception e = new Exception(target);
                // throw e;
                if (isInitializerOk(exceptionSym, decl)) {
                    continue;
                }

                if (!initCauseWasCalledOnUsages(exceptionSym, decl)) {
                    addViolation(data, throwStatement);
                }
            }

        }
        return null;
    }

    private boolean isCtorOk(JVariableSymbol exceptionSym, ASTConstructorCall ctorCall) {
        return ctorCall.isAnonymousClass() && callsInitCauseInAnonInitializer(exceptionSym, ctorCall)
            || hasReferenceAsArgument(ctorCall, exceptionSym);
    }

    private boolean callsInitCauseInAnonInitializer(JVariableSymbol exceptionSym, ASTConstructorCall ctorCall) {
        return ctorCall.getAnonymousClassDeclaration().getDeclarations().map(NodeStream.asInstanceOf(ASTFieldDeclaration.class, ASTInitializer.class))
                       .descendants().filterIs(ASTMethodCall.class)
                       .any(it -> isInitCauseWithTargetInArg(exceptionSym, it));
    }

    private static boolean isInitializerOk(JVariableSymbol exceptionSym, ASTVariableDeclaratorId decl) {
        ASTExpression initializer = peelCasts(decl.getInitializer());
        if (initializer instanceof ASTConstructorCall) {
            return hasReferenceAsArgument((InvocationNode) initializer, exceptionSym);
        } else if (FILL_IN_STACKTRACE.matchesCall(initializer)) {
            return JavaRuleUtil.isReferenceToVar(((ASTMethodCall) initializer).getQualifier(), exceptionSym);
        }
        return false;
    }

    private static boolean initCauseWasCalledOnUsages(JVariableSymbol exceptionSym, ASTVariableDeclaratorId decl) {
        for (ASTNamedReferenceExpr usage : decl.getLocalUsages()) {
            if (isInitCauseWithTargetInArg(exceptionSym, usage.getParent())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isInitCauseWithTargetInArg(JVariableSymbol exceptionSym, JavaNode expr) {
        if (INIT_CAUSE.matchesCall(expr)) {
            ASTMethodCall initCauseCall = (ASTMethodCall) expr;
            return hasReferenceAsArgument(initCauseCall, exceptionSym);
        }
        return false;
    }

    private static boolean hasReferenceAsArgument(InvocationNode thrownExpr, @NonNull JVariableSymbol toFind) {
        for (ASTExpression arg : thrownExpr.getArguments()) {
            if (JavaRuleUtil.isReferenceToVar(arg, toFind)) {
                return true;
            }
        }
        return false;
    }

    private static ASTExpression peelCasts(@Nullable ASTExpression expr) {
        while (expr instanceof ASTCastExpression) {
            expr = ((ASTCastExpression) expr).getOperand();
        }
        return expr;
    }
}
