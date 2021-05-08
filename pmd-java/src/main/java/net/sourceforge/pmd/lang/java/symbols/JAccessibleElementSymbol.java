/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents declarations having access modifiers common to {@link JFieldSymbol},
 * {@link JClassSymbol}, {@link JMethodSymbol}, and {@link JConstructorSymbol}.
 *
 * @since 7.0.0
 */
public interface JAccessibleElementSymbol extends JElementSymbol, AnnotableSymbol {

    /**
     * Conventional return value of {@link #getPackageName()} for
     * primitive types.
     */
    String PRIMITIVE_PACKAGE = "java.lang";


    /**
     * Returns the modifiers of the element represented by this symbol,
     * as decodable by the standard {@link Modifier} API.
     */
    int getModifiers();


    default boolean isStatic() {
        return Modifier.isStatic(getModifiers());
    }


    /**
     * Returns the class that directly encloses this declaration.
     * This is equivalent to {@link Class#getEnclosingClass()}.
     * Returns null if this is a top-level type declaration.
     *
     * <p>This is necessarily an already resolved symbol, because
     * 1. if it's obtained from reflection, then the enclosing class is available
     * 2. if it's obtained from an AST, then the enclosing class is in the same source file so we can
     * know about it
     */
    @Nullable
    JClassSymbol getEnclosingClass();


    /**
     * Returns the name of the package this element is declared in. This
     * recurses into the enclosing elements if needed. If this is an array
     * symbol, returns the package name of the element symbol. If this is
     * a primitive type, returns {@value #PRIMITIVE_PACKAGE}.
     *
     * <p>This is consistent with Java 9's {@code getPackageName()}.
     */
    @NonNull
    String getPackageName();


}
