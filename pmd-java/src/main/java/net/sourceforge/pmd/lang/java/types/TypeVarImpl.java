/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;

@SuppressWarnings("PMD.CompareObjectsWithEquals")
abstract class TypeVarImpl implements JTypeVar {


    final TypeSystem ts;

    // constructor only for the captured version.
    private TypeVarImpl(TypeSystem ts) {
        this.ts = ts;
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
        return new CapturedTypeVar(wildcard);
    }

    static final class RegularTypeVar extends TypeVarImpl {

        private final @NonNull JTypeParameterSymbol symbol;
        private JTypeMirror upperBound;

        RegularTypeVar(TypeSystem ts, @NonNull JTypeParameterSymbol symbol) {
            super(ts);
            this.symbol = symbol;
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
        public JTypeVar substInBounds(Function<? super SubstVar, ? extends @NonNull JTypeMirror> substitution) {
            JTypeMirror newBound = getUpperBound().subst(substitution);
            if (newBound == upperBound) {
                return this;
            }
            RegularTypeVar clone = new RegularTypeVar(this.ts, this.symbol);
            clone.upperBound = newBound;
            return clone;
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

        private CapturedTypeVar(JWildcardType wild) {
            this(wild, wild.asLowerBound(), wild.asUpperBound());
        }

        private CapturedTypeVar(JWildcardType wild, JTypeMirror lower, JTypeMirror upper) {
            super(wild.getTypeSystem());
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
            return new CapturedTypeVar(wild, lower, upper);
        }

        @Override
        public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
            return new CapturedTypeVar(wildcard, lower, upper);
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
