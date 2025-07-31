/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchClause;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTPatternExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypePattern;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
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

    private final Set<ASTVariableId> recursingOnVars = new HashSet<>();

    public PreserveStackTraceRule() {
        super(ASTCatchClause.class);
    }

    @Override
    public Object visit(ASTCatchClause catchStmt, Object data) {
        ASTVariableId exceptionParam = catchStmt.getParameter().getVarId();
        if (JavaRuleUtil.isExplicitUnusedVarName(exceptionParam.getName())) {
            // ignore those
            return null;
        }

        // Inspect all the throw stmt inside the catch stmt
        for (ASTThrowStatement throwStatement : catchStmt.getBody().descendants(ASTThrowStatement.class)) {
            ASTExpression thrownExpr = throwStatement.getExpr();

            if (!exprConsumesException(Collections.singleton(exceptionParam), thrownExpr, true)) {
                asCtx(data).addViolation(thrownExpr, exceptionParam.getName());
            }
        }
        recursingOnVars.clear();
        return null;
    }

    private boolean exprConsumesException(Set<ASTVariableId> exceptionParams, ASTExpression expr, boolean mayBeSelf) {
        if (expr instanceof ASTConstructorCall) {
            // new Exception(e)
            return ctorConsumesException(exceptionParams, (ASTConstructorCall) expr);

        } else if (expr instanceof ASTMethodCall) {

            return methodConsumesException(exceptionParams, (ASTMethodCall) expr);

        } else if (expr instanceof ASTCastExpression) {

            ASTExpression innermost = JavaAstUtils.peelCasts(expr);
            return exprConsumesException(exceptionParams, innermost, mayBeSelf);

        } else if (expr instanceof ASTConditionalExpression) {

            ASTConditionalExpression ternary = (ASTConditionalExpression) expr;
            Set<ASTVariableId> possibleExceptionParams = new HashSet<>(exceptionParams);

            // Peel out a type pattern variable in case this conditional is an instanceof pattern
            NodeStream.of(ternary.getCondition())
                    .filterIs(ASTInfixExpression.class)
                    .filterMatching(ASTInfixExpression::getOperator, BinaryOp.INSTANCEOF)
                    .map(ASTInfixExpression::getRightOperand)
                    .filterIs(ASTPatternExpression.class)
                    .map(ASTPatternExpression::getPattern)
                    .filterIs(ASTTypePattern.class)
                    .map(ASTTypePattern::getVarId)
                    .firstOpt()
                    .ifPresent(possibleExceptionParams::add);

            return exprConsumesException(possibleExceptionParams, ternary.getThenBranch(), mayBeSelf)
                && exprConsumesException(possibleExceptionParams, ternary.getElseBranch(), mayBeSelf);

        } else if (expr instanceof ASTVariableAccess) {
            JVariableSymbol referencedSym = ((ASTVariableAccess) expr).getReferencedSym();
            if (referencedSym == null) {
                return true; // invalid code, avoid FP
            }
            ASTVariableId decl = referencedSym.tryGetNode();

            if (exceptionParams.contains(decl)) {
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

            if (exprConsumesException(exceptionParams, decl.getInitializer(), mayBeSelf)) {
                return true;
            }

            for (ASTNamedReferenceExpr usage : decl.getLocalUsages()) {
                if (assignmentRhsConsumesException(exceptionParams, decl, usage)) {
                    return true;
                }

                if (JavaAstUtils.followingCallChain(usage).any(it -> consumesExceptionNonRecursive(exceptionParams, it))) {
                    return true;
                }
            }

            return false;
        } else {
            // assume it doesn't
            return false;
        }
    }

    private boolean assignmentRhsConsumesException(Set<ASTVariableId> exceptionParams, ASTVariableId lhsVariable, ASTNamedReferenceExpr usage) {
        if (usage.getIndexInParent() == 0) {
            ASTExpression assignmentRhs = JavaAstUtils.getOtherOperandIfInAssignmentExpr(usage);
            boolean rhsIsSelfReferential =
                NodeStream.of(assignmentRhs)
                          .descendantsOrSelf()
                          .filterIs(ASTVariableAccess.class)
                          .any(it -> JavaAstUtils.isReferenceToVar(it, lhsVariable.getSymbol()));
            return !rhsIsSelfReferential && exprConsumesException(exceptionParams, assignmentRhs, true);
        }
        return false;
    }

    private boolean ctorConsumesException(Set<ASTVariableId> exceptionParams, ASTConstructorCall ctorCall) {
        return ctorCall.isAnonymousClass() && callsInitCauseInAnonInitializer(exceptionParams, ctorCall)
            || anArgumentConsumesException(exceptionParams, ctorCall);
    }

    private boolean consumesExceptionNonRecursive(Set<ASTVariableId> exceptionParam, ASTExpression expr) {
        if (expr instanceof ASTConstructorCall) {
            return ctorConsumesException(exceptionParam, (ASTConstructorCall) expr);
        }
        return expr instanceof InvocationNode && anArgumentConsumesException(exceptionParam, (InvocationNode) expr);
    }

    private boolean methodConsumesException(Set<ASTVariableId> exceptionParams, ASTMethodCall call) {
        if (anArgumentConsumesException(exceptionParams, call)) {
            return true;
        }
        ASTExpression qualifier = call.getQualifier();
        if (qualifier == null) {
            return false;
        }
        boolean mayBeSelf = ALLOWED_GETTERS.anyMatch(call);
        return exprConsumesException(exceptionParams, qualifier, mayBeSelf);
    }

    private boolean callsInitCauseInAnonInitializer(Set<ASTVariableId> exceptionParams, ASTConstructorCall ctorCall) {
        return NodeStream.of(ctorCall.getAnonymousClassDeclaration())
                         .flatMap(ASTTypeDeclaration::getDeclarations)
                         .map(NodeStream.asInstanceOf(ASTFieldDeclaration.class, ASTInitializer.class))
                         .descendants().filterIs(ASTMethodCall.class)
                         .any(it -> isInitCauseWithTargetInArg(exceptionParams, it));
    }

    private boolean isInitCauseWithTargetInArg(Set<ASTVariableId> exceptionParams, JavaNode expr) {
        return INIT_CAUSE.matchesCall(expr) && anArgumentConsumesException(exceptionParams, (ASTMethodCall) expr);
    }

    private boolean anArgumentConsumesException(Set<ASTVariableId> exceptionParams, InvocationNode thrownExpr) {
        for (ASTExpression arg : ASTList.orEmptyStream(thrownExpr.getArguments())) {
            if (exprConsumesException(exceptionParams, arg, true)) {
                return true;
            }
        }
        return false;
    }

}
