/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeVar.FreshTypeVar;

class TypeVarImpl implements FreshTypeVar {


    private final @Nullable JTypeParameterSymbol origin;
    private final TypeSystem ts;
    private JTypeMirror upperBound;
    private JTypeMirror lowerBound;

    // constructor only for the captured version.
    private TypeVarImpl(TypeSystem ts,
                        @Nullable JTypeMirror upperBound,
                        @Nullable JTypeMirror lowerBound,
                        @Nullable JTypeParameterSymbol origin) {
        this.ts = ts;
        this.upperBound = upperBound == null ? ts.OBJECT : upperBound;
        this.lowerBound = lowerBound == null ? ts.NULL_TYPE : lowerBound;
        this.origin = origin;
    }

    TypeVarImpl(TypeSystem ts, @NonNull JTypeParameterSymbol tvar) {
        this.ts = ts;
        this.origin = tvar;
        this.upperBound = null;
        this.lowerBound = ts.NULL_TYPE;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public String getName() {
        return origin == null ? null : origin.getSimpleName();
    }

    @Override
    public @Nullable JTypeParameterSymbol getSymbol() {
        return origin;
    }

    @Override
    public void setUpperBound(@NonNull JTypeMirror upperBound) {
        this.upperBound = upperBound;
    }

    @Override
    public void setLowerBound(@NonNull JTypeMirror lowerBound) {
        this.lowerBound = lowerBound;
    }

    @Override
    public @NonNull JTypeMirror getUpperBound() {
        if (upperBound == null) {
            upperBound = origin.computeUpperBound();
        }
        return upperBound;
    }

    @Override
    public @NonNull JTypeMirror getLowerBound() {
        return lowerBound;
    }

    @Override
    public boolean isCaptured() {
        return false;
    }

    @Override
    public JTypeMirror getErasure() {
        return getUpperBound().getErasure();
    }

    @Override
    public boolean isCaptureOf(JWildcardType wildcard) {
        return false;
    }

    @Override
    public @Nullable JWildcardType getCapturedOrigin() {
        return null;
    }

    @Override
    public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
        return new TypeVarImpl(ts, upper, lower, origin);
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
    static FreshTypeVar freshCapture(JWildcardType wildcard) {
        return new CapturedTypeVarImpl(wildcard);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeVarImpl that = (TypeVarImpl) o;
        return origin != null && origin.equals(that.origin);
    }

    @Override
    public int hashCode() {
        return origin.hashCode();
    }

    private static final class CapturedTypeVarImpl extends TypeVarImpl {

        private static final int PRIME = 997;  // largest prime less than 1000

        private final JWildcardType wildcard;
        private final int captureId = hashCode() % PRIME;

        CapturedTypeVarImpl(JWildcardType wild) {
            this(wild, wild.asLowerBound(), wild.asUpperBound());
        }

        CapturedTypeVarImpl(JWildcardType wild, JTypeMirror lower, JTypeMirror upper) {
            super(wild.getTypeSystem(), upper, lower, null);
            this.wildcard = wild;
        }

        public JWildcardType getWildcard() {
            return wildcard;
        }


        @Override
        public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
            return new CapturedTypeVarImpl(wildcard, lower, upper);
        }

        @Override
        public boolean equals(Object o) {
            return this == o || o instanceof CaptureMatcher && o.equals(this);
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }

        @Override
        public boolean isCaptured() {
            return true;
        }

        @Override
        public boolean isCaptureOf(JWildcardType wildcard) {
            return this.wildcard == wildcard;
        }

        @Override
        public @Nullable JWildcardType getCapturedOrigin() {
            return wildcard;
        }

        @Override
        public String getName() {
            return null;
        }

        @Override
        public String toString() {
            // doesn't use TypePrettyPrint
            return "capture#" + captureId + " of " + wildcard;
        }
    }
}
