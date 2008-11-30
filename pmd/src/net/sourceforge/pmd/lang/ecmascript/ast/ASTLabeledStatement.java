/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.LabeledStatement;

public class ASTLabeledStatement extends AbstractEcmascriptNode<LabeledStatement> {
    public ASTLabeledStatement(LabeledStatement labeledStatement) {
	super(labeledStatement);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public int getNumLabels() {
	return node.getLabels().size();
    }

    public ASTLabel getLabel(int index) {
	return (ASTLabel) jjtGetChild(index);
    }

    public EcmascriptNode getStatement() {
	return (EcmascriptNode) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
