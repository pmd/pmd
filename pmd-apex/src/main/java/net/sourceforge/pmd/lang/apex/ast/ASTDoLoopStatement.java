/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.DoWhileLoopStatement;

public final class ASTDoLoopStatement extends AbstractApexNode.Single<DoWhileLoopStatement> {

    ASTDoLoopStatement(DoWhileLoopStatement doWhileLoopStatement) {
        super(doWhileLoopStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
