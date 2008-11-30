/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.VariableDeclaration;

public class ASTVariableDeclaration extends AbstractEcmascriptNode<VariableDeclaration> {
    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
	super(variableDeclaration);
	super.setImage(Token.typeToName(variableDeclaration.getType()).toLowerCase());
    }

    /**
     * Accept the visitor.
     */
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
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