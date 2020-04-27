/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.ForLoopStatement;

public class ASTForLoopStatement extends AbstractApexNode<ForLoopStatement> {

    @Deprecated
    @InternalApi
    public ASTForLoopStatement(ForLoopStatement forLoopStatement) {
        super(forLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
