/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.statement.EnhancedForLoopStatement;

public class ASTForEachStatement extends AbstractApexNode.Single<EnhancedForLoopStatement> {

    ASTForEachStatement(EnhancedForLoopStatement enhancedForLoopStatement) {
        super(enhancedForLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
