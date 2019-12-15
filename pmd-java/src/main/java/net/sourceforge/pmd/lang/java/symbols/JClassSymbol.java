/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.NotImplementedException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;


/**
 * Abstraction over a {@link Class} instance. This is not a type, it's
 * the *declaration* of a type.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JClassSymbol extends JTypeDeclSymbol,
                                      JTypeParameterOwnerSymbol,
                                      BoundToNode<ASTAnyTypeDeclaration> {

    /**
     * Returns the binary name of this type, as specified by the JLS:
     * <a href="https://docs.oracle.com/javase/specs/jls/se7/html/jls-13.html#jls-13.1">the JLS</a>.
     * For array types this returns the binary name of the component followed by "[]".
     */
    @NonNull
    String getBinaryName();



    /**
     * Returns the simple name of this class, as specified by
     * {@link Class#getCanonicalName()}.
     */
    @Nullable
    String getCanonicalName();


    /**
     * Returns true if this class is a symbolic reference to an unresolved
     * class. In that case no information about the symbol are known except
     * its name, and the accessors of this class return default values.
     */
    boolean isUnresolved();


    /**
     * Returns the method or constructor this symbol is declared in, if
     * it represents a {@linkplain #isLocalClass() local class declaration}.
     *
     * <p>Notice, that this returns null also if this class is local to
     * a class or instance initializer.
     */
    @Nullable
    default JExecutableSymbol getEnclosingMethod() {
        throw new NotImplementedException("TODO, trickier than it appears");
    }


    @Override
    default JTypeParameterOwnerSymbol getEnclosingTypeParameterOwner() {
        JExecutableSymbol enclosingMethod = getEnclosingMethod();
        return enclosingMethod != null ? enclosingMethod : getEnclosingClass();
    }


    /**
     * Returns the nested classes declared directly in this class.
     *
     * @see Class#getDeclaredClasses()
     */
    List<JClassSymbol> getDeclaredClasses();


    /**
     * Returns the methods declared directly in this class.
     * <i>This excludes bridges and other synthetic methods.</i>
     *
     * <p>For an array type T[], to the difference of {@link Class},
     * this method returns a one-element list with the {@link Cloneable#clone()}
     * method, as if declared like so: {@code public final T[] clone() {...}}.
     *
     * @see Class#getDeclaredMethods()
     */
    List<JMethodSymbol> getDeclaredMethods();


    /**
     * Returns the constructors declared by this class.
     * <i>This excludes synthetic constructors.</i>
     *
     * <p>For an array type T[], and to the difference of {@link Class},
     * this should return a one-element list with a constructor
     * having the same modifiers as the array type, and a single
     * {@code int} parameter.
     *
     * @see Class#getDeclaredConstructors()
     */
    List<JConstructorSymbol> getConstructors();


    /**
     * Returns the fields declared directly in this class.
     * <i>This excludes synthetic fields.</i>
     *
     * <p>For arrays, and to the difference of {@link Class},
     * this should return a one-element list with the
     * {@code public final int length} field.
     *
     * @see Class#getDeclaredFields()
     */
    List<JFieldSymbol> getDeclaredFields();


    /** Returns a field with the given name accessed defined in this class. */
    @Nullable
    default JFieldSymbol getDeclaredField(String name) {
        for (JFieldSymbol field : getDeclaredFields()) {
            if (field.getSimpleName().equals(name)) {
                return field;
            }
        }
        return null;
    }


    /** Returns all methods with the given name declared in this class. */
    default List<JMethodSymbol> getDeclaredMethods(String name) {
        return getDeclaredMethods().stream().filter(it -> it.getSimpleName().equals(name)).collect(Collectors.toList());
    }


    /**
     * Returns the superclass symbol if it exists. Returns null if this
     * class represents an interface or the class {@link Object}.
     */
    @Nullable
    JClassSymbol getSuperclass();


    /** Returns the direct super-interfaces of this class or interface symbol. */
    List<JClassSymbol> getSuperInterfaces();


    default boolean isAbstract() {
        return Modifier.isAbstract(getModifiers());
    }


    /** Returns the component symbol, returns null if this is not an array. */
    @Nullable
    JTypeDeclSymbol getArrayComponent();


    boolean isArray();

    boolean isPrimitive();

    boolean isInterface();

    boolean isEnum();

    boolean isAnnotation();

    boolean isLocalClass();

    boolean isAnonymousClass();

    default boolean isClass() {
        return !isInterface() && !isArray() && !isPrimitive();
    }

}
