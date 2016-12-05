/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DmlUpdateStatement;

public class ASTDmlUpdateStatement extends AbstractApexNode<DmlUpdateStatement> {

    public ASTDmlUpdateStatement(DmlUpdateStatement dmlUpdateStatement) {
        super(dmlUpdateStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
