/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.subst;

import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * @author Clément Fournier
 */
abstract class AbstractAstVariableSym
    extends AbstractAstAnnotableSym<ASTVariableDeclaratorId>
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
        return node.getName();
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
