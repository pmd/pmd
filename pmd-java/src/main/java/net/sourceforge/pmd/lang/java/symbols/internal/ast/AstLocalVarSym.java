/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.subst;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * @author Clément Fournier
 */
final class AstLocalVarSym extends AbstractAstVariableSym implements JLocalVariableSymbol {

    AstLocalVarSym(ASTVariableDeclaratorId node, AstSymFactory factory) {
        super(node, factory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (!(o instanceof AstLocalVarSym)) {
            return false;
        }
        return node.equals(((AstLocalVarSym) o).node);
    }

    @Override
    public int hashCode() {
        return node.hashCode();
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        return subst(node.getTypeMirror(), subst);
    }
}
