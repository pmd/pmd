/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.Compilation;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTCompilation extends AbstractApexNode<Compilation> implements RootNode {
	public ASTCompilation(Compilation classOrInterface) {
		super(classOrInterface);
	}

	@Override
	public String getImage() {
		return node.getClass().getName();
	}
}
