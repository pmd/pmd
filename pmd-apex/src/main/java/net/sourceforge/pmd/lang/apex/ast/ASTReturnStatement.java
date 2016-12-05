/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ReturnStatement;

public class ASTReturnStatement extends AbstractApexNode<ReturnStatement> {

    public ASTReturnStatement(ReturnStatement returnStatement) {
        super(returnStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
