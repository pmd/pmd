/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;



public class AvoidFieldNamesMatchingTypeName extends AbstractRule {
	
	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(net.sourceforge.pmd.ast.ASTInterfaceDeclaration, java.lang.Object)
	 */
	public Object visit(ASTInterfaceDeclaration node, Object data) {
		// Skip interfaces
		return super.visit(node, data);
	}
	
	/* (non-Javadoc)
	 * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(net.sourceforge.pmd.ast.ASTFieldDeclaration, java.lang.Object)
	 */
	public Object visit(ASTFieldDeclaration node, Object data) {
		String varName = node.getVariableName();
		if (varName!=null) {
			varName = varName.toLowerCase();
			ASTUnmodifiedClassDeclaration cl = (ASTUnmodifiedClassDeclaration) node.getFirstParentOfType(ASTUnmodifiedClassDeclaration.class);
			if (cl!=null && cl.getImage() != null) {
				if (varName.equals(cl.getImage().toLowerCase())) {
					addViolation((RuleContext) data, node);
				}
			}
		}
		return data;
	}
}
