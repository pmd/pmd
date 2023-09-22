/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.EnhancedForLoopStatement;

public final class ASTForEachStatement extends AbstractApexNode.Single<EnhancedForLoopStatement> {

    ASTForEachStatement(EnhancedForLoopStatement enhancedForLoopStatement) {
        super(enhancedForLoopStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
