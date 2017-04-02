/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

public interface SearchFunction<E> {
    /**
     * Applies the search function over a single element.
     * @param o The element to analyze.
     * @return True if the search should continue, false otherwhise.
     */
    boolean applyTo(E o);
}
