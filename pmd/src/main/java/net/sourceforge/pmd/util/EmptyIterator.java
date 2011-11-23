package net.sourceforge.pmd.util;

import java.util.Iterator;

/**
 * A singleton iterator that never has anything.
 * 
 * @author Brian Remedios
 *
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public class EmptyIterator<T extends Object> implements Iterator<T> {

	public static final Iterator instance = new EmptyIterator();
	
	private EmptyIterator() {}
	
	public boolean hasNext() { return false; }

	public T next() { return null;	}

	public void remove() {	
		throw new UnsupportedOperationException();
	}
};