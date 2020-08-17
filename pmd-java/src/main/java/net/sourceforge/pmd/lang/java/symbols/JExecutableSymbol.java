/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * Common supertype for {@linkplain JMethodSymbol method}
 * and {@linkplain JConstructorSymbol constructor symbols}.
 */
public interface JExecutableSymbol extends JAccessibleElementSymbol, JTypeParameterOwnerSymbol {


    /**
     * Returns the formal parameters this executable declares. These are
     * only non-synthetic parameters. For example, a constructor for an
     * inner non-static class will not reflect a parameter for the enclosing
     * instance.
     */
    List<JFormalParamSymbol> getFormalParameters();


    default boolean isDefaultMethod() {
        // Default methods are public non-abstract instance methods
        // declared in an interface.
        return this instanceof JMethodSymbol
            && (getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC
            && getEnclosingClass().isInterface();
    }


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
    default @NonNull String getPackageName() {
        return getEnclosingClass().getPackageName();
    }


    List<JTypeMirror> getFormalParameterTypes(Substitution subst);

    List<JTypeMirror> getThrownExceptionTypes(Substitution subst);


}
