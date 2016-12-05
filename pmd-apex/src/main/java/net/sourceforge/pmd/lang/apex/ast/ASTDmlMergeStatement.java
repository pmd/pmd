/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DmlMergeStatement;

public class ASTDmlMergeStatement extends AbstractApexNode<DmlMergeStatement> {

    public ASTDmlMergeStatement(DmlMergeStatement dmlMergeStatement) {
        super(dmlMergeStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
