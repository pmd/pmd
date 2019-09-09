/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

public final class SingletonNodeStream<T extends Node> implements NodeStream<T> {

    private final T node;

    public SingletonNodeStream(T node) {
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
    public NodeStream<Node> children() {
        return new ChildrenStream<>(node, Node.class);
    }

    @Override
    public <R extends Node> NodeStream<R> children(Class<R> rClass) {
        return new ChildrenStream<>(node, rClass);
    }

    @Override
    public NodeStream<Node> parents() {
        return NodeStream.of(node.jjtGetParent());
    }

    @Override
    public NodeStream<Node> descendants() {
        return new DescendantStream<>(node, Node.class);
    }

    @Override
    public <R extends Node> NodeStream<R> descendants(Class<R> rClass) {
        return new DescendantStream<>(node, rClass);
    }

    public static class AncestorStream<R extends Node> implements NodeStream<R> {

        private final Class<R> target;
        private final Node node;

        public AncestorStream(Node node, Class<R> target) {
            this.target = target;
            this.node = node;
        }

        @Override
        public Stream<R> toStream() {
            return NodeStream.fromIterable(() -> new AncestorOrSelfIterator(node)).drop(1).filterIs(target).toStream();
        }

        @Override
        public Optional<R> get(int n) {
            Node node = this.node;
            while (n >= 0 && node != null) {
                Node parent = node.jjtGetParent();
                if (target.isInstance(parent) && --n == 0) {
                    return Optional.of(target.cast(parent));
                }
                node = parent;
            }
            return Optional.empty();
        }

        @Override
        public NodeStream<R> drop(int n) {
            if (n == 0) {
                return this;
            }
            Optional<R> p = get(n - 1);
            return p.isPresent() ? new AncestorStream<>(p.get(), target)
                                 : NodeStream.empty();
        }

        @Override
        public Optional<R> first() {
            return Optional.ofNullable(TraversalUtils.getFirstParentOfType(target, node));
        }
    }

    public static class DescendantStream<R extends Node> implements NodeStream<R> {

        private final Class<R> target;
        private final Node node;

        public DescendantStream(Node node, Class<R> target) {
            this.target = target;
            this.node = node;
        }

        @Override
        public Stream<R> toStream() {
            return NodeStream.fromIterable(() -> new DescendantOrSelfIterator(node)).drop(1).filterIs(target).toStream();
        }

        @Override
        public Optional<R> first() {
            return Optional.ofNullable(TraversalUtils.getFirstDescendantOfType(target, node));
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstDescendantOfType(target, node) != null;
        }

        @Override
        public List<R> toList() {
            List<R> result = new ArrayList<>();
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    public static class ChildrenStream<R extends Node> implements NodeStream<R> {

        private final Class<R> target;
        private final Node node;

        public ChildrenStream(Node node, Class<R> target) {
            this.target = target;
            this.node = node;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Stream<R> toStream() {
            Spliterator<@NonNull Node> spliter = Spliterators.spliterator(
                new ChildrenIterator(node),
                node.jjtGetNumChildren(),
                Spliterator.SIZED | Spliterator.ORDERED
            );
            return (Stream<R>) StreamSupport.stream(spliter, false).filter(target::isInstance);
        }

        @Override
        public Optional<R> first() {
            return Optional.ofNullable(TraversalUtils.getFirstChildOfType(target, node));
        }

        @Override
        public <R1 extends Node> Optional<R1> first(Class<R1> r1Class) {
            if (target == Node.class) {
                return Optional.ofNullable(TraversalUtils.getFirstChildOfType(r1Class, node));
            }
            return NodeStream.super.first(r1Class);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstChildOfType(target, node) != null;
        }

        @Override
        public List<R> toList() {
            return TraversalUtils.findChildrenOfType(target, node);
        }
    }
}
