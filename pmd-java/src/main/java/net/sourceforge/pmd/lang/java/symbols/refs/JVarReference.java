/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.refs;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Reference to a value, ie {@linkplain JLocalVarReference local variable} or {@linkplain JFieldReference field}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JVarReference extends JCodeReference<ASTVariableDeclaratorId> {

    /**
     * Returns true if this field or variable is declared final.
     */
    boolean isFinal();


    /**
     * Returns true if this is a field reference, in
     * which case it can be safely downcast to {@link JFieldReference}.
     */
    default boolean isField() {
        return this instanceof JFieldReference;
    }


    /**
     * Returns true if this is a reference to a local variable, in
     * which case it can be safely downcast to {@link JLocalVarReference}.
     */
    default boolean isLocalVar() {
        return this instanceof JFieldReference;
    }

}
