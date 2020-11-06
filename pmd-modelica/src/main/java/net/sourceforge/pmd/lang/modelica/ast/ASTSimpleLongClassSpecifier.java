/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.modelica.resolver.ModelicaClassType;

public final class ASTSimpleLongClassSpecifier extends AbstractModelicaClassSpecifierNode {
    ASTSimpleLongClassSpecifier(int id) {
        super(id);
    }

    ASTSimpleLongClassSpecifier(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    void populateExtendsAndImports(ModelicaClassType classTypeDeclaration) {
        super.populateExtendsAndImports(classTypeDeclaration);
        pushExtendsAndImports(classTypeDeclaration, getFirstChildOfType(ASTComposition.class));
    }
}
