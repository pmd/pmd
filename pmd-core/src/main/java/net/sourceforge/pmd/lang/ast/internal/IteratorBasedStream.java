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
abstract class IteratorBasedStream<R extends Node> implements NodeStream<R> {

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
        return new IteratorBasedStream<S>() {
            @Override
            public Iterator<S> iterator() {
                return IteratorUtil.flatMap(
                    IteratorBasedStream.this.iterator(),
                    mapper.<NodeStream<? extends S>>andThen(ns -> ns == null ? NodeStream.empty() : ns)
                        .andThen(NodeStream::iterator)
                );
            }
        };
    }

    @Override
    public NodeStream<R> filter(Predicate<? super R> predicate) {
        return new IteratorBasedStream<R>() {
            @Override
            public Iterator<R> iterator() {
                return IteratorUtil.mapNotNull(IteratorBasedStream.this.iterator(), Filtermap.filter(predicate));
            }
        };
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
                      : new IteratorBasedStream<R>() {
                          @Override
                          public Iterator<R> iterator() {
                              Iterator<R> iter = IteratorBasedStream.this.iterator();
                              IteratorUtil.advance(iter, n);
                              return iter;
                          }
                      };
    }

    @Override
    public NodeStream<R> take(int maxSize) {
        AssertionUtil.assertArgNonNegative(maxSize);
        return maxSize == 0 ? NodeStream.empty()
                            : new IteratorBasedStream<R>() {
                                @Override
                                public Iterator<R> iterator() {
                                    return IteratorUtil.take(IteratorBasedStream.this.iterator(), maxSize);
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
        return iter.hasNext() ? null : iter.next();
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
}
