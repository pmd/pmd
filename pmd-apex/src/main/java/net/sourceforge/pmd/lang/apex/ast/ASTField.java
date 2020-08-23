/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Field;

public final class ASTField extends AbstractApexNode<Field> {

    ASTField(Field field) {
        super(field);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
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
