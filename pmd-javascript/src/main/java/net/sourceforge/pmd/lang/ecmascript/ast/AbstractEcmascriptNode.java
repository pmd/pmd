/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.lang.document.TextRegion;

abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode<AbstractEcmascriptNode<?>, EcmascriptNode<?>> implements EcmascriptNode<T> {

    protected final T node;

    AbstractEcmascriptNode(T node) {
        this.node = node;
    }

    @Override
    protected void addChild(AbstractEcmascriptNode<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    public TextRegion getTextRegion() {
        return TextRegion.fromOffsetLength(node.getAbsolutePosition(), node.getLength());
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (visitor instanceof EcmascriptVisitor) {
            return acceptJsVisitor((EcmascriptVisitor<? super P, ? extends R>) visitor, data);
        }
        return visitor.cannotVisit(this, data);
    }

    protected abstract <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data);

    @Override
    public String getJsDoc() {
        return node.getJsDoc();
    }

    @Override
    public boolean hasSideEffects() {
        return node.hasSideEffects();
    }

    @Override
    public String getXPathNodeName() {
        return node.shortName();
    }

    protected void setTrailingCommaExists(boolean b) {
        // empty. Only needed for ASTArrayLiteral and ASTObjectLiteral
        // This method is protected to not clutter the public API via a interface
    }
}
