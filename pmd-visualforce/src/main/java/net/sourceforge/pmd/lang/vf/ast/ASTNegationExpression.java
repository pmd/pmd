/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

public final class ASTNegationExpression extends AbstractVfNode {
    ASTNegationExpression(int id) {
        super(id);
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
