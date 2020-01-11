/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import static java.lang.Integer.min;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;
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
        int realEnd = min(getEndOffset(), fullText.length()); // TODO EOF token messes things up?
        return fullText.substring(getStartOffset(), realEnd);
    }

    @Override
    public JavaccToken jjtGetFirstToken() {
        return (JavaccToken) super.jjtGetFirstToken();
    }

    @Override
    public JavaccToken jjtGetLastToken() {
        return (JavaccToken) super.jjtGetLastToken();
    }

    @Override
    public N jjtGetChild(int index) {
        return (N) super.jjtGetChild(index);
    }

    @Override
    public N jjtGetParent() {
        return (N) super.jjtGetParent();
    }

    @Override
    public int getBeginLine() {
        return jjtGetFirstToken().getBeginLine();
    }

    @Override
    public int getBeginColumn() {
        return jjtGetFirstToken().getBeginColumn();
    }

    @Override
    public int getEndLine() {
        return jjtGetLastToken().getEndLine();
    }

    @Override
    public int getEndColumn() {
        return jjtGetLastToken().getEndColumn();
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
