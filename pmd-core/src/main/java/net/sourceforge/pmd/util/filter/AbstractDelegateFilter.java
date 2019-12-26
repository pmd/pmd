/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.filter;

/**
 * A base class for Filters which implements behavior using delegation to an
 * underlying filter.
 *
 * @param <T>
 *            The underlying type on which the filter applies.
 * @deprecated See {@link Filter}
 */
@Deprecated
public abstract class AbstractDelegateFilter<T> implements Filter<T> {
    protected Filter<T> filter;

    public AbstractDelegateFilter() {
        // default constructor
    }

    public AbstractDelegateFilter(Filter<T> filter) {
        this.filter = filter;
    }

    public Filter<T> getFilter() {
        return filter;
    }

    public void setFilter(Filter<T> filter) {
        this.filter = filter;
    }

    // Subclass should override to do something other the simply delegate.
    @Override
    public boolean filter(T obj) {
        return filter.filter(obj);
    }

    // Subclass should override to do something other the simply delegate.
    @Override
    public String toString() {
        return filter.toString();
    }
}
