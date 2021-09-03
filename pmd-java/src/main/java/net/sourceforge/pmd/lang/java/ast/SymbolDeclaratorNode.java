/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

/**
 * A node that declares a corresponding {@linkplain JElementSymbol symbol}.
 */
public interface SymbolDeclaratorNode extends JavaNode {

    /** Returns the symbol this node declares. */
    JElementSymbol getSymbol();

}
