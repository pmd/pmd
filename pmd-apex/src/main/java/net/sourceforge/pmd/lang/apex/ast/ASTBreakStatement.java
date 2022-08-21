/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.BreakStatement;

public class ASTBreakStatement extends AbstractApexNode.Single<BreakStatement> {

    @Deprecated
    @InternalApi
    public ASTBreakStatement(BreakStatement breakStatement) {
        super(breakStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
