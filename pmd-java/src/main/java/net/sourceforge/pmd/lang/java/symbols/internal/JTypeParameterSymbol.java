/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;


/**
 * Represents the declaration of a type variable, ie a type parameter. Type variables are reference
 * types, but not class or interface types. They're also not declared with the same node. For those
 * reasons this type of references is distinct from {@link JResolvableClassDeclarationSymbol}.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public class JTypeParameterSymbol extends AbstractDeclarationSymbol<ASTTypeParameter> implements JSimpleTypeDeclarationSymbol<ASTTypeParameter> {


    JTypeParameterSymbol(String simpleName) {
        super(simpleName);
    }


    JTypeParameterSymbol(ASTTypeParameter node, String simpleName) {
        super(node, simpleName);
    }
}
