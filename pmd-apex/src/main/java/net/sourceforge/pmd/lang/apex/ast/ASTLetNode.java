/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.LetNode;

public class ASTLetNode extends AbstractApexNode<LetNode> {
    public ASTLetNode(LetNode letNode) {
	super(letNode);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ASTVariableDeclaration getVariables() {
	return (ASTVariableDeclaration) jjtGetChild(0);
    }

    public boolean hasBody() {
	return node.getBody() != null;
    }

    public ApexNode<?> getBody() {
	if (hasBody()) {
	    return (ApexNode<?>) jjtGetChild(jjtGetNumChildren() - 1);
	} else {
	    return null;
	}
    }
}
