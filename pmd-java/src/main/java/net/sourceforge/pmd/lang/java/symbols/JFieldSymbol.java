/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a field declaration.
 *
 * @since 7.0.0
 */
public interface JFieldSymbol extends JVariableSymbol, JAccessibleElementSymbol {

    @Override
    default boolean isField() {
        return true;
    }

    /** Returns true if this field is an enum constant. */
    boolean isEnumConstant();


    @Override
    default boolean isFinal() {
        return Modifier.isFinal(getModifiers());
    }


    /**
     * Returns the compile-time value of this field if this is a compile-time constant.
     * Otherwise returns null.
     */
    default @Nullable Object getConstValue() {
        return null;
    }

    @Override
    @NonNull JClassSymbol getEnclosingClass();


    @Override
    @NonNull
    default String getPackageName() {
        return getEnclosingClass().getPackageName();
    }


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitField(this, param);
    }
}
