/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectLiteral;

public class ASTObjectLiteral extends AbstractEcmascriptNode<ObjectLiteral>
        implements DestructuringNode, TrailingCommaNode {
    private boolean trailingComma;

    public ASTObjectLiteral(ObjectLiteral objectLiteral) {
        super(objectLiteral);
    }

    /**
     * Accept the visitor.
     */
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
    public void setTrailingComma(boolean trailingComma) {
        this.trailingComma = trailingComma;
    }
}
