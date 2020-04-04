/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.table.coreimpl.ShadowGroupBuilder;

class SymbolGroupBuilder<S extends JElementSymbol> extends ShadowGroupBuilder<S, ScopeInfo> {

    @Override
    protected String getSimpleName(S sym) {
        return sym.getSimpleName();
    }
}
