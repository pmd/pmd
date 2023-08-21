/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.CatchBlockStatement;

public final class ASTCatchBlockStatement extends AbstractApexCommentContainerNode<CatchBlockStatement> {

    ASTCatchBlockStatement(CatchBlockStatement catchBlockStatement) {
        super(catchBlockStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getExceptionType() {
        return String.valueOf(node.getTypeRef());
    }

    public String getVariableName() {
        if (node.getVariable() != null) {
            return node.getVariable().getName();
        }
        return null;
    }

    public ASTBlockStatement getBody() {
        return (ASTBlockStatement) getChild(0);
    }
}
