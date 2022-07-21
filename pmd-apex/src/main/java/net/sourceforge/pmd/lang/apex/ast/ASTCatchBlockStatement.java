/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTCatchBlockStatement extends AbstractApexCommentContainerNode<Node> {

    @Deprecated
    @InternalApi
    public ASTCatchBlockStatement(Node catchBlockStatement) {
        super(catchBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getExceptionType() {
        // return String.valueOf(node.getTypeRef());
        // TODO(b/239648780)
        return null;
    }

    public String getVariableName() {
        // if (node.getVariable() != null) {
        //     return node.getVariable().getName();
        // }
        // TODO(b/239648780)
        return null;
    }

    public ASTBlockStatement getBody() {
        return (ASTBlockStatement) getChild(0);
    }
}
