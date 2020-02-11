/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.internal.util.IteratorUtil;

public final class StreamUtils {

    private StreamUtils() {

    }

    public static <T> Stream<T> streamOf(Collection<T> c) {
        return StrictStream.withElements(c);
    }

    @SafeVarargs
    public static <T> Stream<T> streamOf(T... c) {
        return Stream.of(c);
    }

    public static <T> Stream<T> streamOf(T c) {
        return Stream.of(c);
    }

    public static <T> List<T> toList(Stream<T> c) {
        if (c instanceof StrictStream) {
            return ((StrictStream<T>) c).toList();
        }
        return c.collect(Collectors.toList());
    }

    public static <T> Stream<T> streamOf(Iterator<T> c) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(c, 0), false);
    }

    @NonNull
    static <T, R> StrictStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper, Iterator<T> iterator) {
        List<R> results = new ArrayList<>();
        while (iterator.hasNext()) {
            T elt = iterator.next();
            Stream<? extends R> stream = mapper.apply(elt);
            if (stream instanceof StrictStream) {
                results.addAll(((StrictStream<? extends R>) stream).toList());
            } else {
                stream.forEach(results::add);
            }
        }
        return new StrictStream<>(results);
    }

    public static class Flatmap<T> {

        private final Iterator<T> iter;

        public Flatmap(Iterator<T> iter) {
            this.iter = iter;}

        public <R> StrictStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
            return StreamUtils.flatMap(mapper, iter);
        }
    }

    /**
     * A strict implementation of the {@link Stream} interface. This doesn't
     * respect the specification of the interface at all, as everything is
     * evaluated strictly. Most operations except flatmap are done in place,
     * on a buffer contained in this instance, and return the same stream.
     * You can identify them by their return type, which is StrictStream and
     * not Stream.
     * <p>This paradoxically somewhat matches the usage of the Stream API, ie
     * that streams are single-shot, non-reusable. Some operations fall back
     * on the lazy implementation (eg operations around IntStream, DoubleStream,
     * etc). Some are not supported at all.
     * <p>The intended use case is to help profiling applications that make
     * use of streams in performance-critical sections. Profiling lazy streams
     * is hard, because all the work is merged into the consumption site, not
     * the building site.
     */
    static class StrictStream<T> implements Stream<T> {

        private final List<T> elements;

        private StrictStream(List<T> elements) {
            this.elements = elements;
        }

        @SafeVarargs
        public static <T> StrictStream<T> withElements(T... elts) {
            return new StrictStream<>(new ArrayList<>(Arrays.asList(elts)));
        }

        public static <T> StrictStream<T> withElements(Collection<? extends T> elts) {
            return new StrictStream<>(new ArrayList<>(elts));
        }

        /**
         * Wraps the given list and perform operations like {@link #map(Function)}
         * or {@link #filter(Predicate)} in place. The returned stream
         * supports these if the list is modifiable, otherwise it only
         * supports "terminal" operations like {@link #reduce(Object, BinaryOperator)}.
         * @see #mapInPlace(Function)
         */
        public static <T> StrictStream<T> inPlace(List<T> elts) {
            return new StrictStream<>(elts);
        }

        /**
         * Returns the internal buffer.
         */
        public List<T> toList() {
            return elements;
        }

        public Stream<T> lazy() {
            return elements.stream();
        }

        @Override
        public StrictStream<T> filter(Predicate<? super T> predicate) {
            toList().removeIf(predicate.negate());
            return this;
        }

        @Override
        public <R> StrictStream<R> map(Function<? super T, ? extends R> mapper) {
            return mapInPlace(mapper);
        }

        @NonNull
        public <R> StrictStream<R> mapToNew(Function<? super T, ? extends R> mapper) {
            List<T> ts = toList();
            List<R> result = new ArrayList<>(ts.size());
            for (T t : ts) {
                result.add(mapper.apply(t));
            }
            return new StrictStream<>(result);
        }

        /**
         * Overwrite this instance's buffer by mapping them with the
         * given values. This is type-unsafe, and will fail if eg you
         * explicitly called {@link #inPlace(List)} and passed a list
         * that is unmodifiable, or checked (like {@link Arrays#asList(Object[])}
         * or {@link Collections#checkedList(List, Class)}).
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        public <R> StrictStream<R> mapInPlace(Function<? super T, ? extends R> mapper) {
            List ts = toList();
            for (int i = 0, tsSize = ts.size(); i < tsSize; i++) {
                ts.set(i, mapper.apply((T) ts.get(i)));
            }
            return (StrictStream<R>) this;
        }

        @Override
        public <R> StrictStream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper) {
            return StreamUtils.flatMap(mapper, iterator());
        }

        @Override
        public StrictStream<T> distinct() {
            Set<T> seen = new HashSet<>();
            toList().removeIf(t -> !seen.add(t));
            return this;
        }

        @Override
        @SuppressWarnings( {"unchecked", "rawtypes"})
        public StrictStream<T> sorted() {
            List elements = toList();
            elements.sort(Comparator.naturalOrder());
            return this;
        }

        @Override
        public StrictStream<T> sorted(Comparator<? super T> comparator) {
            toList().sort(comparator);
            return this;
        }

        @Override
        public StrictStream<T> limit(long maxSize) {
            List<T> elts = toList();
            if (maxSize > elts.size()) {
                return this;
            } else {
                elts.subList((int) maxSize, elts.size()).clear();
            }
            return this;
        }

        @Override
        public StrictStream<T> skip(long n) {
            List<T> elts = toList();
            elts.subList(0, Integer.min((int) n, elts.size())).clear();
            return this;
        }

        @Override
        public void forEach(Consumer<? super T> action) {
            for (T element : toList()) {
                action.accept(element);
            }
        }

        @Override
        public void forEachOrdered(Consumer<? super T> action) {
            forEach(action);
        }

        @Override
        public Object[] toArray() {
            return toList().toArray();
        }

        @Override
        public <A> A[] toArray(IntFunction<A[]> generator) {
            return toList().toArray(generator.apply(0));
        }

        @Override
        public T reduce(T identity, BinaryOperator<T> accumulator) {
            T result = identity;
            for (T element : toList()) {
                result = accumulator.apply(result, element);
            }
            return result;
        }

        @Override
        public Optional<T> reduce(BinaryOperator<T> accumulator) {
            boolean foundAny = false;
            T result = null;
            for (T element : toList()) {
                if (!foundAny) {
                    foundAny = true;
                    result = element;
                } else {
                    result = accumulator.apply(result, element);
                }
            }
            return foundAny ? Optional.of(result) : Optional.empty();
        }

        @Override
        public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner) {
            return reduce(identity, accumulator);
        }

        public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator) {
            U result = identity;
            for (T element : toList()) {
                result = accumulator.apply(result, element);
            }
            return result;
        }

        @Override
        public <R> R collect(Supplier<R> supplier, BiConsumer<R, ? super T> accumulator, BiConsumer<R, R> combiner) {
            R acc = supplier.get();
            for (T element : toList()) {
                accumulator.accept(acc, element);
            }
            return acc;
        }

        @Override
        public <R, A> R collect(Collector<? super T, A, R> collector) {
            A acc = collector.supplier().get();
            BiConsumer<A, ? super T> accumulator = collector.accumulator();
            for (T element : toList()) {
                accumulator.accept(acc, element);
            }
            if (collector.characteristics().contains(Characteristics.IDENTITY_FINISH)) {
                return (R) acc;
            }
            return collector.finisher().apply(acc);
        }

        @Override
        public Optional<T> min(Comparator<? super T> comparator) {
            if (count() == 0) {
                return Optional.empty();
            }
            return Optional.of(Collections.min(toList(), comparator));
        }

        @Override
        public Optional<T> max(Comparator<? super T> comparator) {
            if (count() == 0) {
                return Optional.empty();
            }
            return Optional.of(Collections.max(toList(), comparator));
        }

        @Override
        public long count() {
            return toList().size();
        }

        @Override
        public boolean anyMatch(Predicate<? super T> predicate) {
            return IteratorUtil.anyMatch(iterator(), predicate);
        }

        @Override
        public boolean allMatch(Predicate<? super T> predicate) {
            return IteratorUtil.allMatch(iterator(), predicate);
        }

        @Override
        public boolean noneMatch(Predicate<? super T> predicate) {
            return IteratorUtil.noneMatch(iterator(), predicate);
        }

        @Override
        public Optional<T> findFirst() {
            return findAny();
        }

        @Override
        public Optional<T> findAny() {
            Collection<T> element = toList();
            if (element.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(element.iterator().next());
        }

        @Override
        public Iterator<T> iterator() {
            return toList().iterator();
        }

        @Override
        public Spliterator<T> spliterator() {
            return toList().spliterator();
        }

        @Override
        public Stream<T> unordered() {
            return this;
        }

        @Deprecated
        @Override
        public Stream<T> onClose(Runnable closeHandler) {
            throw new UnsupportedOperationException("onClose");
        }

        @Override
        public StrictStream<T> peek(Consumer<? super T> action) {
            // do nothing
            return this;
        }

        @Override
        public void close() {

        }

        @Override
        public boolean isParallel() {
            return false;
        }

        @Override
        public Stream<T> sequential() {
            return this;
        }

        @Override
        public Stream<T> parallel() {
            return lazy().parallel();
        }

        @Override
        public IntStream mapToInt(ToIntFunction<? super T> mapper) {
            return lazy().mapToInt(mapper);
        }

        @Override
        public LongStream mapToLong(ToLongFunction<? super T> mapper) {
            return lazy().mapToLong(mapper);
        }

        @Override
        public DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper) {
            return lazy().mapToDouble(mapper);
        }

        @Override
        public IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper) {
            return lazy().flatMapToInt(mapper);
        }

        @Override
        public LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper) {
            return lazy().flatMapToLong(mapper);
        }

        @Override
        public DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper) {
            return lazy().flatMapToDouble(mapper);
        }
    }
}
