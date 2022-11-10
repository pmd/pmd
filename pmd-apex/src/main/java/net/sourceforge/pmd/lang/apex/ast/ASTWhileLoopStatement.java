/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.WhileLoopStatement;

public class ASTWhileLoopStatement extends AbstractApexNode.Single<WhileLoopStatement> {

    ASTWhileLoopStatement(WhileLoopStatement whileLoopStatement) {
        super(whileLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
