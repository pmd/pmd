/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.JTypeVisitor;
import net.sourceforge.pmd.lang.java.types.SubstVar;
import net.sourceforge.pmd.lang.java.types.TypeSystem;

/**
 * Represents an inference variable. Inference variables are just
 * placeholder for types, used during the inference process.
 * After type inference they should have been erased and hence this
 * type is of no importance outside the implementation of this framework.
 */
public final class InferenceVar implements JTypeMirror, SubstVar {

    private static final String[] NAMES = {
        "α", "β", "γ", "δ", "ε", "ζ", "η", "θ", "ι", "κ", "λ", "μ", "ν",
        "ξ", "ο", "π", "ρ", "σ", "ς", "τ", "υ", "φ", "χ", "ψ", "ω",
    };

    private final InferenceContext ctx;
    private JTypeVar tvar;
    private final int id;
    private Map<BoundKind, Set<JTypeMirror>> bounds = new EnumMap<>(BoundKind.class);
    private JTypeMirror inst;
    private InferenceVar delegate;

    InferenceVar(InferenceContext ctx, JTypeVar tvar, int id) {
        this.ctx = ctx;
        this.tvar = tvar;
        this.id = id;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ctx.ts;
    }

    InferenceContext getCtx() {
        return ctx;
    }

    /**
     * Returns the bounds of a certain kind that apply to
     * this variable.
     */
    Set<JTypeMirror> getBounds(BoundKind kind) {
        return bounds.getOrDefault(kind, Collections.emptySet());
    }


    Set<JTypeMirror> getBounds(Set<BoundKind> kinds) {
        Set<JTypeMirror> bounds = new LinkedHashSet<>();
        for (BoundKind k : kinds) {
            bounds.addAll(getBounds(k));
        }
        return bounds;
    }


    /**
     * Adds a new bound on this variable.
     */
    public void addBound(BoundKind kind, JTypeMirror type) {
        addBound(kind, type, false);
    }

    public void addBound(BoundKind kind, JTypeMirror type, boolean isSubstitution) {
        if (type == this) {
            // may occur because of transitive propagation
            // alpha <: alpha is always true and not interesting
            return;
        }

        if (type instanceof InferenceVar && ((InferenceVar) type).getDelegate() != null) {
            addBound(kind, ((InferenceVar) type).getDelegate());
            return;
        }

        if (delegate != null) {
            delegate.addBound(kind, type);
            return;
        }

        if (type instanceof InferenceVar) {
            if (kind == BoundKind.EQ
                // A <: B && B <: A => A = B
                // this is early propagation
                || getBounds(kind.complement()).contains(type)) {

                ((InferenceVar) type).merge(this);
                return;
            }
        }

        if (type instanceof InferenceVar && ((InferenceVar) type).getDelegate() == this) {

            Set<JTypeMirror> bounds = this.bounds.get(kind);
            if (bounds == null || bounds.isEmpty()) {
                return;
            } else {
                // maybe it's been added previously
                bounds.remove(type);
            }
            return;
        }

        if (bounds.computeIfAbsent(kind, k -> new LinkedHashSet<>()).add(type)) {
            ctx.onBoundAdded(this, kind, type, isSubstitution);
        }
    }

    /**
     * Returns the instantiation of this inference variable if
     * it has already been determined. Returns null otherwise.
     */
    @Nullable
    JTypeMirror getInst() {
        return delegate == null ? inst : delegate.getInst();
    }


    void setInst(JTypeMirror inst) {
        this.inst = inst;
    }

    /**
     * Apply a substitution to the bounds of this variable. Called when
     * an ivar is instantiated.
     *
     * @param substitution The substitution to apply
     */
    void substBounds(Function<? super SubstVar, ? extends JTypeMirror> substitution) {

        for (Entry<BoundKind, Set<JTypeMirror>> entry : bounds.entrySet()) {
            BoundKind kind = entry.getKey();
            Set<JTypeMirror> prevBounds = entry.getValue();


            // put the new bounds before updating
            LinkedHashSet<JTypeMirror> newBounds = new LinkedHashSet<>();
            bounds.put(kind, newBounds);

            for (JTypeMirror prev : prevBounds) {
                // add substituted bound
                JTypeMirror newBound = prev.subst(substitution);
                if (newBound == prev || prevBounds.contains(newBound)) { // NOPMD CompareObjectsWithEquals
                    // not actually new, don't call listeners, etc
                    newBounds.add(prev);
                } else {
                    addBound(kind, newBound);
                }
            }
        }

        if (tvar.isCaptured()) {
            tvar = tvar.substInBounds(substitution);
        }
    }

    JTypeVar getBaseVar() {
        return tvar;
    }


    boolean isCaptured() {
        return tvar.isCaptured();
    }

    InferenceVar getDelegate() {
        return delegate;
    }

    public boolean isSubtypeNoSideEffect(@NonNull JTypeMirror other) {
        return this == other || delegate == other || other.isTop();
    }

    /**
     * Set this ivar's delegate to the given ivar. This
     * - copies all bounds of this ivar onto the delegate
     * - drops all bounds of this ivar
     * - makes {@link #getInst()} delegate to delegate.getInst()
     *
     * <p>The inference engine subsequently only works on the delegate.
     * Proofs about the delegate are reflected by this ivar.
     */
    void merge(InferenceVar candidate) {
        InferenceVar realDelegate = candidate.getDelegate() != null ? candidate.getDelegate() : candidate;
        if (realDelegate == this) {
            return;
        }
        this.delegate = realDelegate;

        Map<BoundKind, Set<JTypeMirror>> bounds = this.bounds;
        this.bounds = Collections.emptyMap();
        for (BoundKind kind : BoundKind.values()) {
            for (JTypeMirror bound : bounds.getOrDefault(kind, Collections.emptySet())) {
                this.delegate.addBound(kind, bound);
            }
        }

        // TODO remove bounds mentioning this var in the delegate

        ctx.onIvarMerged(this, this.delegate);
    }

    @Override
    public @Nullable JTypeDeclSymbol getSymbol() {
        return inst != null ? inst.getSymbol()
                            : new InferenceVarSym(ctx.ts, this);
    }


    @Override
    public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        return subst.apply(this);
    }

    @Override
    public <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitInferenceVar(this, p);
    }


    @Override
    public String toString() {
        return NAMES[id % NAMES.length] + numOfId();
    }

    private String numOfId() {
        int n = id / NAMES.length;
        return n == 0 ? "" : "" + n;
    }


    public enum BoundKind {
        UPPER(" <: ") {
            @Override
            public BoundKind complement() {
                return LOWER;
            }

            @Override
            public Set<BoundKind> complementSet(boolean eqIsAll) {
                return EQ_LOWER;
            }
        },
        EQ(" = ") {
            @Override
            public BoundKind complement() {
                return this;
            }

            @Override
            public Set<BoundKind> complementSet(boolean eqIsAll) {
                return eqIsAll ? ALL : JUST_EQ;
            }
        },
        LOWER(" >: ") {
            @Override
            public BoundKind complement() {
                return UPPER;
            }

            @Override
            public Set<BoundKind> complementSet(boolean eqIsAll) {
                return EQ_UPPER;
            }
        };

        // These sets are shared because otherwise *literal millions* of enumsets are created, with the same constants
        static final Set<BoundKind> ALL = EnumSet.allOf(BoundKind.class);
        static final Set<BoundKind> EQ_LOWER = EnumSet.of(EQ, LOWER);
        private static final Set<BoundKind> EQ_UPPER = EnumSet.of(EQ, UPPER);
        private static final Set<BoundKind> JUST_EQ = Collections.singleton(EQ);

        private final String sym;

        BoundKind(String sym) {
            this.sym = sym;
        }

        public String format(JTypeMirror ivar, JTypeMirror bound) {
            return ivar + sym + bound;
        }

        /**
         * Returns the complementary bound kind.
         * <pre>
         *     complement(LOWER) = UPPER
         *     complement(UPPER) = LOWER
         *     complement(EQ) = EQ
         * </pre>
         */
        public abstract BoundKind complement();


        /**
         * Returns the complement of this kind. There's two ways to complement EQ:
         * - With eqIsAll, this returns all constants.
         * - Otherwise this returns just EQ.
         */
        public abstract Set<BoundKind> complementSet(boolean eqIsAll);

        String getSym() {
            return sym;
        }

        @Override
        public String toString() {
            return sym;
        }
    }
}
