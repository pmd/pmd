/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;
import com.google.summit.ast.modifier.ElementArgument;
import com.google.summit.ast.modifier.ElementValue;

public final class ASTAnnotationParameter extends AbstractApexNode.Single<ElementArgument> {
    public static final String SEE_ALL_DATA = "seeAllData";

    /**
     * The {@code critical} modifier of the {@code @IsTest} annotation, used together with the
     * {@code RunRelevantTests} deployment test level. (Beta, Salesforce API v66.0+)
     *
     * @since 7.14.0
     */
    public static final String CRITICAL = "critical";

    /**
     * The {@code testFor} modifier of the {@code @IsTest} annotation, used together with the
     * {@code RunRelevantTests} deployment test level. (Beta, Salesforce API v66.0+)
     *
     * @since 7.14.0
     */
    public static final String TEST_FOR = "testFor";

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

    /**
     * Checks whether this annotation parameter has the given name.
     * The check is done case-insensitive.
     *
     * @param name the expected annotation parameter name
     * @return {@code true} if this parameter has the expected name.
     * @see #SEE_ALL_DATA
     * @since 7.4.0
     */
    public boolean hasName(@NonNull String name) {
        return name.equalsIgnoreCase(getName());
    }
}
