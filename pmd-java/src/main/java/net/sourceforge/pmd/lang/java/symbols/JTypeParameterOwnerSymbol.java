/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.util.List;


/**
 * Represents a declaration that can declare type parameters,
 * {@literal i.e.} {@link JClassSymbol} or {@link JMethodSymbol}.
 *
 * @since 7.0.0
 */
public interface JTypeParameterOwnerSymbol extends JAccessibleElementSymbol {

    List<JTypeParameterSymbol> getTypeParameters();


    default int getTypeParameterCount() {
        return getTypeParameters().size();
    }


    /**
     * Returns the {@link JClassSymbol#getEnclosingMethod() enclosing method} or
     * the {@link #getEnclosingClass() enclosing class}, in that order
     * of priority.
     */
    default JTypeParameterOwnerSymbol getEnclosingTypeParameterOwner() {
        return getEnclosingClass();
    }
}
