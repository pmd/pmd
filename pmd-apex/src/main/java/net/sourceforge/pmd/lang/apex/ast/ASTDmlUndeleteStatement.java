/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DmlUndeleteStatement;

public final class ASTDmlUndeleteStatement extends AbstractApexNode<DmlUndeleteStatement> {

    ASTDmlUndeleteStatement(DmlUndeleteStatement dmlUndeleteStatement) {
        super(dmlUndeleteStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
