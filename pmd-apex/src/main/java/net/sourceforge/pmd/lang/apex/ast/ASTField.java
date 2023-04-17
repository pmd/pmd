/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Optional;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.Node;
import com.google.summit.ast.TypeRef;
import com.google.summit.ast.declaration.EnumDeclaration;
import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;

public final class ASTField extends AbstractApexNode.Many<Node> {

    private final Identifier name;
    private final Optional<Expression> value;
    private final String typeName;

    ASTField(TypeRef typeRef, Identifier name, Optional<Expression> value) {
        super(value.isPresent()
              ? Arrays.asList(typeRef, name, value.get())
              : Arrays.asList(typeRef, name));
        this.name = name;
        this.value = value;
        this.typeName = caseNormalizedTypeIfPrimitive(typeRef.asCodeString());
    }

    ASTField(EnumDeclaration enumType, Identifier name) {
        super(Arrays.asList(name));
        this.name = name;
        this.value = Optional.empty();
        this.typeName = enumType.getId().asCodeString();
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getName();
    }


    /**
     * Returns the type name.
     *
     * This includes any type arguments. (This is tested.)
     * If the type is a primitive, its case will be normalized.
     */
    public String getType() {
        return typeName;
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getName() {
        return name.getString();
    }

    public String getValue() {
        if (value.isPresent()) {
            if (value.get() instanceof LiteralExpression) {
                return literalToString((LiteralExpression) value.get());
            }
        }
        return null;
    }
}
