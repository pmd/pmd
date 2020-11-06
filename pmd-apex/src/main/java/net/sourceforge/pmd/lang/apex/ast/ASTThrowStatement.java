/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.statement.ThrowStatement;

public class ASTThrowStatement extends AbstractApexNode<ThrowStatement> {

    @Deprecated
    @InternalApi
    public ASTThrowStatement(ThrowStatement throwStatement) {
        super(throwStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
