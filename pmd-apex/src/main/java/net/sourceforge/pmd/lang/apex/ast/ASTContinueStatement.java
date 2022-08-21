/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.ContinueStatement;

public class ASTContinueStatement extends AbstractApexNode.Single<ContinueStatement> {

    @Deprecated
    @InternalApi
    public ASTContinueStatement(ContinueStatement continueStatement) {
        super(continueStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
