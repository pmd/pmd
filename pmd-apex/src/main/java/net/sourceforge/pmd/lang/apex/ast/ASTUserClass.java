/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Field;

import apex.jorje.data.ast.Identifier;
import apex.jorje.semantic.ast.compilation.UserClass;
import net.sourceforge.pmd.Rule;

public class ASTUserClass extends ApexRootNode<UserClass> implements CanSuppressWarnings {

    public ASTUserClass(UserClass userClass) {
        super(userClass);
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

    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
    	for(ASTModifierNode modifier : findChildrenOfType(ASTModifierNode.class)) {
	    	for(ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
	            if (a.suppresses(rule)) {
	                return true;
	            }
	        }
    	}
        return false;
    }
}