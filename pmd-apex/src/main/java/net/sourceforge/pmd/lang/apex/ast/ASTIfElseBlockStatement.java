/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.IfStatement;

public class ASTIfElseBlockStatement extends AbstractApexNode.Single<IfStatement> {

    private final boolean hasElseStatement;

    @Deprecated
    @InternalApi
    public ASTIfElseBlockStatement(IfStatement ifStatement, boolean hasElseStatement) {
        super(ifStatement);
        this.hasElseStatement = hasElseStatement;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasElseStatement() {
        return hasElseStatement;
    }
}
