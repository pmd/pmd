/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ArrayComprehensionLoop;

public class ASTArrayComprehensionLoop extends AbstractApexNode<ArrayComprehensionLoop> {

    public ASTArrayComprehensionLoop(ArrayComprehensionLoop arrayComprehensionLoop) {
	super(arrayComprehensionLoop);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getIterator() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getIteratedObject() {
	return (ApexNode<?>) jjtGetChild(1);
    }
}
