/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.impl.javacc;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.GenericToken;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.TokenBasedNode;

/**
 * Base class for node produced by JJTree. JJTree specific functionality
 * present on the API of {@link Node} and {@link AbstractNode} will be
 * moved here for 7.0.0.
 *
 * <p>This is experimental because it's expected to change for 7.0.0 in
 * unforeseeable ways. Don't use it directly, use the node interfaces.
 */
@Experimental
public abstract class AbstractJjtreeNode<N extends Node, T extends GenericToken> extends AbstractNode implements TokenBasedNode<T> {

    public AbstractJjtreeNode(int id) {
        super(id);
    }

    public AbstractJjtreeNode(int id, int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn) {
        super(id, theBeginLine, theEndLine, theBeginColumn, theEndColumn);
    }


    @Override
    @SuppressWarnings("unchecked")
    public T getFirstToken() {
        return (T) super.jjtGetFirstToken();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getLastToken() {
        return (T) super.jjtGetLastToken();
    }

    @Override
    public N getChild(int index) {
        return (N) super.getChild(index);
    }

    @Override
    public N getParent() {
        return (N) super.getParent();
    }
}
