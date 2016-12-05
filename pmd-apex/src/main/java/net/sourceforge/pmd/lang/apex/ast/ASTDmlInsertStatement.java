/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DmlInsertStatement;

public class ASTDmlInsertStatement extends AbstractApexNode<DmlInsertStatement> {

    public ASTDmlInsertStatement(DmlInsertStatement dmlInsertStatement) {
        super(dmlInsertStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
