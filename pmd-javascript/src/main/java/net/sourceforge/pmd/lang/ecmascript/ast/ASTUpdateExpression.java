/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.UpdateExpression;

public final class ASTUpdateExpression extends AbstractEcmascriptNode<UpdateExpression> {

    ASTUpdateExpression(UpdateExpression updateExpression) {
        super(updateExpression);
        super.setImage(AstRoot.operatorToString(updateExpression.getOperator()));
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getOperator() {
        return AstNode.operatorToString(node.getOperator());
    }

    public boolean isPostfix() {
        return node.isPostfix();
    }

    public boolean isPrefix() {
        return node.isPrefix();
    }
}
