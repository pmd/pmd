/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

/**
 * Root interface for AST visitors. Language modules publish a subinterface
 * with one separate visit method for each type of node in the language,
 * eg JavaVisitor.
 *
 * <p>Usually you never want to call {@code visit} methods manually, instead
 * calling {@link Node#acceptVisitor(AstVisitor, Object) Node::acceptVisitor},
 * which then dispatches to the most specific method of the visitor instance.
 *
 * <p>Use {@link Void} as a type parameter if you don't want a parameter type
 * or a return type.
 *
 * @param <P> Parameter type of the visit method
 * @param <R> Return type of the visit method
 */
public interface AstVisitor<P, R> {


    /**
     * Called by a node when it detects that the visitor is not of the
     * language it is used to visiting. If a visitor wants to visit nodes
     * for several languages, it should provide a useful implementation
     * of this method. The default implementation throws
     *
     * @param node  Node calling back this method
     * @param param Parameter of the visit
     *
     * @return A value (or may throw)
     */
    default R cannotVisit(Node node, P param) {
        throw new UnsupportedOperationException("Cannot visit " + node);
    }

    /**
     * Visit a node. This method is dispatched statically, you should
     * use {@link Node#acceptVisitor(AstVisitor, Object)} if you want
     * to call the most specific method instead.
     *
     * @param node  Node to visit
     * @param param Parameter
     *
     * @return Some result
     */
    R visitNode(Node node, P param);

}
