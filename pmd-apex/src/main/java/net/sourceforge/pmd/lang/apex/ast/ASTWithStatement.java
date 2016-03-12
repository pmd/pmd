/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.WithStatement;

public class ASTWithStatement extends AbstractApexNode<WithStatement> {
    public ASTWithStatement(WithStatement withStatement) {
	super(withStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getExpression() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getStatement() {
	return (ApexNode<?>) jjtGetChild(1);
    }
}
