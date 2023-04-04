/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.ScopeInfo;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowChainBuilder;

class SymbolChainBuilder<S extends JElementSymbol> extends ShadowChainBuilder<S, ScopeInfo> {

    @Override
    public String getSimpleName(S sym) {
        return sym.getSimpleName();
    }
}
