/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectLiteral;

public final class ASTObjectLiteral extends AbstractEcmascriptNode<ObjectLiteral>
        implements DestructuringNode, TrailingCommaNode {
    private boolean trailingComma;

    ASTObjectLiteral(ObjectLiteral objectLiteral) {
        super(objectLiteral);
    }

    @Override
    protected <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public ASTObjectProperty getObjectProperty(int index) {
        return (ASTObjectProperty) getChild(index);
    }

    @Override
    public boolean isDestructuring() {
        return node.isDestructuring();
    }

    @Override
    public boolean isTrailingComma() {
        return trailingComma;
    }

    @Override
    protected void setTrailingCommaExists(boolean b) {
        this.trailingComma = b;
    }
}
