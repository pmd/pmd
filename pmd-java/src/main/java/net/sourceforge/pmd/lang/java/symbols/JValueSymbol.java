/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Reference to a value, ie {@linkplain JLocalVariableSymbol local variable},
 * {@linkplain JFormalParamSymbol formal parameter}, or {@linkplain JFieldSymbol field}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JValueSymbol extends BoundToNode<ASTVariableDeclaratorId> {

    /**
     * Returns true if this declaration is declared final.
     * This takes implicit modifiers into account.
     */
    boolean isFinal();
}
