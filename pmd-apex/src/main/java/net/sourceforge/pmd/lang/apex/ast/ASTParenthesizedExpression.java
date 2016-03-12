/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.ast.ParenthesizedExpression;

public class ASTParenthesizedExpression extends AbstractApexNode<ParenthesizedExpression> {
    public ASTParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
	super(parenthesizedExpression);
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
