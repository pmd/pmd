/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.WhileLoopStatement;

public class ASTWhileLoopStatement extends AbstractApexNode<WhileLoopStatement> {

    @Deprecated
    @InternalApi
    public ASTWhileLoopStatement(WhileLoopStatement whileLoopStatement) {
        super(whileLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
