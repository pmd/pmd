/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sourceforge.pmd.lang.ast.Node;

import javafx.scene.control.TreeItem;


/**
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class DesignerIteratorUtil {

    // TODO move that into PMD core I can't stand it anymore

    private DesignerIteratorUtil() {

    }


    /**
     * Returns an iterator over the parents of the given node, in innermost to outermost order.
     */
    public static Iterator<Node> parentIterator(Node deepest, boolean includeSelf) {
        return iteratorFrom(deepest, n -> n.jjtGetParent() != null, Node::jjtGetParent, includeSelf);
    }


    /**
     * Returns an iterator over the parents of the given node, in innermost to outermost order.
     */
    public static <T> Iterator<TreeItem<T>> parentIterator(TreeItem<T> deepest, boolean includeSelf) {
        return iteratorFrom(deepest, n -> n.getParent() != null, TreeItem::getParent, includeSelf);
    }


    /**
     * Gets an iterator with a successor fun.
     *
     * @param seed         Seed item
     * @param hasSuccessor Tests whether the seed / the last item output has a successor
     * @param successorFun Successor function
     * @param includeSeed  Whether to include the seed as the first item of the iterator
     * @param <T>          Type of values
     *
     * @return An iterator
     */
    private static <T> Iterator<T> iteratorFrom(T seed, Predicate<T> hasSuccessor, Function<T, T> successorFun, boolean includeSeed) {

        return new Iterator<T>() {

            private T current = seed;
            private boolean myIncludeCurrent = includeSeed; // include the current item iff it's the first and includeFirst


            @Override
            public boolean hasNext() {
                return myIncludeCurrent || hasSuccessor.test(current);
            }


            @Override
            public T next() {

                if (myIncludeCurrent) {
                    myIncludeCurrent = false;
                } else {
                    current = successorFun.apply(current);
                }

                return current;
            }
        };
    }


}
