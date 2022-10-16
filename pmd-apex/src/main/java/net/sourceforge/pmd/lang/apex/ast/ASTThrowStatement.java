/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.ThrowStatement;

public class ASTThrowStatement extends AbstractApexNode.Single<ThrowStatement> {

    ASTThrowStatement(ThrowStatement throwStatement) {
        super(throwStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
