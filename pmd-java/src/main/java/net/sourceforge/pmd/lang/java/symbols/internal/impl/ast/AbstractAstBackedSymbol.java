/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import net.sourceforge.pmd.lang.java.ast.InternalApiBridge;
import net.sourceforge.pmd.lang.java.ast.SymbolDeclaratorNode;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstBackedSymbol<T extends SymbolDeclaratorNode> implements JElementSymbol {

    protected final T node;
    protected final AstSymFactory factory;

    public AbstractAstBackedSymbol(T node, AstSymFactory factory) {
        this.node = node;
        this.factory = factory;
        InternalApiBridge.setSymbol(node, this);
    }


}
