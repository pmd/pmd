/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Optimised node stream implementation for a single element. Streams
 * returned by eg {@link #descendants()} have optimised implementations
 * for several common operations which most of the time don't need to
 * iterate a stream directly. Their performance is equivalent to pre
 * 7.0.0 traversal operations defined on the {@link Node} interface.
 * When they don't have an optimised implementation, they fall back on
 * stream processing.
 *
 * <p>This ensures that short pipelines like {@code node.descendants().first()}
 * are as efficient as the pre 7.0.0 methods.
 */
final class SingletonNodeStream<T extends Node> extends IteratorBasedNStream<T> implements DescendantNodeStream<T> {

    private final T node;

    SingletonNodeStream(@NonNull T node) {
        assert node != null : "null node!";
        this.node = node;
    }

    @Override
    public Stream<T> toStream() {
        return Stream.of(node);
    }

    @Override
    public List<T> toList() {
        return Collections.singletonList(node);
    }

    @Override
    public <R> List<R> toList(Function<? super T, ? extends R> mapper) {
        return Collections.singletonList(mapper.apply(node));
    }

    @Override
    public int count() {
        return 1;
    }

    @Override
    public T first() {
        return node;
    }

    @Override
    public T last() {
        return node;
    }

    @Override
    public boolean nonEmpty() {
        return true;
    }

    @Override
    public Iterator<T> iterator() {
        return IteratorUtil.singletonIterator(node);
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        action.accept(node);
    }

    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return predicate.test(node) ? this : NodeStream.empty();
    }

    @Override
    public NodeStream<T> drop(int n) {
        AssertionUtil.requireNonNegative("n", n);
        return n == 0 ? this : NodeStream.empty();
    }

    @Override
    public NodeStream<T> take(int maxSize) {
        AssertionUtil.requireNonNegative("maxSize", maxSize);
        return maxSize >= 1 ? this : NodeStream.empty();
    }

    @Override
    public NodeStream<T> cached() {
        return this;
    }

    @Override
    public NodeStream<T> distinct() {
        return this;
    }


    @Override
    public NodeStream<T> takeWhile(Predicate<? super T> predicate) {
        return filter(predicate);
    }

    @Override
    public <R extends Node> NodeStream<@NonNull R> map(Function<? super T, ? extends @Nullable R> mapper) {
        return NodeStream.of(mapper.apply(node));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return (NodeStream<R>) mapper.apply(node);
    }

    @Override
    public boolean any(Predicate<? super T> predicate) {
        return predicate.test(node);
    }

    @Override
    public boolean all(Predicate<? super T> predicate) {
        return predicate.test(node);
    }

    @Override
    public boolean none(Predicate<? super T> predicate) {
        return !predicate.test(node);
    }

    /*
        tree navigation
     */

    @Override
    public NodeStream<Node> children() {
        return StreamImpl.children(node);
    }

    @Override
    public <R extends Node> NodeStream<R> children(Class<? extends R> rClass) {
        return StreamImpl.children(node, rClass);
    }

    @Override
    public <R extends Node> NodeStream<R> firstChild(Class<? extends R> rClass) {
        return NodeStream.of(node.firstChild(rClass));
    }

    @Override
    public NodeStream<Node> parents() {
        return NodeStream.of(node.getParent());
    }

    @Override
    public NodeStream<Node> ancestors() {
        return StreamImpl.ancestors(node);
    }

    @Override
    public <R extends Node> NodeStream<R> ancestors(Class<? extends R> rClass) {
        return StreamImpl.ancestors(node, rClass);
    }

    @Override
    public NodeStream<Node> ancestorsOrSelf() {
        return StreamImpl.ancestorsOrSelf(node);
    }

    @Override
    public DescendantNodeStream<Node> descendants() {
        return StreamImpl.descendants(node);
    }

    @Override
    public <R extends Node> DescendantNodeStream<R> descendants(Class<? extends R> rClass) {
        return StreamImpl.descendants(node, rClass);
    }

    @Override
    public DescendantNodeStream<Node> descendantsOrSelf() {
        return StreamImpl.descendantsOrSelf(node);
    }

    @Override
    public NodeStream<Node> followingSiblings() {
        return StreamImpl.followingSiblings(node);
    }

    @Override
    public NodeStream<Node> precedingSiblings() {
        return StreamImpl.precedingSiblings(node);
    }

    @Override
    public DescendantNodeStream<T> crossFindBoundaries(boolean cross) {
        return this; // doesn't mean anything
    }
}
