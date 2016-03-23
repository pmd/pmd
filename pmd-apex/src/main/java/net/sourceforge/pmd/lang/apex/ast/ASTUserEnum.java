/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserEnum;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTUserEnum extends AbstractApexNode<UserEnum> implements RootNode {
	
	public ASTUserEnum(UserEnum userEnum) {
		super(userEnum);
	}

	@Override
	public String getImage() {
		return node.getClass().getName();
	}
}
