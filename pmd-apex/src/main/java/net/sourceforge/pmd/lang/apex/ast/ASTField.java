/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

import apex.jorje.semantic.ast.member.Field;

public final class ASTField extends AbstractApexNode<Field> implements Node {

    ASTField(Field field) {
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


    public String getType() {
        return node.getFieldInfo().getType().getApexName();
    }

    public ASTModifierNode getModifiers() {
        return getFirstChildOfType(ASTModifierNode.class);
    }

    public String getName() {
        return node.getFieldInfo().getName();
    }

    public String getValue() {
        if (node.getFieldInfo().getValue() != null) {
            return String.valueOf(node.getFieldInfo().getValue());
        }
        return null;
    }
}
