/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


/**
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class IteratorUtil {

    private IteratorUtil() {

    }

    public static <T> Iterator<T> reverse(Iterator<T> it) {
        List<T> tmp = toList(it);
        Collections.reverse(tmp);
        return tmp.iterator();
    }


    public static <T> List<T> toList(Iterator<T> it) {
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }


    public static <T> Iterable<T> toIterable(final Iterator<T> it) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return it;
            }
        };
    }


    /** Counts the items in this iterator, exhausting it. */
    public static int count(Iterator<?> it) {
        int count = 0;
        while (it.hasNext()) {
            it.next();
            count++;
        }
        return count;
    }


    public static <T> Iterable<T> asReversed(final List<T> lst) {

        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    ListIterator<T> li = lst.listIterator(lst.size());


                    @Override
                    public boolean hasNext() {
                        return li.hasPrevious();
                    }


                    @Override
                    public T next() {
                        return li.previous();
                    }


                    @Override
                    public void remove() {
                        li.remove();
                    }
                };
            }
        };
    }

    public abstract static class AbstractIterator<T> implements Iterator<T> {

        private State state = State.NOT_READY;
        private T next = null;


        @Override
        public boolean hasNext() {
            switch (state) {
            case DONE:
                return false;
            case READY:
                return true;
            default:
                state = null;
                computeNext();
                if (state == null) {
                    throw new IllegalStateException("Should have called done or setNext");
                }
                return state == State.READY;
            }
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            state = State.NOT_READY;
            return next;
        }

        protected final void setNext(T t) {
            next = t;
            state = State.READY;
        }

        protected final void done() {
            state = State.DONE;
        }

        protected abstract void computeNext();

        enum State {
            READY, NOT_READY, DONE
        }

        @Deprecated
        @Override
        public final void remove() {
            throw new UnsupportedOperationException();
        }

    }
}
