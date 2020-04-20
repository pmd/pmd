/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.DmlMergeStatement;

public class ASTDmlMergeStatement extends AbstractApexNode<DmlMergeStatement> {

    @Deprecated
    @InternalApi
    public ASTDmlMergeStatement(DmlMergeStatement dmlMergeStatement) {
        super(dmlMergeStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
