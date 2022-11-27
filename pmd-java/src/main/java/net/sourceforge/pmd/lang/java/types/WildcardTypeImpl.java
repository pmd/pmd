/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.util.CollectionUtil;


final class WildcardTypeImpl implements JWildcardType {

    private final JTypeMirror bound;
    private final boolean isUpperBound;
    private final TypeSystem ts;
    private final List<SymAnnot> typeAnnots;


    WildcardTypeImpl(TypeSystem ts, boolean isUpperBound, @Nullable JTypeMirror bound, List<SymAnnot> typeAnnots) {
        this.ts = ts;
        this.typeAnnots = typeAnnots;
        this.bound = bound != null ? bound
                                   : isUpperBound ? ts.OBJECT : ts.NULL_TYPE;
        this.isUpperBound = isUpperBound;
    }

    @Override
    public JWildcardType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst) {
        JTypeMirror newBound = getBound().subst(subst);
        return newBound == getBound() ? this : ts.wildcard(isUpperBound(), newBound); // NOPMD CompareObjectsWithEquals
    }

    @Override
    public List<SymAnnot> getTypeAnnotations() {
        return typeAnnots;
    }

    @Override
    public JTypeMirror withAnnotations(List<SymAnnot> symAnnots) {
        if (symAnnots.isEmpty() && !typeAnnots.isEmpty()) {
            return ts.wildcard(isUpperBound, bound);
        } else if (!symAnnots.isEmpty()) {
            return new WildcardTypeImpl(ts, isUpperBound(), bound, CollectionUtil.defensiveUnmodifiableCopy(symAnnots));
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
