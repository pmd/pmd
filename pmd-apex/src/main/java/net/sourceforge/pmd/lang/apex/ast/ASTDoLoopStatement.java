/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.DoWhileLoopStatement;

public class ASTDoLoopStatement extends AbstractApexNode.Single<DoWhileLoopStatement> {

    @Deprecated
    @InternalApi
    public ASTDoLoopStatement(DoWhileLoopStatement doWhileLoopStatement) {
        super(doWhileLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
