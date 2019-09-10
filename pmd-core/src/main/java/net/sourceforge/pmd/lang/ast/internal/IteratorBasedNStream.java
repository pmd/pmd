/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.Filtermap;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Implementations are based on the iterator rather than the stream.
 * Benchmarking shows that stream overhead is significant, and doesn't
 * decrease when the pipeline grows longer.
 */
abstract class IteratorBasedNStream<R extends Node> implements NodeStream<R> {

    @Override
    public abstract Iterator<R> iterator();

    @Override
    public Spliterator<R> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
    }

    @Override
    public Stream<R> toStream() {
        return StreamSupport.stream(spliterator(), false);
    }

    @Override
    public <S extends Node> NodeStream<S> flatMap(Function<? super R, ? extends NodeStream<? extends S>> mapper) {
        return mapIter(iter -> IteratorUtil.flatMap(
            iter,
            mapper.<NodeStream<? extends S>>andThen(ns -> ns == null ? NodeStream.empty() : ns)
                .andThen(NodeStream::iterator)
        ));
    }

    @Override
    public <R1 extends Node> NodeStream<R1> map(Function<? super R, ? extends R1> mapper) {
        return mapIter(iter -> IteratorUtil.mapNotNull(iter, mapper));
    }

    @Override
    public NodeStream<R> filter(Predicate<? super R> predicate) {
        return mapIter(it -> IteratorUtil.mapNotNull(it, Filtermap.filter(predicate)));
    }

    @Override
    public void forEach(Consumer<? super R> action) {
        iterator().forEachRemaining(action);
    }

    @Override
    public @Nullable R get(int n) {
        return IteratorUtil.getNth(iterator(), n);
    }

    @Override
    public NodeStream<R> drop(int n) {
        AssertionUtil.assertArgNonNegative(n);
        return n == 0 ? this
                      : mapIter(iter -> {
                          IteratorUtil.advance(iter, n);
                          return iter;
                      });
    }

    @Override
    public NodeStream<R> take(int maxSize) {
        AssertionUtil.assertArgNonNegative(maxSize);
        return maxSize == 0 ? NodeStream.empty()
                            : mapIter(iter -> IteratorUtil.take(iter, maxSize));
    }

    @Override
    public NodeStream<R> takeWhile(Predicate<? super R> predicate) {
        return mapIter(iter -> IteratorUtil.takeWhile(iter, predicate));
    }

    @Override
    public NodeStream<R> distinct() {
        return mapIter(IteratorUtil::distinct);
    }

    @Override
    public NodeStream<R> peek(Consumer<? super R> action) {
        return mapIter(iter -> IteratorUtil.peek(iter, action));
    }

    @Override
    public NodeStream<R> append(NodeStream<? extends R> right) {
        return mapIter(iter -> IteratorUtil.concat(iter, right.iterator()));
    }

    @Override
    public NodeStream<R> prepend(NodeStream<? extends R> right) {
        return mapIter(iter -> IteratorUtil.concat(right.iterator(), iter));
    }

    @Override
    public NodeStream<R> cached() {
        return new IteratorBasedNStream<R>() {

            private List<R> cache;

            @Override
            public Iterator<R> iterator() {
                return toList().iterator();
            }

            @Override
            public int count() {
                return toList().size();
            }

            @Override
            public List<R> toList() {
                if (cache == null) {
                    cache = IteratorBasedNStream.this.toList();
                }
                return cache;
            }
        };
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
    public @Nullable R first() {
        Iterator<R> iter = iterator();
        return iter.hasNext() ? iter.next() : null;
    }

    @Override
    public List<R> toList() {
        return IteratorUtil.toList(iterator());
    }

    @Override
    public <S extends Node> @Nullable S first(Class<S> r1Class) {
        for (R r : this) {
            if (r1Class.isInstance(r)) {
                return r1Class.cast(r);
            }
        }
        return null;
    }

    @Override
    public @Nullable R first(Predicate<? super R> predicate) {
        for (R r : this) {
            if (predicate.test(r)) {
                return r;
            }
        }
        return null;
    }

    private <S extends Node> IteratorMapping<S> mapIter(Function<Iterator<R>, Iterator<S>> fun) {
        return new IteratorMapping<S>(fun);
    }

    private class IteratorMapping<S extends Node> extends IteratorBasedNStream<S> {

        private final Function<Iterator<R>, Iterator<S>> fun;


        private IteratorMapping(Function<Iterator<R>, Iterator<S>> fun) {
            this.fun = fun;
        }


        @Override
        public Iterator<S> iterator() {
            return fun.apply(IteratorBasedNStream.this.iterator());
        }
    }
}
