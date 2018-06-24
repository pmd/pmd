/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

public abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode implements EcmascriptNode<T> {

    protected final T node;

    public AbstractEcmascriptNode(T node) {
        super(node.getType());
        this.node = node;
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

    /**
     * Accept the visitor. *
     */
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Accept the visitor. *
     */
    @Override
    public Object childrenAccept(EcmascriptParserVisitor visitor, Object data) {
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                // we know that the children here
                // are all EcmascriptNodes
                @SuppressWarnings("unchecked")
                EcmascriptNode<T> ecmascriptNode = (EcmascriptNode<T>) children[i];
                ecmascriptNode.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    @Override
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
}
