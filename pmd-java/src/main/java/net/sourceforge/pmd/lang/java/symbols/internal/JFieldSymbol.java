/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Represents a field declaration.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JFieldSymbol extends JAccessibleDeclarationSymbol<ASTVariableDeclaratorId>, JValueSymbol {
    boolean isVolatile();


    boolean isTransient();


    /**
     * Returns true if this declaration is static.
     */
    boolean isStatic();
}
