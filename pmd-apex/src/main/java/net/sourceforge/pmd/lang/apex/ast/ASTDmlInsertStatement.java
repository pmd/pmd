/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.DmlInsertStatement;

public final class ASTDmlInsertStatement extends AbstractApexNode<DmlInsertStatement> {

    ASTDmlInsertStatement(DmlInsertStatement dmlInsertStatement) {
        super(dmlInsertStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
