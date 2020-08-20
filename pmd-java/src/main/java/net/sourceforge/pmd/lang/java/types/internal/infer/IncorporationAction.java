/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static net.sourceforge.pmd.lang.java.types.TypeOps.isConvertible;
import static net.sourceforge.pmd.lang.java.types.TypeOps.isSameType;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JPrimitiveType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
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
     * Check that a bound is compatible with the other current bounds
     * of an ivar.
     */
    static class CheckBound extends IncorporationAction {

        private static final ThreadLocal<Map<JTypeMirror, Set<JTypeMirror>>> CHECK_CACHE = ThreadLocal.withInitial(() -> new LinkedHashMap<JTypeMirror, Set<JTypeMirror>>() {
            // Even with a relatively small cache size, the hit ratio is
            // very high (around 75% on the tests we have here, discounting
            // the stress tests)
            // TODO analyse on real data - needs a realistic disambiguation
            private static final int MAX_SIZE = 200;

            @Override
            protected boolean removeEldestEntry(Entry<JTypeMirror, Set<JTypeMirror>> eldest) {
                return size() > MAX_SIZE;
            }
        });


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
         * if eg the bound is {@literal alpha <: T}, then we must check
         * that {@literal S <: T} holds for all bounds {@literal S <: alpha}.
         */
        Set<BoundKind> boundsToCheck() {
            return myKind.complementSet(true);
        }

        @Override
        public void apply(InferenceContext ctx) {
            for (BoundKind k : boundsToCheck()) {
                for (JTypeMirror b : ivar.getBounds(k)) {
                    if (!checkBound(b, k)) {
                        throw ResolutionFailedException.incompatibleBound(ctx.logger, ivar, myKind, myBound, k, b);
                    }
                }
            }
        }

        /**
         * Check compatibility between this bound and another.
         */
        private boolean checkBound(JTypeMirror otherBound, BoundKind otherKind) {
            // myKind != EQ => otherKind != myKind

            int compare = myKind.compareTo(otherKind);

            // these tests perform side-effects on the mentioned inference vars,
            // effectively setting constraints on them

            if (compare > 0) {
                // myBound <: alpha, alpha <: otherBound
                return checkBound(false, myBound, otherBound);
            } else if (compare < 0) {
                // otherBound <: alpha, alpha <: myBound
                return checkBound(false, otherBound, myBound);
            } else {
                return checkBound(true, myBound, otherBound);
            }
        }

        /**
         * If 'eq', checks that {@code T = S}, else checks that {@code T <: S}.
         */
        boolean checkBound(boolean eq, JTypeMirror t, JTypeMirror s) {
            // eq bounds are so rare we shouldn't care if they're cached
            return eq ? isSameType(t, s, true)
                      : checkSubtype(t, s);
        }

        private static boolean checkSubtype(JTypeMirror t, JTypeMirror s) {
            JTypeMirror key = cacheKey(t);
            if (key == null) { // don't cache result
                return isConvertible(t, s).somehow();
            }

            Set<JTypeMirror> supertypesOfT = CHECK_CACHE.get().computeIfAbsent(key, k -> new HashSet<>());
            if (supertypesOfT.contains(s)) {
                return true;
            } else if (isConvertible(t, s).somehow()) {
                supertypesOfT.add(s);
                return true;
            }
            return false;
        }

        private static @Nullable JTypeMirror cacheKey(JTypeMirror t) {
            if (t instanceof InferenceVar || t instanceof JPrimitiveType) {
                return null; // don't cache those
            } else if (t instanceof JTypeVar) {
                JTypeVar tvar = (JTypeVar) t;
                if (tvar.isCaptured()) {
                    // don't cache captures of the same wildcard separately
                    return tvar.getCapturedOrigin();
                }
            }
            return t;
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
                    freeVar.substBounds(it -> isInstanceOfThisVar(it) ? inst : it);
                }
                // check instantiation is compatible
                new CheckBound(ivar, BoundKind.EQ, inst).apply(ctx);
            }
        }

        private boolean isInstanceOfThisVar(JTypeMirror it) {
            return it == ivar // NOPMD CompareObjectsWithEquals
                || it instanceof InferenceVar && ((InferenceVar) it).getDelegate() == ivar;
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
                for (JTypeMirror bound : ivar.getBounds(kind)) {
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

            // beta = alpha, merge beta into alpha
            if (kind == BoundKind.EQ && bound instanceof InferenceVar) {
                ((InferenceVar) bound).merge(alpha);
                return;
            }

            // The following is the hottest spot in the inference framework
            // (10% of runtime on the stress tests)

            // forward propagation
            // alpha <: T and beta <: alpha => beta <: T
            //            and beta = alpha => beta <: T
            // it's symmetric for upper bounds
            for (BoundKind k : kind.complementSet(false)) {
                for (JTypeMirror b : alpha.getBounds(k)) {
                    if (b instanceof InferenceVar) {
                        InferenceVar beta = (InferenceVar) b;
                        beta.addBound(kind, ctx.ground(bound));
                    }
                }
            }

            if (bound instanceof InferenceVar) {
                InferenceVar beta = (InferenceVar) bound;
                // symmetric propagation
                // Eg propagates alpha <: beta to beta >: alpha
                beta.addBound(kind.complement(), alpha);

                // backwards propagation
                // alpha <: beta causes alpha to adopt all the upper bounds of beta
                for (BoundKind k : kind.complementSet(true)) {
                    for (JTypeMirror b : beta.getBounds(k)) {
                        alpha.addBound(k, b);
                    }
                }
            }
        }

        @Override
        public String toString() {
            return "Propagate bound " + kind.format(ivar, bound);
        }
    }

}
