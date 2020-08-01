/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

/** Fake symbol for inference variables. */
class FakeTypeSymbol extends UnresolvedClassImpl {


    FakeTypeSymbol(SymbolFactory core, String name) {
        super(core, null, name);
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }
}
