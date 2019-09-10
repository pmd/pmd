/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.Collections;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.AncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.ChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.DescendantStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredAncestorOrSelfStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredChildrenStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.FilteredDescendantStream;
import net.sourceforge.pmd.lang.ast.internal.AxisStream.SlicedChildrenStream;

public final class StreamImpl {

    private static final NodeStream EMPTY = new IteratorBasedNStream() {
        @Override
        public Iterator iterator() {
            return Collections.emptyIterator();
        }
    };

    private StreamImpl() {
        // utility class
    }

    public static <T extends Node> NodeStream<T> singleton(@NonNull T node) {
        return new SingletonNodeStream<>(node);
    }

    public static <T extends Node> NodeStream<T> fromIterable(Iterable<T> iterable) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.filterNotNull(iterable.iterator());
            }

            @Override
            public Spliterator<T> spliterator() {
                Spliterator<T> spliter = iterable.spliterator();
                return Spliterators.spliterator(iterator(), spliter.estimateSize(),
                                                spliter.characteristics() & Spliterator.NONNULL & ~Spliterator.SIZED
                                                    & ~Spliterator.SUBSIZED);
            }
        };
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
    public static <T extends Node> NodeStream<T> empty() {
        return EMPTY;
    }

    public static <R extends Node> NodeStream<R> children(@NonNull Node node, Class<R> target) {
        return new FilteredChildrenStream<>(node, Filtermap.isInstance(target));
    }

    public static NodeStream<Node> children(@NonNull Node root) {
        return new ChildrenStream(root);
    }

    public static NodeStream<Node> descendants(@NonNull Node node) {
        return new DescendantStream(node);
    }

    public static <R extends Node> NodeStream<R> descendants(@NonNull Node node, Class<R> rClass) {
        return new FilteredDescendantStream<>(node, Filtermap.isInstance(rClass));
    }

    public static NodeStream<Node> descendantsOrSelf(@NonNull Node node) {
        return new DescendantOrSelfStream(node);
    }

    public static NodeStream<Node> followingSiblings(@NonNull Node node) {
        Node parent = node.jjtGetParent();
        return parent == null ? empty()
                              : new SlicedChildrenStream(parent, node.jjtGetChildIndex() + 1, parent.jjtGetNumChildren());
    }

    public static NodeStream<Node> precedingSiblings(@NonNull Node node) {
        Node parent = node.jjtGetParent();
        return parent == null ? empty()
                              : new SlicedChildrenStream(parent, 0, node.jjtGetChildIndex());
    }


    public static NodeStream<Node> ancestorsOrSelf(@Nullable Node node) {
        if (node == null) {
            return empty();
        } else if (node.jjtGetParent() == null) {
            return singleton(node);
        }
        return new AncestorOrSelfStream(node);
    }

    static <T extends Node> NodeStream<T> ancestorsOrSelf(@Nullable Node node, Filtermap<Node, T> target) {
        if (node == null) {
            return empty();
        } else if (node.jjtGetParent() == null) {
            T apply = target.apply(node);
            return apply != null ? singleton(apply) : empty();
        }
        return new FilteredAncestorOrSelfStream<>(node, target);
    }

    public static NodeStream<Node> ancestors(@NonNull Node node) {
        return ancestorsOrSelf(node.jjtGetParent());
    }

    static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Filtermap<Node, R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), target);
    }

    public static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Class<R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), Filtermap.isInstance(target));
    }


}
