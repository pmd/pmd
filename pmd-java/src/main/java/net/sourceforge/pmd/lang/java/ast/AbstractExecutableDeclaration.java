/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.types.JMethodSig;

abstract class AbstractExecutableDeclaration<T extends JExecutableSymbol> extends AbstractJavaNode
        implements ASTExecutableDeclaration, LeftRecursiveNode {

    private T symbol;
    private JMethodSig sig;
    private JavaccToken identToken;

    AbstractExecutableDeclaration(int i) {
        super(i);
    }

    void setIdentToken(JavaccToken identToken) {
        this.identToken = identToken;
        setImage(identToken.getImage());
    }

    @Override
    public String getImage() {
        return null;
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

    @Override
    public FileLocation getReportLocation() {
        return identToken.getReportLocation();
    }

    @Override
    public String getName() {
        return super.getImage();
    }
}
