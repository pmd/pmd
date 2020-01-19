/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.TextAvailableNode;

/**
 * Base class for node produced by JJTree. JJTree specific functionality
 * present on the API of {@link Node} and {@link AbstractNode} will be
 * moved here for 7.0.0.
 *
 * <p>This is experimental because it's expected to change for 7.0.0 in
 * unforeseeable ways. Don't use it directly, use the node interfaces.
 */
@Experimental
public abstract class AbstractJjtreeNode<N extends Node> extends AbstractNode implements TextAvailableNode {


    public AbstractJjtreeNode(int id) {
        super(id);
    }

    @Override
    public CharSequence getText() {
        String fullText = jjtGetFirstToken().document.getFullText();
        return fullText.substring(getStartOffset(), getEndOffset());
    }

    @Override
    public JavaccToken jjtGetFirstToken() {
        return (JavaccToken) super.jjtGetFirstToken();
    }

    @Override
    public JavaccToken jjtGetLastToken() {
        return (JavaccToken) super.jjtGetLastToken();
    }

    // the super methods query line & column, which we want to avoid

    @Override
    public void jjtSetLastToken(GenericToken token) {
        this.lastToken = token;
    }

    @Override
    public void jjtSetFirstToken(GenericToken token) {
        this.firstToken = token;
    }

    @Override
    @SuppressWarnings("unchecked")
    public N getChild(int index) {
        return (N) super.jjtGetChild(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public N getParent() {
        return (N) super.jjtGetParent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public NodeStream<? extends N> children() {
        return (NodeStream<N>) super.children();
    }

    @Override
    public int getBeginLine() {
        return firstToken.getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return firstToken.getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return lastToken.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return lastToken.getEndColumn();
    }

    /**
     * This toString implementation is only meant for debugging purposes.
     */
    @Override
    public String toString() {
        return "[" + getXPathNodeName() + ":" + getBeginLine() + ":" + getBeginColumn() + "]" + getText();
    }

    private int getStartOffset() {
        return this.jjtGetFirstToken().getStartInDocument();
    }


    private int getEndOffset() {
        return this.jjtGetLastToken().getEndInDocument();
    }
}
