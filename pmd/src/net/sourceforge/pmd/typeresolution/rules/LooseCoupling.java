/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.typeresolution.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * This is a separate rule, uses the type resolution facade
 */
public class LooseCoupling extends AbstractRule {

	public LooseCoupling() {
		super();
	}

	public Object visit(ASTClassOrInterfaceType node, Object data) {
		Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
		Class clazzType = node.getType();
		boolean isType = CollectionUtil.isCollectionType(clazzType, false);
		if (isType
				&& (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
			addViolation(data, node, node.getImage());
		}
		return data;
	}
}
