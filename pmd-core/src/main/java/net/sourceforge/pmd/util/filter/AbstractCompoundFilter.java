/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A base class for Filters which implements behavior using a List of other
 * Filters.
 * 
 * @param <T>
 *            The underlying type on which the filter applies.
 */
public abstract class AbstractCompoundFilter<T> implements Filter<T> {

    protected List<Filter<T>> filters;

    public AbstractCompoundFilter() {
        filters = new ArrayList<>(2);
    }

    public AbstractCompoundFilter(Filter<T>... filters) {
        this.filters = Arrays.asList(filters);
    }

    public List<Filter<T>> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter<T>> filters) {
        this.filters = filters;
    }

    public void addFilter(Filter<T> filter) {
        filters.add(filter);
    }

    protected abstract String getOperator();

    @Override
    public String toString() {

        if (filters.isEmpty()) {
            return "()";
        }

        StringBuilder builder = new StringBuilder();
        builder.append('(').append(filters.get(0));

        for (int i = 1; i < filters.size(); i++) {
            builder.append(' ').append(getOperator()).append(' ');
            builder.append(filters.get(i));
        }
        builder.append(')');
        return builder.toString();
    }
}
