/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.TryCatchFinallyBlockStatement;

public class ASTTryCatchFinallyBlockStatement extends AbstractApexNode<TryCatchFinallyBlockStatement> {

    public ASTTryCatchFinallyBlockStatement(TryCatchFinallyBlockStatement tryCatchFinallyBlockStatement) {
        super(tryCatchFinallyBlockStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
