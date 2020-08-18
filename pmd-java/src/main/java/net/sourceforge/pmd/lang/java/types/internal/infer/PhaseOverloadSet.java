/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.definitely;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeSystem;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.util.OptionalBool;

final class PhaseOverloadSet extends OverloadSet<MethodCtDecl> {

    private final Infer infer;
    private final TypeSystem ts;
    private final TypeInferenceLogger log;
    private final MethodResolutionPhase phase;
    private final MethodCallSite site;


    PhaseOverloadSet(Infer infer, MethodResolutionPhase phase, MethodCallSite site) {
        this.infer = infer;
        this.ts = infer.getTypeSystem();
        this.log = infer.getLogger();
        this.phase = phase;
        this.site = site;
    }

    /**
     * It's a given that the method is applicable to the site.
     *
     * <p>https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12.2.5
     */
    @Override
    void add(MethodCtDecl sig) {
        super.add(sig);
    }

    public @NonNull MethodCtDecl getMostSpecificOrLogAmbiguity(TypeInferenceLogger logger) {
        assert !isEmpty();
        List<MethodCtDecl> overloads = getOverloadsMutable();
        MethodCtDecl main = overloads.get(0);
        if (overloads.size() != 1) {
            logger.ambiguityError(site, main, overloads);
            main = main.asFailed();
        }
        return main;
    }

    @Override
    protected OptionalBool shouldTakePrecedence(MethodCtDecl m1, MethodCtDecl m2) {
        return isMoreSpecific(m1.getMethodType().internalApi().adaptedMethod(),
                              m2.getMethodType().internalApi().adaptedMethod());
    }


    private OptionalBool isMoreSpecific(@NonNull JMethodSig m1, @NonNull JMethodSig m2) {

        boolean m1OverM2 = isMoreSpecificImpl(m1, m2, site, phase);
        boolean m2OverM1 = isMoreSpecificImpl(m2, m1, site, phase);

        if (m1OverM2 ^ m2OverM1) {
            // then one of them is strictly more specific than the other
            return definitely(m1OverM2);
        } else if (areOverrideEquivalent(m1, m2)) {
            return OverloadSet.shouldAlwaysTakePrecedence(m1, m2, site.getExpr().getReceiverType());
        }

        return UNKNOWN;
    }

    private boolean isMoreSpecificImpl(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        return m2.isGeneric() ? isInferredMoreSpecific(m1, m2, site, phase)
                              : isMoreSpecificNonGeneric(m1, m2, site, phase);
    }


    private boolean isInferredMoreSpecific(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        // https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html#jls-18.5.4
        try {
            return doInfer(m1, m2, site, phase);
        } catch (ResolutionFailedException e) {
            return false;
        }
    }

    private boolean doInfer(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        InferenceContext ctx = infer.newContextFor(m2);

        // even if m1 is generic, the type parameters of m1 are treated as type variables, not inference variables.

        JMethodSig m2p = ctx.mapToIVars(m2);

        List<ExprMirror> es = site.getExpr().getArgumentExpressions();
        List<JTypeMirror> m1Formals = m1.getFormalParameters();
        List<JTypeMirror> m2Formals = m2p.getFormalParameters();

        int k = es.size();

        for (int i = 0; i < k; i++) {
            JTypeMirror ti = phase.ithFormal(m2Formals, i);
            JTypeMirror si = phase.ithFormal(m1Formals, i);
            ExprMirror ei = es.get(i);

            if (ctx.isGround(ti) && isTypeMoreSpecificForArg(si, ti, ei) == NO) {
                return true;
            }

            // Otherwise, if Ti is not a functional interface type, the
            // constraint formula ‹Si <: Ti› is generated.
            JMethodSig fun = TypeOps.findFunctionalInterfaceMethod(ti);
            if (fun == null) {
                // not a functional interface
                infer.checkConvertibleOrDefer(ctx, si, ti, ei, phase, null);
            }

            // todo special conditions for lambdas/ mrefs

        }

        if (phase.requiresVarargs() && m2Formals.size() == k + 1) {
            // that is, the invocation has no arguments for the varargs, eg Stream.of()
            infer.checkConvertibleOrDefer(ctx, phase.ithFormal(m1Formals, k), m2Formals.get(k), site.getExpr(), phase, null);
        }

        ctx.solve();         // throws ResolutionFailedException
        ctx.callListeners(); // may throw ResolutionFailedException

        return true;
    }

    private boolean isMoreSpecificNonGeneric(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        List<JTypeMirror> m1Formals = m1.getFormalParameters();
        List<JTypeMirror> m2Formals = m2.getFormalParameters();
        List<ExprMirror> args = site.getExpr().getArgumentExpressions();
        int k = args.size();
        for (int i = 0; i < k; i++) {
            JTypeMirror si = phase.ithFormal(m1Formals, i);
            JTypeMirror ti = phase.ithFormal(m2Formals, i);
            if (isTypeMoreSpecificForArg(si, ti, args.get(i)) == NO) {
                return false;
            }
        }

        if (phase.requiresVarargs() && m2Formals.size() == k + 1) {
            // if the varargs argument has length 0, then the last
            // formal of m1 must be more specific than the last formal of m2
            return phase.ithFormal(m1Formals, k).isSubtypeOf(m2Formals.get(k));
        }

        return true;
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private OptionalBool isTypeMoreSpecificForArg(JTypeMirror si, JTypeMirror ti, ExprMirror argExpr) {
        // A type S is more specific than a type T for any expression if S <: T (§4.10).
        if (argExpr.getStandaloneType() == ts.UNRESOLVED_TYPE) {
            return UNKNOWN;
        }
        // TODO checks for lambdas/method refs are much more complicated
        return definitely(si.isSubtypeOf(ti, true));
    }

}
