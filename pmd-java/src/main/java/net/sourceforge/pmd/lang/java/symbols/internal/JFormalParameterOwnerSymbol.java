/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.List;


/**
 * Represents a declaration that can declare type parameters,
 * i.e. {@link JClassSymbol} or {@link JMethodSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JFormalParameterOwnerSymbol extends JAccessibleElementSymbol {

    List<JLocalVariableSymbol> getFormalParameters();

}
