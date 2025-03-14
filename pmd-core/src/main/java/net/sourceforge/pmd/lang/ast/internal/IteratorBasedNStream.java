/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Implementations are based on the iterator rather than the stream.
 * Benchmarking shows that stream overhead is significant, and doesn't
 * decrease when the pipeline grows longer.
 */
abstract class IteratorBasedNStream<T extends Node> implements NodeStream<T> {

    @Override
    public abstract Iterator<T> iterator();

    @Override
    public Spliterator<T> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
    }

    @Override
    public Stream<T> toStream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends @Nullable NodeStream<? extends R>> mapper) {
        // Note temporary function is complete typing is needed so that it compiles with ejc
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=561482
        Function<? super T, Iterator<? extends R>> mapped = mapper.andThen(IteratorBasedNStream::safeMap);
        return mapIter(iter -> IteratorUtil.flatMap(iter, mapped));
    }

    private static <R extends Node> @NonNull Iterator<? extends R> safeMap(@Nullable NodeStream<? extends R> ns) {
        return ns == null ? Collections.emptyIterator() : ns.iterator();
    }

    @Override
    public <R extends Node> NodeStream<@NonNull R> map(Function<? super T, ? extends @Nullable R> mapper) {
        return mapIter(iter -> IteratorUtil.mapNotNull(iter, mapper));
    }

    @Override
    public NodeStream<T> filter(Predicate<? super @NonNull T> predicate) {
        return mapIter(it -> IteratorUtil.mapNotNull(it, Filtermap.filter(predicate)));
    }

    @Override
    public <R extends Node> NodeStream<R> filterIs(Class<? extends R> rClass) {
        return mapIter(it -> IteratorUtil.mapNotNull(it, Filtermap.isInstance(rClass)));
    }

    @Override
    public DescendantNodeStream<Node> descendants() {
        return flatMapDescendants(Node::descendants);
    }

    @Override
    public DescendantNodeStream<Node> descendantsOrSelf() {
        return flatMapDescendants(Node::descendantsOrSelf);
    }

    @Override
    public <R extends Node> DescendantNodeStream<R> descendants(Class<? extends R> rClass) {
        return flatMapDescendants(node -> node.descendants(rClass));
    }


    @NonNull
    protected <R extends Node> DescendantNodeStream<R> flatMapDescendants(Function<T, DescendantNodeStream<? extends R>> mapper) {
        return new DescendantMapping<>(this, mapper);
    }


    @Override
    public void forEach(Consumer<? super T> action) {
        iterator().forEachRemaining(action);
    }

    @Override
    public @Nullable T get(int n) {
        if (n == 0) {
            return first();
        }
        return IteratorUtil.getNth(iterator(), n);
    }

    @Override
    public NodeStream<T> drop(int n) {
        AssertionUtil.requireNonNegative("n", n);
        return n == 0 ? this : mapIter(iter -> IteratorUtil.drop(iter, n));
    }

    @Override
    public NodeStream<T> take(int maxSize) {
        AssertionUtil.requireNonNegative("maxSize", maxSize);
        return maxSize == 0 ? NodeStream.empty() : mapIter(iter -> IteratorUtil.take(iter, maxSize));
    }

    @Override
    public NodeStream<T> dropLast(int n) {
        AssertionUtil.requireNonNegative("n", n);
        return n == 0 ? this : mapIter(iter -> IteratorUtil.dropLast(iter, n));
    }

    @Override
    public NodeStream<T> takeWhile(Predicate<? super T> predicate) {
        return mapIter(iter -> IteratorUtil.takeWhile(iter, predicate));
    }

    @Override
    public final <R, A> R collect(Collector<? super T, A, R> collector) {
        A container = collector.supplier().get();
        BiConsumer<A, ? super T> accumulator = collector.accumulator();
        forEach(u -> accumulator.accept(container, u));
        return CollectionUtil.finish(collector, container);
    }

    @Override
    public NodeStream<T> distinct() {
        return mapIter(IteratorUtil::distinct);
    }

    @Override
    public NodeStream<T> peek(Consumer<? super T> action) {
        return mapIter(iter -> IteratorUtil.peek(iter, action));
    }

    @Override
    public NodeStream<T> append(NodeStream<? extends T> right) {
        return mapIter(iter -> IteratorUtil.concat(iter, right.iterator()));
    }

    @Override
    public NodeStream<T> prepend(NodeStream<? extends T> right) {
        return mapIter(iter -> IteratorUtil.concat(right.iterator(), iter));
    }

    @Override
    public boolean any(Predicate<? super T> predicate) {
        return IteratorUtil.anyMatch(iterator(), predicate);
    }

    @Override
    public boolean none(Predicate<? super T> predicate) {
        return IteratorUtil.noneMatch(iterator(), predicate);
    }

    @Override
    public boolean all(Predicate<? super T> predicate) {
        return IteratorUtil.allMatch(iterator(), predicate);
    }


    @Override
    public int count() {
        return IteratorUtil.count(iterator());
    }

    @Override
    public boolean nonEmpty() {
        return iterator().hasNext();
    }

    @Override
    public @Nullable T first() {
        Iterator<T> iter = iterator();
        return iter.hasNext() ? iter.next() : null;
    }

    @Override
    public @Nullable T last() {
        return IteratorUtil.last(iterator());
    }

    @Override
    public List<T> toList() {
        return IteratorUtil.toList(iterator());
    }

    @Override
    public <R extends Node> @Nullable R first(Class<? extends R> r1Class) {
        for (T t : this) {
            if (r1Class.isInstance(t)) {
                return r1Class.cast(t);
            }
        }
        return null;
    }

    @Override
    public @Nullable T first(Predicate<? super T> predicate) {
        for (T t : this) {
            if (predicate.test(t)) {
                return t;
            }
        }
        return null;
    }

    @Override
    public NodeStream<T> cached() {
        return StreamImpl.fromNonNullList(toList());
    }

    protected <R extends Node> NodeStream<R> mapIter(Function<Iterator<T>, Iterator<R>> fun) {
        return new IteratorMapping<>(fun);
    }


    @Override
    public String toString() {
        return getClass().getSimpleName() + " ["
            + toStream().map(Objects::toString).collect(Collectors.joining(", "))
            + "]";
    }

    private final class IteratorMapping<S extends Node> extends IteratorBasedNStream<S> {

        private final Function<Iterator<T>, Iterator<S>> fun;


        private IteratorMapping(Function<Iterator<T>, Iterator<S>> fun) {
            this.fun = fun;
        }


        @Override
        public Iterator<S> iterator() {
            return fun.apply(IteratorBasedNStream.this.iterator());
        }
    }


    private static class DescendantMapping<T extends Node, S extends Node> extends IteratorBasedNStream<S> implements DescendantNodeStream<S> {

        private final Function<? super T, ? extends DescendantNodeStream<? extends S>> fun;
        private final TreeWalker walker;
        private final IteratorBasedNStream<T> upstream;


        private DescendantMapping(IteratorBasedNStream<T> upstream, Function<? super T, ? extends DescendantNodeStream<? extends S>> fun, TreeWalker walker) {
            this.fun = fun;
            this.walker = walker;
            this.upstream = upstream;
        }

        DescendantMapping(IteratorBasedNStream<T> upstream, Function<? super T, ? extends DescendantNodeStream<? extends S>> fun) {
            this(upstream, fun, TreeWalker.DEFAULT);
        }

        @Override
        public Iterator<S> iterator() {
            return IteratorUtil.flatMap(
                upstream.iterator(),
                t -> {
                    DescendantNodeStream<? extends S> app = fun.apply(t);
                    return walker.apply(app).iterator();
                });
        }

        @Override
        public DescendantNodeStream<S> crossFindBoundaries(boolean cross) {
            return walker.isCrossFindBoundaries() == cross
                   ? this
                   : new DescendantMapping<>(upstream, fun, walker.crossFindBoundaries(cross));
        }
    }
}
