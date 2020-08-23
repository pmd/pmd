/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.ast.impl.AbstractNodeWithTextCoordinates;

abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNodeWithTextCoordinates<AbstractEcmascriptNode<?>, EcmascriptNode<?>> implements EcmascriptNode<T> {

    protected final T node;
    private String image;

    AbstractEcmascriptNode(T node) {
        this.node = node;
    }

    @Override
    protected void addChild(AbstractEcmascriptNode<?> child, int index) {
        super.addChild(child, index);
    }

    @Override
    public String getImage() {
        return image;
    }

    protected void setImage(String image) {
        this.image = image;
    }

    /* package private */
    void calculateLineNumbers(SourceCodePositioner positioner) {
        int startOffset = node.getAbsolutePosition();
        int endOffset = startOffset + node.getLength();

        this.beginLine = positioner.lineNumberFromOffset(startOffset);
        this.beginColumn = positioner.columnFromOffset(this.beginLine, startOffset);
        this.endLine = positioner.lineNumberFromOffset(endOffset);
        // end column is inclusive
        this.endColumn = positioner.columnFromOffset(this.endLine, endOffset) - 1;
        if (this.endColumn < 0) {
            this.endColumn = 0;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <P, R> R acceptVisitor(AstVisitor<? super P, ? extends R> visitor, P data) {
        if (!(visitor instanceof EcmascriptVisitor)) {
            return super.acceptVisitor(visitor, data);
        }
        return acceptJsVisitor((EcmascriptVisitor<? super P, ? extends R>) visitor, data);
    }

    protected abstract <P, R> R acceptJsVisitor(EcmascriptVisitor<? super P, ? extends R> visitor, P data);

    @Override
    @Deprecated
    public T getNode() {
        return node;
    }

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
