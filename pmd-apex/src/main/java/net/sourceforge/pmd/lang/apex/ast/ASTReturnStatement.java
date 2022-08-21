/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.ReturnStatement;

public class ASTReturnStatement extends AbstractApexNode.Single<ReturnStatement> {

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
