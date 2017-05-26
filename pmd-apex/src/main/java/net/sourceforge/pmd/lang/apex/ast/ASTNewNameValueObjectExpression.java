/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.expression.NewNameValueObjectExpression;

public class ASTNewNameValueObjectExpression extends AbstractApexNode<NewNameValueObjectExpression> {

    public ASTNewNameValueObjectExpression(NewNameValueObjectExpression newNameValueObjectExpression) {
        super(newNameValueObjectExpression);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
