/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import org.mozilla.apex.Token;
import org.mozilla.apex.ast.VariableDeclaration;

public class ASTVariableDeclaration extends AbstractApexNode<VariableDeclaration> {
    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
	super(variableDeclaration);
	super.setImage(Token.typeToName(variableDeclaration.getType()).toLowerCase());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ASTVariableInitializer getVariableInitializer(int index) {
	return (ASTVariableInitializer) jjtGetChild(index);
    }

    public boolean isVar() {
	return node.isVar();
    }

    public boolean isLet() {
	return node.isLet();
    }

    public boolean isConst() {
	return node.isConst();
    }
}