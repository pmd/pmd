/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.lang.reflect.Modifier;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a field declaration.
 *
 * <p>(Experimental) <b>Record support:</b> Record components declarations
 * give rise to a normal field symbol, and a corresponding formal parameter
 * symbol whose scope is the special record constructor. For example:
 *
 * <pre>
 *
 * record Point(int x, int y) {
 *     Point {  // the ctor symbol has two formal parameters
 *
 *         this.x      // refers to the field symbol
 *                = x; // refers to the formal parameter symbol
 *     }
 * }
 *
 * </pre>
 *
 * @since 7.0.0
 */
public interface JFieldSymbol extends JAccessibleElementSymbol, JVariableSymbol {


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


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitField(this, param);
    }
}
