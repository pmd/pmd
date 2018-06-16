/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.Identifier;
import apex.jorje.semantic.ast.compilation.UserTrigger;

public class ASTUserTrigger extends ApexRootNode<UserTrigger> {

    public ASTUserTrigger(UserTrigger userTrigger) {
        super(userTrigger);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        try {
            Field field = node.getClass().getDeclaredField("name");
            field.setAccessible(true);
            Identifier name = (Identifier) field.get(node);
            return name.getValue();
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
