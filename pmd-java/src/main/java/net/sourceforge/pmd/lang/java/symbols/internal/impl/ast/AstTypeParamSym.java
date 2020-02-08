/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl.ast;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolEquality;
import net.sourceforge.pmd.lang.java.symbols.internal.impl.SymbolToStrings;

final class AstTypeParamSym
    extends AbstractAstBackedSymbol<ASTTypeParameter>
    implements JTypeParameterSymbol {

    private final AbstractAstTParamOwner<?> owner;
    //    private final FreshTypeVar typeVar;

    public AstTypeParamSym(ASTTypeParameter node, AstSymFactory factory, AbstractAstTParamOwner<?> owner) {
        super(node, factory);
        this.owner = owner;
        //        this.typeVar = factory.types().newTypeVar(this);
    }

    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return owner;
    }

    @Override
    public @Nullable Class<?> getJvmRepr() {
        return null;
    }

    @NonNull
    @Override
    public String getSimpleName() {
        return node.getParameterName();
    }

    @Override
    public boolean equals(Object o) {
        return SymbolEquality.TYPE_PARAM.equals(this, o);
    }

    @Override
    public int hashCode() {
        return SymbolEquality.TYPE_PARAM.hash(this);
    }

    @Override
    public String toString() {
        return SymbolToStrings.AST.typeParamToString(this);
    }
}
