/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassDeclaration;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTNestedClassDeclaration;
import net.sourceforge.pmd.ast.ASTNestedInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTUnmodifiedClassDeclaration;



public class TestClassWithoutTestCases extends AbstractRule {
	
	public Object visit(ASTInterfaceDeclaration node, Object data) {
		// Skip interfaces
		//return super.visit(node, data);
		return data;
	}
	
	public Object visit(ASTNestedClassDeclaration node, Object data) {
		// Skip nested clases 
//		return super.visit(node, data);
		return data;
	}
	
	public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
		// Skip nested interfaces
//		return super.visit(node, data);
		return data;
	}

	public Object visit(ASTClassDeclaration node, Object data) {
		String className = ((ASTUnmodifiedClassDeclaration)node.getFirstChildOfType(ASTUnmodifiedClassDeclaration.class)).getImage();
		if (className.endsWith("Test")) {
			List m = node.findChildrenOfType(ASTMethodDeclarator.class);
			boolean testsFound = false;
			if (m!=null) {
				for (Iterator it = m.iterator() ; it.hasNext() && !testsFound ; ) {
					ASTMethodDeclarator md = (ASTMethodDeclarator) it.next();
					if (!isInInnerClassOrInterface(md)
							&& md.getImage().startsWith("test")) {
								testsFound = true;				
					}
				}
			} 
			
			if (!testsFound) {
				addViolation((RuleContext)data, node.getBeginLine());
			}
			
		}
		return data;
	}

	private boolean isInInnerClassOrInterface(ASTMethodDeclarator md) {
		Object p;
		p = md.getFirstParentOfType(ASTNestedClassDeclaration.class);
		if (p!=null)
			return true;
		p = md.getFirstParentOfType(ASTNestedInterfaceDeclaration.class);
		if (p!=null)
			return true;
		return false;
	}
}
