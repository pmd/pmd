/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.filter;

/**
 * A Filter interface, used for filtering arbitrary objects.
 *
 * @param <T>
 *            The underlying type on which the filter applies.
 *
 * @deprecated Will be replaced with standard java.util.function.Predicate with 7.0.0
 */
@Deprecated
public interface Filter<T> {
    boolean filter(T obj);
}
