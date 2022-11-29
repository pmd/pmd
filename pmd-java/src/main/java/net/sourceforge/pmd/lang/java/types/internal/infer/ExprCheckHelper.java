/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeConversion.capture;
import static net.sourceforge.pmd.lang.java.types.TypeOps.areSameTypesInInference;
import static net.sourceforge.pmd.lang.java.types.TypeOps.asClassType;
import static net.sourceforge.pmd.lang.java.types.TypeOps.findFunctionalInterfaceMethod;
import static net.sourceforge.pmd.lang.java.types.TypeOps.mentionsAny;
import static net.sourceforge.pmd.lang.java.types.TypeOps.nonWildcardParameterization;
import static net.sourceforge.pmd.lang.java.types.internal.infer.ExprOps.methodRefAsInvocation;
import static net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind.EQ;
import static net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind.LOWER;
import static net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind.UPPER;

import java.util.List;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JWildcardType;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.LambdaExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.MethodRefMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;
import net.sourceforge.pmd.util.CollectionUtil;

@SuppressWarnings("PMD.CompareObjectsWithEquals")
final class ExprCheckHelper {

    private final InferenceContext infCtx;
    private final MethodResolutionPhase phase;
    private final ExprChecker checker;
    private final @Nullable MethodCallSite site;
    private final Infer infer;
    private final TypeSystem ts;

    ExprCheckHelper(InferenceContext infCtx,
                    MethodResolutionPhase phase,
                    ExprChecker checker,
                    @Nullable MethodCallSite site,
                    Infer infer) {

        this.infCtx = infCtx;
        this.phase = phase;
        this.checker = checker;
        this.site = site;
        this.infer = infer;
        this.ts = infer.getTypeSystem();
    }

    /**
     * Recurse on all relevant subexpressions to add constraints.
     * E.g when a parameter must be a {@code Supplier<T>},
     * then for the lambda {@code () -> "string"} we add a constraint
     * that {@code T} must be a subtype of string.
     *
     * <p>Instead of gathering expressions explicitly into a bound set,
     * we explore the structure of the expression and:
     * 1. if it's standalone, we immediately add a constraint {@code type(<arg>) <: formalType}
     * 2. otherwise, it depends on the form of the expression
     *  i) if it's a method or constructor invocation:
     *     i.i) we instantiate it. If that method is generic, we add its
     *     inference variables in this context and solve them globally.
     *     i.ii) we add a constraint linking the return type of the invocation and the formal type
     *  ii) if it's a lambda:
     *     ii.i) we ensure that the formalType is a functional interface
     *     ii.ii) we find the lambda's return expressions and add constraints
     *     on each one recursively with this algorithm.
     *
     *
     * @param targetType Target, not necessarily ground
     * @param expr       Expression
     *
     * @return true if it's compatible (or we don't have enough info, and the check is deferred)
     *
     * @throws ResolutionFailedException If the expr is not compatible, and we want to add a message for the reason
     */
    boolean isCompatible(JTypeMirror targetType, ExprMirror expr) {
        final boolean isStandalone;
        {
            JTypeMirror standalone = expr.getStandaloneType();
            if (standalone != null) {
                if (mayMutateExpr()) {
                    expr.setInferredType(standalone);
                    expr.finishStandaloneInference(standalone);
                }
                isStandalone = true;

                // defer check if fi is not ground
                checker.checkExprConstraint(infCtx, standalone, targetType);
                if (!(expr instanceof PolyExprMirror)) {
                    return true;
                }
                // otherwise fallthrough, we potentially need to finish
                // inferring some things on the polys
            } else {
                isStandalone = false;
            }
        }

        if (expr instanceof FunctionalExprMirror) { // those are never standalone
            JClassType funType = getProbablyFunctItfType(targetType, expr);
            if (funType == null) {
                /*
                 * The functional expression has an inference variable as a target type,
                 * and that ivar does not have enough bounds to be resolved to a functional interface type yet.
                 *
                 * <p>This should not prevent ctdecl resolution to proceed. The additional
                 * bounds may be contributed by the invocation constraints of an enclosing
                 * inference process.
                 */
                infer.LOG.functionalExprNeedsInvocationCtx(targetType, expr);
                return true; // deferred to invocation
            }

            if (expr instanceof LambdaExprMirror) {
                LambdaExprMirror lambda = (LambdaExprMirror) expr;
                try {
                    return isLambdaCompatible(funType, lambda);
                } catch (ResolutionFailedException e) {
                    // need to cleanup the partial data
                    if (mayMutateExpr()) {
                        lambda.setInferredType(null);
                        lambda.setFunctionalMethod(null);
                    }

                    if (site != null) {
                        site.maySkipInvocation(false);
                    }
                    throw e;
                }
            } else {
                return isMethodRefCompatible(funType, (MethodRefMirror) expr);
            }

        } else if (expr instanceof InvocationMirror) {
            // then the argument is a poly invoc expression itself
            // in that case we need to infer that as well
            return isInvocationCompatible(targetType, (InvocationMirror) expr, isStandalone);
        } else if (expr instanceof BranchingMirror) {
            return ((BranchingMirror) expr).branchesMatch(it -> isCompatible(targetType, it));
        }

        return false;
    }

    private boolean isInvocationCompatible(JTypeMirror targetType, InvocationMirror invoc, boolean isStandalone) {
        MethodCallSite nestedSite = infer.newCallSite(invoc, targetType, this.site, this.infCtx, isSpecificityCheck());

        MethodCtDecl argCtDecl = infer.determineInvocationTypeOrFail(nestedSite);
        JMethodSig mostSpecific = argCtDecl.getMethodType();

        JTypeMirror actualType = mostSpecific.getReturnType();

        if (argCtDecl == infer.FAILED_INVOCATION) {
            throw ResolutionFailedException.incompatibleFormal(infer.LOG, invoc, ts.ERROR, targetType);
        } else if (argCtDecl == infer.NO_CTDECL) {
            JTypeMirror fallback = invoc.unresolvedType();
            if (fallback != null) {
                actualType = fallback;
            }
            // else it's ts.UNRESOLVED
            if (mayMutateExpr()) {
                invoc.setInferredType(fallback);
                invoc.setCtDecl(infer.NO_CTDECL);
            }
        }

        if (site != null) {
            site.maySkipInvocation(nestedSite.canSkipInvocation());
        }

        // now if the return type of the arg is polymorphic and unsolved,
        // there are some additional bounds on our own infCtx

        if (!isStandalone) {
            // If the expr was standalone, the constraint was already added.
            // We must take care not to duplicate the constraint, because if
            // it's eg checking a wildcard parameterized type, we could have
            // two equality constraints on separate captures of the same wild -> incompatible
            checker.checkExprConstraint(infCtx, actualType, targetType);
        }

        if (!argCtDecl.isFailed() && mayMutateExpr()) {
            infCtx.addInstantiationListener(
                infCtx.freeVarsIn(mostSpecific),
                solved -> {
                    JMethodSig ground = solved.ground(mostSpecific);
                    invoc.setInferredType(ground.getReturnType());
                    invoc.setCtDecl(argCtDecl.withMethod(ground));
                }
            );
        }
        return true;
    }

    private @Nullable JClassType getProbablyFunctItfType(final JTypeMirror targetType, ExprMirror expr) {
        JClassType asClass;
        if (targetType instanceof InferenceVar && site != null) {
            if (site.isInFinalInvocation()) {
                asClass = asClassType(softSolve(targetType)); // null if not funct itf
            } else {
                return null; // defer
            }
        } else {
            asClass = asClassType(targetType);
        }

        if (asClass == null) {
            throw ResolutionFailedException.notAFunctionalInterface(infer.LOG, targetType, expr);
        }
        return asClass;
    }

    // we can't ask the infctx to solve the ivar, as that would require all bounds to be ground
    // We want however to be able to add constraints on the functional interface type's inference variables
    // This is useful to infer lambdas generic in their return type
    // Eg Function<String, R>, where R is not yet instantiated at the time
    // we check the argument, should not be ground: we want the lambda to
    // add a constraint on R according to its return expressions.
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    private @Nullable JTypeMirror softSolve(JTypeMirror t) {
        if (!(t instanceof InferenceVar)) {
            return t;
        }
        InferenceVar ivar = (InferenceVar) t;
        Set<JTypeMirror> bounds = ivar.getBounds(EQ);
        if (bounds.size() == 1) {
            return bounds.iterator().next();
        }
        bounds = ivar.getBounds(LOWER);
        if (!bounds.isEmpty()) {
            JTypeMirror lub = ts.lub(bounds);
            return lub != ivar ? softSolve(lub) : null;
        }
        bounds = ivar.getBounds(UPPER);
        if (!bounds.isEmpty()) {
            JTypeMirror glb = ts.glb(bounds);
            return glb != ivar ? softSolve(glb) : null;
        }
        return null;
    }

    private boolean isMethodRefCompatible(@NonNull JClassType functionalItf, MethodRefMirror mref) {
        // See JLS§18.2.1. Expression Compatibility Constraints

        // A constraint formula of the form ‹MethodReference → T›,
        // where T mentions at least one inference variable, is reduced as follows:

        // If T is not a functional interface type, or if T is a functional interface
        // type that does not have a function type (§9.9), the constraint reduces to false.

        JClassType nonWildcard = nonWildcardParameterization(functionalItf);
        if (nonWildcard == null) {
            throw ResolutionFailedException.notAFunctionalInterface(infer.LOG, functionalItf, mref);
        }

        JMethodSig fun = findFunctionalInterfaceMethod(nonWildcard);
        if (fun == null) {
            throw ResolutionFailedException.notAFunctionalInterface(infer.LOG, functionalItf, mref);
        }

        JMethodSig exactMethod = ExprOps.getExactMethod(mref);
        if (exactMethod != null) {
            //  if the method reference is exact (§15.13.1), then let P1, ..., Pn be the parameter
            //  types of the function type of T, and let F1, ..., Fk be
            //  the parameter types of the potentially applicable method
            List<JTypeMirror> ps = fun.getFormalParameters();
            List<JTypeMirror> fs = exactMethod.getFormalParameters();

            int n = ps.size();
            int k = fs.size();

            if (n == k + 1) {
                // The parameter of type P1 is to act as the target reference of the invocation.
                // The method reference expression necessarily has the form ReferenceType :: [TypeArguments] Identifier.
                // The constraint reduces to ‹P1 <: ReferenceType› and, for all i (2 ≤ i ≤ n), ‹Pi → Fi-1›.
                JTypeMirror lhs = mref.getLhsIfType();
                if (lhs == null) {
                    // then the constraint reduces to false (it may be,
                    // that the candidate is wrong, so that n is wrong ^^)
                    return false;
                }

                // The receiver may not be boxed
                JTypeMirror receiver = ps.get(0);
                if (receiver.isPrimitive()) {
                    throw ResolutionFailedException.cannotInvokeInstanceMethodOnPrimitive(infer.LOG, receiver, mref);
                }
                checker.checkExprConstraint(infCtx, receiver, lhs);

                for (int i = 1; i < n; i++) {
                    checker.checkExprConstraint(infCtx, ps.get(i), fs.get(i - 1));
                }
            } else if (n != k) {
                throw ResolutionFailedException.incompatibleArity(infer.LOG, k, n, mref);
            } else {
                // n == k
                for (int i = 0; i < n; i++) {
                    checker.checkExprConstraint(infCtx, ps.get(i), fs.get(i));
                }
            }

            //  If the function type's result is not void, let R be its
            //  return type.
            //
            JTypeMirror r = fun.getReturnType();
            if (r != ts.NO_TYPE) {
                //  Then, if the result of the potentially applicable
                //  compile-time declaration is void, the constraint reduces to false.
                JTypeMirror r2 = exactMethod.getReturnType();
                if (r2 == ts.NO_TYPE) {
                    return false;
                }

                //  Otherwise, the constraint reduces to ‹R' → R›, where R' is the
                //  result of applying capture conversion (§5.1.10) to the return
                //  type of the potentially applicable compile-time declaration.
                checker.checkExprConstraint(infCtx, capture(r2), r);
            }
            completeMethodRefInference(mref, nonWildcard, fun, exactMethod, true);
        } else {
            // Otherwise, the method reference is inexact, and:

            // (If one or more of the function's formal parameter types
            // is not a proper type, the constraint reduces to false.)

            // This is related to the input variable trickery used
            // to resolve input vars before resolving the constraint:
            // https://docs.oracle.com/javase/specs/jls/se12/html/jls-18.html#jls-18.5.2.2

            // here we defer the check until the variables are ground
            infCtx.addInstantiationListener(
                infCtx.freeVarsIn(fun.getFormalParameters()),
                solvedCtx -> solveInexactMethodRefCompatibility(mref, solvedCtx.ground(nonWildcard), solvedCtx.ground(fun))
            );
        }
        return true;
    }

    private void solveInexactMethodRefCompatibility(MethodRefMirror mref, JClassType nonWildcard, JMethodSig fun) {
        // Otherwise, a search for a compile-time declaration is performed, as specified in §15.13.1.
        @Nullable MethodCtDecl ctdecl0 = infer.exprOps.findInexactMethodRefCompileTimeDecl(mref, fun);

        // If there is no compile-time declaration for the method reference, the constraint reduces to false.
        if (ctdecl0 == null) {
            throw ResolutionFailedException.noCtDeclaration(infer.LOG, fun, mref);
        }

        JMethodSig ctdecl = ctdecl0.getMethodType();

        // Otherwise, there is a compile-time declaration, and: (let R be the result of the function type)
        JTypeMirror r = fun.getReturnType();
        if (r == ts.NO_TYPE) {
            // If R is void, the constraint reduces to true.
            completeMethodRefInference(mref, nonWildcard, fun, ctdecl, false);
            return;
        }

        boolean fixInstantiation = false;

        // Otherwise, if the method reference expression elides TypeArguments, and the compile-time
        // declaration is a generic method, and the return type of the compile-time declaration mentions
        // at least one of the method's type parameters, then:
        if (mref.getExplicitTypeArguments().isEmpty() && ExprOps.isContextDependent(ctdecl)) {

            // If R mentions one of the type parameters of the function type, the constraint reduces to false.
            if (mentionsAny(r, fun.getTypeParameters())) {
                // Rationale from JLS
                // In this case, a constraint in terms of R might lead an inference variable to
                // be bound by an out-of-scope type variable. Since instantiating an inference
                // variable with an out-of-scope type variable is nonsensical, we prefer to
                // avoid the situation by giving up immediately whenever the possibility arises.

                // Apparently javac allows compiling stuff like that. There's a test case in
                // MethodRefInferenceTest, which was found in our codebase.
                // We try one last thing to avoid the possibility of referencing out-of-scope stuff

                if (!TypeOps.haveSameTypeParams(ctdecl, fun)) {
                    // then we really can't do anything
                    throw ResolutionFailedException.unsolvableDependency(infer.LOG);
                } else {
                    fixInstantiation = true;
                }
            }

            // JLS:
            // If R does not mention one of the type parameters of the function type, then the
            // constraint reduces to the bound set B3 which would be used to determine the
            // method reference's compatibility when targeting the return type of the function
            // type, as defined in §18.5.2.1. B3 may contain new inference variables, as well
            // as dependencies between these new variables and the inference variables in T.

            if (phase.isInvocation()) {
                JMethodSig sig = inferMethodRefInvocation(mref, fun, ctdecl0);
                if (fixInstantiation) {
                    // We know that fun & sig have the same type params
                    // We need to fix those that are out-of-scope
                    sig = sig.subst(Substitution.mapping(fun.getTypeParameters(), sig.getTypeParameters()));
                }
                completeMethodRefInference(mref, nonWildcard, fun, sig, false);
            }
        } else {
            // Otherwise, let R' be the result of applying capture conversion (§5.1.10) to the return
            // type of the invocation type (§15.12.2.6) of the compile-time declaration. If R' is void,
            // the constraint reduces to false; otherwise, the constraint reduces to ‹R' → R›.
            if (ctdecl.getReturnType() == ts.NO_TYPE) {
                throw ResolutionFailedException.incompatibleReturn(infer.LOG, mref, ctdecl.getReturnType(), r);
            } else {
                checker.checkExprConstraint(infCtx, capture(ctdecl.getReturnType()), r);
                completeMethodRefInference(mref, nonWildcard, fun, ctdecl, false);
            }
        }
    }

    private void completeMethodRefInference(MethodRefMirror mref, JClassType groundTargetType, JMethodSig functionalMethod, JMethodSig ctDecl, boolean isExactMethod) {
        if ((phase.isInvocation() || isExactMethod) && mayMutateExpr()) {
            // if exact, then the arg is relevant to applicability and there
            // may not be an invocation round
            infCtx.addInstantiationListener(
                infCtx.freeVarsIn(groundTargetType),
                solved -> {
                    mref.setInferredType(solved.ground(groundTargetType));
                    mref.setFunctionalMethod(solved.ground(functionalMethod).internalApi().withOwner(solved.ground(functionalMethod.getDeclaringType())));
                    mref.setCompileTimeDecl(solved.ground(ctDecl));
                }
            );
        }
    }


    JMethodSig inferMethodRefInvocation(MethodRefMirror mref, JMethodSig targetType, MethodCtDecl ctdecl) {
        InvocationMirror wrapper = methodRefAsInvocation(mref, targetType, false);
        wrapper.setCtDecl(ctdecl);
        MethodCallSite mockSite = infer.newCallSite(wrapper, /* expected */ targetType.getReturnType(), site, infCtx, isSpecificityCheck());
        return infer.determineInvocationTypeOrFail(mockSite).getMethodType();
    }

    /**
     * Only executed if {@link MethodResolutionPhase#isInvocation()},
     * as per {@link ExprOps#isPertinentToApplicability(ExprMirror, JMethodSig, JTypeMirror, InvocationMirror)}.
     */
    private boolean isLambdaCompatible(@NonNull JClassType functionalItf, LambdaExprMirror lambda) {

        JClassType groundTargetType = groundTargetType(functionalItf, lambda);
        if (groundTargetType == null) {
            throw ResolutionFailedException.notAFunctionalInterface(infer.LOG, functionalItf, lambda);
        }

        JMethodSig groundFun = findFunctionalInterfaceMethod(groundTargetType);
        if (groundFun == null) {
            throw ResolutionFailedException.notAFunctionalInterface(infer.LOG, functionalItf, lambda);
        }


        // might be partial, whatever
        // We use that, so that the parameters may at least have a type
        // in the body of the lambda, to infer its return type

        // this is because the lazy type resolver uses the functional
        // method to resolve the type of a LambdaParameter
        if (mayMutateExpr()) {
            lambda.setInferredType(groundTargetType);
            lambda.setFunctionalMethod(groundFun);

            // set the final type when done
            if (phase.isInvocation()) {
                infCtx.addInstantiationListener(
                    infCtx.freeVarsIn(groundTargetType),
                    solved -> {
                        JClassType solvedGround = solved.ground(groundTargetType);
                        lambda.setInferredType(solvedGround);
                        lambda.setFunctionalMethod(solved.ground(groundFun).internalApi().withOwner(solved.ground(groundFun.getDeclaringType())));
                    }
                );
            }
        }

        return isLambdaCongruent(functionalItf, groundTargetType, groundFun, lambda);
    }

    private boolean mayMutateExpr() {
        return !isSpecificityCheck();
    }

    private boolean isSpecificityCheck() {
        return site != null && site.isSpecificityCheck();
    }

    // functionalItf    = T
    // groundTargetType = T'
    @SuppressWarnings("PMD.UnusedFormalParameter")
    private boolean isLambdaCongruent(@NonNull JClassType functionalItf,
                                      @NonNull JClassType groundTargetType,
                                      @NonNull JMethodSig groundFun,
                                      LambdaExprMirror lambda) {

        if (groundFun.isGeneric()) {
            throw ResolutionFailedException.lambdaCannotTargetGenericFunction(infer.LOG, groundFun, lambda);
        }

        //  If the number of lambda parameters differs from the number
        //  of parameter types of the function type, the constraint
        //  reduces to false.
        if (groundFun.getArity() != lambda.getParamCount()) {
            throw ResolutionFailedException.incompatibleArity(infer.LOG, lambda.getParamCount(), groundFun.getArity(), lambda);
        }

        // If the function type's result is void and the lambda body is
        // neither a statement expression nor a void-compatible block,
        // the constraint reduces to false.
        JTypeMirror result = groundFun.getReturnType();
        if (result == ts.NO_TYPE && !lambda.isVoidCompatible()) {
            throw ResolutionFailedException.lambdaCannotTargetVoidMethod(infer.LOG, lambda);
        }

        // If the function type's result is not void and the lambda
        // body is a block that is not value-compatible, the constraint
        // reduces to false.
        if (result != ts.NO_TYPE && !lambda.isValueCompatible()) {
            throw ResolutionFailedException.lambdaCannotTargetValueMethod(infer.LOG, lambda);
        }

        // If the lambda parameters have explicitly declared types F1, ..., Fn
        // and the function type has parameter types G1, ..., Gn, then
        // i) for all i (1 ≤ i ≤ n), ‹Fi = Gi›
        if (lambda.isExplicitlyTyped()
            && !areSameTypesInInference(groundFun.getFormalParameters(), lambda.getExplicitParameterTypes())) {
            throw ResolutionFailedException.mismatchedLambdaParameters(infer.LOG, groundFun, lambda.getExplicitParameterTypes(), lambda);
        }

        // and ii) ‹T' <: T›.
        // if (!groundTargetType.isSubtypeOf(functionalItf)) {
        //     return false;
        // }

        // finally, add bounds
        if (result != ts.NO_TYPE) {
            infCtx.addInstantiationListener(
                infCtx.freeVarsIn(groundFun.getFormalParameters()),
                solvedCtx -> {
                    if (mayMutateExpr()) {
                        lambda.setInferredType(solvedCtx.ground(groundTargetType));
                        JMethodSig solvedGroundFun = solvedCtx.ground(groundFun);
                        lambda.setFunctionalMethod(solvedGroundFun);
                        lambda.updateTypingContext(solvedGroundFun);
                    }
                    JTypeMirror groundResult = solvedCtx.ground(result);
                    for (ExprMirror expr : lambda.getResultExpressions()) {
                        if (!isCompatible(groundResult, expr)) {
                            return;
                        }
                    }
                });
        }

        if (mayMutateExpr()) { // we know that the lambda matches now
            lambda.updateTypingContext(groundFun);
        }
        return true;
    }

    private @Nullable JClassType groundTargetType(JClassType type, LambdaExprMirror lambda) {


        List<JTypeMirror> targs = type.getTypeArgs();
        if (CollectionUtil.none(targs, it -> it instanceof JWildcardType)) {
            return type;
        }

        if (lambda.isExplicitlyTyped() && lambda.getParamCount() > 0) {
            // TODO infer, normally also for lambdas with no param, i'm just lazy
            //  https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.5.3
            return null;
        } else {
            return nonWildcardParameterization(type);
        }
    }


    @FunctionalInterface
    interface ExprChecker {

        /**
         * In JLS terms, adds a constraint formula {@code < exprType -> formalType >}.
         *
         * <p>This method throws ResolutionFailedException if the constraint
         * can be asserted as false immediately. Otherwise the check is
         * deferred until both types have been inferred (but bounds on the
         * type vars are added).
         */
        void checkExprConstraint(InferenceContext infCtx, JTypeMirror exprType, JTypeMirror formalType) throws ResolutionFailedException;
    }


}
