/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.VariableInitializer;

public class ASTVariableInitializer extends AbstractApexNode<VariableInitializer> implements DestructuringNode {
    public ASTVariableInitializer(VariableInitializer variableInitializer) {
	super(variableInitializer);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ApexNode<?> getTarget() {
	return (ApexNode<?>) jjtGetChild(0);
    }

    public ApexNode<?> getInitializer() {
	if (jjtGetNumChildren() > 0) {
	    return (ApexNode<?>) jjtGetChild(1);
	} else {
	    return null;
	}
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }
}
