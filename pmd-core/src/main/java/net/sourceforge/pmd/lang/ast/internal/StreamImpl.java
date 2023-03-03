/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.AncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.ChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredAncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredDescendantStream;
import net.sourceforge.pmd.lang.ast.internal.GreedyNStream.GreedyKnownNStream;
import net.sourceforge.pmd.util.IteratorUtil;

public final class StreamImpl {

    @SuppressWarnings({"rawtypes", "PMD.UseDiamondOperator"})
    private static final DescendantNodeStream EMPTY = new EmptyNodeStream();

    private StreamImpl() {
        // utility class
    }

    public static <T extends Node> DescendantNodeStream<T> singleton(@NonNull T node) {
        return new SingletonNodeStream<>(node);
    }

    public static <T extends Node> NodeStream<T> fromIterable(Iterable<? extends @Nullable T> iterable) {
        if (iterable instanceof Collection) {
            Collection<? extends @Nullable T> coll = (Collection<T>) iterable;
            if (coll.isEmpty()) {
                return empty();
            } else if (coll.size() == 1) {
                return NodeStream.of(coll.iterator().next());
            }
        }

        return fromNonNullList(IteratorUtil.toNonNullList(iterable.iterator()));
    }

    public static <T extends Node> NodeStream<T> union(Iterable<? extends @Nullable NodeStream<? extends T>> streams) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.flatMap(streams.iterator(), NodeStream::iterator);
            }
        };
    }


    @SuppressWarnings("unchecked")
    public static <T extends Node> DescendantNodeStream<T> empty() {
        return EMPTY;
    }

    public static <R extends Node> NodeStream<R> children(@NonNull Node node, Class<? extends R> target) {
        return sliceChildren(node, Filtermap.isInstance(target), 0, node.getNumChildren());
    }

    public static NodeStream<Node> children(@NonNull Node node) {
        return sliceChildren(node, Filtermap.NODE_IDENTITY, 0, node.getNumChildren());
    }

    public static DescendantNodeStream<Node> descendants(@NonNull Node node) {
        return node.getNumChildren() == 0 ? empty() : new DescendantStream(node, TreeWalker.DEFAULT);
    }

    public static <R extends Node> DescendantNodeStream<R> descendants(@NonNull Node node, Class<? extends R> rClass) {
        return node.getNumChildren() == 0 ? empty()
                                          : new FilteredDescendantStream<>(node, TreeWalker.DEFAULT, Filtermap.isInstance(rClass));
    }

    public static DescendantNodeStream<Node> descendantsOrSelf(@NonNull Node node) {
        return node.getNumChildren() == 0 ? singleton(node) : new DescendantOrSelfStream(node, TreeWalker.DEFAULT);
    }

    public static NodeStream<Node> followingSiblings(@NonNull Node node) {
        Node parent = node.getParent();
        if (parent == null || parent.getNumChildren() == 1) {
            return NodeStream.empty();
        }
        return sliceChildren(parent, Filtermap.NODE_IDENTITY,
                             node.getIndexInParent() + 1,
                             parent.getNumChildren() - node.getIndexInParent() - 1
        );
    }

    public static NodeStream<Node> precedingSiblings(@NonNull Node node) {
        Node parent = node.getParent();
        if (parent == null || parent.getNumChildren() == 1) {
            return NodeStream.empty();
        }
        return sliceChildren(parent, Filtermap.NODE_IDENTITY, 0, node.getIndexInParent());
    }

    static <T extends Node> NodeStream<T> sliceChildren(Node parent, Filtermap<Node, ? extends T> filtermap, int from, int length) {
        // these assertions are just for tests
        assert parent != null;
        assert from >= 0 && from <= parent.getNumChildren() : "from should be a valid index";
        assert length >= 0 : "length should not be negative";
        assert from + length >= 0 && from + length <= parent.getNumChildren() : "from+length should be a valid index";

        if (length == 0) {
            return empty();
        } else if (filtermap == Filtermap.NODE_IDENTITY) { // NOPMD CompareObjectsWithEquals
            @SuppressWarnings("unchecked")
            NodeStream<T> res = length == 1 ? (NodeStream<T>) singleton(parent.getChild(from))
                                           : (NodeStream<T>) new ChildrenStream(parent, from, length);
            return res;
        } else {
            if (length == 1) {
                // eager evaluation, empty or singleton
                return NodeStream.of(filtermap.apply(parent.getChild(from)));
            } else {
                return new FilteredChildrenStream<>(parent, filtermap, from, length);
            }
        }
    }


    public static NodeStream<Node> ancestorsOrSelf(@Nullable Node node) {
        return ancestorsOrSelf(node, Filtermap.NODE_IDENTITY);
    }

    static <T extends Node> NodeStream<T> ancestorsOrSelf(@Nullable Node node, Filtermap<Node, ? extends T> target) {
        if (node == null) {
            return empty();
        }

        if (target == Filtermap.NODE_IDENTITY) { // NOPMD CompareObjectsWithEquals
            return (NodeStream<T>) new AncestorOrSelfStream(node);
        }

        T first = TraversalUtils.getFirstParentOrSelfMatching(node, target);
        if (first == null) {
            return empty();
        }

        return new FilteredAncestorOrSelfStream<>(first, target);
    }

    public static NodeStream<Node> ancestors(@NonNull Node node) {
        return ancestorsOrSelf(node.getParent());
    }

    static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Filtermap<Node, ? extends R> target) {
        return ancestorsOrSelf(node.getParent(), target);
    }

    public static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Class<? extends R> target) {
        return ancestorsOrSelf(node.getParent(), Filtermap.isInstance(target));
    }

    static <T extends Node> NodeStream<T> fromNonNullList(List<@NonNull T> coll) {
        if (coll.isEmpty()) {
            return empty();
        } else if (coll.size() == 1) {
            return singleton(coll.get(0));
        }

        return new GreedyKnownNStream<>(coll);
    }


    private static final class EmptyNodeStream<N extends Node> extends IteratorBasedNStream<N> implements DescendantNodeStream<N> {

        @Override
        protected <R extends Node> NodeStream<R> mapIter(Function<Iterator<N>, Iterator<R>> fun) {
            return StreamImpl.empty();
        }

        @Override
        protected @NonNull <R extends Node> DescendantNodeStream<R> flatMapDescendants(Function<N, DescendantNodeStream<? extends R>> mapper) {
            return StreamImpl.empty();
        }

        @Override
        public DescendantNodeStream<N> crossFindBoundaries(boolean cross) {
            return this;
        }

        @Override
        public Iterator<N> iterator() {
            return Collections.emptyIterator();
        }

        @Override
        public List<N> toList() {
            return Collections.emptyList();
        }

        @Override
        public <R> List<R> toList(Function<? super N, ? extends R> mapper) {
            return Collections.emptyList();
        }

        @Override
        public Spliterator<N> spliterator() {
            return Spliterators.emptySpliterator();
        }

        @Override
        public NodeStream<N> cached() {
            return this;
        }

        @Override
        public String toString() {
            return "EmptyStream";
        }
    }

}
