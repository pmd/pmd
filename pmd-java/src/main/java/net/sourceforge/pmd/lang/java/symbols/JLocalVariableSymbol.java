/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;


/**
 * Represents all use cases of {@link ASTVariableDeclaratorId} except field declarations
 * and method parameters.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JLocalVariableSymbol extends JValueSymbol {

    // todo maybe add isParameter, isLocalVariable, isCatchParameter, etc.

}
