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
	
	@Override
	public boolean hasNext() { return false; }

	@Override
	public T next() { return null;	}

	@Override
	public void remove() {	
		throw new UnsupportedOperationException();
	}
};