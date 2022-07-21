/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTField extends AbstractApexNode<Node> implements CanSuppressWarnings {

    @Deprecated
    @InternalApi
    public ASTField(Node field) {
        super(field);
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
        // return node.getFieldInfo().getType().getApexName();
        // TODO(b/239648780)
        return null;
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getName() {
        // return node.getFieldInfo().getName();
        // TODO(b/239648780)
        return null;
    }

    public String getValue() {
        /*
        if (node.getFieldInfo().getValue() != null) {
            return String.valueOf(node.getFieldInfo().getValue());
        }
         */
        // TODO(b/239648780)
        return null;
    }
}
