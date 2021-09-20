/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.apache.commons.lang3.reflect.FieldUtils;

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
            return String.valueOf(FieldUtils.readDeclaredField(node, "name", true));
        } catch (SecurityException | ReflectiveOperationException e) {
            return null;
        }
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
