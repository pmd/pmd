/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent;
import static net.sourceforge.pmd.lang.java.types.TypeOps.shouldTakePrecedence;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror.MethodCtDecl;
import net.sourceforge.pmd.util.OptionalBool;

final class OverloadComparator {

    private final Infer infer;
    private final TypeInferenceLogger log;


    OverloadComparator(Infer infer) {
        this.infer = infer;
        this.log = infer.getLogger();
    }


    /**
     * Returns the most specific method of the two methods for the
     * given invocation expression, given both are applicable to the
     * site AND given that the currentBest may be shadowing/overriding/hiding
     * the candidate if they are override-equivalent.
     *
     * <p>https://docs.oracle.com/javase/specs/jls/se8/html/jls-15.html#jls-15.12.2.5
     */
    @NonNull MethodCtDecl selectMoreSpecific(MethodCtDecl currentBest, MethodCtDecl candidate, MethodCallSite site, MethodResolutionPhase phase) {
        if (currentBest.isFailed()) {
            return candidate;
        } else if (candidate.isFailed()) {
            return currentBest;
        }

        JMethodSig m1 = currentBest.getMethodType().internalApi().adaptedMethod();
        JMethodSig m2 = candidate.getMethodType().internalApi().adaptedMethod();

        return isMoreSpecific(m1, m2, site, phase)
               ? currentBest
               : candidate;
    }

    private boolean isMoreSpecific(@NonNull JMethodSig m1, @NonNull JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {

        boolean m1OverM2 = isMoreSpecificImpl(m1, m2, site, phase);
        boolean m2OverM1 = isMoreSpecificImpl(m2, m1, site, phase);

        if (m1OverM2 != m2OverM1) {
            // then one of them is strictly more specific than the other
            return m1OverM2;
        } else if (areOverrideEquivalent(m1, m2)) {

            OptionalBool result = shouldTakePrecedence(m1, m2, site.getExpr().getReceiverType());
            if (result == UNKNOWN) {
                log.ambiguityError(site, m1, m2);
                // todo if a third method comes in that is more specific
                //  than all ambiguous methods, then the ambiguity is lifted
                return true;
            } else {
                return result == YES;
            }
        }

        return false;
    }

    private boolean isMoreSpecificImpl(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        return m2.isGeneric() ? isInferredMoreSpecific(m1, m2, site, phase)
                              : isMoreSpecificNonGeneric(m1, m2, site, phase);
    }


    private boolean isInferredMoreSpecific(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
        /* TODO
             https://docs.oracle.com/javase/specs/jls/se8/html/jls-18.html#jls-18.5.4
     
             Test case:
         */
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

            if (ctx.isGround(ti) && !isTypeMoreSpecificForArg(si, ti, ei)) {
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
            if (!isTypeMoreSpecificForArg(si, ti, args.get(i))) {
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
    private boolean isTypeMoreSpecificForArg(JTypeMirror si, JTypeMirror ti, ExprMirror argExpr) {
        // A type S is more specific than a type T for any expression if S <: T (§4.10).
        return si.isSubtypeOf(ti, true); // TODO checks for lambdas/method refs are much more complicated
    }

}
