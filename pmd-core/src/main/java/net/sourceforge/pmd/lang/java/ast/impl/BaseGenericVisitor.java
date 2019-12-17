/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast.impl;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Base class for tree visitors that take one parameter, and return a
 * value.
 *
 * <p>Non-generic subclasses extend only the raw type of this class.
 * This is to avoid source-level incompatibilities when generifying
 * those classes. For example, For now {@code JavaParserVisitor <: BaseGenericVisitor},
 * where the {@code AbstractVisitorAdapter} is a raw type. For 7.0.0,
 * this relation will become {@code JavaParserVisitor<R, P> <: BaseGenericVisitor<JavaNode, R, P>}.
 * Because {@code JavaParserVisitor} used to extend the raw type, the
 * relation {@code JavaParserVisitor <: BaseGenericVisitor}, where
 * now both are raw types, will still hold. On the other hand, if we today
 * make {@code JavaParserVisitor} extend {@code BaseGenericVisitor<JavaNode, Object, Object>},
 * then that relation will break in 7.0.0, as {@code JavaParserVisitor}
 * as a raw type is not convertible to {@code BaseGenericVisitor<JavaNode, Object, Object>}
 * anymore, only to the other raw type {@code BaseGenericVisitor}.
 *
 * @param <N> Type of node this visitor accepts
 * @param <P> Type of parameter for the visitor
 * @param <R> Return type of the visit methods
 */
@Experimental
public abstract class BaseGenericVisitor<N extends Node, R, P> {

    /**
     * Initial value when combining values returned by children.
     * Note that non-generic subclasses return {@code data} today,
     * which is only valid if {@code R = P}. In 7.0.0 this will
     * be changed to return null.
     *
     * @param parent Parent node
     * @param data   Value of the parameter for the parent
     */
    protected R zero(N parent, P data) {
        return null;
    }

    /**
     * Merge two values of type Object, used to combine values returned by children.
     * By default returns the [newest] value.
     *
     * @param acc    Accumulated value for the children preceding the current one
     * @param newest Newest value to combine with the accumulator
     * @param parent Parent node
     * @param data   Value of the parameter for the parent
     * @param idx    Index of the child in the parent, for which [newest] is the corresponding value
     */
    protected R combine(R acc, R newest, N parent, P data, int idx) {
        return newest;
    }

    /**
     * Make the node accept this visitor with parameter [data].
     */
    protected abstract R visitChildAt(N node, int idx, P data);


    public R visit(N node, P data) {
        R returnValue = zero(node, data);
        for (int i = 0; i < node.getNumChildren(); ++i) {
            returnValue = combine(returnValue, visitChildAt(node, i, data), node, data, i);
        }
        return returnValue;
    }

}
