/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.Objects;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * Test only. This binds to the first capture variable it tests equal
 * with, and keeps the same binding forever.
 */
final class CaptureMatcher implements JTypeVar {

    private final JWildcardType wild;
    private @Nullable JTypeVar captured = null;

    CaptureMatcher(JWildcardType wild) {
        this.wild = wild;
    }

    @Override
    public TypeSystem getTypeSystem() {
        return wild.getTypeSystem();
    }

    @Override
    public boolean isCaptured() {
        return true;
    }

    @Override
    public JTypeVar withAnnotations(PSet<SymAnnot> newTypeAnnots) {
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public JTypeVar withUpperBound(@NonNull JTypeMirror newUB) {
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public PSet<SymAnnot> getTypeAnnotations() {
        if (captured != null) {
            return captured.getTypeAnnotations();
        }
        return HashTreePSet.empty();
    }

    @Override
    public @Nullable JTypeParameterSymbol getSymbol() {
        return null; // captured
    }

    @Override
    public @NonNull String getName() {
        if (captured != null) {
            return captured.getName();
        }
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public @NonNull JTypeMirror getUpperBound() {
        return captured != null ? captured.getUpperBound() : getTypeSystem().OBJECT;
    }

    @Override
    public @NonNull JTypeMirror getLowerBound() {
        return captured != null ? captured.getLowerBound() : getTypeSystem().NULL_TYPE;
    }

    @Override
    public boolean isCaptureOf(JWildcardType wildcard) {
        return this.wild.equals(wildcard);
    }

    @Override
    public @Nullable JWildcardType getCapturedOrigin() {
        return wild;
    }

    @Override
    public JTypeVar cloneWithBounds(JTypeMirror lower, JTypeMirror upper) {
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public JTypeVar substInBounds(Function<? super SubstVar, ? extends @NonNull JTypeMirror> substitution) {
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof JTypeVar)) {
            return false;
        }
        if (captured != null) {
            return captured.equals(o);
        }

        JTypeVar that = (JTypeVar) o;
        if (!Objects.equals(that.getCapturedOrigin(), this.getCapturedOrigin())) {
            return false;
        }
        this.captured = that;
        return true;
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("this is a test only object which should only be used for equals");
    }

    @Override
    public String toString() {
        return captured == null ? "unbound capture matcher"
                                : "bound(" + captured.toString() + ")";
    }
}
