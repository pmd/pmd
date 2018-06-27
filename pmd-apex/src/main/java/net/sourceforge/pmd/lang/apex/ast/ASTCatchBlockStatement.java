/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.CatchBlockStatement;

public class ASTCatchBlockStatement extends AbstractApexNode<CatchBlockStatement> {

    public ASTCatchBlockStatement(CatchBlockStatement catchBlockStatement) {
        super(catchBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
