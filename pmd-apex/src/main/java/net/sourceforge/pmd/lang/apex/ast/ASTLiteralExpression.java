/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.util.Optional;

import org.apache.commons.lang3.reflect.FieldUtils;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTLiteralExpression extends AbstractApexNode.Single<Node> {

    @Deprecated
    @InternalApi
    public ASTLiteralExpression(Node literalExpression) {
        super(literalExpression);
    }


    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /*
    public LiteralType getLiteralType() {
        return node.getLiteralType();
    }
     */
    // TODO(b/239648780)

    public boolean isString() {
        // return node.getLiteralType() == LiteralType.STRING;
        // TODO(b/239648780)
        return false;
    }

    public boolean isBoolean() {
        // return node.getLiteralType() == LiteralType.TRUE || node.getLiteralType() == LiteralType.FALSE;
        // TODO(b/239648780)
        return false;
    }

    public boolean isInteger() {
        // return node.getLiteralType() == LiteralType.INTEGER;
        // TODO(b/239648780)
        return false;
    }

    public boolean isDouble() {
        // return node.getLiteralType() == LiteralType.DOUBLE;
        // TODO(b/239648780)
        return false;
    }

    public boolean isLong() {
        // return node.getLiteralType() == LiteralType.LONG;
        // TODO(b/239648780)
        return false;
    }

    public boolean isDecimal() {
        // return node.getLiteralType() == LiteralType.DECIMAL;
        // TODO(b/239648780)
        return false;
    }

    public boolean isNull() {
        // return node.getLiteralType() == LiteralType.NULL;
        // TODO(b/239648780)
        return false;
    }

    @Override
    public String getImage() {
        /*
        if (node.getLiteral() != null) {
            return String.valueOf(node.getLiteral());
        }
         */
        // TODO(b/239648780)
        return null;
    }

    public String getName() {
        /*
        if (getParent() instanceof ASTNewKeyValueObjectExpression) {
            ASTNewKeyValueObjectExpression parent = (ASTNewKeyValueObjectExpression) getParent();
            Optional<NameValueParameter> parameter = parent.node.getParameters().stream().filter(p -> {
                try {
                    return this.node.equals(FieldUtils.readDeclaredField(p, "expression", true));
                } catch (IllegalArgumentException | ReflectiveOperationException e) {
                    return false;
                }
            }).findFirst();

            return parameter.map(p -> {
                try {
                    return (Identifier) FieldUtils.readDeclaredField(p, "name", true);
                } catch (IllegalArgumentException | ReflectiveOperationException e) {
                    return null;
                }
            }).map(Identifier::getValue).orElse(null);
        }
         */
        // TODO(b/239648780)
        return null;
    }
}
