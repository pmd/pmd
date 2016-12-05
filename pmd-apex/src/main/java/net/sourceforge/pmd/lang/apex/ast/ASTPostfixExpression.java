/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.PostfixExpression;

public class ASTPostfixExpression extends AbstractApexNode<PostfixExpression> {

    public ASTPostfixExpression(PostfixExpression postfixExpression) {
        super(postfixExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
