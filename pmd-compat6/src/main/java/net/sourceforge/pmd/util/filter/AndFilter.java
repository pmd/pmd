/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

// This file has been taken from 6.55.0

package net.sourceforge.pmd.util.filter;

/**
 * A logical AND of a list of Filters. This implementation is short circuiting.
 *
 * @param <T>
 *            The underlying type on which the filter applies.
 * @deprecated See {@link Filter}
 */
@Deprecated
public class AndFilter<T> extends AbstractCompoundFilter<T> {

    public AndFilter() {
        super();
    }

    public AndFilter(Filter<T>... filters) {
        super(filters);
    }

    @Override
    public boolean filter(T obj) {
        boolean match = true;
        for (Filter<T> filter : filters) {
            if (!filter.filter(obj)) {
                match = false;
                break;
            }
        }
        return match;
    }

    @Override
    protected String getOperator() {
        return "and";
    }
}
