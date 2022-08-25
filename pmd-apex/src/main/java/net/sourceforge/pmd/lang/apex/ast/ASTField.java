/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.TypeRef;
import com.google.summit.ast.expression.Expression;
import com.google.summit.ast.expression.LiteralExpression;

public class ASTField extends AbstractApexNode implements CanSuppressWarnings {

    private final TypeRef type;
    private final Identifier name;
    private final Expression value;

    @Deprecated
    @InternalApi
    public ASTField(TypeRef type, Identifier name, Expression value) {
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
        if(value instanceof LiteralExpression) {
            return literalToString((LiteralExpression) value);
        }
        return null;
    }
}
