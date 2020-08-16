/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeVar.FreshTypeVar;


final class WildcardTypeImpl implements JWildcardType {

    private final JTypeMirror bound;
    private final boolean isUpperBound;
    private final TypeSystem ts;


    WildcardTypeImpl(TypeSystem ts, boolean isUpperBound, @Nullable JTypeMirror bound) {
        this.ts = ts;
        this.bound = bound != null ? bound
                                   : isUpperBound ? ts.OBJECT : ts.NULL_TYPE;
        this.isUpperBound = isUpperBound;
    }

    @Override
    public JTypeMirror subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newBound = getBound().subst(subst);
        return newBound == getBound() ? this : ts.wildcard(isUpperBound(), newBound);
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
    }

    @Override
    public FreshTypeVar captureWildcard() {
        return TypeVarImpl.freshCapture(this);
    }

    @Override
    public @NonNull JTypeMirror getBound() {
        return bound;
    }


    @Override
    public boolean isUpperBound() {
        return isUpperBound;
    }

    @Override
    public String toString() {
        return TypePrettyPrint.prettyPrint(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JWildcardType)) {
            return false;
        }
        JWildcardType that = (JWildcardType) o;
        return isUpperBound == that.isUpperBound() && Objects.equals(bound, that.getBound());
    }

    @Override
    public int hashCode() {
        return Objects.hash(bound, isUpperBound);
    }
}
