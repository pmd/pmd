/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ForLoopStatement;

public class ASTForLoopStatement extends AbstractApexNode<ForLoopStatement> {

    public ASTForLoopStatement(ForLoopStatement forLoopStatement) {
        super(forLoopStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
