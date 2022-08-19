/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.IfStatement;

public class ASTIfBlockStatement extends AbstractApexNode.Single<IfStatement> {

    @Deprecated
    @InternalApi
    public ASTIfBlockStatement(IfStatement ifStatement) {
        super(ifStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
