/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import org.apache.commons.lang3.ClassUtils.Interfaces;
import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * Common supertype for {@linkplain JMethodSymbol method}
 * and {@linkplain JConstructorSymbol constructor symbols}.
 */
public interface JExecutableSymbol extends JAccessibleElementSymbol, JTypeParameterOwnerSymbol {


    /** Returns the formal parameters this executable declares. */
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
    @NonNull
    default String getPackageName() {
        return getEnclosingClass().getPackageName();
    }


    List<JTypeMirror> getFormalParameterTypes(Substitution subst);

    List<JTypeMirror> getThrownExceptionTypes(Substitution subst);


    /**
     * Returns true if this symbol is accessible in the given class symbol
     * according to the modifiers of this element and its enclosing classes.
     *
     * TODO this is not entirely specified, though it's enough for overload resolution
     *  Maybe this should be moved to the internals of the inference framework
     *
     * TODO visibility of local classes in separate instance initializers
     *   is impossible to recover from reflection. Luckily local classes may
     *   normally only be reached from inside a parsed compilation unit.
     * <pre>{@code
     *
     * public class Outer {
     *      private int f;
     *
     *      {
     *          class Local1 {
     *              private int loki;
     *          }
     *
     *          class Local2 {
     *
     *          }
     *
     *          new Local1().loki = 0;
     *      }
     *
     *      void foo() {
     *
     *      }
     *
     * }
     *
     * }</pre>
     *
     * Fields {@code f} and {@code loki} are accessible in both local classes,
     * but neither local classes are accessible (technically, not even *visible*)
     * in method foo(). Visibility is handled by symbol tables.
     */
    @Experimental
    default boolean isAccessible(JClassSymbol ctx) {

        if (ctx == null) {
            throw new IllegalArgumentException("Cannot check a null symbol");
        }

        int mods = getModifiers();
        if (Modifier.isPublic(mods)) {
            return true;
        }

        JClassSymbol owner = getEnclosingClass();

        if (Modifier.isPrivate(mods)) {
            return ctx.getNestRoot().equals(owner.getNestRoot());
        } else if (owner instanceof JArrayType) {
            return true;
        }

        return ctx.getPackageName().equals(owner.getPackageName())
            // we can exclude interfaces because their members are all public
            || Modifier.isProtected(mods) && ctx.isSubClassOf(owner, Interfaces.EXCLUDE);
    }

}
