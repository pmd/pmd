/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.member.Method;
import net.sourceforge.pmd.Rule;

public class ASTMethod extends AbstractApexNode<Method> implements CanSuppressWarnings {

    public ASTMethod(Method method) {
        super(method);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getMethodInfo().getIdentifier().value;
    }

    @Override
    public int getEndLine() {
        ASTBlockStatement block = getFirstChildOfType(ASTBlockStatement.class);
        if (block != null) {
            return block.getEndLine();
        }

        return super.getEndLine();
    }

    @Override
    public int getEndColumn() {
        ASTBlockStatement block = getFirstChildOfType(ASTBlockStatement.class);
        if (block != null) {
            return block.getEndColumn();
        }

        return super.getEndColumn();
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