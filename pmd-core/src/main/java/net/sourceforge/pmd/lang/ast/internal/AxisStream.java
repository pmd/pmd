/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.util.AssertionUtil;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * Stream that iterates over one axis of the tree.
 */
abstract class AxisStream<T extends Node> extends IteratorBasedNStream<T> {

    /** Spec of this field depends on the subclass. */
    protected final Node node;
    /** Filter, for no filter, this is {@link Filtermap#NODE_IDENTITY}. */
    protected final Filtermap<Node, ? extends T> filter;

    AxisStream(@NonNull Node root, Filtermap<Node, ? extends T> filter) {
        super();
        this.node = root;
        this.filter = filter;
    }

    @Override
    public final Iterator<T> iterator() {
        return Filtermap.applyIterator(baseIterator(), filter);
    }

    protected abstract Iterator<Node> baseIterator();


    @Override
    public <R extends Node> NodeStream<@NonNull R> map(Function<? super T, ? extends @Nullable R> mapper) {
        return copyWithFilter(filter.thenApply(mapper));
    }

    @Override
    public NodeStream<T> filter(Predicate<? super @NonNull T> predicate) {
        return copyWithFilter(filter.thenFilter(predicate));
    }

    @Override
    public <S extends Node> NodeStream<S> filterIs(Class<? extends S> r1Class) {
        return copyWithFilter(filter.thenCast(r1Class));
    }

    /*
     * Override one of these three to implement all the overloads of first/last/toList
     */

    protected <O extends Node> @Nullable O firstImpl(Filtermap<? super Node, ? extends O> filter) {
        Iterator<? extends O> iter = filter.filterMap(baseIterator());
        return iter.hasNext() ? iter.next() : null;
    }

    protected <O extends Node> @Nullable O lastImpl(Filtermap<? super Node, ? extends O> filter) {
        Iterator<? extends O> iter = filter.filterMap(baseIterator());
        return IteratorUtil.last(iter);
    }

    protected <O> List<O> toListImpl(Filtermap<? super Node, ? extends O> filter) {
        Iterator<? extends O> iter = filter.filterMap(baseIterator());
        return IteratorUtil.toList(iter);
    }

    @Override
    public @Nullable T first() {
        return firstImpl(filter);
    }

    @Override
    public <R extends Node> @Nullable R first(Class<? extends R> r1Class) {
        return firstImpl(filter.thenCast(r1Class));
    }

    @Override
    public @Nullable T first(Predicate<? super T> predicate) {
        return firstImpl(filter.thenFilter(predicate));
    }

    @Nullable
    @Override
    public T last() {
        return lastImpl(filter);
    }

    @Override
    public List<T> toList() {
        return toListImpl(this.filter);
    }

    @Override
    public <R> List<R> toList(Function<? super T, ? extends R> mapper) {
        return toListImpl(this.filter.thenApply(mapper));
    }

    @Override
    public <R extends Node> @Nullable R last(Class<? extends R> rClass) {
        return lastImpl(filter.thenCast(rClass));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + node + "] -> " + toList();
    }

    /**
     * Returns a copy of this instance, with the given filter.
     * Implementations of this method should not compose the given filter
     * with their current filter.
     */
    protected abstract <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, ? extends S> filterMap);

    static class FilteredAncestorOrSelfStream<T extends Node> extends AxisStream<T> {

        // the first node always matches the filter
        FilteredAncestorOrSelfStream(@NonNull T node, Filtermap<Node, ? extends T> target) {
            super(node, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return new AncestorOrSelfIterator(node);
        }

        @Override
        public NodeStream<T> drop(int n) {
            AssertionUtil.requireNonNegative("n", n);
            if (n == 0) {
                return this;
            }
            // eg for NodeStream.of(a,b,c).drop(2)
            Node nth = get(n); // get(2) == c
            return StreamImpl.ancestorsOrSelf(nth, filter); // c.ancestorsOrSelf() == [c]
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, ? extends S> filterMap) {
            S newFirst = TraversalUtils.getFirstParentOrSelfMatching(node, filterMap);
            if (newFirst == null) {
                return StreamImpl.empty();
            } else {
                return new FilteredAncestorOrSelfStream<>(newFirst, filterMap);
            }
        }

        @Override
        public @Nullable T first() {
            return (T) node;
        }

        @Override
        public boolean nonEmpty() {
            return true;
        }

        @Override
        protected <O extends Node> @Nullable O firstImpl(Filtermap<? super Node, ? extends O> filter) {
            return TraversalUtils.getFirstParentOrSelfMatching(node, filter);
        }
    }

    static class AncestorOrSelfStream extends FilteredAncestorOrSelfStream<Node> {

        AncestorOrSelfStream(@NonNull Node node) {
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
            while (last.getParent() != null) {
                last = last.getParent();
            }
            return last;
        }
    }

    abstract static class DescendantStreamBase<T extends Node> extends AxisStream<T> implements DescendantNodeStream<T> {

        final TreeWalker walker;

        DescendantStreamBase(@NonNull Node root,
                             TreeWalker walker,
                             Filtermap<Node, ? extends T> filter) {
            super(root, filter);
            this.walker = walker;
        }

        protected abstract <S extends Node> DescendantNodeStream<S> copyWithWalker(Filtermap<Node, ? extends S> filterMap, TreeWalker walker);

        @Override
        public DescendantNodeStream<T> crossFindBoundaries(boolean cross) {
            return walker.isCrossFindBoundaries() == cross
                   ? this
                   : copyWithWalker(this.filter, walker.crossFindBoundaries(cross));
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, ? extends S> filterMap) {
            return copyWithWalker(filterMap, walker);
        }
    }

    static class FilteredDescendantStream<T extends Node> extends DescendantStreamBase<T> {

        FilteredDescendantStream(Node node,
                                 TreeWalker walker,
                                 Filtermap<Node, ? extends T> target) {
            super(node, walker, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return walker.descendantIterator(node);
        }

        @Override
        protected <S extends Node> DescendantNodeStream<S> copyWithWalker(Filtermap<Node, ? extends S> filterMap, TreeWalker walker) {
            return new FilteredDescendantStream<>(node, walker, filterMap);
        }

        @Override
        protected <O extends Node> @Nullable O firstImpl(Filtermap<? super Node, ? extends O> filter) {
            return walker.getFirstDescendantOfType(node, filter);
        }

        @Override
        public boolean nonEmpty() {
            return walker.getFirstDescendantOfType(node, filter) != null;
        }

        @Override
        protected <O> List<O> toListImpl(Filtermap<? super Node, ? extends O> filter) {
            return walker.findDescendantsMatching(node, filter);
        }
    }

    static class DescendantStream extends FilteredDescendantStream<Node> {

        DescendantStream(Node node, TreeWalker walker) {
            super(node, walker, Filtermap.NODE_IDENTITY);
        }

        @Override
        public DescendantNodeStream<Node> crossFindBoundaries(boolean cross) {
            return new DescendantStream(node, walker.crossFindBoundaries(cross));
        }


        @Override
        public boolean nonEmpty() {
            return node.getNumChildren() > 0;
        }
    }

    static class FilteredDescendantOrSelfStream<T extends Node> extends DescendantStreamBase<T> {

        FilteredDescendantOrSelfStream(Node node,
                                       TreeWalker walker,
                                       Filtermap<Node, ? extends T> filtermap) {
            super(node, walker, filtermap);
        }

        @Override
        public Iterator<Node> baseIterator() {
            return walker.descendantOrSelfIterator(node);
        }

        @Override
        protected <S extends Node> DescendantNodeStream<S> copyWithWalker(Filtermap<Node, ? extends S> filterMap, TreeWalker walker) {
            return new FilteredDescendantOrSelfStream<>(node, walker, filterMap);
        }

        @Override
        protected <O> List<O> toListImpl(Filtermap<? super Node, ? extends O> filter) {
            List<O> result = new ArrayList<>();
            O top = filter.apply(node);
            if (top != null) {
                result.add(top);
            }
            walker.findDescendantsMatching(node, filter, result);
            return result;
        }
    }

    static final class DescendantOrSelfStream extends FilteredDescendantOrSelfStream<Node> {

        DescendantOrSelfStream(Node node, TreeWalker walker) {
            super(node, walker, Filtermap.NODE_IDENTITY);
        }

        @Override
        public DescendantNodeStream<Node> crossFindBoundaries(boolean cross) {
            return new DescendantOrSelfStream(node, walker.crossFindBoundaries(cross));
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


    /**
     * Implements following/preceding sibling streams, and children streams.
     */
    static class FilteredChildrenStream<T extends Node> extends AxisStream<T> {

        final int low; // inclusive
        final int len;

        FilteredChildrenStream(@NonNull Node root, Filtermap<Node, ? extends T> filtermap, int low, int len) {
            super(root, filtermap);
            this.low = low;
            this.len = len;
        }


        @Override
        public <R extends Node> NodeStream<R> flatMap(Function<? super T, ? extends @Nullable NodeStream<? extends R>> mapper) {
            // all operations like #children, #followingSiblings, etc
            // operate on an eagerly evaluated stream. May be empty or
            // singleton
            return StreamImpl.fromNonNullList(toList()).flatMap(mapper);
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, ? extends S> filterMap) {
            return new FilteredChildrenStream<>(node, filterMap, low, len);
        }

        @Override
        public Spliterator<T> spliterator() {
            return Spliterators.spliterator(iterator(), count(), Spliterator.SIZED | Spliterator.ORDERED);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return TraversalUtils.childrenIterator(node, low, low + len);
        }

        @Override
        protected <O extends Node> @Nullable O firstImpl(Filtermap<? super Node, ? extends O> filter) {
            return TraversalUtils.getFirstChildMatching(node, filter, low, len);
        }

        @Override
        protected <O extends Node> @Nullable O lastImpl(Filtermap<? super Node, ? extends O> filter) {
            return TraversalUtils.getLastChildMatching(node, filter, low, len);
        }

        @Override
        public int count() {
            return TraversalUtils.countChildrenMatching(node, filter, low, len);
        }

        @Override
        public boolean nonEmpty() {
            return first() != null;
        }

        @Override
        protected <O> List<O> toListImpl(Filtermap<? super Node, ? extends O> filter) {
            return TraversalUtils.findChildrenMatching(node, filter, low, len);
        }

        @Override
        public NodeStream<T> take(int maxSize) {
            AssertionUtil.requireNonNegative("maxSize", maxSize);
            // eager evaluation
            if (maxSize == 1) {
                return NodeStream.of(TraversalUtils.getFirstChildMatching(node, filter, low, len));
            }
            List<T> matching = TraversalUtils.findChildrenMatching(node, filter, low, len, maxSize);
            return StreamImpl.fromNonNullList(matching);
        }

        @Override
        public NodeStream<T> drop(int n) {
            AssertionUtil.requireNonNegative("n", n);
            if (n == 0) {
                return this;
            }
            return StreamImpl.fromNonNullList(toList()).drop(n);
        }

        @Override
        public String toString() {
            return "FilteredSlice[" + node + ", " + low + ".." + (low + len) + "] -> " + toList();
        }
    }


    /** Implements following/preceding sibling streams. */
    static class ChildrenStream extends FilteredChildrenStream<Node> {

        ChildrenStream(@NonNull Node root, int low, int len) {
            super(root, Filtermap.NODE_IDENTITY, low, len);
        }

        @Nullable
        @Override
        public Node first() {
            return len > 0 ? node.getChild(low) : null;
        }

        @Nullable
        @Override
        public Node last() {
            return len > 0 ? node.getChild(low + len - 1) : null;
        }

        @Nullable
        @Override
        public Node get(int n) {
            AssertionUtil.requireNonNegative("n", n);
            return len > 0 && n < len ? node.getChild(low + n) : null;
        }


        @Override
        public NodeStream<Node> take(int maxSize) {
            AssertionUtil.requireNonNegative("maxSize", maxSize);
            return StreamImpl.sliceChildren(node, filter, low, min(maxSize, len));
        }

        @Override
        public NodeStream<Node> drop(int n) {
            AssertionUtil.requireNonNegative("n", n);
            if (n == 0) {
                return this;
            }
            int newLow = min(low + n, node.getNumChildren());
            int newLen = max(len - n, 0);
            return StreamImpl.sliceChildren(node, filter, newLow, newLen);
        }

        @Override
        public NodeStream<Node> dropLast(int n) {
            AssertionUtil.requireNonNegative("n", n);
            if (n == 0) {
                return this;
            }
            return take(max(len - n, 0));
        }

        @Override
        public boolean nonEmpty() {
            return len > 0;
        }

        @Override
        public int count() {
            return len;
        }

        @Override
        public String toString() {
            return "Slice[" + node + ", " + low + ".." + (low + len) + "] -> " + toList();
        }
    }

}
