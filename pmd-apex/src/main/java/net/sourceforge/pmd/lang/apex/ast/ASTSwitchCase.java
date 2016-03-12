/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.SwitchCase;

public class ASTSwitchCase extends AbstractApexNode<SwitchCase> {
    public ASTSwitchCase(SwitchCase switchCase) {
	super(switchCase);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isDefault() {
	return node.isDefault();
    }

    public ApexNode<?> getExpression() {
	if (!isDefault()) {
	    return (ApexNode<?>) jjtGetChild(0);
	} else {
	    return null;
	}
    }

    public int getNumStatements() {
	// TODO Tell Rhino folks about null Statements, should be empty List?
	return node.getStatements() != null ? node.getStatements().size() : 0;
    }

    public ApexNode<?> getStatement(int index) {
        int statementIndex = index;
	if (!isDefault()) {
	    statementIndex++;
	}
	return (ApexNode<?>) jjtGetChild(statementIndex);
    }
}
