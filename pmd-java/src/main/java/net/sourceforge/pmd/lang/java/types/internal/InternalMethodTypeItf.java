/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal;

import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.SubstVar;

/**
 * Internal API of {@link JMethodSig}. These methods are internal to
 * the inference framework.
 */
public interface InternalMethodTypeItf {

    /**
     * Returns a new method type with the given return type and all the
     * same characteristics as this one.
     */
    JMethodSig withReturnType(JTypeMirror returnType);


    JMethodSig markAsAdapted();

    /**
     * Returns a new method type with the given type parameters. Nothing
     * is done to the other types presented by this object. If null, resets
     * them to the value of the symbol (but this takes care of the enclosing
     * type subst in bounds).
     */
    JMethodSig withTypeParams(@Nullable List<JTypeVar> tparams);


    JMethodSig subst(Function<? super SubstVar, ? extends JTypeMirror> fun);

    /**
     * @throws IllegalArgumentException If the type of the owner is not appropriate
     */
    JMethodSig withOwner(JTypeMirror newOwner);

    JMethodSig originalMethod();

    JMethodSig adaptedMethod();

}
