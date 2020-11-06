/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.Collections;
import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;

/**
 * A singleton iterator that never has anything.
 *
 * @author Brian Remedios
 *
 * @param <T>
 * @deprecated Use {@link Collections#emptyIterator()}
 */
@InternalApi
@Deprecated
public final class EmptyIterator<T extends Object> implements Iterator<T> {

    @SuppressWarnings("rawtypes")
    public static final Iterator INSTANCE = new EmptyIterator();

    private EmptyIterator() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends Object> Iterator<T> instance() {
        return Collections.emptyIterator();
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
