/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Common supertype for {@linkplain JMethodSymbol method}
 * and {@linkplain JConstructorSymbol constructor symbols}.
 *
 * @author Clément Fournier
 */
public interface JExecutableSymbol extends JAccessibleElementSymbol, JTypeParameterOwnerSymbol {


    /** Returns the formal parameters this executable declares. */
    List<JFormalParamSymbol> getFormalParameters();


    /** Returns true if the last formal parameter is a varargs parameter. */
    boolean isVarargs();


    /**
     * Returns the number of formal parameters expected. This must be the
     * length of {@link #getFormalParameters()} but if it can be implemented
     * without creating the formal parameters, it should.
     *
     * <p>A varargs parameter counts as a single parameter.
     */
    int getArity();


    /**
     * Returns the class symbol declaring this method or constructor.
     * This is similar to {@link Constructor#getDeclaringClass()}, resp.
     * {@link Method#getDeclaringClass()}. Never null.
     */
    @Override
    @NonNull
    JClassSymbol getEnclosingClass();


    @Override
    @NonNull
    default String getPackageName() {
        return getEnclosingClass().getPackageName();
    }
}
