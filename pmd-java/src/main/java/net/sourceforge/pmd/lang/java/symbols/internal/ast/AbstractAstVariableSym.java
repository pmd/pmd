/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.subst;

import org.pcollections.PSet;

import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolicValue.SymAnnot;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstVariableSym
    extends AbstractAstBackedSymbol<ASTVariableDeclaratorId>
    implements JVariableSymbol {
    
    private final PSet<SymAnnot> declaredAnnotations;

    AbstractAstVariableSym(ASTVariableDeclaratorId node, AstSymFactory factory) {
        super(node, factory);

        this.declaredAnnotations = SymbolResolutionPass.getSymbolicAnnotations(node);
    }

    @Override
    public boolean isFinal() {
        return node.isFinal();
    }

    @Override
    public String getSimpleName() {
        return node.getVariableName();
    }
    
    @Override
    public PSet<SymAnnot> getDeclaredAnnotations() {
        return declaredAnnotations;
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        ASTType typeNode = node.getTypeNode();
        /*
            Overridden on LocalVarSym.

            This gives up on inferred types until a LazyTypeResolver has
            been set for the compilation unit.

            Thankfully, the type of local vars is never requested by
            anything before that moment.
         */
        assert typeNode != null : "This implementation expects explicit types (" + this + ")";
        return subst(node.getTypeMirror(), subst);
    }
}
