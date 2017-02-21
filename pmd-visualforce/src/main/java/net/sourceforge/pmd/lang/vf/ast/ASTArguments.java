
package net.sourceforge.pmd.lang.vf.ast;

public class ASTArguments extends AbstractVFNode {
    public ASTArguments(int id) {
        super(id);
    }

    public ASTArguments(VfParser p, int id) {
        super(p, id);
    }

    /** Accept the visitor. **/
    public Object jjtAccept(VfParserVisitor visitor, Object data) {

        return visitor.visit(this, data);
    }
}
