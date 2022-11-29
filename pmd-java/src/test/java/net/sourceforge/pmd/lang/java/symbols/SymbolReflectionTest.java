/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols;

/**
 *
 */
public class SymbolReflectionTest extends AbstractSymbolTest {

    public SymbolReflectionTest() {
        super(false); // ASM assumes no debug symbols are available
    }

    @Override
    protected JClassSymbol resolveSymbol(Class<?> clazz) {
        return loader.resolveClassFromBinaryName(clazz.getName());
    }
}
