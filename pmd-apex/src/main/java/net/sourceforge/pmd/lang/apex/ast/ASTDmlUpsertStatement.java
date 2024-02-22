/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.DmlStatement;

public final class ASTDmlUpsertStatement extends AbstractDmlStatement {

    ASTDmlUpsertStatement(DmlStatement dmlUpsertStatement) {
        super(dmlUpsertStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
