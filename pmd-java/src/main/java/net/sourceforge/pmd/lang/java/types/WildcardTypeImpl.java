/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;


final class WildcardTypeImpl implements JWildcardType {

    private final JTypeMirror bound;
    private final boolean isUpperBound;
    private final TypeSystem ts;
    private final PSet<SymAnnot> typeAnnots;


    WildcardTypeImpl(TypeSystem ts, boolean isUpperBound, @Nullable JTypeMirror bound, PSet<SymAnnot> typeAnnots) {
        this.ts = ts;
        this.typeAnnots = typeAnnots;
        this.bound = bound != null ? bound
                                   : isUpperBound ? ts.OBJECT : ts.NULL_TYPE;
        this.isUpperBound = isUpperBound;
    }

    @Override
    public JWildcardType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newBound = getBound().subst(subst);
        return newBound == getBound() ? this : ts.wildcard(isUpperBound(), newBound).withAnnotations(typeAnnots); // NOPMD CompareObjectsWithEquals
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        return typeAnnots;
    }

    @Override
    public JWildcardType withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        if (newTypeAnnots.isEmpty() && !typeAnnots.isEmpty()) {
            return ts.wildcard(isUpperBound, bound);
        } else if (!newTypeAnnots.isEmpty()) {
            return new WildcardTypeImpl(ts, isUpperBound(), bound, newTypeAnnots);
        }
        return this;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return ts;
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
