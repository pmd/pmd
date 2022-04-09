/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;


abstract class AbstractMethodOrConstructorDeclaration<T extends JExecutableSymbol>
    extends AbstractJavaNode
    implements ASTMethodOrConstructorDeclaration,
               LeftRecursiveNode {

    private T symbol;
    private JMethodSig sig;

    AbstractMethodOrConstructorDeclaration(int i) {
        super(i);
    }


    void setSymbol(T symbol) {
        AbstractTypedSymbolDeclarator.assertSymbolNull(this.symbol, this);
        this.symbol = symbol;
    }

    @Override
    public T getSymbol() {
        AbstractTypedSymbolDeclarator.assertSymbolNotNull(symbol, this);
        return symbol;
    }

    @Override
    public JMethodSig getGenericSignature() {
        if (sig == null) {
            sig = getTypeSystem().sigOf(getSymbol());
        }
        return sig;
    }
}
