/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.InternalApiBridge;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;

public final class ASTSimpleShortClassSpecifier extends AbstractModelicaClassSpecifierNode {
    ASTSimpleShortClassSpecifier(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptModelicaVisitor(ModelicaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public void populateExtendsAndImports(ModelicaClassType classTypeDeclaration) {
        super.populateExtendsAndImports(classTypeDeclaration);
        InternalApiBridge.addExtendToClass(
                classTypeDeclaration,
                Visibility.UNSPEC,
                firstChild(ASTName.class).getCompositeName()
        );
    }
}
