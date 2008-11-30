/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ForInLoop;

public class ASTForInLoop extends AbstractEcmascriptNode<ForInLoop> {
    public ASTForInLoop(ForInLoop forInLoop) {
	super(forInLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getIterator() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public EcmascriptNode getIteratedObject() {
	return (EcmascriptNode) jjtGetChild(1);
    }

    public EcmascriptNode getBody() {
	return (EcmascriptNode) jjtGetChild(2);
    }

    public boolean isForEach() {
	return node.isForEach();
    }
}
