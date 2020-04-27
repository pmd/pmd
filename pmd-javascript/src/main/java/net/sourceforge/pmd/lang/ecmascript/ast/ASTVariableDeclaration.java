/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.Locale;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.VariableDeclaration;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTVariableDeclaration extends AbstractEcmascriptNode<VariableDeclaration> {
    @Deprecated
    @InternalApi
    public ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
        super.setImage(Token.typeToName(variableDeclaration.getType()).toLowerCase(Locale.ROOT));
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTVariableInitializer getVariableInitializer(int index) {
        return (ASTVariableInitializer) getChild(index);
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
