/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.BreakStatement;

public class ASTBreakStatement extends AbstractApexNode<BreakStatement> {

    public ASTBreakStatement(BreakStatement breakStatement) {
        super(breakStatement);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
