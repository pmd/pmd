/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.JTypeVar;

final class AstTypeParamSym
    extends AbstractAstAnnotableSym<ASTTypeParameter>
    implements JTypeParameterSymbol {

    private final JTypeVar tvar;
    private final AbstractAstTParamOwner<?> owner;

    AstTypeParamSym(ASTTypeParameter node, AstSymFactory factory, AbstractAstTParamOwner<?> owner) {
        super(node, factory);
        this.owner = owner;
        this.tvar = factory.types().newTypeVar(this);
    }

    @Override
    public JTypeVar getTypeMirror() {
        return tvar;
    }

    @Override
    public JTypeMirror computeUpperBound() {
        ASTType bound = node.getTypeBoundNode();
        return bound == null ? node.getTypeSystem().OBJECT
                             : bound.getTypeMirror();
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return owner;
    }

    @NonNull
    @Override
    public String getSimpleName() {
        return node.getName();
    }

}
