/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTList;
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
import net.sourceforge.pmd.lang.java.types.InvocationMatcher.CompoundInvocationMatcher;

public class PreserveStackTraceRule extends AbstractJavaRulechainRule {
    // todo dfa

    private static final InvocationMatcher INIT_CAUSE = InvocationMatcher.parse("java.lang.Throwable#initCause(_)");
    private static final CompoundInvocationMatcher ALLOWED_GETTERS = InvocationMatcher.parseAll(
        "java.lang.Throwable#fillInStackTrace()", // returns this
        "java.lang.reflect.InvocationTargetException#getTargetException()", // allowed, to unwrap reflection frames
        "java.lang.reflect.InvocationTargetException#getCause()", // this is equivalent to getTargetException, see javadoc
        // same rationale as for InvocationTargetException
        "java.security.PrivilegedActionException#getException()",
        "java.security.PrivilegedActionException#getCause()"
    );

    private final Set<ASTVariableDeclaratorId> recursingOnVars = new HashSet<>();

    public PreserveStackTraceRule() {
        super(ASTCatchClause.class);
    }

    @Override
    public Object visit(ASTCatchClause catchStmt, Object data) {
        ASTVariableDeclaratorId exceptionParam = catchStmt.getParameter().getVarId();
        if (JavaRuleUtil.isExplicitUnusedVarName(exceptionParam.getName())) {
            // ignore those
            return null;
        }

        // Inspect all the throw stmt inside the catch stmt
        for (ASTThrowStatement throwStatement : catchStmt.getBody().descendants(ASTThrowStatement.class)) {
            ASTExpression thrownExpr = throwStatement.getExpr();

            if (!exprConsumesException(exceptionParam, thrownExpr, true)) {
                addViolation(data, thrownExpr, exceptionParam.getName());
            }
        }
        recursingOnVars.clear();
        return null;
    }

    private boolean exprConsumesException(ASTVariableDeclaratorId exceptionParam, ASTExpression expr, boolean mayBeSelf) {
        if (expr instanceof ASTConstructorCall) {
            // new Exception(e)
            return ctorConsumesException(exceptionParam, (ASTConstructorCall) expr);

        } else if (expr instanceof ASTMethodCall) {

            return methodConsumesException(exceptionParam, (ASTMethodCall) expr);

        } else if (expr instanceof ASTCastExpression) {

            ASTExpression innermost = JavaRuleUtil.peelCasts(expr);
            return exprConsumesException(exceptionParam, innermost, mayBeSelf);

        } else if (expr instanceof ASTConditionalExpression) {

            ASTConditionalExpression ternary = (ASTConditionalExpression) expr;
            return exprConsumesException(exceptionParam, ternary.getThenBranch(), mayBeSelf)
                && exprConsumesException(exceptionParam, ternary.getElseBranch(), mayBeSelf);

        } else if (expr instanceof ASTVariableAccess) {
            JVariableSymbol referencedSym = ((ASTVariableAccess) expr).getReferencedSym();
            if (referencedSym == null) {
                return true; // invalid code, avoid FP
            }
            ASTVariableDeclaratorId decl = referencedSym.tryGetNode();

            if (decl == exceptionParam) {
                return mayBeSelf;
            } else if (decl == null || decl.isFormalParameter() || decl.isField()) {
                return false;
            }

            if (!this.recursingOnVars.add(decl)) {
                // already recursing on this variable, avoid stackoverflow
                return false;
            }

            // if any of the initializer and usages consumes the variable,
            // answer true.

            if (exprConsumesException(exceptionParam, decl.getInitializer(), mayBeSelf)) {
                return true;
            }

            for (ASTNamedReferenceExpr usage : decl.getLocalUsages()) {
                if (assignmentRhsConsumesException(exceptionParam, decl, usage)) {
                    return true;
                }

                if (JavaRuleUtil.followingCallChain(usage).any(it -> consumesExceptionNonRecursive(exceptionParam, it))) {
                    return true;
                }
            }

            return false;
        } else {
            // assume it doesn't
            return false;
        }
    }

    private boolean assignmentRhsConsumesException(ASTVariableDeclaratorId exceptionParam, ASTVariableDeclaratorId lhsVariable, ASTNamedReferenceExpr usage) {
        if (usage.getIndexInParent() == 0) {
            ASTExpression assignmentRhs = JavaRuleUtil.getOtherOperandIfInAssignmentExpr(usage);
            boolean rhsIsSelfReferential =
                NodeStream.of(assignmentRhs)
                          .descendantsOrSelf()
                          .filterIs(ASTVariableAccess.class)
                          .any(it -> JavaRuleUtil.isReferenceToVar(it, lhsVariable.getSymbol()));
            return !rhsIsSelfReferential && exprConsumesException(exceptionParam, assignmentRhs, true);
        }
        return false;
    }

    private boolean ctorConsumesException(ASTVariableDeclaratorId exceptionParam, ASTConstructorCall ctorCall) {
        return ctorCall.isAnonymousClass() && callsInitCauseInAnonInitializer(exceptionParam, ctorCall)
            || anArgumentConsumesException(exceptionParam, ctorCall);
    }

    private boolean consumesExceptionNonRecursive(ASTVariableDeclaratorId exceptionParam, ASTExpression expr) {
        if (expr instanceof ASTConstructorCall) {
            return ctorConsumesException(exceptionParam, (ASTConstructorCall) expr);
        }
        return expr instanceof InvocationNode && anArgumentConsumesException(exceptionParam, (InvocationNode) expr);
    }

    private boolean methodConsumesException(ASTVariableDeclaratorId exceptionParam, ASTMethodCall call) {
        if (anArgumentConsumesException(exceptionParam, call)) {
            return true;
        }
        ASTExpression qualifier = call.getQualifier();
        if (qualifier == null) {
            return false;
        }
        boolean mayBeSelf = ALLOWED_GETTERS.anyMatch(call);
        return exprConsumesException(exceptionParam, qualifier, mayBeSelf);
    }

    private boolean callsInitCauseInAnonInitializer(ASTVariableDeclaratorId exceptionParam, ASTConstructorCall ctorCall) {
        return NodeStream.of(ctorCall.getAnonymousClassDeclaration())
                         .flatMap(ASTAnyTypeDeclaration::getDeclarations)
                         .map(NodeStream.asInstanceOf(ASTFieldDeclaration.class, ASTInitializer.class))
                         .descendants().filterIs(ASTMethodCall.class)
                         .any(it -> isInitCauseWithTargetInArg(exceptionParam, it));
    }

    private boolean isInitCauseWithTargetInArg(ASTVariableDeclaratorId exceptionSym, JavaNode expr) {
        if (INIT_CAUSE.matchesCall(expr)) {
            return anArgumentConsumesException(exceptionSym, (ASTMethodCall) expr);
        }
        return false;
    }

    private boolean anArgumentConsumesException(@NonNull ASTVariableDeclaratorId exceptionParam, InvocationNode thrownExpr) {
        for (ASTExpression arg : ASTList.orEmptyStream(thrownExpr.getArguments())) {
            if (exprConsumesException(exceptionParam, arg, true)) {
                return true;
            }
        }
        return false;
    }

}
