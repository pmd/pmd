/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;


/**
 * Abstract class for type declarations nodes.
 */
abstract class AbstractTypedSymbolDeclarator<T extends JElementSymbol>
    extends AbstractJavaTypeNode
    implements SymbolDeclaratorNode {

    private T symbol;

    AbstractTypedSymbolDeclarator(int i) {
        super(i);
    }

    @NonNull
    @Override
    public T getSymbol() {
        assertSymbolNotNull(symbol, this);
        return symbol;
    }

    static void assertSymbolNotNull(JElementSymbol symbol, SymbolDeclaratorNode node) {
        assert symbol != null : "Symbol was null, not set by resolver, on " + node;
    }

    static void assertSymbolNull(JElementSymbol symbol, SymbolDeclaratorNode node) {
        assert symbol == null : "Symbol was not null, already set to " + symbol + " by  resolver, on " + node;
    }

    void setSymbol(T symbol) {
        assertSymbolNull(this.symbol, this);
        this.symbol = symbol;
    }

}

