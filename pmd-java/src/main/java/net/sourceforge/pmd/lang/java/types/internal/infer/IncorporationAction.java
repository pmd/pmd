/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.isConvertible;

import java.util.ArrayList;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.InternalApiBridge;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;
import net.sourceforge.pmd.util.AssertionUtil;

/**
 * An action to execute during the incorporation phase.
 * Actions are targeted on a specific ivar (or bound),
 * so that the whole set is not reviewed if there are
 * no changes.
 */
abstract class IncorporationAction {

    final InferenceVar ivar;
    final boolean doApplyToInstVar;

    IncorporationAction(InferenceVar ivar) {
        this(ivar, false);
    }

    IncorporationAction(InferenceVar ivar, boolean doApplyToInstVar) {
        this.ivar = ivar;
        this.doApplyToInstVar = doApplyToInstVar;
    }

    abstract void apply(InferenceContext ctx);

    /**
     * Check that a bound is compatible with the other current bounds
     * of an ivar.
     */
    static class CheckBound extends IncorporationAction {

        private final BoundKind myKind;
        private final JTypeMirror myBound;

        CheckBound(InferenceVar ivar, BoundKind kind, JTypeMirror bound) {
            super(ivar);
            myKind = kind;
            this.myBound = bound;
        }

        /** This is either a class (not interface) or array. */
        private static boolean mayBePrimaryBound(JTypeMirror bound) {
            JTypeDeclSymbol symbol = bound.getSymbol();
            return symbol instanceof JClassSymbol && !symbol.isInterface();
        }

        /** This is either a class, interface, or array. */
        private static boolean hasClassSymbol(JTypeMirror bound) {
            JTypeDeclSymbol symbol = bound.getSymbol();
            return symbol instanceof JClassSymbol;
        }

        /**
         * The list of bound kinds to be checked. If the new bound is
         * equality, then all other bounds need to be checked. Otherwise,
         * if eg the bound is {@code alpha <: T}, then we must check
         * that {@code S <: T} holds for all bounds {@code S <: alpha}.
         */
        Set<BoundKind> boundsToCheck() {
            return myKind.complementSet(true);
        }

        @Override
        public void apply(InferenceContext ctx) {
            for (BoundKind k : boundsToCheck()) {
                for (JTypeMirror b : ivar.getBounds(k)) {
                    if (!checkBound(b, k, ctx)) {
                        throw ResolutionFailedException.incompatibleBound(ctx.logger, ivar, myKind, myBound, k, b);
                    }
                }
            }

            if (myKind == BoundKind.UPPER && hasClassSymbol(myBound)) {
                // Check that other upper bounds that are class types are related to this bound.
                // Otherwise, GLB does not exist and its construction would fail during ReductionStep#UPPER.

                for (JTypeMirror otherBound : ivar.getBounds(BoundKind.UPPER)) {
                    if (otherBound != myBound && hasClassSymbol(otherBound)) { // NOPMD CompareObjectsWithEquals
                        // So we have ivar <: myBound and ivar <: otherBound
                        // But we don't know whether myBound <: otherBound or the other way around.
                        // We will check in both directions without adding constraints first, otherwise
                        // we risk introducing contradictions.
                        BoundKind ordering = getOrderingBetweenBounds(myBound, otherBound);

                        if (ordering == null) {
                            if (mustTypesHaveAnOrdering(myBound, otherBound)) {
                                throw ResolutionFailedException.incompatibleBound(ctx.logger, ivar, myKind, myBound, BoundKind.UPPER, otherBound);
                            }
                            continue;
                        }

                        // Now that we found out the relative ordering of myBound and otherBound,
                        // we can add constraints on them without creating extra contradictions.
                        switch (ordering) {
                        case UPPER:
                            // otherBound <: myBound
                            checkBound(false, otherBound, myBound, ctx);
                            break;
                        case EQ:
                            // mybound = otherBound
                            checkBound(true, myBound, otherBound, ctx);
                            break;
                        case LOWER:
                            // mybound <: otherBound
                            checkBound(false, myBound, otherBound, ctx);
                            break;
                        default:
                            throw AssertionUtil.shouldNotReachHere("exhaustive switch");
                        }
                    }
                }
            }
        }

        private static boolean mustTypesHaveAnOrdering(JTypeMirror myBound, JTypeMirror otherBound) {
            return mayBePrimaryBound(myBound) && mayBePrimaryBound(otherBound)
                   || myBound.getErasure().isConvertibleTo(otherBound.getErasure()).somehow()
                   || otherBound.getErasure().isConvertibleTo(myBound.getErasure()).somehow();
        }

        private static @Nullable BoundKind getOrderingBetweenBounds(JTypeMirror myBound, JTypeMirror otherBound) {
            // Since we are testing both directions we cannot let those tests add bounds on the ivars,
            // because they could be contradictory.

            if (TypeOps.isConvertiblePure(myBound, otherBound).somehow()) {
                Convertibility otherConvertible = TypeOps.isConvertiblePure(otherBound, myBound);
                if (otherConvertible.withoutWarnings()) {
                    return BoundKind.EQ;
                } else {
                    return BoundKind.LOWER;
                }
            } else if (TypeOps.isConvertiblePure(otherBound, myBound).somehow()) {
                return BoundKind.UPPER;
            }
            return null;
        }

        /**
         * Check compatibility between this bound and another.
         */
        private boolean checkBound(JTypeMirror otherBound, BoundKind otherKind, InferenceContext ctx) {
            // myKind != EQ => otherKind != myKind

            int compare = myKind.compareTo(otherKind);

            // these tests perform side-effects on the mentioned inference vars,
            // effectively setting constraints on them

            if (compare > 0) {
                // myBound <: alpha, alpha <: otherBound
                return checkBound(false, myBound, otherBound, ctx);
            } else if (compare < 0) {
                // otherBound <: alpha, alpha <: myBound
                return checkBound(false, otherBound, myBound, ctx);
            } else {
                return checkBound(true, myBound, otherBound, ctx);
            }
        }

        /**
         * If 'eq', checks that {@code T = S}, else checks that {@code T <: S}.
         */
        static boolean checkBound(boolean eq, JTypeMirror t, JTypeMirror s, InferenceContext ctx) {
            // eq bounds are so rare we shouldn't care if they're cached
            return eq ? InternalApiBridge.isSameTypeInInference(t, s)
                      : checkSubtype(t, s, ctx);
        }

        private static boolean checkSubtype(JTypeMirror t, JTypeMirror s, InferenceContext ctx) {
            if (ctx.getSupertypeCheckCache().isCertainlyASubtype(t, s)) {
                return true; // supertype was already cached
            }

            Convertibility isConvertible = isConvertible(t, s);
            boolean doCache = true;
            if (isConvertible.withUncheckedWarning()) {
                ctx.setNeedsUncheckedConversion();
                // cannot cache those, or the side effect
                // will not occur on every context
                doCache = false;
            }

            boolean result = isConvertible.somehow();
            if (doCache && result) {
                ctx.getSupertypeCheckCache().remember(t, s);
            }
            return result;
        }

        @Override
        public String toString() {
            return "Check " + myKind.format(ivar, myBound);
        }


    }

    /**
     * Replace a free vars in bounds with its instantiation, and check
     * that the inferred type conforms to all bounds.
     */
    static class SubstituteInst extends IncorporationAction {

        private final JTypeMirror inst;

        SubstituteInst(InferenceVar ivar, JTypeMirror inst) {
            super(ivar, true);
            this.inst = inst;
        }

        @Override
        public void apply(InferenceContext ctx) {
            if (inst != null) {
                for (InferenceVar freeVar : ctx.getFreeVars()) {
                    freeVar.substBounds(it -> ivar.isEquivalentTo(it) ? inst : it);
                }
                // check instantiation is compatible
                new CheckBound(ivar, BoundKind.EQ, inst).apply(ctx);
            }
        }

        @Override
        public String toString() {
            return "Substitute " + ivar + " with " + inst;
        }
    }

    /** Propagate all bounds of an ivar. */
    static class PropagateAllBounds extends IncorporationAction {

        PropagateAllBounds(InferenceVar ivar) {
            super(ivar);
        }

        @Override
        void apply(InferenceContext ctx) {
            for (BoundKind kind : BoundKind.values()) {
                for (JTypeMirror bound : new ArrayList<>(ivar.getBounds(kind))) { //copy to avoid comodification
                    new PropagateBounds(ivar, kind, bound).apply(ctx);
                }
            }
        }


        @Override
        public String toString() {
            return "Propagate all bounds of " + ivar;
        }
    }

    /**
     * Propagates the bounds of an ivar to the ivars already appearing
     * in its bounds.
     */
    static class PropagateBounds extends IncorporationAction {


        private final BoundKind kind;
        private final JTypeMirror bound;

        PropagateBounds(InferenceVar ivar, BoundKind kind, JTypeMirror bound) {
            super(ivar);
            this.kind = kind;
            this.bound = bound;
        }

        @Override
        public void apply(InferenceContext ctx) {
            InferenceVar alpha = ivar;

            // forward propagation
            // alpha <: T
            //   && alpha >: beta ~> beta <: T      |  beta <: alpha <: T
            //   && alpha = beta  ~> beta <: T      |  beta =  alpha <: T

            // alpha >: T
            //   && alpha <: beta ~> beta >: T      |  T <: alpha <: beta
            //   && alpha = beta  ~> beta >: T      |  T <: alpha =  beta

            for (JTypeMirror b : alpha.getBounds(kind.complement())) {
                if (b instanceof InferenceVar) {
                    InferenceVar beta = (InferenceVar) b;
                    beta.addBound(kind, ctx.ground(bound));
                }
            }

            if (bound instanceof InferenceVar) {
                InferenceVar beta = (InferenceVar) bound;

                if (kind == BoundKind.EQ) {
                    beta.adoptAllBounds(alpha);
                    return;
                }

                // symmetric propagation

                // alpha <: beta ~> beta >: alpha
                // alpha >: beta ~> beta <: alpha
                beta.addBound(kind.complement(), alpha);

                // backwards propagation
                // alpha <: beta
                //   && beta = T  ~> alpha <: T
                //   && beta <: T ~> alpha <: T

                // alpha >: beta
                //   && beta = T  ~> alpha >: T
                //   && beta >: T ~> alpha >: T

                for (JTypeMirror b : beta.getBounds(kind)) {
                    alpha.addBound(kind, b);
                }
            }
        }

        @Override
        public String toString() {
            return "Propagate bound " + kind.format(ivar, bound);
        }
    }

}
