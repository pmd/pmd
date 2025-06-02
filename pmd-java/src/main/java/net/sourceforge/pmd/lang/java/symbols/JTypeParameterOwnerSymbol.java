/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.types.JTypeVar;
import net.sourceforge.pmd.lang.java.types.LexicalScope;


/**
 * Represents a declaration that can declare type parameters,
 * {@literal i.e.} {@link JClassSymbol} or {@link JMethodSymbol}.
 *
 * @since 7.0.0
 */
public interface JTypeParameterOwnerSymbol extends JAccessibleElementSymbol {

    /**
     * Returns an unmodifiable list of the type variables declared by
     * this symbol.
     */
    List<JTypeVar> getTypeParameters();


    /**
     * Returns the lexical scope of this symbol. This is little more than
     * a map of all the type parameters that are in scope at the point
     * of this declaration, indexed by their name. For example, for a
     * method, this includes the type parameters of the method, the type
     * parameters of its enclosing class, and all the other enclosing
     * classes.
     */
    default LexicalScope getLexicalScope() {
        JTypeParameterOwnerSymbol encl = getEnclosingTypeParameterOwner();
        LexicalScope base = encl != null ? encl.getLexicalScope() : LexicalScope.EMPTY;
        return base.andThen(getTypeParameters());
    }


    default int getTypeParameterCount() {
        return getTypeParameters().size();
    }


    default boolean isGeneric() {
        return getTypeParameterCount() > 0;
    }


    /**
     * Returns the {@link JClassSymbol#getEnclosingMethod() enclosing method} or
     * the {@link #getEnclosingClass() enclosing class}, in that order
     * of priority.
     */
    @Nullable
    default JTypeParameterOwnerSymbol getEnclosingTypeParameterOwner() {
        // may be overridden to add getEnclosingMethod
        return getEnclosingClass();
    }
}
