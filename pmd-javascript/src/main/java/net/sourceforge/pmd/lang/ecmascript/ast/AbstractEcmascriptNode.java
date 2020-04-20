/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstNode;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

@Deprecated
@InternalApi
public abstract class AbstractEcmascriptNode<T extends AstNode> extends AbstractNode implements EcmascriptNode<T> {

    protected final T node;

    @Deprecated
    @InternalApi
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
