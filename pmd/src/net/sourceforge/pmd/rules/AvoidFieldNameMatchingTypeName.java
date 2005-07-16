/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

public class AvoidFieldNameMatchingTypeName extends AbstractRule {

	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
		    return data;
        }
        return super.visit(node, data);
	}

	public Object visit(ASTFieldDeclaration node, Object data) {
		String varName = node.getVariableName();
		if (varName!=null) {
			varName = varName.toLowerCase();
			ASTClassOrInterfaceDeclaration cl = (ASTClassOrInterfaceDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
			if (cl!=null && cl.getImage() != null) {
				if (varName.equals(cl.getImage().toLowerCase())) {
					addViolation(data, node);
				}
			}
		}
		return data;
	}
}
