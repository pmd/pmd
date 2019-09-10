/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.Filtermap;
import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

public final class StreamImpl {

    private StreamImpl() {
        // utility class
    }

    public static <T extends Node> NodeStream<T> singleton(T node) {
        return new SingletonNodeStream<>(node);
    }

    public static <T extends Node> NodeStream<T> fromIterable(Iterable<T> iterable) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.mapNotNull(iterable.iterator(), Function.identity());
            }

            @Override
            public Spliterator<T> spliterator() {
                Spliterator<T> spliter = iterable.spliterator();
                return Spliterators.spliterator(iterator(), spliter.estimateSize(),
                                                spliter.characteristics() & ~Spliterator.SIZED & ~Spliterator.SUBSIZED);
            }
        };
    }

    @SafeVarargs
    public static <T extends Node> NodeStream<T> union(NodeStream<? extends T>... streams) {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return IteratorUtil.flatMap(Arrays.asList(streams).iterator(), NodeStream::iterator);
            }
        };
    }


    public static <T extends Node> NodeStream<T> empty() {
        return new IteratorBasedNStream<T>() {
            @Override
            public Iterator<T> iterator() {
                return Collections.emptyIterator();
            }
        };
    }

    public static <R extends Node> NodeStream<R> children(Node node, Class<R> target) {
        return new FilteredChildrenStream<>(node, Filtermap.isInstance(target));
    }

    public static NodeStream<Node> children(Node root) {
        return new FilteredChildrenStream<>(root, Filtermap.NODE_IDENTITY);
    }

    public static NodeStream<Node> descendants(Node node) {
        return new DescendantStream(node);
    }

    public static <R extends Node> NodeStream<R> descendants(Node node, Class<R> rClass) {
        return new FilteredDescendantStream<>(node, Filtermap.isInstance(rClass));
    }

    public static NodeStream<Node> descendantsOrSelf(Node node) {
        return new DescendantOrSelfStream(node);
    }

    public static NodeStream<Node> followingSiblings(Node node) {
        Node parent = node.jjtGetParent();
        return parent == null ? empty()
                              : new SlicedChildrenStream(parent, node.jjtGetChildIndex() + 1, parent.jjtGetNumChildren());
    }

    public static NodeStream<Node> precedingSiblings(Node node) {
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

    private static <R extends Node> NodeStream<R> ancestorsOrSelf(@Nullable Node node, Filtermap<Node, R> target) {
        if (node == null) {
            return empty();
        } else if (node.jjtGetParent() == null) {
            R apply = target.apply(node);
            return apply != null ? singleton(apply) : empty();
        }
        return new FilteredAncestorOrSelfStream<>(node, target);
    }

    public static NodeStream<Node> ancestors(@NonNull Node node) {
        return ancestorsOrSelf(node.jjtGetParent());
    }

    private static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Filtermap<Node, R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), target);
    }

    public static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Class<R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), Filtermap.isInstance(target));
    }


    private abstract static class AxisStream<R extends Node> extends IteratorBasedNStream<R> {

        protected final Node node;
        protected final Filtermap<Node, R> target;

        AxisStream(@NonNull Node root, Filtermap<Node, R> target) {
            super();
            this.node = root;
            this.target = target;
        }

        @Override
        public final Iterator<R> iterator() {
            return target.filterMap(baseIterator());
        }

        protected abstract Iterator<Node> baseIterator();


        @Override
        public NodeStream<R> filter(Predicate<? super R> predicate) {
            return copyWithFilter(target.then(Filtermap.filter(predicate)));
        }

        @Override
        public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
            return copyWithFilter(target.thenCast(r1Class));
        }

        protected abstract <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap);

    }

    private static class FilteredAncestorOrSelfStream<R extends Node> extends AxisStream<R> {

        private FilteredAncestorOrSelfStream(@NonNull Node node, Filtermap<Node, R> target) {
            super(node, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return new AncestorOrSelfIterator(node);
        }

        @Override
        public NodeStream<R> drop(int n) {
            AssertionUtil.assertArgNonNegative(n);
            switch (n) {
            case 0:
                return this;
            case 1:
                return StreamImpl.ancestors(node, target);
            default:
                // eg for NodeStream.of(a,b,c).drop(2)
                Node nth = get(n); // get(2) == c
                return nth == null ? NodeStream.empty() : copy(nth); // c.ancestorsOrSelf() == [c]
            }
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return new FilteredAncestorOrSelfStream<>(node, filterMap);
        }

        @Override
        public @Nullable R first() {
            return TraversalUtils.getFirstParentOrSelfMatching(node, target);
        }

        protected NodeStream<R> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start, target);
        }
    }


    private static class AncestorOrSelfStream extends FilteredAncestorOrSelfStream<Node> {

        private AncestorOrSelfStream(@NonNull Node node) {
            super(node, Filtermap.NODE_IDENTITY);
        }

        @Nullable
        @Override
        public Node first() {
            return node;
        }

        @Override
        public boolean nonEmpty() {
            return true;
        }

        @Override
        public @Nullable Node last() {
            Node last = node;
            while (last.jjtGetParent() != null) {
                last = last.jjtGetParent();
            }
            return last;
        }

        @Override
        protected NodeStream<Node> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start);
        }
    }

    private static class FilteredDescendantStream<R extends Node> extends AxisStream<R> {

        FilteredDescendantStream(Node node, Filtermap<Node, R> target) {
            super(node, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            DescendantOrSelfIterator iter = new DescendantOrSelfIterator(node);
            iter.next(); // skip self
            return iter;
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return new FilteredDescendantStream<>(node, filterMap);
        }

        @Override
        public @Nullable R first() {
            return TraversalUtils.getFirstDescendantOfType(node, target);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstDescendantOfType(node, target) != null;
        }

        @Override
        public List<R> toList() {
            List<R> result = new ArrayList<>();
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    private static class DescendantStream extends FilteredDescendantStream<Node> {

        DescendantStream(Node node) {
            super(node, Filtermap.NODE_IDENTITY);
        }

        @Override
        public boolean nonEmpty() {
            return node.jjtGetNumChildren() > 0;
        }
    }

    private static class FilteredDescendantOrSelfStream<R extends Node> extends AxisStream<R> {

        FilteredDescendantOrSelfStream(Node node, Filtermap<Node, R> filtermap) {
            super(node, filtermap);
        }

        @Override
        public Iterator<Node> baseIterator() {
            return new DescendantOrSelfIterator(node);
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return new FilteredDescendantStream<>(node, filterMap);
        }

        @Override
        public List<R> toList() {
            List<R> result = new ArrayList<>();
            R top = target.apply(node);
            if (top != null) {
                result.add(top);
            }
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    private static final class DescendantOrSelfStream extends FilteredDescendantOrSelfStream<Node> {

        DescendantOrSelfStream(Node node) {
            super(node, Filtermap.NODE_IDENTITY);
        }

        @Nullable
        @Override
        public Node first() {
            return node;
        }

        @Override
        public boolean nonEmpty() {
            return true;
        }
    }

    private static class FilteredChildrenStream<R extends Node> extends AxisStream<R> {

        FilteredChildrenStream(@NonNull Node node, Filtermap<Node, R> target) {
            super(node, target);
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return new FilteredChildrenStream<>(node, filterMap);
        }

        @Override
        public Spliterator<R> spliterator() {
            return Spliterators.spliterator(iterator(), count(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return TraversalUtils.childrenIterator(node);
        }

        @Override
        public @Nullable R first() {
            return TraversalUtils.getFirstChildMatching(node, target);
        }

        @Override
        public @Nullable R last() {
            return TraversalUtils.getLastChildMatching(node, target);
        }


        @Override
        public <R extends Node> @Nullable R first(Class<R> rClass) {
            return TraversalUtils.getFirstChildMatching(node, target.thenCast(rClass));
        }

        @Override
        public <R extends Node> @Nullable R last(Class<R> rClass) {
            return TraversalUtils.getLastChildMatching(node, target.thenCast(rClass));
        }

        @Override
        public int count() {
            return TraversalUtils.countChildrenMatching(node, target);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstChildMatching(node, target) != null;
        }

        @Override
        public List<R> toList() {
            return TraversalUtils.findChildrenMatching(node, target);
        }
    }

    /** Implements following/preceding sibling streams. */
    private static class SlicedChildrenStream extends IteratorBasedNStream<Node> {

        private final Node node;
        private final int low; // inclusive
        private final int high; // exclusive

        SlicedChildrenStream(@NonNull Node root, int low, int high) {
            this.node = root;
            this.low = low;
            this.high = high;
        }


        @Override
        public Spliterator<Node> spliterator() {
            return Spliterators.spliterator(iterator(), count(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        @Override
        public Iterator<Node> iterator() {
            return count() > 0 ? TraversalUtils.childrenIterator(node, low, high)
                               : IteratorUtil.emptyIterator();
        }

        @Nullable
        @Override
        public Node first() {
            return low < high && low >= 0 ? node.jjtGetChild(low) : null;
        }

        @Nullable
        @Override
        public Node last() {
            return low < high && high <= node.jjtGetNumChildren() ? node.jjtGetChild(high - 1) : null;
        }

        @Override
        public NodeStream<Node> take(int maxSize) {
            return new SlicedChildrenStream(node, low, high - maxSize);
        }

        @Override
        public NodeStream<Node> drop(int n) {
            return new SlicedChildrenStream(node, low + n, high);
        }

        @Override
        public boolean nonEmpty() {
            return count() > 0;
        }

        @Override
        public int count() {
            return Math.min(Math.max(high - low, 0), node.jjtGetNumChildren());
        }
    }
}
