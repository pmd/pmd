/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Common supertype for {@link JMethodSig} and {@link JTypeMirror}.
 * Those are the kinds of objects that a {@link JTypeVisitor} can
 * explore.
 */
public interface JTypeVisitable {

    /**
     * Replace the type variables occurring in the given type by their
     * image by the given function. Substitutions are not applied
     * recursively (ie, is not applied on the result of a substitution).
     *
     * @param subst Substitution function, eg a {@link Substitution}
     */
    JTypeVisitable subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);


    /**
     * Accept a type visitor, dispatching on this object's runtime type
     * to the correct method of the visitor.
     *
     * @param <P> Type of data of the visitor
     * @param <T> Type of result of the visitor
     */
    <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p);

}
