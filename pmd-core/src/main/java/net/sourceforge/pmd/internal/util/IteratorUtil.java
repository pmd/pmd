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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;


/**
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class IteratorUtil {

    private IteratorUtil() {

    }


    public static <T> Stream<T> takeWhile(Stream<T> stream, Predicate<? super T> predicate) {
        return StreamSupport.stream(takeWhile(stream.spliterator(), predicate), false);
    }


    private static <T> Spliterator<T> takeWhile(Spliterator<T> splitr, Predicate<? super T> predicate) {
        return new Spliterators.AbstractSpliterator<T>(splitr.estimateSize(), 0) {
            boolean stillGoing = true;

            @Override
            public boolean tryAdvance(Consumer<? super T> consumer) {
                if (stillGoing) {
                    boolean hadNext = splitr.tryAdvance(elem -> {
                        if (predicate.test(elem)) {
                            consumer.accept(elem);
                        } else {
                            stillGoing = false;
                        }
                    });
                    return hadNext && stillGoing;
                }
                return false;
            }
        };
    }


    public static <T> Iterator<T> reverse(Iterator<T> it) {
        List<T> tmp = toList(it);
        Collections.reverse(tmp);
        return tmp.iterator();
    }

    public static <T, R> Iterator<@NonNull R> filterCast(Iterator<? extends T> it, Class<R> type) {
        return new Iterator<R>() {

            private R next;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                while (it.hasNext()) {
                    T next1 = it.next();
                    if (type.isInstance(next1)) { // returns false if null
                        this.next = type.cast(next1);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public R next() {
                if (next == null) {
                    throw new NoSuchElementException();
                }
                R r = next;
                next = null;
                return r;
            }
        };
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
}
