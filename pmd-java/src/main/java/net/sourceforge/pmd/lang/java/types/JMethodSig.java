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
 * type parameters have been given a specific instantiation. But If a signature
 * is produced by type inference, it may not match
 * its symbol exactly (ie, not just be a substitution applied to the
 * symbol's type parameters), to account for special cases depending
 * on context information. For example, the actual return type of a method
 * whose applicability required an unchecked conversion is the erasure of the
 * return type of the declaration.
 */
public interface JMethodSig extends JTypeVisitable {

    /** Return the type system with which this method was created. */
    TypeSystem getTypeSystem();


    /** Return the symbol of the method or constructor. */
    JExecutableSymbol getSymbol();

    /**
     * Return the name of the method. If this is a constructor,
     * returns {@link JConstructorSymbol#CTOR_NAME}.
     */
    default String getName() {
        return getSymbol().getSimpleName();
    }

    /** Return whether this is a constructor. */
    default boolean isConstructor() {
        return JConstructorSymbol.CTOR_NAME.equals(getName());
    }


    /** Return method modifiers as decodable by {@link java.lang.reflect.Modifier}. */
    default int getModifiers() {
        return getSymbol().getModifiers();
    }


    /**
     * Return the type that declares this method. May be an array type,
     * a class type. If this is a constructor for a generic class, returns
     * the generic type declaration of the constructor.
     */
    JTypeMirror getDeclaringType();

    /**
     * Return the type of {@code this} in the body of the method. This
     * is the declaring type with
     */
    JTypeMirror getAnnotatedReceiverType();


    /**
     * Return the result type of the method. If this is a constructor,
     * returns the type of the instance produced by the constructor. In
     * particular, for a diamond constructor call, returns the inferred
     * type. For example for {@code List<String> l = new ArrayList<>()},
     * returns {@code ArrayList<String>}.
     */
    JTypeMirror getReturnType();


    /**
     * The erasure of a method is a new, non-generic method, whose
     * parameters, owner, and return type, are erased. For example:
     * <pre>{@code
     *     <N extends Number, U> U fun(N, Supplier<U>, U);
     * }</pre>
     * erases to
     * <pre>
     *     Object fun(Number, Supplier, Object);
     * </pre>
     */
    JMethodSig getErasure();


    /**
     * Return the types of the formal parameters. If this is a varargs
     * method, the last parameter should have an array type. For generic
     * methods that have been inferred, these are substituted with the
     * inferred type parameters. For example for {@code Arrays.asList("a", "b")},
     * returns a singleton list containing {@code String[]}.
     */
    List<JTypeMirror> getFormalParameters();


    /**
     * Number of formal parameters. A varargs parameter counts as one.
     */
    default int getArity() {
        return getSymbol().getArity();
    }

    /**
     * Return the type parameters of the method. After type inference,
     * occurrences of these type parameters are replaced by their instantiation
     * in formals, return type and thrown exceptions (but not type parameter bounds).
     * If instantiation failed, some variables might have been substituted
     * with {@link TypeSystem#ERROR}.
     */
    List<JTypeVar> getTypeParameters();

    /**
     * Return the list of thrown exception types. Exception types may
     * be type variables of the method or of an enclosing context, that
     * extend Throwable.
     */
    List<JTypeMirror> getThrownExceptions();


    /** Return true if this method is abstract. */
    default boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }


    /** Return true if this method is static. */
    default boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }

    /** Return true if this method has a varargs parameter. */
    default boolean isVarargs() {
        return getSymbol().isVarargs();
    }

    /**
     * Return true if this method signature declares type parameters.
     */
    default boolean isGeneric() {
        // do not override
        // the type parameters may be adapted and we
        // can't compare to the symbol
        return !getTypeParameters().isEmpty();
    }


    @Override
    JMethodSig subst(Function<? super SubstVar, ? extends @NonNull JTypeMirror> subst);


    @Override
    default <T, P> T acceptVisitor(JTypeVisitor<T, P> visitor, P p) {
        return visitor.visitMethodType(this, p);
    }


    /**
     * Internal API, should not be used outside of the type inference
     * implementation.
     */
    @InternalApi
    InternalMethodTypeItf internalApi();

}
