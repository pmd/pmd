/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.sourceforge.pmd.lang.ast.Node;


/**
 * @author Clément Fournier
 * @since 6.6.0
 */
public final class IteratorUtil {

    private IteratorUtil() {

    }

    public static <T> Iterator<T> reverse(Iterator<T> it) {
        List<T> tmp = toList(it);
        Collections.reverse(tmp);
        return tmp.iterator();
    }

    public static <T> List<T> toList(Iterator<T> it) {
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }


    public static <T> Iterable<T> wrap(Iterator<T> it) {
        return () -> it;
    }


    /**
     * Returns an iterator over the parents of the given node, in innermost to outermost order.
     */
    public static Iterator<Node> parentIterator(Node deepest, boolean includeDeepest) {
        return iteratorFrom(deepest, n -> n.jjtGetParent() != null, Node::jjtGetParent, includeDeepest);
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
            private boolean includeCurrent = includeSeed; // include the current item iff it's the first and includeFirst


            @Override
            public boolean hasNext() {
                return includeCurrent || hasSuccessor.test(current);
            }


            @Override
            public T next() {

                if (includeCurrent) {
                    includeCurrent = false;
                } else {
                    current = successorFun.apply(current);
                }

                return current;
            }
        };
    }


}
