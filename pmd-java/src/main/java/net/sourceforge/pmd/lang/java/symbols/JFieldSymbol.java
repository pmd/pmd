/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a field declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JFieldSymbol extends JAccessibleElementSymbol, JValueSymbol {


    /** Returns true if this field is an enum constant. */
    boolean isEnumConstant();


    @Override
    default boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }


    @Override
    @NonNull JClassSymbol getEnclosingClass();


    @Override
    @NonNull
    default String getPackageName() {
        return getEnclosingClass().getPackageName();
    }

}
