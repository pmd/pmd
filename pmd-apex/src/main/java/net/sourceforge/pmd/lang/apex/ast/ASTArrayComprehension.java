/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ArrayComprehension;

public class ASTArrayComprehension extends AbstractApexNode<ArrayComprehension> {
    public ASTArrayComprehension(ArrayComprehension arrayComprehension) {
	super(arrayComprehension);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getResult() {
	return (ApexNode<?>) jjtGetChild(0);
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

    public ApexNode<?> getFilter() {
	return (ApexNode<?>) jjtGetChild(jjtGetNumChildren() - 1);
    }
}
