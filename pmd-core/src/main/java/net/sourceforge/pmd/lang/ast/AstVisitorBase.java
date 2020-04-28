/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Base implementation of {@link AstVisitor}, that performs a top-down
 * (preorder) visit and may accumulate a result.
 */
public abstract class AstVisitorBase<P, R> implements AstVisitor<P, R> {

    /** Initial value when combining values returned by children. */
    protected R zero() {
        return null;
    }

    /**
     * Merge two values of type R, used to combine values returned by children.
     *
     * @param acc        Current accumulated value for the previous siblings
     *                   (or {@link #zero()} if the child is the first child)
     * @param childValue Value for the new child
     *
     * @return New accumulated value to use for the next sibling
     */
    protected R combine(R acc, R childValue) {
        return acc;
    }

    /**
     * Visit the children. By default the data parameter is passed unchanged
     * to all descendants. The {@link #zero() zero} and {@link #combine(Object, Object) combine}
     * functions should be implemented if this is to return something else
     * than null.
     *
     * @param node Node whose children should be visited
     * @param data Parameter of the visit
     *
     * @return Some value for the children
     */
    // kept separate from super.visit for clarity
    protected R visitChildren(Node node, P data) {
        R result = zero();
        for (Node child : node.children()) {
            R r1 = child.acceptVisitor(this, data);
            result = combine(result, r1);
        }
        return result;
    }

    @Override
    public R visitNode(Node node, P param) {
        return visitChildren(node, param);
    }

}
