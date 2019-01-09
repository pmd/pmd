/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Represents all use cases of {@link ASTVariableDeclaratorId} except field declarations.
 * TODO do we need to split those into their own type of reference? This is e.g. done in INRIA/Spoon,
 * but for now doesn't appear to be an interesting tradeoff
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JLocalVariableSymbol extends JValueSymbol {

    // todo maybe add isParameter, isLocalVariable, isCatchParameter, etc.

}
