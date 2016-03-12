/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.LabeledStatement;

public class ASTLabeledStatement extends AbstractApexNode<LabeledStatement> {
    public ASTLabeledStatement(LabeledStatement labeledStatement) {
	super(labeledStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public int getNumLabels() {
	return node.getLabels().size();
    }

    public ASTLabel getLabel(int index) {
	return (ASTLabel) jjtGetChild(index);
    }

    public ApexNode<?> getStatement() {
	return (ApexNode<?>) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
