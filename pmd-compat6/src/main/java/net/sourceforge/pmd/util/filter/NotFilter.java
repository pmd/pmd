/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.util.filter;

/**
 * A logical NEGATION of a Filter.
 *
 * @param <T>
 *            The underlying type on which the filter applies.
 * @deprecated See {@link Filter}
 */
@Deprecated
public class NotFilter<T> extends net.sourceforge.pmd.util.filter.AbstractDelegateFilter<T> {
    public NotFilter() {
        super();
    }

    public NotFilter(Filter<T> filter) {
        super(filter);
    }

    @Override
    public boolean filter(T obj) {
        return !filter.filter(obj);
    }

    @Override
    public String toString() {
        return "not (" + filter + ")";
    }
}
