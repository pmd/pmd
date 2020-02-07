/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstVariableSym
    extends AbstractAstBackedSymbol<ASTVariableDeclaratorId>
    implements JVariableSymbol {


    AbstractAstVariableSym(ASTVariableDeclaratorId node, AstSymFactory factory) {
        super(node, factory);
    }

    @Override
    public boolean isFinal() {
        return node.isFinal();
    }

    @Override
    public String getSimpleName() {
        return node.getVariableName();
    }

}
