/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.semantic.ast.statement.TypeWhenBlock;

public final class ASTTypeWhenBlock extends AbstractApexNode<TypeWhenBlock> {


    ASTTypeWhenBlock(TypeWhenBlock node) {
        super(node);
    }

    public String getType() {
        return String.valueOf(node.getTypeRef());
    }

    public String getName() {
        // unfortunately the name is not exposed...
        try {
            Field nameField = TypeWhenBlock.class.getDeclaredField("name");
            nameField.setAccessible(true);
            return String.valueOf(nameField.get(node));
        } catch (SecurityException | ReflectiveOperationException e) {
            return null;
        }
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
