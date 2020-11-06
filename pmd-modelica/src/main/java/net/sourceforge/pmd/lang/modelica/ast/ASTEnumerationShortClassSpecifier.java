/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

public final class ASTEnumerationShortClassSpecifier extends AbstractModelicaClassSpecifierNode {
    ASTEnumerationShortClassSpecifier(int id) {
        super(id);
    }

    ASTEnumerationShortClassSpecifier(ModelicaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(ModelicaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
