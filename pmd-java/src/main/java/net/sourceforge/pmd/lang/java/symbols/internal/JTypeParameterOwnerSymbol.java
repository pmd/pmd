/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.util.List;

import net.sourceforge.pmd.lang.java.symbols.internal.impl.JClassSymbolImpl;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.JMethodSymbolImpl;


/**
 * Represents a declaration that can declare type parameters,
 * i.e. {@link JClassSymbolImpl} or {@link JMethodSymbolImpl}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public interface JTypeParameterOwnerSymbol {

    List<JTypeParameterSymbol> getTypeParameters();

}
