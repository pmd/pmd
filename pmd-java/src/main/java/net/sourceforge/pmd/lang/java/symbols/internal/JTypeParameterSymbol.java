/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import java.lang.reflect.TypeVariable;
import java.util.Objects;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;


/**
 * Represents the declaration of a type variable, ie a type parameter. Type variables are reference
 * types, but not class or interface types. They're also not declared with the same node. For those
 * reasons this type of references is distinct from {@link JResolvableClassSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public final class JTypeParameterSymbol extends AbstractDeclarationSymbol<ASTTypeParameter> implements JSimpleTypeDeclarationSymbol<ASTTypeParameter> {

    // TODO add bounds information

    private final JTypeParameterOwnerSymbol myOwnerSymbol;


    JTypeParameterSymbol(JTypeParameterOwnerSymbol owner, TypeVariable<?> reflected) {
        super(reflected.getName());
        this.myOwnerSymbol = owner;
    }


    JTypeParameterSymbol(JTypeParameterOwnerSymbol owner, ASTTypeParameter node) {
        super(node, node.getParameterName());
        this.myOwnerSymbol = owner;
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
        JTypeParameterSymbol that = (JTypeParameterSymbol) o;
        return Objects.equals(myOwnerSymbol, that.myOwnerSymbol);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), myOwnerSymbol);
    }
}
