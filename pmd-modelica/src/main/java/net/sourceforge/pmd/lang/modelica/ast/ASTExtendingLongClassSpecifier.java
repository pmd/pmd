/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;

public final class ASTExtendingLongClassSpecifier extends AbstractModelicaClassSpecifierNode {
    ASTExtendingLongClassSpecifier(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public void populateExtendsAndImports(ModelicaClassType classTypeDeclaration) {
        super.populateExtendsAndImports(classTypeDeclaration);
        pushExtendsAndImports(classTypeDeclaration, getFirstChildOfType(ASTComposition.class));
        // TODO
    }
}
