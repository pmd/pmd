/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class IteratorUtil {

    private IteratorUtil() {

    }

    private static final Iterator EMPTY = new Iterator() {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public void forEachRemaining(Consumer action) {
            // do nothing
        }

        @Override
        public Object next() {
            throw new NoSuchElementException("empty iterator");
        }
    };

    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> emptyIterator() {
        return EMPTY;
    }


    public static <T> Iterator<T> takeWhile(Iterator<T> splitr, Predicate<? super T> predicate) {
        return new Iterator<T>() {

            private T next;
            private boolean closed;

            @Override
            public boolean hasNext() {
                if (closed) {
                    return false;
                }
                while (next != null && splitr.hasNext()) {
                    T t = splitr.next();
                    if (predicate.test(t)) {
                        next = t;
                    } else {
                        closed = true;
                    }
                }
                return next != null;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return next;
            }
        };
    }

    public static <T> Iterator<T> reverse(Iterator<T> it) {
        List<T> tmp = toList(it);
        Collections.reverse(tmp);
        return tmp.iterator();
    }

    public static <T, R> Iterator<R> flatMap(Iterator<? extends T> iter, Function<? super T, ? extends Iterator<? extends R>> f) {
        return new Iterator<R>() {

            private Iterator<? extends R> current = null;

            @Override
            public boolean hasNext() {
                if (current != null && current.hasNext()) {
                    return true;
                } else {
                    while (iter.hasNext()) {
                        Iterator<? extends R> next = f.apply(iter.next());
                        if (next != null && next.hasNext()) {
                            current = next;
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override
            public R next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return current.next();
            }
        };
    }

    public static <T> Iterator<@NonNull T> filterNotNull(Iterator<? extends T> it) {
        return mapNotNull(it, Function.identity());
    }

    public static <T, R> Iterator<@NonNull R> mapNotNull(Iterator<? extends T> it, Function<? super @NonNull T, ? extends @Nullable R> mapper) {
        return new Iterator<R>() {

            private R next;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                while (it.hasNext()) {
                    T next1 = it.next();
                    if (next1 != null) {
                        R map = mapper.apply(next1);
                        if (map != null) {
                            this.next = map;
                            return true;
                        }
                    }
                }
                next = null;
                return false;
            }

            @Override
            public R next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                R r = next;
                next = null;
                return r;
            }
        };
    }

    public static <T> Iterator<T> peek(Iterator<? extends T> iter, Consumer<? super T> action) {
        return new Iterator<T>() {


            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public T next() {
                T t = iter.next();
                action.accept(t);
                return t;
            }
        };
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> as, Iterator<? extends T> bs) {
        return new Iterator<T>() {

            @Override
            public boolean hasNext() {
                return as.hasNext() || bs.hasNext();
            }

            @Override
            public T next() {
                return as.hasNext() ? as.next() : bs.next();
            }
        };
    }

    // Not a general purpose implementation, because mapNotNull doesn't let null values through
    public static <T> Iterator<T> distinct(Iterator<? extends T> iter) {
        Set<T> seen = new HashSet<>();
        return mapNotNull(iter, Filtermap.filter(seen::add));
    }


    public static <T> List<T> toList(Iterator<T> it) {
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }


    public static <T> Iterable<T> toIterable(final Iterator<T> it) {
        return () -> it;
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

    public static <T> @Nullable T last(Iterator<T> iterator) {
        T next = null;
        while (iterator.hasNext()) {
            next = iterator.next();
        }
        return next;
    }

    public static <T> @Nullable T getNth(Iterator<T> iterator, int n) {
        advance(iterator, n);
        return iterator.hasNext() ? iterator.next() : null;
    }

    /** Advance {@code n} times. */
    public static void advance(Iterator<?> iterator, int n) {
        AssertionUtil.assertArgNonNegative(n);

        while (n > 0 && iterator.hasNext()) {
            iterator.next();
            n--;
        }
    }


    /** Limit the number of elements yielded by this iterator to the given number. */
    public static <T> Iterator<T> take(Iterator<T> iterator, final int n) {
        AssertionUtil.assertArgNonNegative(n);
        if (n == 0) {
            return emptyIterator();
        }

        return new Iterator<T>() {
            private int yielded = 0;

            @Override
            public boolean hasNext() {
                return iterator.hasNext() && yielded < n;
            }

            @Override
            public T next() {
                yielded++;
                return iterator.next();
            }
        };
    }


    private static final int MATCH_ANY = 0;
    private static final int MATCH_ALL = 1;
    private static final int MATCH_NONE = 2;

    public static <T> boolean anyMatch(Iterator<? extends T> iterator, Predicate<? super T> pred) {
        return matches(iterator, pred, MATCH_ANY);
    }

    public static <T> boolean allMatch(Iterator<? extends T> iterator, Predicate<? super T> pred) {
        return matches(iterator, pred, MATCH_ALL);
    }

    public static <T> boolean noneMatch(Iterator<? extends T> iterator, Predicate<? super T> pred) {
        return matches(iterator, pred, MATCH_NONE);
    }

    private static <T> boolean matches(Iterator<? extends T> iterator, Predicate<? super T> pred, int matchKind) {
        final boolean kindAny = matchKind == MATCH_ANY;
        final boolean kindAll = matchKind == MATCH_ALL;

        while (iterator.hasNext()) {
            final T value = iterator.next();
            final boolean match = pred.test(value);
            if (match ^ kindAll) { // xor
                return kindAny && match;
            }
        }
        return !kindAny;
    }

    public static <T> Iterable<T> asReversed(final List<T> lst) {

        return () -> new Iterator<T>() {

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
}
