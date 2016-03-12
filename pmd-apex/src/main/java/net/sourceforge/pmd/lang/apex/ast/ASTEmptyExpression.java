/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.EmptyExpression;

public class ASTEmptyExpression extends AbstractApexNode<EmptyExpression> {
    public ASTEmptyExpression(EmptyExpression emptyExpression) {
	super(emptyExpression);
    }

    /**
     * Accept the visitor.
     */
    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
