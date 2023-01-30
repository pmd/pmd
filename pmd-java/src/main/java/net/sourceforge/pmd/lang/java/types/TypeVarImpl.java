/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.util.AssertionUtil;

@SuppressWarnings("PMD.CompareObjectsWithEquals")
abstract class TypeVarImpl implements JTypeVar {


    final TypeSystem ts;
    JTypeMirror upperBound;

    /**
     * These are only the type annotations on a usage of the variable.
     * Annotations on the declaration of the tparam are not considered.
     */
    final PSet<SymAnnot> typeAnnots;

    // constructor only for the captured version.
    private TypeVarImpl(TypeSystem ts, PSet<SymAnnot> typeAnnots) {
        this.ts = ts;
        this.typeAnnots = Objects.requireNonNull(typeAnnots);
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return typeAnnots;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public JTypeMirror getErasure() {
        return getUpperBound().getErasure();
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    /**
     * Returns a fresh type variable, whose bounds will be initialised by
     * the capture conversion algo in {@link TypeConversion#capture(JTypeMirror)}.
     * Captured variables use reference identity as equality relation.
     */
    static TypeVarImpl.CapturedTypeVar freshCapture(JWildcardType wildcard) {
        return new CapturedTypeVar(wildcard, wildcard.getTypeAnnotations());
    }

    static final class RegularTypeVar extends TypeVarImpl {

        private final @NonNull JTypeParameterSymbol symbol;

        RegularTypeVar(TypeSystem ts, @NonNull JTypeParameterSymbol symbol, PSet<SymAnnot> typeAnnots) {
            super(ts, typeAnnots);
            this.symbol = symbol;
        }

        private RegularTypeVar(RegularTypeVar base, PSet<SymAnnot> typeAnnots) {
            this(base.ts, base.symbol, typeAnnots);
            this.upperBound = base.upperBound;
        }

        @Override
        public boolean isCaptured() {
            return false;
        }

        @Override
        public @NonNull JTypeMirror getLowerBound() {
            return ts.NULL_TYPE;
        }

        @Override
        public @NonNull JTypeParameterSymbol getSymbol() {
            return symbol;
        }

        @Override
        public @NonNull String getName() {
            return symbol.getSimpleName();
        }

        @Override
        public @NonNull JTypeMirror getUpperBound() {
            if (upperBound == null) {
                upperBound = symbol.computeUpperBound();
            }
            return upperBound;
        }

        @Override
        public JTypeVar withUpperBound(@NonNull JTypeMirror newUB) {
            RegularTypeVar tv = new RegularTypeVar(this, this.typeAnnots);
            tv.upperBound = newUB;
            return tv;
        }


        @Override
        public JTypeVar withAnnotations(PSet<SymAnnot> newTypeAnnots) {
            if (newTypeAnnots.isEmpty() && this.typeAnnots.isEmpty()) {
                return this;
            }
            return new RegularTypeVar(this, newTypeAnnots);
        }

        @Override
        public JTypeVar substInBounds(Function<? super SubstVar, ? extends @NonNull JTypeMirror> substitution) {
            if (Substitution.isEmptySubst(substitution)) {
                return this;
            }
            JTypeMirror newBound = getUpperBound().subst(substitution);
            if (newBound == upperBound) {
                return this;
            }
            RegularTypeVar newTVar = new RegularTypeVar(this.ts, this.symbol, this.getTypeAnnotations());
            newTVar.upperBound = newBound;
            return newTVar;
        }

        @Override
        public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
            throw new UnsupportedOperationException("Not a capture variable");
        }

        @Override
        public boolean isCaptureOf(JWildcardType wildcard) {
            return false;
        }

        @Override
        public @Nullable JWildcardType getCapturedOrigin() {
            return null;
        }

        // we only compare the symbol
        // the point is to make tvars whose bound was substed equal to the original
        // tvar, for substs to work repeatedly. Maybe improving how JMethodSig works
        // would remove the need for that
        // Eg it would be nice to conceptualize JMethodSig as just a method symbol +
        // a substitution mapping type params in scope at that point to actual types
        // The problem is that we may want to subst it with type vars, and then use
        // those

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RegularTypeVar that = (RegularTypeVar) o;
            return symbol.equals(that.symbol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(symbol);
        }
    }

    static final class CapturedTypeVar extends TypeVarImpl {

        private static final int PRIME = 997;  // largest prime less than 1000

        private final @NonNull JWildcardType wildcard;

        private JTypeMirror upperBound;
        private JTypeMirror lowerBound;

        private CapturedTypeVar(JWildcardType wild, PSet<SymAnnot> typeAnnots) {
            this(wild, wild.asLowerBound(), wild.asUpperBound(), typeAnnots);
        }

        private CapturedTypeVar(JWildcardType wild, @NonNull JTypeMirror lower, @NonNull JTypeMirror upper, PSet<SymAnnot> typeAnnots) {
            super(wild.getTypeSystem(), typeAnnots);
            this.upperBound = upper;
            this.lowerBound = lower;
            this.wildcard = wild;
        }

        void setUpperBound(@NonNull JTypeMirror upperBound) {
            this.upperBound = upperBound;
        }


        void setLowerBound(@NonNull JTypeMirror lowerBound) {
            this.lowerBound = lowerBound;
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof CaptureMatcher && o.equals(this);
        }

        @Override
        public int hashCode() { // NOPMD UselessOverridingMethod
            return super.hashCode();
        }

        @Override
        public boolean isCaptured() {
            return true;
        }

        @Override
        public boolean isCaptureOf(JWildcardType wildcard) {
            return this.wildcard.equals(wildcard);
        }

        @Override
        public JWildcardType getCapturedOrigin() {
            return wildcard;
        }

        @Override
        public JTypeVar substInBounds(Function<? super SubstVar, ? extends @NonNull JTypeMirror> substitution) {
            JWildcardType wild = this.wildcard.subst(substitution);
            JTypeMirror lower = getLowerBound().subst(substitution);
            JTypeMirror upper = getUpperBound().subst(substitution);
            if (wild == this.wildcard && lower == this.lowerBound && upper == this.lowerBound) {
                return this;
            }
            return new CapturedTypeVar(wild, lower, upper, getTypeAnnotations());
        }

        @Override
        public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
            return new CapturedTypeVar(wildcard, lower, upper, getTypeAnnotations());
        }


        @Override
        public JTypeVar withAnnotations(PSet<SymAnnot> newTypeAnnots) {
            if (newTypeAnnots.isEmpty() && typeAnnots.isEmpty()) {
                return this;
            }
            return new CapturedTypeVar(wildcard, lowerBound, upperBound, newTypeAnnots);
        }

        @Override
        public JTypeVar withUpperBound(@NonNull JTypeMirror newUB) {
            AssertionUtil.requireParamNotNull("upper bound", newUB);
            return new CapturedTypeVar(wildcard, lowerBound, newUB, getTypeAnnotations());
        }

        @Override
        public @NonNull JTypeMirror getUpperBound() {
            return upperBound;
        }

        @Override
        public @NonNull JTypeMirror getLowerBound() {
            return lowerBound;
        }

        @Override
        public @Nullable JTypeParameterSymbol getSymbol() {
            return null;
        }

        @Override
        public @NonNull String getName() {
            return "capture#" + hashCode() % PRIME + " of " + wildcard;
        }
    }
}
