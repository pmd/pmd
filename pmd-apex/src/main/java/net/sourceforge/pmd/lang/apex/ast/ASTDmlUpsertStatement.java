/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.DmlUpsertStatement;

public class ASTDmlUpsertStatement extends AbstractApexNode<DmlUpsertStatement> {

    @Deprecated
    @InternalApi
    public ASTDmlUpsertStatement(DmlUpsertStatement dmlUpsertStatement) {
        super(dmlUpsertStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
