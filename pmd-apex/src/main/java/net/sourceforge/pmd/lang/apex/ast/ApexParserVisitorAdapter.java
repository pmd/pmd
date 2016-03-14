/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

public class ApexParserVisitorAdapter implements ApexParserVisitor {
   
    public Object visit(ApexNode<?> node, Object data) {
        node.childrenAccept(this, data);
        return null;
    }

    public Object visit(ASTMethod node, Object data) {
        return visit((ApexNode<?>) node, data);
    }

    public Object visit(ASTUserClass node, Object data) {
        // TODO Auto-generated method stub
        return null;
    }
}
