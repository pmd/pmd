/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.internal.InternalMethodTypeItf;

/**
 * Represents the signature of methods and constructors. An instance of
 * this interface is a {@link JMethodSymbol} viewed under a particular
 * substitution.
 *
 * <p>All the types returned by {@link #getFormalParameters()},
 * {@link #getReturnType()}, {@link #getTypeParameters()} and
 * {@link #getThrownExceptions()} can mention type parameters of the method,
 * of its {@linkplain #getDeclaringType() declaring type} and all its enclosing
 * types.
 *
 * <p>Typically the output of type inference is a method symbol whose
 * type parameters have been given a specific instantiation.
 */
public interface JMethodSig extends JTypeVisitable {

    TypeSystem getTypeSystem();


    JExecutableSymbol getSymbol();


    @Override
    JMethodSig subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);


    /**
     * Returns the name of the method. If this is a constructor,
     * returns {@link JConstructorSymbol#CTOR_NAME}.
     */
    default String getName() {
        return getSymbol().getSimpleName();
    }


    default boolean isConstructor() {
        return JConstructorSymbol.CTOR_NAME.equals(getName());
    }


    /** Returns method modifiers as decodable by {@link java.lang.reflect.Modifier}. */
    default int getModifiers() {
        return getSymbol().getModifiers();
    }


    /** Returns the type that declares this method. May be an array type, a class type. */
    JTypeMirror getDeclaringType();


    /**
     * Returns the result type of the method. If this is a constructor,
     * returns the type that declares this constructor.
     */
    JTypeMirror getReturnType();


    /**
     * The erasure of a method is a new, non-generic method, whose
     * parameters, owner, and return type, are erased.
     */
    JMethodSig getErasure();


    /**
     * Returns the types of the formal parameters. If this is a varargs
     * method, the last parameter should have an array type.
     */
    List<JTypeMirror> getFormalParameters();


    /** Arity of the symbol. A varargs parameter counts as one. */
    default int getArity() {
        return getSymbol().getArity();
    }


    default boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }


    default boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }


    default boolean isVarargs() {
        return getSymbol().isVarargs();
    }


    boolean isBridge();


    /** Returns the type parameters of the method. */
    List<JTypeVar> getTypeParameters();


    List<JTypeMirror> getThrownExceptions();


    default boolean isGeneric() {
        return !getTypeParameters().isEmpty();
    }


    default boolean isAccessible(JClassType ctx) {
        return getSymbol().isAccessible(ctx.getSymbol());
    }


    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitMethodType(this, p);
    }


    @InternalApi
    InternalMethodTypeItf internalApi();

}
