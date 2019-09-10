/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
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

    public static <T extends Node> NodeStream<T> empty() {
        return Stream::empty;
    }

    public static <R extends Node> NodeStream<R> children(Node node, Class<R> target) {
        return new FilteredChildrenStream<>(node, target);
    }

    public static NodeStream<Node> children(Node root) {
        return new ChildrenStream(root);
    }

    public static NodeStream<Node> descendants(Node node) {
        return new DescendantStream(node);
    }

    public static <R extends Node> NodeStream<R> descendants(Node node, Class<R> rClass) {
        return new FilteredDescendantStream<>(node, rClass);
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

    public static <R extends Node> NodeStream<R> ancestorsOrSelf(@Nullable Node node, Class<R> target) {
        if (node == null) {
            return empty();
        } else if (node.jjtGetParent() == null) {
            return target.isInstance(node) ? singleton(target.cast(node)) : empty();
        }
        return new FilteredAncestorOrSelfStream<>(node, target);
    }

    public static NodeStream<Node> ancestors(@NonNull Node node) {
        return ancestorsOrSelf(node.jjtGetParent());
    }

    public static <R extends Node> NodeStream<R> ancestors(@NonNull Node node, Class<R> target) {
        return ancestorsOrSelf(node.jjtGetParent(), target);
    }


    /**
     * Implementations are based on the iterator rather than the stream.
     * For small pipelines this makes a difference, as the pipeline grows
     * longer, streams becomes more efficient. Any call to {@link NodeStream#flatMap(Function)}
     * produces a streaming implementation.
     */
    private abstract static class IteratorBasedStream<R extends Node> implements NodeStream<R> {

        @Override
        public Stream<R> toStream() {
            return StreamSupport.stream(spliterator(), false);
        }

        @Override
        public Spliterator<R> spliterator() {
            return Spliterators.spliteratorUnknownSize(iterator(), Spliterator.ORDERED);
        }

        @Override
        public void forEach(Consumer<? super R> action) {
            iterator().forEachRemaining(action);
        }

        @Override
        public abstract Iterator<R> iterator();


        @Override
        public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
            return new IteratorBasedStream<S>() {
                @Override
                public Iterator<S> iterator() {
                    return IteratorUtil.filterCast(IteratorBasedStream.this.iterator(), r1Class);
                }
            };
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

    private abstract static class AxisStream<R extends Node> extends IteratorBasedStream<R> {

        protected final Node node;
        protected final Class<R> target;

        AxisStream(@NonNull Node root, Class<R> target) {
            super();
            this.node = root;
            this.target = target;
        }
    }

    private static class FilteredAncestorOrSelfStream<R extends Node> extends AxisStream<R> {

        private FilteredAncestorOrSelfStream(@NonNull Node node, Class<R> target) {
            super(node, target);
        }

        @Override
        public Iterator<R> iterator() {
            return IteratorUtil.filterCast(new AncestorOrSelfIterator(node), target);
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
        public @Nullable R first() {
            return TraversalUtils.getFirstParentOrSelfOfType(node, target);
        }

        protected NodeStream<R> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start, target);
        }
    }


    private static class AncestorOrSelfStream extends FilteredAncestorOrSelfStream<Node> {

        private AncestorOrSelfStream(@NonNull Node node) {
            super(node, Node.class);
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
        public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
            return new FilteredAncestorOrSelfStream<>(node, r1Class);
        }

        @Override
        protected NodeStream<Node> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start);
        }

        @Override
        public Iterator<Node> iterator() {
            return new AncestorOrSelfIterator(node);
        }
    }

    private static class FilteredDescendantStream<R extends Node> extends AxisStream<R> {

        FilteredDescendantStream(Node node, Class<R> target) {
            super(node, target);
        }

        @Override
        public Iterator<R> iterator() {
            DescendantOrSelfIterator iter = new DescendantOrSelfIterator(node);
            iter.next(); // skip self
            return IteratorUtil.filterCast(iter, target);
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
            super(node, Node.class);
        }

        @Override
        public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
            // node.descendants().filterIs(r1Class) === node.descendants(r1Class)
            return new FilteredDescendantStream<>(node, r1Class);
        }

        @Override
        public Iterator<Node> iterator() {
            DescendantOrSelfIterator iter = new DescendantOrSelfIterator(node);
            iter.next(); // skip self
            return iter;
        }

        @Override
        public boolean nonEmpty() {
            return node.jjtGetNumChildren() > 0;
        }
    }

    private static final class DescendantOrSelfStream extends AxisStream<Node> {

        DescendantOrSelfStream(Node node) {
            super(node, Node.class);
        }

        @Override
        public Iterator<Node> iterator() {
            return new DescendantOrSelfIterator(node);
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
        public List<Node> toList() {
            List<Node> result = new ArrayList<>();
            result.add(node);
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    /** Implements the {@code children(class)} stream optimally. */
    private static class FilteredChildrenStream<R extends Node> extends AxisStream<R> {

        private final Class<R> target;

        FilteredChildrenStream(@NonNull Node node, Class<R> target) {
            super(node, target);
            this.target = target;
        }

        @Override
        public Spliterator<R> spliterator() {
            return Spliterators.spliterator(iterator(), count(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        @Override
        public Iterator<R> iterator() {
            return IteratorUtil.filterCast(TraversalUtils.childrenIterator(node), target);
        }

        @Override
        public @Nullable R first() {
            return TraversalUtils.getFirstChildOfType(node, target);
        }

        @Override
        public @Nullable R last() {
            return TraversalUtils.getLastChildOfType(node, target);
        }

        @Override
        public int count() {
            return TraversalUtils.countChildrenOfType(node, target);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstChildOfType(node, target) != null;
        }

        @Override
        public List<R> toList() {
            return TraversalUtils.findChildrenOfType(node, target);
        }
    }

    /** Implements the {@code children()} stream optimally. */
    private static class ChildrenStream extends FilteredChildrenStream<Node> {

        ChildrenStream(@NonNull Node root) {
            super(root, Node.class);
        }

        @Override
        public @NonNull Iterator<Node> iterator() {
            return TraversalUtils.childrenIterator(node);
        }

        @Override
        public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
            // node.children().filterIs(r1Class) === node.children(r1Class)
            return StreamImpl.children(node, r1Class);
        }

        @Override
        public <R extends Node> @Nullable R first(Class<R> rClass) {
            return TraversalUtils.getFirstChildOfType(node, rClass);
        }

        @Override
        public <R extends Node> @Nullable R last(Class<R> rClass) {
            return TraversalUtils.getLastChildOfType(node, rClass);
        }
    }

    /** Implements following/preceding sibling streams optimally. */
    private static class SlicedChildrenStream extends AxisStream<Node> {

        private final int low; // inclusive
        private final int high; // exclusive

        SlicedChildrenStream(@NonNull Node root, int low, int high) {
            super(root, Node.class);
            this.low = low;
            this.high = high;
        }


        @Override
        public Spliterator<Node> spliterator() {
            return Spliterators.spliterator(iterator(), count(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        @Override
        public @NonNull Iterator<Node> iterator() {
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
