/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.VariableDeclaration;

public final class ASTVariableDeclaration extends AbstractEcmascriptNode<VariableDeclaration> {
    ASTVariableDeclaration(VariableDeclaration variableDeclaration) {
        super(variableDeclaration);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
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
