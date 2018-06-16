/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.IfBlockStatement;

public class ASTIfBlockStatement extends AbstractApexNode<IfBlockStatement> {

    public ASTIfBlockStatement(IfBlockStatement ifBlockStatement) {
        super(ifBlockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
