/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.FieldDeclaration;
import net.sourceforge.pmd.Rule;

public class ASTFieldDeclaration extends AbstractApexNode<FieldDeclaration> implements CanSuppressWarnings {

    public ASTFieldDeclaration(FieldDeclaration fieldDeclaration) {
        super(fieldDeclaration);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
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