/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayComprehension;

public class ASTArrayComprehension extends AbstractEcmascriptNode<ArrayComprehension> {
    public ASTArrayComprehension(ArrayComprehension arrayComprehension) {
	super(arrayComprehension);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getResult() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public int getNumArrayComprehensionLoops() {
	return node.getLoops().size();
    }

    public ASTArrayComprehensionLoop getArrayComprehensionLoop(int index) {
	return (ASTArrayComprehensionLoop) jjtGetChild(index + 1);
    }

    public boolean hasFilter() {
	return node.getFilter() != null;
    }

    public EcmascriptNode getFilter() {
	return (EcmascriptNode) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
