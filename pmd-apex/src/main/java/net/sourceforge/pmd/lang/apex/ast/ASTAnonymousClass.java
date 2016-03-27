/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.AnonymousClass;
import net.sourceforge.pmd.lang.ast.RootNode;

public class ASTAnonymousClass extends AbstractApexNode<AnonymousClass> implements RootNode {

	public ASTAnonymousClass(AnonymousClass anonymousClass) {
		super(anonymousClass);
	}


    /**
     * Accept the visitor.
     * Note: This needs to be in each concrete node class, as otherwise
     * the visitor won't work - as java resolves the type "this" at compile
     * time.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

	@Override
	public String getImage() {
		return node.getClass().getName();
	}
}
