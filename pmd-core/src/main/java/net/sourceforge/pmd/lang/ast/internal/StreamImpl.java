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

    public static NodeStream<Node> ancestorOrSelf(Node node) {
        return new AncestorOrSelfStream(node);
    }

    public static NodeStream<Node> ancestors(Node node) {
        return new AncestorStream(node);
    }

    public static <R extends Node> NodeStream<R> ancestors(Node node, Class<R> target) {
        return new FilteredAncestorStream<>(node, target);
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

        AxisStream(Node root, Class<R> target) {
            super();
            this.node = root;
            this.target = target;
        }
    }

    private static class FilteredAncestorStream<R extends Node> extends AxisStream<R> {

        private FilteredAncestorStream(Node node, Class<R> target) {
            super(node, target);
        }

        @Override
        public Iterator<R> iterator() {
            AncestorOrSelfIterator iter = new AncestorOrSelfIterator(node);
            iter.next(); // skip self
            return IteratorUtil.filterCast(iter, target);
        }

        @Override
        public NodeStream<R> drop(int n) {
            if (n == 0) {
                return this;
            }
            @Nullable R p = get(n - 1);
            return p != null ? copy(p) : NodeStream.empty();
        }

        @Override
        public @Nullable R first() {
            return TraversalUtils.getFirstParentOfType(target, node);
        }

        protected NodeStream<R> copy(Node start) {
            return StreamImpl.ancestors(start, target);
        }
    }

    private static class AncestorStream extends FilteredAncestorStream<Node> {

        private AncestorStream(Node node) {
            super(node, Node.class);
        }

        @Override
        public Iterator<Node> iterator() {
            AncestorOrSelfIterator iter = new AncestorOrSelfIterator(node);
            iter.next(); // skip self
            return iter;
        }

        @Override
        protected NodeStream<Node> copy(Node start) {
            return new AncestorStream(start);
        }

        @Override
        public boolean nonEmpty() {
            return node.jjtGetParent() != null;
        }
    }

    private static class AncestorOrSelfStream extends AncestorStream {

        private AncestorOrSelfStream(Node node) {
            super(node);
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
            return TraversalUtils.getFirstDescendantOfType(target, node);
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

    private static final class DescendantOrSelfStream extends DescendantStream {

        DescendantOrSelfStream(Node node) {
            super(node);
        }

        @Override
        public Iterator<Node> iterator() {
            return new DescendantOrSelfIterator(node);
        }

        @Override
        public boolean nonEmpty() {
            return true;
        }
    }

    private static class FilteredChildrenStream<R extends Node> extends AxisStream<R> {

        private final Class<R> target;

        FilteredChildrenStream(Node node, Class<R> target) {
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
            return TraversalUtils.getFirstChildOfType(target, node);
        }

        @Override
        public int count() {
            return TraversalUtils.countChildrenOfType(target, node);
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

    private static class ChildrenStream extends FilteredChildrenStream<Node> {

        ChildrenStream(Node root) {
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

        @Nullable
        @Override
        public Node first() {
            return node.jjtGetNumChildren() > 0 ? node.jjtGetChild(0) : null;
        }

        @Override
        public <R extends Node> @Nullable R first(Class<R> rClass) {
            return TraversalUtils.getFirstChildOfType(rClass, node);
        }

        @Override
        public boolean nonEmpty() {
            return node.jjtGetNumChildren() > 0;
        }

        @Override
        public int count() {
            return node.jjtGetNumChildren();
        }
    }

    private static class SlicedChildrenStream extends AxisStream<Node> {

        private final int low;
        private final int high;

        SlicedChildrenStream(Node root, int low, int high) {
            super(root, Node.class);
            this.low = low;
            this.high = high;
        }

        @Override
        public @NonNull Iterator<Node> iterator() {
            return count() > 0 ? TraversalUtils.childrenIterator(node, low, high)
                               : IteratorUtil.emptyIterator();
        }

        @Nullable
        @Override
        public Node first() {
            return low < high ? node.jjtGetChild(low) : null;
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
            return Math.max(high - low, 0);
        }
    }
}
