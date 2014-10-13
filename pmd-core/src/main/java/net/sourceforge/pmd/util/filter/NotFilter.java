/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.filter;

/**
 * A logical NEGATION of a Filter.
 * 
 * @param <T>
 *            The underlying type on which the filter applies.
 */
public class NotFilter<T> extends AbstractDelegateFilter<T> {
	public NotFilter() {
		super();
	}

	public NotFilter(Filter<T> filter) {
		super(filter);
	}

	public boolean filter(T obj) {
		return !filter.filter(obj);
	}

	public String toString() {
		return "not (" + filter + ")";
	}
}
