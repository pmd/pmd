/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeConversion.capture;
import static net.sourceforge.pmd.lang.java.types.TypeConversion.isWilcardParameterized;
import static net.sourceforge.pmd.lang.java.types.TypeOps.asList;
import static net.sourceforge.pmd.lang.java.types.TypeOps.subst;
import static net.sourceforge.pmd.lang.java.types.internal.infer.ExprOps.isPertinentToApplicability;
import static net.sourceforge.pmd.lang.java.types.internal.infer.MethodResolutionPhase.INVOC_LOOSE;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.Substitution;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprCheckHelper.ExprChecker;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.CtorInvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.FunctionalExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.LambdaExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.MethodRefMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.PolyExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Main entry point for type inference.
 */
@SuppressWarnings({"PMD.FieldNamingConventions", "PMD.CompareObjectsWithEquals"})
public final class Infer {

    final ExprOps exprOps;

    public final TypeInferenceLogger LOG; // SUPPRESS CHECKSTYLE just easier to read I think

    private final boolean isPreJava8;
    private final TypeSystem ts;

    final MethodCtDecl NO_CTDECL; // SUPPRESS CHECKSTYLE same

    /** This is a sentinel for when the CTDecl was resolved, but invocation failed. */
    final MethodCtDecl FAILED_INVOCATION; // SUPPRESS CHECKSTYLE same

    private final SupertypeCheckCache supertypeCheckCache = new SupertypeCheckCache();

    /**
     * Creates a new instance.
     *
     * @param ts         Type system
     * @param jdkVersion JDK version to use. Type inference was changed
     *                   in Java 8 to propagate the context type.
     * @param logger     Strategy to log failures
     */
    public Infer(TypeSystem ts, int jdkVersion, TypeInferenceLogger logger) {
        this.ts = ts;
        this.isPreJava8 = jdkVersion < 8;
        this.LOG = logger;

        this.NO_CTDECL = MethodCtDecl.unresolved(ts);
        this.FAILED_INVOCATION = MethodCtDecl.unresolved(ts);

        this.exprOps = new ExprOps(this);
    }

    public boolean isPreJava8() {
        return isPreJava8;
    }

    public TypeSystem getTypeSystem() {
        return ts;
    }

    public TypeInferenceLogger getLogger() {
        return LOG;
    }

    public PolySite<FunctionalExprMirror> newFunctionalSite(FunctionalExprMirror mirror, @Nullable JTypeMirror expectedType) {
        return new PolySite<>(mirror, expectedType);
    }

    public MethodCallSite newCallSite(InvocationMirror expr, @Nullable JTypeMirror expectedType) {
        return newCallSite(expr, expectedType, null, null, false);
    }

    /** Site for a nested poly expr. */
    // package
    MethodCallSite newCallSite(InvocationMirror expr,
                               @Nullable JTypeMirror expectedType,
                               @Nullable MethodCallSite outerSite,
                               @Nullable InferenceContext outerCtx,
                               boolean isSpecificityCheck) {
        return new MethodCallSite(expr, expectedType, outerSite, outerCtx != null ? outerCtx : emptyContext(), isSpecificityCheck);
    }

    InferenceContext emptyContext() {
        return newContextFor(Collections.emptyList());
    }

    @NonNull
    InferenceContext newContextFor(JMethodSig m) {
        return newContextFor(m.getTypeParameters());
    }

    InferenceContext newContextFor(List<JTypeVar> tvars) {
        return new InferenceContext(ts, supertypeCheckCache, tvars, LOG);
    }

    /**
     * Infer lambdas and method references that have a target type: cast contexts,
     * and some assignment contexts (not inferred, not return from lambda).
     */
    public void inferFunctionalExprInUnambiguousContext(PolySite<FunctionalExprMirror> site) {
        FunctionalExprMirror expr = site.getExpr();
        JTypeMirror expected = site.getExpectedType();
        try {
            if (expected == null) {
                throw ResolutionFailedException.missingTargetTypeForFunctionalExpr(LOG, expr);
            }
            addBoundOrDefer(null, emptyContext(), INVOC_LOOSE, expr, expected);
        } catch (ResolutionFailedException rfe) {
            rfe.getFailure().addContext(null, site, null);
            LOG.logResolutionFail(rfe.getFailure());
            // here we set expected if not null, the lambda will have the target type
            expr.setInferredType(expected == null ? ts.UNKNOWN : expected);
            if (expr instanceof MethodRefMirror) {
                MethodRefMirror mref = (MethodRefMirror) expr;
                mref.setFunctionalMethod(ts.UNRESOLVED_METHOD);
                mref.setCompileTimeDecl(ts.UNRESOLVED_METHOD);
            } else {
                LambdaExprMirror lambda = (LambdaExprMirror) expr;
                lambda.setFunctionalMethod(ts.UNRESOLVED_METHOD);
            }
        }
    }


    /**
     * Determines the most specific applicable method for the given call site.
     *
     * <p>The returned method type may be {@link TypeSystem#UNRESOLVED_METHOD},
     * in which case no method is applicable (compile-time error).
     *
     * <p>The returned method type may contain un-instantiated inference
     * variables, which depend on the target type. In that case those
     * variables and their bounds will have been duplicated into the
     * inference context of the [site].
     *
     * <p>The given call site should mention information like the expected
     * return type, to help inference. This should be non-null if we're
     * in an invocation or assignment context, otherwise can be left blank.
     */
    public void inferInvocationRecursively(MethodCallSite site) {
        MethodCtDecl ctdecl = goToInvocationWithFallback(site);
        InvocationMirror expr = site.getExpr();
        expr.setCtDecl(ctdecl);
        if (ctdecl == NO_CTDECL) {
            expr.setInferredType(fallbackType(expr));
        } else {
            expr.setInferredType(ctdecl.getMethodType().getReturnType());
        }
    }

    private MethodCtDecl goToInvocationWithFallback(MethodCallSite site) {
        MethodCtDecl ctdecl = getCompileTimeDecl(site);
        if (ctdecl == NO_CTDECL) { // NOPMD CompareObjectsWithEquals
            return NO_CTDECL;
        }

        site.clearFailures();

        // do invocation

        { // reduce scope of invocType, outside of here it's failed
            final MethodCtDecl invocType = finishInstantiation(site, ctdecl);
            if (invocType != FAILED_INVOCATION) { // NOPMD CompareObjectsWithEquals
                return invocType;
            }
        }
        // ok we failed, we can still use some info from the ctdecl

        JMethodSig fallback = deleteTypeParams(ctdecl.getMethodType().internalApi().adaptedMethod());
        LOG.fallbackInvocation(fallback, site);

        return ctdecl.withMethod(fallback, true);
    }

    private JTypeMirror fallbackType(PolyExprMirror expr) {
        JTypeMirror t = expr.unresolvedType();
        return t == null ? ts.UNKNOWN : t;
    }

    // If the invocation fails, replace type parameters with a placeholder,
    // to not hide a bad failure, while preserving the method if possible
    private JMethodSig deleteTypeParams(JMethodSig m) {
        if (!m.isGeneric()) {
            return m;
        }
        List<JTypeVar> tparams = m.getTypeParameters();
        List<JTypeMirror> nErrors = Collections.nCopies(tparams.size(), ts.ERROR);
        return m.subst(Substitution.mapping(tparams, nErrors));
    }

    /**
     * Similar to {@link #inferInvocationRecursively(MethodCallSite)} for
     * subexpressions. This never returns a fallback method.
     *
     * <p>A return of {@link #NO_CTDECL} indicates no overload is applicable.
     * <p>A return of {@link #FAILED_INVOCATION} means there is a maximally
     * specific compile-time declaration, but it failed invocation, meaning,
     * it couldn't be linked to its context. If so, the outer inference process
     * must be terminated with a failure.
     * <p>This ne
     *
     * <p>The returned method type may contain un-instantiated inference
     * variables, which depend on the target type. In that case those
     * variables and their bounds will have been duplicated into the
     * inference context of the [site].
     */
    @NonNull MethodCtDecl determineInvocationTypeOrFail(MethodCallSite site) {
        MethodCtDecl ctdecl = getCompileTimeDecl(site);
        if (ctdecl == NO_CTDECL) { // NOPMD CompareObjectsWithEquals
            return ctdecl;
        }

        return finishInstantiation(site, ctdecl);
    }


    public @NonNull MethodCtDecl getCompileTimeDecl(MethodCallSite site) {
        if (site.getExpr().getCtDecl() == null) {
            MethodCtDecl ctdecl = computeCompileTimeDecl(site);
            site.getExpr().setCtDecl(ctdecl); // cache it for later
        }
        return site.getExpr().getCtDecl();
    }

    /**
     * Determines the most specific applicable method for the given call site.
     *
     * <p>The returned method type may be null, in which case no method is
     * applicable (compile-time error).
     */
    private @NonNull MethodCtDecl computeCompileTimeDecl(MethodCallSite site) {

        /*
         *  The process starts with a set of candidates and refines it
         *  iteratively. Applicability/best applicability are the only
         *  ones which needs inference.
         *
         *  visible ⊇ accessible ⊇ potentially applicable ⊇ applicable ⊇ best applicable
         */
        List<JMethodSig> potentiallyApplicable = new ArrayList<>();
        for (JMethodSig it : site.getExpr().getAccessibleCandidates()) {
            if (isPotentiallyApplicable(it, site.getExpr())) {
                potentiallyApplicable.add(it);
            }
        }

        if (potentiallyApplicable.isEmpty()) {
            LOG.noApplicableCandidates(site);
            return NO_CTDECL;
        }

        for (MethodResolutionPhase phase : MethodResolutionPhase.APPLICABILITY_TESTS) {
            PhaseOverloadSet applicable = new PhaseOverloadSet(this, phase, site);
            for (JMethodSig m : potentiallyApplicable) {
                site.resetInferenceData();

                MethodCtDecl candidate = logInference(site, phase, m);

                if (!candidate.isFailed()) {
                    applicable.add(candidate);
                }
            }

            if (applicable.nonEmpty()) {
                MethodCtDecl bestApplicable = applicable.getMostSpecificOrLogAmbiguity(LOG);
                JMethodSig adapted = ExprOps.adaptGetClass(bestApplicable.getMethodType(),
                                                           site.getExpr()::getErasedReceiverType);
                return bestApplicable.withMethod(adapted);
            }
        }


        LOG.noCompileTimeDeclaration(site);

        return NO_CTDECL;
    }

    @NonNull MethodCtDecl finishInstantiation(MethodCallSite site, MethodCtDecl ctdecl) {
        JMethodSig m = ctdecl.getMethodType();
        InvocationMirror expr = site.getExpr();

        site.loadInferenceData(ctdecl);
        site.setInInvocation();

        if (site.canSkipInvocation()) {
            assert assertReturnIsGround(m);

            expr.setInferredType(m.getReturnType());
            LOG.skipInstantiation(m, site);
            return ctdecl;
        }

        // start the inference over with the original method, including
        // arguments that are not pertinent to applicability (lambdas)
        // to instantiate all tvars

        return logInference(site,
                            ctdecl.getResolvePhase().asInvoc(),
                            ctdecl.getMethodType().internalApi().adaptedMethod());
    }

    // this is skipped when running without assertions
    private boolean assertReturnIsGround(JMethodSig t) {
        subst(t.getReturnType(), var -> {
            assert !(var instanceof InferenceVar)
                : "Expected a ground type " + t;
            assert !(var instanceof JTypeVar) || !t.getTypeParameters().contains(var)
                : "Some type parameters have not been instantiated";
            return var;
        });
        return true;
    }


    private @NonNull MethodCtDecl logInference(MethodCallSite site, MethodResolutionPhase phase, JMethodSig m) {
        LOG.startInference(m, site, phase);
        @Nullable JMethodSig candidate = instantiateMethodOrCtor(site, phase, m);
        LOG.endInference(candidate);

        if (candidate == null) {
            return FAILED_INVOCATION;
        } else {
            return new MethodCtDecl(candidate,
                                    phase,
                                    site.canSkipInvocation(),
                                    site.needsUncheckedConversion(),
                                    false);
        }
    }


    private @Nullable JMethodSig instantiateMethodOrCtor(MethodCallSite site, MethodResolutionPhase phase, JMethodSig m) {
        return site.getExpr() instanceof CtorInvocationMirror ? instantiateConstructor(m, site, phase)
                                                              : instantiateMethod(m, site, phase);
    }


    /**
     * Infer type arguments for the given method at the method call.
     * Returns null if no instantiations exist, ie the method is not
     * applicable.
     *
     * @param m     Candidate method
     * @param site  Descriptor of the context of the call.
     * @param phase Phase in which the method is reviewed
     */
    private @Nullable JMethodSig instantiateMethod(JMethodSig m,
                                                   MethodCallSite site,
                                                   MethodResolutionPhase phase) {
        if (phase.requiresVarargs() && !m.isVarargs()) {
            return null; // don't log such a dumb mistake
        }
        try {
            return instantiateMaybeNoInfer(m, site, phase);
        } catch (ResolutionFailedException e) {
            ResolutionFailure failure = e.getFailure();
            failure.addContext(m, site, phase);
            LOG.logResolutionFail(failure);
            return null;
        }
    }

    private @Nullable JMethodSig instantiateConstructor(JMethodSig cons,
                                                        MethodCallSite site,
                                                        MethodResolutionPhase phase) {

        CtorInvocationMirror expr = (CtorInvocationMirror) site.getExpr();

        JTypeMirror newTypeMaybeInvalid = expr.getNewType();
        if (!(newTypeMaybeInvalid instanceof JClassType)) {
            // no constructor, note also, that array type constructors
            // don't go through these routines because there's no overloading
            // of array ctors. They're handled entirely in LazyTypeResolver.
            return null;
        }

        JClassType newType = (JClassType) newTypeMaybeInvalid;
        boolean isAdapted = needsAdaptation(expr, newType);
        JMethodSig adapted = isAdapted
                             ? adaptGenericConstructor(cons, newType, expr)
                             : cons;

        site.maySkipInvocation(!isAdapted);

        @Nullable JMethodSig result = instantiateMethod(adapted, site, phase);
        if (isAdapted && result != null) {
            // undo the adaptation

            JTypeMirror rtype = result.getReturnType();
            if (!rtype.isInterface()) {
                // this is for anonymous class ctors
                // an interface cannot declare a constructor
                result = result.internalApi().withOwner(rtype);
            }
            return result.internalApi().withTypeParams(null);

        }
        return result;
    }

    private boolean needsAdaptation(CtorInvocationMirror expr, JClassType newType) {
        return expr.isDiamond()
            || newType.isParameterizedType() // ???
            || expr.isAnonymous();
    }

    /**
     * Transform the constructor of a generic class so that its type parameters
     * mention the type params of the declaring class. This enables diamond
     * inference, we just treat the class type params to infer as
     * additional inference variables.
     *
     * <p>E.g. for
     *
     * {@code class ArrayList<T> { ArrayList() {} } }
     *
     * the constructor is represented as a method type:
     *
     * {@code <T> ArrayList<T> new() }
     *
     * the return type being that of the created instance.
     */
    private static JMethodSig adaptGenericConstructor(JMethodSig cons, JClassType newType, CtorInvocationMirror expr) {
        assert cons.isConstructor() : cons + " should be a constructor";

        if (cons.getDeclaringType().isArray()) {
            // array methods do not need to be adapted and don't support it
            return cons;
        }

        // replace the return type so that anonymous class ctors return the supertype
        JMethodSig adaptedSig = cons.internalApi().withReturnType(newType).internalApi().markAsAdapted();

        List<JTypeVar> newTypeFormals = newType.getFormalTypeParams();
        if (newTypeFormals.isEmpty()) {
            // non-generic type
            return adaptedSig;
        } else {
            // else transform the constructor to add the type parameters
            // of the constructed type
            List<JTypeVar> consParams = cons.getTypeParameters();
            if (consParams.size() > cons.getSymbol().getTypeParameterCount()) {
                // it's already been adapted
                assert consParams.equals(CollectionUtil.concatView(cons.getSymbol().getTypeParameters(), newTypeFormals));
                return adaptedSig;
            } else if (!expr.isDiamond()) {
                // it doesn't need adaptation, we're not doing diamond inference
                return adaptedSig;
            }

            List<JTypeVar> tparams = CollectionUtil.concatView(consParams, newTypeFormals);

            // type parameters are not part of the adapted signature, so that when we reset
            // the signature for invocation inference, we don't duplicate new type parameters
            return adaptedSig.internalApi().withTypeParams(tparams).internalApi().markAsAdapted();
        }
    }

    /**
     * Catch the easy cases before starting inference.
     */
    private JMethodSig instantiateMaybeNoInfer(JMethodSig m, MethodCallSite site, MethodResolutionPhase phase) {

        if (!m.isGeneric()) {
            // non-generic methods may mention explicit type arguments
            // for compatibility, they must be ignored.

            // check that the arguments are conformant
            // the inference context is empty because all param types are ground.
            addArgsConstraints(emptyContext(), m, site, phase);
            return m;
        }

        InvocationMirror expr = site.getExpr();
        List<JTypeMirror> explicitTargs = expr.getExplicitTypeArguments();

        if (!explicitTargs.isEmpty()) {
            // we have explicit type arguments
            List<JTypeVar> tparams = m.getTypeParameters();

            if (tparams.size() != explicitTargs.size()) {
                // normally checked by isPotentiallyApplicable
                throw ResolutionFailedException.incompatibleTypeParamCount(LOG, site.getExpr(), m, explicitTargs.size(), tparams.size());
            }

            Substitution explicitSubst = Substitution.mapping(tparams, explicitTargs);

            for (int i = 0; i < tparams.size(); i++) {
                JTypeMirror explicit = explicitTargs.get(i);
                JTypeMirror upperBound = tparams.get(i).getUpperBound().subst(explicitSubst);

                if (explicit.isConvertibleTo(upperBound).never()) {
                    throw ResolutionFailedException.incompatibleBound(LOG, explicit, upperBound, expr.getExplicitTargLoc(i));
                }
            }


            JMethodSig subst = m.subst(explicitSubst);

            // check that the arguments are conformant
            // the inference context is empty because all param types are ground.
            addArgsConstraints(emptyContext(), subst, site, phase);

            return subst;
        }


        site.maySkipInvocation(!ExprOps.isContextDependent(m) && site.getOuterCtx().isGround(m.getReturnType()));

        return instantiateImpl(m, site, phase);
    }

    /**
     * Perform actual inference. If the method is return-type-polymorphic,
     * then we delegate the solving to the call site's inference context,
     * which knows more, however we add inference vars and their constraints
     * to it.
     */
    private JMethodSig instantiateImpl(JMethodSig m, MethodCallSite site, MethodResolutionPhase phase) {

        InferenceContext infCtx = newContextFor(m); // b0
        LOG.ctxInitialization(infCtx, m);

        try {

            if (phase.isInvocation() && !isPreJava8) {
                m = doReturnChecksAndChangeReturnType(m, site, infCtx);
            }

            addArgsConstraints(infCtx, m, site, phase); // c
            infCtx.incorporate(); // b2

            if (phase.isInvocation()) {

                boolean shouldPropagate = shouldPropagateOutwards(m.getReturnType(), site, infCtx);

                //propagate outwards if needed
                if (shouldPropagate) {
                    // propagate inference context outwards and exit
                    // the outer context will solve the variables and call listeners
                    // of this context
                    LOG.propagateAndAbort(infCtx, site.getOuterCtx());
                    infCtx.duplicateInto(site.getOuterCtx());
                    return infCtx.mapToIVars(m);
                }
            }

            // this may throw for incompatible bounds
            boolean isDone = infCtx.solve(/*onlyBoundedVars:*/isPreJava8());

            if (isPreJava8() && !isDone) {
                // this means we're not in an invocation context,
                // if we are, we must ignore it in java 7
                if (site.getOuterCtx().isEmpty()) {
                    // Then add the return contraints late
                    // Java 7 only uses the context type if the arguments are not enough
                    // https://docs.oracle.com/javase/specs/jls/se7/html/jls-15.html#jls-15.12.2.8
                    m = doReturnChecksAndChangeReturnType(m, site, infCtx);
                }
                // otherwise force solving remaining vars
                infCtx.solve();
            }

            if (infCtx.needsUncheckedConversion()) {
                site.setNeedsUncheckedConversion();
            }

            // instantiate vars and return
            return InferenceContext.finalGround(infCtx.mapToIVars(m));
        } finally {
            // Note that even if solve succeeded, listeners checking deferred
            // bounds may still throw ResolutionFailedException, in which case
            // by the laws of finally, this exception will be thrown and the
            // return value will be ignored.
            infCtx.callListeners();
        }
    }

    private JMethodSig doReturnChecksAndChangeReturnType(JMethodSig m, MethodCallSite site, InferenceContext infCtx) {
        LOG.startReturnChecks();
        JTypeMirror actualResType = addReturnConstraints(infCtx, m, site); // b3
        LOG.endReturnChecks();
        m = m.internalApi().withReturnType(actualResType);
        return m;
    }


    private boolean shouldPropagateOutwards(JTypeMirror resultType, MethodCallSite target, InferenceContext inferenceContext) {
        return !isPreJava8
            && !target.getOuterCtx().isEmpty()  //enclosing context is a generic method
            && !inferenceContext.isGround(resultType)   //return type contains inference vars
            && !(resultType instanceof InferenceVar    //no eager instantiation is required (as per 18.5.2)
            && needsEagerInstantiation((InferenceVar) resultType, target.getExpectedType(), inferenceContext));
    }

    /**
     * Add more constraints on the inference vars based on the expected
     * return type at the call site. This is described in
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.5.2.1
     *
     * under "Let B3 be the bound set derived from B2 as follows."
     *
     * <p>This binds the ivars of this context to those of the outer context.
     */
    private JTypeMirror addReturnConstraints(InferenceContext infCtx, JMethodSig m, MethodCallSite site) {

        /*
            Remember: calling stuff like isConvertible or isSubtype
            adds constraints on the type variables that are found there.
         */

        JTypeMirror resultType = m.getReturnType();
        if (site.needsUncheckedConversion()) {
            // if unchecked conversion is necessary, the result type,
            // and all thrown exception types, are erased.
            resultType = resultType.getErasure();
        }
        resultType = infCtx.mapToIVars(resultType);
        InferenceContext outerInfCtx = site.getOuterCtx();

        if (!infCtx.isGround(resultType) && !outerInfCtx.isEmpty() && resultType instanceof JClassType) {
            JClassType resClass = capture((JClassType) resultType);
            resultType = resClass;

            for (JTypeMirror targ : resClass.getTypeArgs()) {
                if (targ instanceof JTypeVar && ((JTypeVar) targ).isCaptured()) {
                    infCtx.addVar((JTypeVar) targ);
                }
            }
            resultType = infCtx.mapToIVars(resultType);
        }

        JTypeMirror actualRes = site.getExpectedType();
        if (actualRes == null) {
            actualRes = ts.OBJECT;
        }

        if (resultType instanceof InferenceVar) {
            InferenceVar retVar = (InferenceVar) resultType;
            if (needsEagerInstantiation(retVar, actualRes, infCtx)) {
                infCtx.solve(retVar);
                infCtx.callListeners();
                if (isConvertible(retVar.getInst(), actualRes, true).never()) {
                    actualRes = ts.OBJECT;
                }
            } else if (actualRes.isPrimitive()) {
                actualRes = actualRes.box();
            }
        }

        if (isConvertible(resultType, outerInfCtx.mapToIVars(actualRes), true).never()) {
            throw ResolutionFailedException.incompatibleReturn(LOG, site.getExpr(), resultType, actualRes);
        }

        return resultType;
    }


    /**
     * Returns true if the inference var needs to be instantiated eagerly,
     * as described in JLS§18.5.2.1. (Poly Method Invocation Compatibility)
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.5.2.1
     *
     * @param alpha  Inference var
     * @param t      Target type of the invocation
     * @param infCtx Inference context
     */
    private boolean needsEagerInstantiation(InferenceVar alpha, JTypeMirror t, InferenceContext infCtx) {
        if (t == null) {
            return false;
        }

        if (t.isPrimitive()) {
            // T is a primitive type, and one of the primitive wrapper classes is an instantiation,
            // upper bound, or lower bound for alpha in B2.

            for (JTypeMirror b : alpha.getBounds(BoundKind.ALL)) {
                if (b.isBoxedPrimitive()) {
                    return true;
                }
            }
            return false;
        }

        // T is a reference type, but is not a wildcard-parameterized type, and either

        if (!t.isPrimitive() && !isWilcardParameterized(t)) {
            // i) B2 contains a bound of one of the forms alpha = S or S <: alpha,
            //    where S is a wildcard-parameterized type, or
            for (JTypeMirror s : alpha.getBounds(BoundKind.EQ_LOWER)) {
                if (isWilcardParameterized(s)) {
                    return true;
                }
            }

            // ii) B2 contains two bounds of the forms S1 <: alpha and S2 <: alpha,
            // where S1 and S2 have supertypes that are two different
            // parameterizations of the same generic class or interface.

            for (JTypeMirror aLowerBound : alpha.getBounds(BoundKind.LOWER)) {
                for (JTypeMirror anotherLowerBound : alpha.getBounds(BoundKind.LOWER)) {
                    if (aLowerBound != anotherLowerBound // NOPMD CompareObjectsWithEquals
                        && infCtx.isGround(aLowerBound)
                        && infCtx.isGround(anotherLowerBound)
                        && commonSuperWithDiffParameterization(aLowerBound, anotherLowerBound)) {
                        return true;
                    }
                }
            }
        }

        // T is a parameterization of a generic class or interface, G,
        // and B2 contains a bound of one of the forms alpha = S or S <: alpha,
        // where there exists no type of the form G<...> that is a
        // supertype of S, but the raw type G is a supertype of S

        if (t.isParameterizedType()) {
            for (JTypeMirror b : alpha.getBounds(BoundKind.EQ_LOWER)) {
                JTypeMirror sup = b.getAsSuper(((JClassType) t).getSymbol());
                if (sup != null && sup.isRaw()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean commonSuperWithDiffParameterization(JTypeMirror t, JTypeMirror s) {
        JTypeMirror lubResult = ts.lub(listOf(t, s));
        if (lubResult.isBottom() || lubResult.isTop()) {
            return false;
        }
        for (JTypeMirror sup : asList(lubResult)) {
            if (sup.isParameterizedType()) {
                JClassSymbol sym = ((JClassType) sup).getSymbol();
                JTypeMirror asSuperOfT = t.getAsSuper(sym);
                JTypeMirror asSuperOfS = s.getAsSuper(sym);
                if (!asSuperOfS.equals(asSuperOfT)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Generate bounds on the ivars based on the expected/actual types
     * of the arguments to the call. This is described in
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.5.1
     *
     * as being the set C.
     *
     * <p>For invocation applicability inference (phases {@link MethodResolutionPhase#STRICT STRICT}
     * through {@link MethodResolutionPhase#VARARGS VARARGS}), only arguments
     * that are {@linkplain ExprOps#isPertinentToApplicability(ExprMirror, JMethodSig, JTypeMirror, InvocationMirror)
     * pertinent to applicability}
     * are considered. Arguments like lambdas do not influence the applicability
     * check beyond checking their basic 'shape' (number of params)
     * to check that the method is {@linkplain #isPotentiallyApplicable(JMethodSig, InvocationMirror) potentially
     * applicable}, which is done very much earlier.
     * So they don't add constraints during those first phases.
     *
     * <p>When we have found an applicable method and are instantiating it
     * (phases {@link MethodResolutionPhase#INVOC_STRICT INVOC_STRICT} through {@link
     * MethodResolutionPhase#INVOC_VARARGS INVOC_VARARGS}),
     * all arguments are considered so as to yield sharper bounds.
     *
     * @param infCtx Inference context
     * @param m      Tested method
     * @param site   Invocation expression
     * @param phase  Phase (determines what constraints are allowed)
     */
    private void addArgsConstraints(InferenceContext infCtx, JMethodSig m, MethodCallSite site, MethodResolutionPhase phase) {
        LOG.startArgsChecks();

        InvocationMirror expr = site.getExpr();

        boolean varargsRequired = phase.requiresVarargs();

        if (!varargsRequired && m.getArity() != expr.getArgumentCount()) {
            throw ResolutionFailedException.incompatibleArity(LOG, expr.getArgumentCount(), m.getArity(), expr);
        }

        List<JTypeMirror> fs = m.getFormalParameters();

        @Nullable
        JArrayType varargsParam = varargsRequired && m.isVarargs() ? (JArrayType) fs.get(fs.size() - 1) : null;
        int lastP = varargsParam == null ? fs.size() : fs.size() - 1;

        List<ExprMirror> args = expr.getArgumentExpressions();

        for (int i = 0; i < lastP; i++) {
            ExprMirror ei = args.get(i);


            if (phase.isInvocation() || isPertinentToApplicability(ei, m, fs.get(i), expr)) {
                JTypeMirror stdType = ei.getStandaloneType();
                JTypeMirror fi = infCtx.mapToIVars(fs.get(i));

                LOG.startArg(i, ei, fi);

                if (!phase.canBox()) {
                    // these are cases where applicability is impossible (in strict ctx)
                    if (stdType != null && stdType.isPrimitive() != fi.isPrimitive() && stdType != ts.UNKNOWN) {
                        throw ResolutionFailedException.incompatibleFormal(LOG, ei, stdType, fi);
                    }
                }

                addBoundOrDefer(site, infCtx, phase, ei, fi);

                LOG.endArg();
            } else {
                // then the final reinvocation is necessary
                site.maySkipInvocation(false);
                LOG.skipArgAsNonPertinent(i, ei);
            }
        }

        if (varargsRequired && varargsParam != null) {
            JTypeMirror varargsComponent = infCtx.mapToIVars(varargsParam.getComponentType());

            // possibly some varargs arguments left
            for (int i = lastP; i < args.size(); i++) {
                ExprMirror ei = args.get(i);

                if (phase.isInvocation() || isPertinentToApplicability(ei, m, varargsComponent, expr)) {
                    LOG.startArg(i, ei, varargsComponent);
                    addBoundOrDefer(site, infCtx, phase, ei, varargsComponent);
                    LOG.endArg();
                } else {
                    site.maySkipInvocation(false);
                    LOG.skipArgAsNonPertinent(i, ei);
                }
            }
        }
        LOG.endArgsChecks();
    }

    /**
     * This corresponds to the attribution of expression compatibility
     * constraints in https://docs.oracle.com/javase/specs/jls/se9/html/jls-18.html#jls-18.2.1
     * although it's not implemented as described.
     *
     * See {@link ExprCheckHelper#isCompatible(JTypeMirror, ExprMirror)}.
     */
    private void addBoundOrDefer(@Nullable MethodCallSite site, InferenceContext infCtx, MethodResolutionPhase phase, @NonNull ExprMirror arg, @NonNull JTypeMirror formalType) {
        ExprChecker exprChecker =
            (ctx, exprType, formalType1) -> checkConvertibleOrDefer(ctx, exprType, formalType1, arg, phase, site);

        ExprCheckHelper helper = new ExprCheckHelper(infCtx, phase, exprChecker, site, this);
        if (!helper.isCompatible(formalType, arg)) {
            throw ResolutionFailedException.incompatibleFormalExprNoReason(LOG, arg, formalType);
        }
    }

    /**
     * Add a compatibility constraint between an exprType and a formalType.
     * This asserts {@code exprType <: formalType}, the arg parameter is only
     * used for reporting.
     *
     * <p>This method is called back to by {@link ExprCheckHelper#isCompatible(JTypeMirror, ExprMirror)}.
     */
    void checkConvertibleOrDefer(InferenceContext infCtx, JTypeMirror exprType, JTypeMirror formalType, ExprMirror arg, MethodResolutionPhase phase, @Nullable MethodCallSite site) {
        if (!infCtx.isGround(formalType) || !infCtx.isGround(exprType)) {
            // defer the check
            infCtx.addInstantiationListener(setOf(formalType, exprType), solvedCtx -> checkConvertibleOrDefer(solvedCtx, exprType, formalType, arg, phase, site));
        }

        JTypeMirror groundE = infCtx.ground(exprType);
        JTypeMirror groundF = infCtx.ground(formalType);

        // This method call does all the work of adding constraints
        // If groundE or groundF are in fact not ground, then constraints
        // on the ivars that appear within them are implicitly added during
        // the subtyping check. The call then returns true and we return
        // normally

        // If they are ground, then they must conform to each other else
        // the exception stops the resolution process.
        Convertibility isConvertible = isConvertible(groundE, groundF, phase.canBox());
        if (isConvertible.never()) {
            throw ResolutionFailedException.incompatibleFormal(LOG, arg, groundE, groundF);
        } else if (isConvertible.withUncheckedWarning() && site != null) {
            site.setNeedsUncheckedConversion();
        }
    }

    /**
     * Convertibility in *invocation* context.
     *
     * https://docs.oracle.com/javase/specs/jls/se8/html/jls-5.html#jls-5.3
     */
    static Convertibility isConvertible(JTypeMirror exprType, JTypeMirror formalType, boolean canBox) {
        if (exprType == formalType) { // NOPMD CompareObjectsWithEquals
            // fast path
            return Convertibility.SUBTYPING;
        }

        if (canBox && exprType.isPrimitive() ^ formalType.isPrimitive()) {
            // then boxing conversions may be useful
            Convertibility result = TypeOps.isConvertible(exprType.box(), formalType.box());
            if (!result.never()) {
                return result;
            } else {
                return TypeOps.isConvertible(exprType.unbox(), formalType.unbox());
            }
        }

        return TypeOps.isConvertible(exprType, formalType);
    }

    /**
     * Returns true if the method is potentially applicable to the invocation
     * expression expr, as specified in JLS§15.12.2.1.
     *
     * https://docs.oracle.com/javase/specs/jls/se9/html/jls-15.html#jls-15.12.2.1
     *
     * <p>This assumes the name of the method matches the expression, and
     * the method is accessible.
     *
     * @param m    Method to test
     * @param expr Invocation expression
     */
    private boolean isPotentiallyApplicable(JMethodSig m, InvocationMirror expr) {

        if (m.isGeneric()
            && !expr.getExplicitTypeArguments().isEmpty()
            && expr.getExplicitTypeArguments().size() != m.getTypeParameters().size()) {
            return false;
        }

        List<ExprMirror> args = expr.getArgumentExpressions();

        if (!m.isVarargs()) {
            // we can avoid computing formal parameters by using getArity here
            if (args.size() != m.getArity()) {
                return false;
            }

            List<JTypeMirror> fs = m.getFormalParameters();
            for (int i = 0; i < args.size(); i++) {
                if (!exprOps.isPotentiallyCompatible(m, args.get(i), fs.get(i))) {
                    return false;
                }
            }

        } else {
            List<JTypeMirror> fs = m.getFormalParameters();

            // test first n-1 params
            int varargIdx = fs.size() - 1;
            for (int i = 0; i < varargIdx; i++) {
                if (i >= args.size()) {
                    // not enough arguments
                    return false;
                }

                if (!exprOps.isPotentiallyCompatible(m, args.get(i), fs.get(i))) {
                    return false;
                }
            }

            if (args.size() == varargIdx - 1) {
                return true;
            }

            if (args.size() == fs.size()) {
                ExprMirror last = args.get(varargIdx);
                JArrayType t = (JArrayType) fs.get(varargIdx);

                return exprOps.isPotentiallyCompatible(m, last, t)
                    || exprOps.isPotentiallyCompatible(m, last, t.getComponentType());
            }

            if (args.size() > fs.size()) {
                JTypeMirror t = ((JArrayType) fs.get(varargIdx)).getComponentType();
                for (int i = varargIdx; i < args.size(); i++) {
                    if (!exprOps.isPotentiallyCompatible(m, args.get(i), t)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


}
