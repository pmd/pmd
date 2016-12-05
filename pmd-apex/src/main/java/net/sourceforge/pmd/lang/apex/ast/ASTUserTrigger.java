/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserTrigger;

public class ASTUserTrigger extends ApexRootNode<UserTrigger> {

    public ASTUserTrigger(UserTrigger userTrigger) {
        super(userTrigger);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        try {
            Field field = node.getClass().getDeclaredField("name");
            field.setAccessible(true);
            Identifier name = (Identifier) field.get(node);
            return name.value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.getImage();
    }
}
