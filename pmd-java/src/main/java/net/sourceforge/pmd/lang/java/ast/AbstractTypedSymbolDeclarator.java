/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.symbols.AnnotableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;


/**
 * Abstract class for nodes that declare a symbol and can be annotated.
 */
abstract class AbstractTypedSymbolDeclarator<T extends AnnotableSymbol>
    extends AbstractJavaTypeNode
    implements SymbolDeclaratorNode, Annotatable {

    private T symbol;
    private PSet<SymAnnot> annots;

    AbstractTypedSymbolDeclarator(int i) {
        super(i);
    }

    @Override
    public @NonNull T getSymbol() {
        assertSymbolNotNull(symbol, this);
        return symbol;
    }

    @Override
    public PSet<SymAnnot> getSymbolicAnnotations() {
        if (annots == null) {
            annots = Annotatable.super.getSymbolicAnnotations();
        }
        return annots;
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

