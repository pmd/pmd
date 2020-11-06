/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.ReturnStatement;

public class ASTReturnStatement extends AbstractApexNode<ReturnStatement> {

    @Deprecated
    @InternalApi
    public ASTReturnStatement(ReturnStatement returnStatement) {
        super(returnStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
