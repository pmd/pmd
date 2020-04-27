/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.TryCatchFinallyBlockStatement;

public class ASTTryCatchFinallyBlockStatement extends AbstractApexNode<TryCatchFinallyBlockStatement> {

    @Deprecated
    @InternalApi
    public ASTTryCatchFinallyBlockStatement(TryCatchFinallyBlockStatement tryCatchFinallyBlockStatement) {
        super(tryCatchFinallyBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
