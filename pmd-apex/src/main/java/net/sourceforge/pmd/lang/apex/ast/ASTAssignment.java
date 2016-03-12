/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.Assignment;

public class ASTAssignment extends AbstractInfixApexNode<Assignment> {
    public ASTAssignment(Assignment asssignment) {
	super(asssignment);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
