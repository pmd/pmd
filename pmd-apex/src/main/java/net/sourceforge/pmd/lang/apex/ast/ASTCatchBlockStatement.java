/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.CatchBlockStatement;

public class ASTCatchBlockStatement extends AbstractApexNode<CatchBlockStatement> {

    @Deprecated
    @InternalApi
    public ASTCatchBlockStatement(CatchBlockStatement catchBlockStatement) {
        super(catchBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
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
}
