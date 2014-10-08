/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Creates a single compound Iterator from an array of Iterators.
 * 
 * @param <T> The type returned by the Iterator.
 * 
 * @see Iterator
 */
public class CompoundIterator<T> implements Iterator<T> {
    private final Iterator<T>[] iterators;
    private int index;

    /**
     * 
     * @param iterators The iterators use.
     */
    public CompoundIterator(Iterator<T>... iterators) {
	this.iterators = iterators;
	this.index = 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNext() {
	return getNextIterator() != null;
    }

    /**
     * {@inheritDoc}
     */
    public T next() {
	Iterator<T> iterator = getNextIterator();
	if (iterator != null) {
	    return iterator.next();
	} else {
	    throw new NoSuchElementException();
	}
    }

    /**
     * {@inheritDoc}
     */
    public void remove() {
	Iterator<T> iterator = getNextIterator();
	if (iterator != null) {
	    iterator.remove();
	} else {
	    throw new IllegalStateException();
	}
    }

    // Get the next iterator with values, returns null if there is no such iterator
    private Iterator<T> getNextIterator() {
	while (index < iterators.length) {
	    if (iterators[index].hasNext()) {
		return iterators[index];
	    } else {
		index++;
	    }
	}
	return null;
    }
}
