/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.BreakStatement;

public final class ASTBreakStatement extends AbstractApexNode<BreakStatement> {

    ASTBreakStatement(BreakStatement breakStatement) {
        super(breakStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
