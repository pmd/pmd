/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.data.ast.LiteralType;
import apex.jorje.semantic.ast.expression.LiteralExpression;


public class ASTLiteralExpression extends AbstractApexNode<LiteralExpression> {

    public ASTLiteralExpression(LiteralExpression literalExpression) {
        super(literalExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    public LiteralType getLiteralType() {
        return node.getLiteralType();
    }
}
