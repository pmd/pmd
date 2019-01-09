/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.impl;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.lang.java.symbols.internal.JTypeParameterOwnerSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.JTypeParameterSymbol;



public final class JTypeParameterSymbolImpl extends AbstractDeclarationSymbol<ASTTypeParameter> implements JTypeParameterSymbol {

    // TODO add bounds information

    private final JTypeParameterOwnerSymbol myOwnerSymbol;


    JTypeParameterSymbolImpl(JTypeParameterOwnerSymbol owner, TypeVariable<?> reflected) {
        super(reflected.getName());
        this.myOwnerSymbol = owner;
    }


    JTypeParameterSymbolImpl(JTypeParameterOwnerSymbol owner, ASTTypeParameter node) {
        super(node, node.getParameterName());
        this.myOwnerSymbol = owner;
    }


    @Override
    public JTypeParameterOwnerSymbol getDeclaringSymbol() {
        return myOwnerSymbol;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        JTypeParameterSymbolImpl that = (JTypeParameterSymbolImpl) o;
        return Objects.equals(myOwnerSymbol, that.myOwnerSymbol);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), myOwnerSymbol);
    }
}
