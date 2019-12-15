/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

/** Fake symbol for inference variables. */
class FakeTypeSymbol extends UnresolvedClassImpl {


    public FakeTypeSymbol(String name) {
        super(name);
    }

    @Override
    public boolean isUnresolved() {
        return false;
    }
}
