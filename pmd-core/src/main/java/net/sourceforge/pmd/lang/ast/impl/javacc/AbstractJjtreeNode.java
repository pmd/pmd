/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.AbstractNode;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for node produced by JJTree. JJTree specific functionality
 * present on the API of {@link Node} and {@link AbstractNode} will be
 * moved here for 7.0.0.
 *
 * <p>This is experimental because it's expected to change for 7.0.0 in
 * unforeseeable ways. Don't use it directly, use the node interfaces.
 */
@Experimental
public abstract class AbstractJjtreeNode<N extends Node> extends AbstractNode {

    public AbstractJjtreeNode(int id) {
        super(id);
    }

    public AbstractJjtreeNode(int id, int theBeginLine, int theEndLine, int theBeginColumn, int theEndColumn) {
        super(id, theBeginLine, theEndLine, theBeginColumn, theEndColumn);
    }


    @Override
    @SuppressWarnings("unchecked")
    public N getChild(int index) {
        return (N) super.getChild(index);
    }

    @Override
    @SuppressWarnings("unchecked")
    public N getParent() {
        return (N) super.getParent();
    }


    @Override
    @SuppressWarnings("unchecked")
    public Iterable<? extends N> children() {
        return (Iterable<N>) super.children();
    }
}
