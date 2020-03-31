/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;


public interface NSymbolTable {


    ShadowGroup<JVariableSymbol> variables();


    ShadowGroup<JTypeDeclSymbol> types();


    ShadowGroup<JMethodSymbol> methods();


}
