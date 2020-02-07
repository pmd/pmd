/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;

/**
 * @author Clément Fournier
 */
final class AstFormalParamSym extends AbstractAstVariableSym implements JFormalParamSymbol {

    private final AbstractAstExecSymbol<?> owner;

    public AstFormalParamSym(ASTVariableDeclaratorId node, AstSymFactory factory, AbstractAstExecSymbol<?> owner) {
        super(node, factory);
        this.owner = owner;
    }

    @Override
    public JExecutableSymbol getDeclaringSymbol() {
        return owner;
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.FORMAL_PARAM.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.FORMAL_PARAM.hash(this);
    }
}
