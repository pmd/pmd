/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

public class ASTDotExpression extends AbstractVFNode {
    public ASTDotExpression(int id) {
        super(id);
    }

    public ASTDotExpression(VfParser p, int id) {
        super(p, id);
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
