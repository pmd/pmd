/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTTryCatchFinallyBlockStatement extends AbstractApexNode<Node> {

    @Deprecated
    @InternalApi
    public ASTTryCatchFinallyBlockStatement(Node tryCatchFinallyBlockStatement) {
        super(tryCatchFinallyBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTBlockStatement getTryBlock() {
        return (ASTBlockStatement) getChild(0);
    }

    public List<ASTCatchBlockStatement> getCatchClauses() {
        return findChildrenOfType(ASTCatchBlockStatement.class);
    }

    public ASTBlockStatement getFinallyBlock() {
        ApexNode<?> lastChild = null;
        if (getNumChildren() >= 2) {
            lastChild = getChild(getNumChildren() - 1);
        }
        if (lastChild instanceof ASTBlockStatement) {
            return (ASTBlockStatement) lastChild;
        }
        return null;
    }
}
