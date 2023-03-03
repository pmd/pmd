/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * Represents a wildcard type. Such types are converted to {@link JTypeVar}
 * by {@linkplain TypeConversion#capture(JTypeMirror) capture conversion}.
 *
 * <p>This implements JTypeMirror for convenience, however, it may only
 * occur as a type argument, and as such some of the behaviour of JTypeMirror
 * is undefined: {@link #isSubtypeOf(JTypeMirror) subtyping} and {@link #getErasure() erasure}.
 */
public interface JWildcardType extends JTypeMirror {


    /** Returns the bound. Interpretation is given by {@link #isUpperBound()}. */
    @NonNull
    JTypeMirror getBound();



    /** Returns true if this is an "extends" wildcard, with no bound ("?"). */
    default boolean isUnbounded() {
        return isUpperBound() && getBound().isTop();
    }


    /** Returns true if this is an "extends" wildcard, the bound is then an upper bound. */
    boolean isUpperBound();


    /** Returns true if this is a "super" wildcard, the bound is then a lower bound. */
    default boolean isLowerBound() {
        return !isUpperBound();
    }


    /** Returns the lower bound, or the bottom type if this is an "extends" wildcard. */
    default @NonNull JTypeMirror asLowerBound() {
        return isUpperBound() ? getTypeSystem().NULL_TYPE : getBound();
    }


    /** Returns the upper bound, or Object if this is a "super" wildcard. */
    default @NonNull JTypeMirror asUpperBound() {
        return isUpperBound() ? getBound() : getTypeSystem().OBJECT;
    }


    /**
     * This is implemented for convenience. However, the erasure of a
     * wildcard type is undefined and useless. This is because they can
     * only occur in type arguments, which are erased themselves.
     */
    @Override
    default JTypeMirror getErasure() {
        return this;
    }

    @Override
    default Stream<JMethodSig> streamMethods(Predicate<? super JMethodSymbol> prefilter) {
        return asUpperBound().streamMethods(prefilter);
    }

    @Override
    JWildcardType subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);

    @Override
    JWildcardType withAnnotations(PSet<SymAnnot> newTypeAnnots);

    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitWildcard(this, p);
    }

}
