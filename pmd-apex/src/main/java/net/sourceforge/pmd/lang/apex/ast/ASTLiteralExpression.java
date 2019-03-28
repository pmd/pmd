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

    public boolean isString() {
        return node.getLiteralType() == LiteralType.STRING;
    }

    public boolean isBoolean() {
        return node.getLiteralType() == LiteralType.TRUE || node.getLiteralType() == LiteralType.FALSE;
    }

    public boolean isInteger() {
        return node.getLiteralType() == LiteralType.INTEGER;
    }

    public boolean isDouble() {
        return node.getLiteralType() == LiteralType.DOUBLE;
    }

    public boolean isLong() {
        return node.getLiteralType() == LiteralType.LONG;
    }

    public boolean isDecimal() {
        return node.getLiteralType() == LiteralType.DECIMAL;
    }

    public boolean isNull() {
        return node.getLiteralType() == LiteralType.NULL;
    }

    @Override
    public String getImage() {
        if (node.getLiteral() != null) {
            return String.valueOf(node.getLiteral());
        }
        return null;
    }
}
