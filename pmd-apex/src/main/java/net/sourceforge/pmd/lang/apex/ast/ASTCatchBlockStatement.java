/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.TryStatement;

public class ASTCatchBlockStatement extends AbstractApexCommentContainerNode<TryStatement.CatchBlock> {

    @Deprecated
    @InternalApi
    public ASTCatchBlockStatement(TryStatement.CatchBlock catchBlock) {
        super(catchBlock);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getExceptionType() {
        return node.getExceptionVariable().getType().asCodeString();
    }

    public String getVariableName() {
        return node.getExceptionVariable().getId().getString();
    }

    public ASTBlockStatement getBody() {
        return getFirstChildOfType(ASTBlockStatement.class);
    }
}
