/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.ContinueStatement;

public class ASTContinueStatement extends AbstractApexNode.Single<ContinueStatement> {

    ASTContinueStatement(ContinueStatement continueStatement) {
        super(continueStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
