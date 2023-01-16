/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.SymbolDeclaratorNode;
import net.sourceforge.pmd.lang.java.symbols.AnnotableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstAnnotableSym<T extends SymbolDeclaratorNode & Annotatable>
    extends AbstractAstBackedSymbol<T> implements AnnotableSymbol {

    private PSet<SymAnnot> annots;


    AbstractAstAnnotableSym(T node, AstSymFactory factory) {
        super(node, factory);
    }

    @Override
    public PSet<SymAnnot> getDeclaredAnnotations() {
        if (annots == null) {
            annots = SymbolResolutionPass.buildSymbolicAnnotations(node.getDeclaredAnnotations());
        }
        return annots;
    }
}
