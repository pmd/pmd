/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;

/**
 * @author Clément Fournier
 */
final class AstLocalVarSym extends AbstractAstVariableSym implements JLocalVariableSymbol {

    AstLocalVarSym(ASTVariableDeclaratorId node, AstSymFactory factory) {
        super(node, factory);
    }

    @Override
    public boolean equals(Object o) {
        return node.equals(o);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }
}
