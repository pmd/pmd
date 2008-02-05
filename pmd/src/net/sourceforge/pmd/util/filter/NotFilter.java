package net.sourceforge.pmd.util.filter;

/**
 * A logical NEGATION of a Filter.
 * 
 * @param <T>
 *            The underlying type on which the filter applies.
 */
public class NotFilter<T> implements Filter<T> {
	protected Filter<T> filter;

	public NotFilter() {
	}

	public NotFilter(Filter<T> filter) {
		this.filter = filter;
	}

	public Filter<T> getFilter() {
		return filter;
	}

	public void setFilter(Filter<T> filter) {
		this.filter = filter;
	}

	public boolean filter(T obj) {
		return !filter.filter(obj);
	}

	public String toString() {
		return "not (" + filter + ")";
	}
}
