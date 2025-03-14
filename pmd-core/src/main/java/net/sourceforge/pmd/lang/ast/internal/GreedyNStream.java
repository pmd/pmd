/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.CollectionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * A greedy stream evaluates all axis operations, except for descendants,
 * greedily.
 */
abstract class GreedyNStream<T extends Node> extends IteratorBasedNStream<T> {

    @Override
    protected <R extends Node> NodeStream<R> mapIter(Function<Iterator<T>, Iterator<R>> fun) {
        return StreamImpl.fromNonNullList(IteratorUtil.toNonNullList(fun.apply(iterator())));
    }

    @Override
    public T first() {
        return toList().get(0);
    }

    @Override
    public @Nullable T get(int n) {
        AssertionUtil.requireNonNegative("n", n);
        List<T> tList = toList();
        return n < tList.size() ? tList.get(n) : null;
    }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
    }

    @Override
    public int count() {
        return toList().size();
    }

    @Override
    public NodeStream<T> drop(int n) {
        if (n == 0) {
            return this;
        }
        return StreamImpl.fromNonNullList(CollectionUtil.drop(toList(), n));
    }

    @Override
    public NodeStream<T> take(int maxSize) {
        if (maxSize >= count()) {
            return this;
        }
        return StreamImpl.fromNonNullList(CollectionUtil.take(toList(), maxSize));
    }

    @Override
    public abstract List<T> toList();

    @Override
    public Spliterator<T> spliterator() {
        Spliterator<T> spliter = toList().spliterator();
        return Spliterators.spliterator(iterator(), spliter.estimateSize(),
                                        spliter.characteristics() | Spliterator.NONNULL);
    }

    @Override
    public NodeStream<T> cached() {
        return this;
    }

    static class GreedyKnownNStream<T extends Node> extends GreedyNStream<T> {

        private final List<@NonNull T> coll;

        GreedyKnownNStream(List<@NonNull T> coll) {
            this.coll = coll;
        }

        @Override
        public List<T> toList() {
            return coll;
        }
    }
}
