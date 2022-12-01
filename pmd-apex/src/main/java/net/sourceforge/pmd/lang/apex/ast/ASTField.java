/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Optional;

import net.sourceforge.pmd.Rule;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.Node;
import com.google.summit.ast.TypeRef;
import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;

public class ASTField extends AbstractApexNode.Many<Node> implements CanSuppressWarnings {

    private final TypeRef type;
    private final Identifier name;
    private final Optional<Expression> value;

    ASTField(TypeRef type, Identifier name, Optional<Expression> value) {
        super(value.isPresent()
              ? Arrays.asList(type, name, value.get())
              : Arrays.asList(type, name));
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getName();
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        for (ASTModifierNode modifier : findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getType() {
        return type.asCodeString();
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
