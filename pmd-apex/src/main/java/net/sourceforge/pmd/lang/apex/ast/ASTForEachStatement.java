/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.statement.EnhancedForLoopStatement;

public class ASTForEachStatement extends AbstractApexNode.Single<EnhancedForLoopStatement> {

    @Deprecated
    @InternalApi
    public ASTForEachStatement(EnhancedForLoopStatement enhancedForLoopStatement) {
        super(enhancedForLoopStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
