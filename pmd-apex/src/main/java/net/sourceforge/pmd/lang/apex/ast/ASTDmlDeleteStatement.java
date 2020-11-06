/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.DmlDeleteStatement;

public class ASTDmlDeleteStatement extends AbstractApexNode<DmlDeleteStatement> {

    @Deprecated
    @InternalApi
    public ASTDmlDeleteStatement(DmlDeleteStatement dmlDeleteStatement) {
        super(dmlDeleteStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
