/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.isConvertible;

import java.util.ArrayList;
import java.util.Set;

import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.InternalApiBridge;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeOps.Convertibility;
import net.sourceforge.pmd.lang.java.types.internal.infer.InferenceVar.BoundKind;

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
     * Check that an upper bound with a class (not interface) or array
     * is compatible with other upper bounds of class or array type.
     * This is necessary to guarantee the existence of a glb for these,
     * for {@link ReductionStep#UPPER}.
     *
     * <p>If the bound is {@code alpha <: T}, then we must check
     * that {@code S <: T} or {@code T <: S} holds for all bounds
     * {@code alpha <: S}, where S is a class or array type. Otherwise,
     * the GLB does not exist.
     */
    static class CheckClassUpperBound extends IncorporationAction {

        private final JTypeMirror myBound;

        CheckClassUpperBound(InferenceVar ivar, JTypeMirror bound) {
            super(ivar);
            this.myBound = bound;
        }

        public static boolean needsCheck(BoundKind kind, JTypeMirror bound) {
            if (kind == BoundKind.UPPER) {
                JTypeDeclSymbol symbol = bound.getSymbol();
                return symbol instanceof JClassSymbol && !symbol.isInterface();
            }
            return false;
        }


        @Override
        public void apply(InferenceContext ctx) {
            for (BoundKind k : BoundKind.EQ_UPPER) {
                for (JTypeMirror b : ivar.getBounds(k)) {
                    if (!checkBound(b, ctx)) {
                        throw ResolutionFailedException.incompatibleBound(ctx.logger, ivar, BoundKind.UPPER, myBound, k, b);
                    }
                }
            }
        }

        private boolean checkBound(JTypeMirror otherBound, InferenceContext ctx) {

            JTypeDeclSymbol sym = otherBound.getSymbol();
            // either the bound is not a concrete class or array type
            return !(sym instanceof JClassSymbol) || sym.isInterface()
                // or both bounds are related in some way
                || CheckBound.checkBound(false, otherBound, myBound, ctx)
                || CheckBound.checkBound(false, myBound, otherBound, ctx);

        }


        @Override
        public String toString() {
            return "Check class bound " + BoundKind.UPPER.format(ivar, myBound);
        }


    }

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
