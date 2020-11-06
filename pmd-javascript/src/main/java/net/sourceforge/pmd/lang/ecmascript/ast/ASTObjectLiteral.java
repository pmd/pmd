/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectLiteral;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTObjectLiteral extends AbstractEcmascriptNode<ObjectLiteral>
        implements DestructuringNode, TrailingCommaNode {
    private boolean trailingComma;

    @Deprecated
    @InternalApi
    public ASTObjectLiteral(ObjectLiteral objectLiteral) {
        super(objectLiteral);
    }

    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
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
    @Deprecated
    @InternalApi
    public void setTrailingComma(boolean trailingComma) {
        setTrailingCommaExists(trailingComma);
    }

    @Override
    protected void setTrailingCommaExists(boolean b) {
        this.trailingComma = b;
    }
}
