/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

public class ASTCommentExpression extends AbstractVFNode {

    public ASTCommentExpression(int id) {
        super(id);
    }

    public ASTCommentExpression(VfParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    public Object jjtAccept(VfParserVisitor visitor, Object data) {

        return visitor.visit(this, data);
    }
}
