/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Base implementation of {@link AstVisitor}, that performs a top-down
 * (preorder) visit and may accumulate a result.
 *
 * <p>Note that if you care about the result ({@code <R>}), then you need
 * to override {@link #visitChildren(Node, Object) visitChildren} to implement
 * the logic that combines values from children, if any.
 */
public abstract class AstVisitorBase<P, R> implements AstVisitor<P, R> {

    /**
     * Visit the children. By default the data parameter is passed unchanged
     * to all descendants, and null is returned. Override this method to customize
     * this behavior.
     *
     * @param node Node whose children should be visited
     * @param data Parameter of the visit
     *
     * @return Some value for the children
     */
    // kept separate from super.visit for clarity
    protected R visitChildren(Node node, P data) {
        // this explicit loop is faster than iterating on a children node stream.
        for (int i = 0, numChildren = node.getNumChildren(); i < numChildren; i++) {
            node.getChild(i).acceptVisitor(this, data);
        }
        return null;
    }

    @Override
    public R visitNode(Node node, P param) {
        return visitChildren(node, param);
    }

}
