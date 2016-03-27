/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DoLoopStatement;

public class ASTDoLoopStatement extends AbstractApexNode<DoLoopStatement> {

    public ASTDoLoopStatement(DoLoopStatement doLoopStatement) {
        super(doLoopStatement);
    }

    /**
     * Accept the visitor. Note: This needs to be in each concrete node class,
     * as otherwise the visitor won't work - as java resolves the type "this" at
     * compile time.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
