/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * @author Clément Fournier
 * @since 6.11.0
 */
public final class IteratorUtil {

    private static final int MATCH_ANY = 0;
    private static final int MATCH_ALL = 1;
    private static final int MATCH_NONE = 2;

    private IteratorUtil() {

    }

    public static <T> Iterator<T> takeWhile(Iterator<T> iter, Predicate<? super T> predicate) {
        return new AbstractIterator<T>() {
            @Override
            protected void computeNext() {
                if (iter.hasNext()) {
                    T next = iter.next();
                    if (predicate.test(next)) {
                        setNext(next);
                        return;
                    }
                }
                done();
            }
        };
    }

    public static <T> Iterator<T> reverse(Iterator<T> it) {
        List<T> tmp = toList(it);
        Collections.reverse(tmp);
        return tmp.iterator();
    }

    public static <T, R> Iterator<R> flatMap(Iterator<? extends T> iter, Function<? super T, ? extends @Nullable Iterator<? extends R>> f) {
        return new AbstractIterator<R>() {
            private Iterator<? extends R> current = null;

            @Override
            protected void computeNext() {
                if (current != null && current.hasNext()) {
                    setNext(current.next());
                } else {
                    while (iter.hasNext()) {
                        Iterator<? extends R> next = f.apply(iter.next());
                        if (next != null && next.hasNext()) {
                            current = next;
                            setNext(current.next());
                            return;
                        }
                    }
                    done();
                }
            }
        };
    }

    /**
     * Like flatMap, but yields each element of the input iterator before
     * yielding the results of the mapper function. Null elements of the
     * input iterator are both yielded by the returned iterator and passed
     * to the stepper. If the stepper returns null, that result is ignored.
     */
    public static <R> Iterator<R> flatMapWithSelf(Iterator<? extends R> iter, Function<? super R, ? extends @Nullable Iterator<? extends R>> f) {
        return new AbstractIterator<R>() {
            private Iterator<? extends R> current = null;

            @Override
            protected void computeNext() {
                if (current != null && current.hasNext()) {
                    setNext(current.next());
                } else {
                    // current is exhausted
                    current = null;
                    if (iter.hasNext()) {
                        R next = iter.next();
                        setNext(next);
                        current = f.apply(next);
                    } else {
                        done();
                    }
                }
            }
        };
    }

    public static <T> Iterator<@NonNull T> filterNotNull(Iterator<? extends T> it) {
        return filter(it, Objects::nonNull);
    }

    public static <T, R> Iterator<@NonNull R> mapNotNull(Iterator<? extends T> it, Function<@NonNull ? super T, @Nullable ? extends R> mapper) {
        return new AbstractIterator<R>() {
            @Override
            protected void computeNext() {
                while (it.hasNext()) {
                    T next = it.next();
                    if (next != null) {
                        R map = mapper.apply(next);
                        if (map != null) {
                            setNext(map);
                            return;
                        }
                    }
                }
                done();
            }
        };
    }

    public static <T> Iterator<T> filter(Iterator<? extends T> it, Predicate<? super T> filter) {
        return new AbstractIterator<T>() {
            @Override
            protected void computeNext() {
                while (it.hasNext()) {
                    T next = it.next();
                    if (filter.test(next)) {
                        setNext(next);
                        return;
                    }
                }
                done();
            }
        };
    }

    public static <T> Iterator<T> peek(Iterator<? extends T> iter, Consumer<? super T> action) {
        return map(iter, it -> {
            action.accept(it);
            return it;
        });
    }

    public static <T, R> Iterator<R> map(Iterator<? extends T> iter, Function<? super T, ? extends R> mapper) {
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public R next() {
                return mapper.apply(iter.next());
            }
        };
    }

    /**
     * Apply a transform on the iterator of an iterable.
     */
    public static <T, R> Iterable<R> mapIterator(Iterable<? extends T> iter, Function<? super Iterator<? extends T>, ? extends Iterator<R>> mapper) {
        return () -> mapper.apply(iter.iterator());
    }

    @SafeVarargs
    public static <T> Iterator<T> iterate(T... elements) {
        return Arrays.asList(elements).iterator();
    }

    public static <T> Iterator<T> concat(Iterator<? extends T> as, Iterator<? extends T> bs) {
        if (!as.hasNext()) {
            return (Iterator<T>) bs;
        } else if (!bs.hasNext()) {
            return (Iterator<T>) as;
        }
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

    public static <T> Iterator<T> distinct(Iterator<? extends T> iter) {
        Set<T> seen = new HashSet<>();
        return filter(iter, seen::add);
    }

    public static <T> List<T> toList(Iterator<? extends T> it) {
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    public static <T> List<@NonNull T> toNonNullList(Iterator<? extends @Nullable T> it) {
        List<@NonNull T> list = new ArrayList<>();
        while (it.hasNext()) {
            T next = it.next();
            if (next != null) {
                list.add(next);
            }
        }
        return list;
    }

    /**
     * Remove the last n elements of the iterator. This uses n elements as a lookahead.
     */
    public static <T> Iterator<@NonNull T> dropLast(Iterator<? extends @Nullable T> it, final int n) {
        AssertionUtil.requireNonNegative("n", n);
        if (n == 0) {
            return coerceWildcard(it); // noop
        } else if (n == 1) { // i guess this will be common
            if (!it.hasNext()) {
                return Collections.emptyIterator();
            }
            return new AbstractIterator<T>() {
                T next = it.next();

                @Override
                protected void computeNext() {
                    if (it.hasNext()) {
                        setNext(next);
                        next = it.next();
                    } else {
                        done();
                    }
                }
            };
        }

        // fill a circular lookahead buffer
        Object[] ringBuffer = new Object[n];
        for (int i = 0; i < n && it.hasNext(); i++) {
            ringBuffer[i] = it.next();
        }
        if (!it.hasNext()) {
            // the original iterator has less than n elements
            return Collections.emptyIterator();
        }

        return new AbstractIterator<T>() {
            private int idx = 0;

            @Override
            protected void computeNext() {
                if (it.hasNext()) {
                    setNext((T) ringBuffer[idx]); // yield element X from the buffer
                    ringBuffer[idx] = it.next();  // overwrite with the element X+n
                    idx = (idx + 1) % ringBuffer.length; // compute idx of element X+1
                } else {
                    // that's it: our buffer contains the n tail elements
                    // that we don't want to see.
                    done();
                }
            }
        };
    }

    /**
     * Coerce an iterator with a wildcard. This is safe because the Iterator
     * interface is covariant (not {@link ListIterator} though).
     */
    @SuppressWarnings("unchecked")
    public static <T> Iterator<T> coerceWildcard(final Iterator<? extends T> it) {
        return (Iterator<T>) it;
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

    public static <T> @Nullable T last(Iterator<? extends T> iterator) {
        T next = null;
        while (iterator.hasNext()) {
            next = iterator.next();
        }
        return next;
    }

    /**
     * Returns the nth element of this iterator, or null if the iterator
     * is shorter.
     *
     * @throws IllegalArgumentException If n is negative
     */
    public static <T> @Nullable T getNth(Iterator<? extends T> iterator, int n) {
        advance(iterator, n);
        return iterator.hasNext() ? iterator.next() : null;
    }


    /** Advance {@code n} times. */
    public static void advance(Iterator<?> iterator, int n) {
        AssertionUtil.requireNonNegative("n", n);

        while (n > 0 && iterator.hasNext()) {
            iterator.next();
            n--;
        }
    }

    /** Limit the number of elements yielded by this iterator to the given number. */
    public static <T> Iterator<T> take(Iterator<? extends T> iterator, final int n) {
        AssertionUtil.requireNonNegative("n", n);
        if (n == 0) {
            return Collections.emptyIterator();
        }

        return new AbstractIterator<T>() {
            private int yielded = 0;

            @Override
            protected void computeNext() {
                if (yielded >= n || !iterator.hasNext()) {
                    done();
                } else {
                    setNext(iterator.next());
                }
                yielded++;
            }
        };
    }

    /** Produce an iterator whose first element is the nth element of the given source. */
    public static <T> Iterator<T> drop(Iterator<? extends T> source, final int n) {
        AssertionUtil.requireNonNegative("n", n);
        if (n == 0) {
            return (Iterator<T>) source;
        }

        return new AbstractIterator<T>() {
            private int yielded = 0;

            @Override
            protected void computeNext() {
                while (yielded++ < n && source.hasNext()) {
                    source.next();
                }

                if (!source.hasNext()) {
                    done();
                } else {
                    setNext(source.next());
                }
            }
        };
    }

    /**
     * Returns an iterator that applies a stepping function to the previous
     * value yielded. Iteration stops on the first null value returned by
     * the stepper.
     *
     * @param seed    First value. If null then the iterator is empty
     * @param stepper Step function
     * @param <T>     Type of values
     */
    public static <T> Iterator<@NonNull T> generate(@Nullable T seed, Function<? super @NonNull T, ? extends @Nullable T> stepper) {
        return new AbstractIterator<T>() {
            T next = seed;

            @Override
            protected void computeNext() {
                if (next == null) {
                    done();
                    return;
                }
                setNext(next);
                next = stepper.apply(next);
            }
        };
    }

    /**
     * Returns whether some element match the predicate. If empty then {@code false}
     * is returned.
     */
    public static <T> boolean anyMatch(Iterator<? extends T> iterator, Predicate<? super T> pred) {
        return matches(iterator, pred, MATCH_ANY);
    }

    /**
     * Returns whether all elements match the predicate. If empty then {@code true}
     * is returned.
     */
    public static <T> boolean allMatch(Iterator<? extends T> iterator, Predicate<? super T> pred) {
        return matches(iterator, pred, MATCH_ALL);
    }

    /**
     * Returns whether no elements match the predicate. If empty then {@code true}
     * is returned.
     */
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

    public static <T> Iterator<T> singletonIterator(T value) {
        class SingletonIterator implements Iterator<T> {
            private boolean done;

            @Override
            public boolean hasNext() {
                return !done;
            }

            @Override
            public T next() {
                if (done) {
                    throw new NoSuchElementException();
                }
                done = true;
                return value;
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                action.accept(value);
            }
        }

        return new SingletonIterator();
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

    public static <T> Stream<T> toStream(Iterator<? extends T> iter) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, 0), false);
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
            assert state == null : "Must call exactly one of setNext or done";
            next = t;
            state = State.READY;
        }

        protected final void done() {
            assert state == null : "Must call exactly one of setNext or done";
            state = State.DONE;
        }

        /**
         * Compute the next element. Implementations must call either
         * {@link #done()} or {@link #setNext(Object)} exactly once.
         */
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

    public abstract static class AbstractPausingIterator<T> extends AbstractIterator<T> {

        private int numYielded = 0;
        private T currentValue;

        @Override
        public T next() {
            T next = super.next();
            currentValue = next;
            prepareViewOn(next);
            numYielded++;
            return next;
        }

        protected void prepareViewOn(T current) {
            // to be overridden
        }

        protected final int getIterationCount() {
            return numYielded;
        }

        protected T getCurrentValue() {
            ensureReadable();
            return currentValue;
        }

        protected void ensureReadable() {
            if (numYielded == 0) {
                throw new IllegalStateException("No values were yielded, should have called next");
            }
        }
    }
}
