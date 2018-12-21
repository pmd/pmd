/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Reference to a value, ie {@linkplain JLocalVariableSymbol local variable} or {@linkplain JFieldSymbol field}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JValueSymbol extends JDeclarationSymbol<ASTVariableDeclaratorId> {

    /**
     * Returns true if this field or variable is declared final.
     */
    boolean isFinal();


    /**
     * Returns true if this is a field reference, in
     * which case it can be safely downcast to {@link JFieldSymbol}.
     */
    default boolean isField() {
        return this instanceof JFieldSymbol;
    }


    /**
     * Returns true if this is a reference to a local variable, in
     * which case it can be safely downcast to {@link JLocalVariableSymbol}.
     */
    default boolean isLocalVar() {
        return this instanceof JFieldSymbol;
    }

}
