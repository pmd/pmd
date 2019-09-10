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

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

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
final class SingletonNodeStream<T extends Node> implements NodeStream<T> {

    private final T node;

    SingletonNodeStream(@NonNull T node) {
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
    public T first() {
        return node;
    }

    @Override
    public T last() {
        return node;
    }

    @Override
    public boolean nonEmpty() {
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return toList().iterator();
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
        AssertionUtil.assertArgNonNegative(n);
        return n == 0 ? this : NodeStream.empty();
    }

    @Override
    public NodeStream<T> take(int maxSize) {
        AssertionUtil.assertArgNonNegative(maxSize);
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
    public int count() {
        return 1;
    }

    @Override
    public NodeStream<T> takeWhile(Predicate<? super T> predicate) {
        return filter(predicate);
    }

    @Override
    public <R extends Node> NodeStream<R> map(Function<? super T, ? extends R> mapper) {
        return NodeStream.of(mapper.apply(node));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends NodeStream<? extends R>> mapper) {
        return (NodeStream<R>) mapper.apply(node);
    }

    @Override
    public NodeStream<Node> children() {
        return StreamImpl.children(node);
    }

    @Override
    public <R extends Node> NodeStream<R> children(Class<R> rClass) {
        return StreamImpl.children(node, rClass);
    }

    @Override
    public NodeStream<Node> parents() {
        return NodeStream.of(node.jjtGetParent());
    }

    @Override
    public NodeStream<Node> ancestors() {
        return StreamImpl.ancestors(node);
    }

    @Override
    public <R extends Node> NodeStream<R> ancestors(Class<R> rClass) {
        return StreamImpl.ancestors(node, rClass);
    }

    @Override
    public NodeStream<Node> ancestorsOrSelf() {
        return StreamImpl.ancestorsOrSelf(node);
    }

    @Override
    public NodeStream<Node> descendants() {
        return StreamImpl.descendants(node);
    }

    @Override
    public <R extends Node> NodeStream<R> descendants(Class<R> rClass) {
        return StreamImpl.descendants(node, rClass);
    }

    @Override
    public NodeStream<Node> descendantsOrSelf() {
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
}
