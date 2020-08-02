/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.areOverrideEquivalent;
import static net.sourceforge.pmd.lang.java.types.TypeOps.overrides;
import static net.sourceforge.pmd.util.OptionalBool.NO;
import static net.sourceforge.pmd.util.OptionalBool.UNKNOWN;
import static net.sourceforge.pmd.util.OptionalBool.YES;
import static net.sourceforge.pmd.util.OptionalBool.definitely;

import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JClassType;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
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
    JMethodSig selectMostSpecific(JMethodSig currentBest, JMethodSig candidate, MethodCallSite site, MethodResolutionPhase phase) {
        if (currentBest == null) {
            return candidate;
        } else if (candidate == null) {
            return currentBest;
        }

        JMethodSig m1 = currentBest.internalApi().originalMethod();
        JMethodSig m2 = candidate.internalApi().originalMethod();

        return isStrictlyMoreSpecific(m1, m2, site, phase)
               ? currentBest
               : candidate;
    }

    private boolean isStrictlyMoreSpecific(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {

        boolean m1OverM2 = isMoreSpecific(m1, m2, site, phase);
        boolean m2OverM1 = isMoreSpecific(m2, m1, site, phase);

        if (m1OverM2 != m2OverM1) {
            // then one of them is strictly more specific than the other
            return m1OverM2;
        } else if (areOverrideEquivalent(m1, m2)) {

            OptionalBool result = shouldTakePrecedence(m1, m2, site.getExpr().getEnclosingType());
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

    // test only

    /**
     * Given that m1 and m2 are override-equivalent, should m1 be chosen
     * over m2 (YES/NO), or should an ambiguity error arise (UNKNOWN). This
     * handles a few cases about shadowing/overriding/hiding that are not
     * covered strictly by the definition of "specificity".
     *
     * <p>If m1 and m2 are equal, returns the first one by convention.
     */
    static OptionalBool shouldTakePrecedence(JMethodSig m1, JMethodSig m2, JClassType site) {
        // select
        // 1. the non-bridge
        // 2. the one that overrides the other
        // 3. the non-abstract method

        if (m1.isBridge() != m2.isBridge()) {
            return definitely(!m1.isBridge());
        } else if (overrides(m1, m2, site)) {
            return YES;
        } else if (overrides(m2, m1, site)) {
            return NO;
        } else if (m1.isAbstract() != m2.isAbstract()) {
            return definitely(!m1.isAbstract());
        }

        OptionalBool shadows = shadows(m1, m2, site);
        if (shadows.isKnown()) {
            return shadows;
        } else if (m1.isAbstract() && m2.isAbstract()) { // last ditch effort
            // both are unrelated abstract, inherited into 'site'
            // their signature would be merged into the site
            // if exactly one is declared in a class, prefer it
            // if both are declared in a class, ambiguity error (recall, neither overrides the other)
            // if both are declared in an interface, select any of them
            boolean m1InClass = m1.getSymbol().getEnclosingClass().isClass();
            boolean m2Class = m2.getSymbol().getEnclosingClass().isClass();

            return m1InClass && m2Class ? UNKNOWN : definitely(m1InClass);
        }

        return UNKNOWN;
    }

    // TODO test if doesOverride handles hiding between static methods properly


    /**
     * Returns whether m1 shadows m2 in the body of the given site, ie
     * m1 is declared in a class C1 that encloses the site, and m2 is declared
     * in a type that strictly encloses C1.
     *
     * <p>Assumes m1 and m2 are override-equivalent, and declared in different
     * classes.
     */
    static OptionalBool shadows(JMethodSig m1, JMethodSig m2, JClassType site) {
        final JClassSymbol c1 = m1.getSymbol().getEnclosingClass();
        final JClassSymbol c2 = m2.getSymbol().getEnclosingClass();

        // We go outward from the `site`. The height measure is the distance
        // from the site (ie, the reverted depth of each class)

        int height = 0;
        int c1Height = -1;
        int c2Height = -1;
        JClassSymbol c = site.getSymbol();

        while (c != null) {
            if (c.equals(c1)) {
                c1Height = height;
            }
            if (c.equals(c2)) {
                c2Height = height;
            }
            c = c.getEnclosingClass();
            height++;
        }

        if (c1Height < 0 || c2Height < 0 || c1Height == c2Height) {
            return UNKNOWN;
        }
        return definitely(c1Height < c2Height);
    }

    private boolean isMoreSpecific(JMethodSig m1, JMethodSig m2, MethodCallSite site, MethodResolutionPhase phase) {
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
            JMethodSig fun = TypeOps.findFunctionalInterfaceMethod(ctx.toClassType(ti));
            if (fun == null) {
                // not a functional interface
                infer.checkConvertibleOrDefer(ctx, si, ti, ei, phase);
            }

            // todo special conditions for lambdas/ mrefs

        }

        if (phase.requiresVarargs() && m2Formals.size() == k + 1) {
            // that is, the invocation has no arguments for the varargs, eg Stream.of()
            infer.checkConvertibleOrDefer(ctx, phase.ithFormal(m1Formals, k), m2Formals.get(k), site.getExpr(), phase);
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
