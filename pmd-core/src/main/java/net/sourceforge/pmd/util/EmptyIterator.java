/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util;

import java.util.Iterator;

/**
 * A singleton iterator that never has anything.
 * 
 * @author Brian Remedios
 *
 * @param <T>
 */
public final class EmptyIterator<T extends Object> implements Iterator<T> {

    @SuppressWarnings("rawtypes")
    public static final Iterator INSTANCE = new EmptyIterator();

    private EmptyIterator() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> Iterator<T> instance() {
        return INSTANCE;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public T next() {
        return null;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
}