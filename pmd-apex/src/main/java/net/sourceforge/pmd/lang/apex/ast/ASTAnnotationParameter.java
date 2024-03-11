/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;
import com.google.summit.ast.modifier.ElementArgument;
import com.google.summit.ast.modifier.ElementValue;

public final class ASTAnnotationParameter extends AbstractApexNode.Single<ElementArgument> {
    public static final String SEE_ALL_DATA = "seeAllData";

    ASTAnnotationParameter(ElementArgument elementArgument) {
        super(elementArgument);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        return node.getName().getString();
    }

    public String getValue() {
        if (node.getValue() instanceof ElementValue.ExpressionValue) {
            Expression value = ((ElementValue.ExpressionValue) node.getValue()).getValue();
            if (value instanceof LiteralExpression) {
                return literalToString((LiteralExpression) value);
            }
        }
        return null;
    }

    public Boolean getBooleanValue() {
        if (node.getValue() instanceof ElementValue.ExpressionValue) {
            Expression value = ((ElementValue.ExpressionValue) node.getValue()).getValue();
            if (value instanceof LiteralExpression.BooleanVal) {
                return ((LiteralExpression.BooleanVal) value).getValue();
            }
        }
        return false;
    }

    @Override
    public String getImage() {
        return getValue();
    }
}
