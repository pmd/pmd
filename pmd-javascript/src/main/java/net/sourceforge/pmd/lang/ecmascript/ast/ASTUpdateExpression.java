/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.UpdateExpression;

public final class ASTUpdateExpression extends AbstractEcmascriptNode<UpdateExpression> {

    ASTUpdateExpression(UpdateExpression updateExpression) {
        super(updateExpression);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
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
