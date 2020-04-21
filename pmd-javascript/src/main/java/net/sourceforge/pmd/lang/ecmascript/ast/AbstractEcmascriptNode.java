/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;
import net.sourceforge.pmd.lang.ast.impl.AbstractNode;
import net.sourceforge.pmd.util.document.FileLocation;
import net.sourceforge.pmd.util.document.TextDocument;
import net.sourceforge.pmd.util.document.TextRegion;

abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode<AbstractEcmascriptNode<?>, EcmascriptNode<?>> implements EcmascriptNode<T> {

    protected final T node;
    private String image;
    private TextDocument textDocument;

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
    void calculateLineNumbers(TextDocument positioner) {
        this.textDocument = positioner;
    }

    @Override
    public FileLocation getReportLocation() {
        return textDocument.toLocation(TextRegion.fromOffsetLength(node.getAbsolutePosition(), node.getLength()));
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
