/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;


import com.google.summit.ast.Node;

public final class ASTValueWhenBlock extends AbstractApexNode<Node> {


    ASTValueWhenBlock(Node node) {
        super(node);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
