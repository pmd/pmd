/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.CatchClause;

public class ASTCatchClause extends AbstractApexNode<CatchClause> {
    public ASTCatchClause(CatchClause catchClause) {
	super(catchClause);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ASTName getVariableName() {
	return (ASTName) jjtGetChild(0);
    }

    public boolean isIf() {
	return node.getCatchCondition() != null;
    }

    public ApexNode<?> getCatchCondition() {
	return (ApexNode<?>) jjtGetChild(1);
    }

    public ASTBlock getBlock() {
	return (ASTBlock) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
