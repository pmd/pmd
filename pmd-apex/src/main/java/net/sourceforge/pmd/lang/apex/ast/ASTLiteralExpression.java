/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;
import java.util.Optional;

import apex.jorje.data.Identifier;
import apex.jorje.data.ast.LiteralType;
import apex.jorje.semantic.ast.expression.LiteralExpression;
import apex.jorje.semantic.ast.expression.NewKeyValueObjectExpression.NameValueParameter;


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

    public String getName() {
        if (getParent() instanceof ASTNewKeyValueObjectExpression) {
            ASTNewKeyValueObjectExpression parent = (ASTNewKeyValueObjectExpression) getParent();
            try {
                Field exprField = NameValueParameter.class.getDeclaredField("expression");
                exprField.setAccessible(true);
                Optional<NameValueParameter> parameter = parent.node.getParameters().stream().filter(p -> {
                    try {
                        return exprField.get(p) == this.node;
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        return false;
                    }
                }).findFirst();

                Field nameField = NameValueParameter.class.getDeclaredField("name");
                nameField.setAccessible(true);
                return parameter.map(p -> {
                    try {
                        return (Identifier) nameField.get(p);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        return null;
                    }
                }).map(Identifier::getValue).orElse(null);
            } catch (NoSuchFieldException | SecurityException e1) {
                return null;
            }
        }
        return null;
    }
}
