/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;


/**
 * Reference to a variable, ie {@linkplain JLocalVariableSymbol local variable},
 * {@linkplain JFormalParamSymbol formal parameter}, or {@linkplain JFieldSymbol field}.
 *
 * @since 7.0.0
 */
public interface JVariableSymbol extends BoundToNode<ASTVariableDeclaratorId>, AnnotableSymbol {

    /**
     * Returns true if this is a field symbol.
     *
     * @see JFieldSymbol
     */
    default boolean isField() {
        return false;
    }

    /**
     * Returns true if this declaration is declared final.
     * This takes implicit modifiers into account.
     */
    boolean isFinal();


    /** Returns the type of this value, under the given substitution. */
    JTypeMirror getTypeMirror(Substitution subst);
}
