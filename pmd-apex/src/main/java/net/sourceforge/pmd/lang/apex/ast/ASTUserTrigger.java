/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.UserTrigger;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTUserTrigger extends AbstractApexNode<UserTrigger> implements RootNode {
	
	public ASTUserTrigger(UserTrigger userTrigger) {
		super(userTrigger);
	}

	@Override
	public String getImage() {
		return node.getClass().getName();
	}
}
