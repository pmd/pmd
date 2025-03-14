/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.symbols;

/**
 * Represents a formal parameter of a {@link JExecutableSymbol}.
 *
 * @since 7.0.0
 */
public interface JFormalParamSymbol extends JLocalVariableSymbol {

    /** Returns the symbol declaring this parameter. */
    JExecutableSymbol getDeclaringSymbol();


    @Override
    default <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitFormal(this, param);
    }
}
