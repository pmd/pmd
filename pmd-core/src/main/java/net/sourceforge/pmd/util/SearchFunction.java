/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

/**
 * @deprecated Will be replaced with standard java.util.function.Predicate with 7.0.0
 */
@Deprecated
public interface SearchFunction<E> {
    /**
     * Applies the search function over a single element.
     * @param o The element to analyze.
     * @return True if the search should continue, false otherwhise.
     */
    boolean applyTo(E o);
}
