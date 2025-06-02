/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import static net.sourceforge.pmd.lang.java.types.TypeOps.subst;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTRecordComponent;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JRecordComponentSymbol;
import net.sourceforge.pmd.lang.java.symbols.SymbolVisitor;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.Substitution;

/**
 * @author Clément Fournier
 */
class AstRecordComponentSym extends AbstractAstAnnotableSym<ASTRecordComponent> implements JRecordComponentSymbol {

    private final JClassSymbol owner;

    AstRecordComponentSym(ASTRecordComponent node, AstSymFactory symFactory, JClassSymbol owner) {
        super(node, symFactory);
        this.owner = owner;
    }

    @Override
    public String getSimpleName() {
        return node.getVarId().getName();
    }

    @Override
    public @NonNull JClassSymbol getEnclosingClass() {
        return owner;
    }

    @Override
    public JTypeMirror getTypeMirror(Substitution subst) {
        return subst(node.getVarId().getTypeMirror(), subst);
    }

    @Override
    public <R, P> R acceptVisitor(SymbolVisitor<R, P> visitor, P param) {
        return visitor.visitRecordComponent(this, param);
    }
}
