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

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Stream that iterates over one axis of the tree.
 */
abstract class AxisStream<T extends Node> extends IteratorBasedNStream<T> {

    /** Spec of this field depends on the subclass. */
    protected final Node node;
    /** Filter, for no filter, this is {@link Filtermap#NODE_IDENTITY}. */
    protected final Filtermap<Node, T> filter;

    AxisStream(@NonNull Node root, Filtermap<Node, T> filter) {
        super();
        this.node = root;
        this.filter = filter;
    }

    @Override
    public final Iterator<T> iterator() {
        return filter.filterMap(baseIterator());
    }

    protected abstract Iterator<Node> baseIterator();


    @Override
    public <R extends Node> NodeStream<@NonNull R> map(Function<? super T, ? extends @Nullable R> mapper) {
        return copyWithFilter(filter.thenApply(mapper));
    }

    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return copyWithFilter(filter.thenApply(Filtermap.filter(predicate)));
    }

    @Override
    public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
        return copyWithFilter(filter.thenCast(r1Class));
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
    protected abstract <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap);

    static class FilteredAncestorOrSelfStream<T extends Node> extends AxisStream<T> {

        FilteredAncestorOrSelfStream(@NonNull Node node, Filtermap<Node, T> target) {
            super(node, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            return new AncestorOrSelfIterator(node);
        }

        @Override
        public NodeStream<T> drop(int n) {
            AssertionUtil.requireNonNegative("n", n);
            switch (n) {
            case 0:
                return this;
            case 1:
                return StreamImpl.ancestors(node, filter);
            default:
                // eg for NodeStream.of(a,b,c).drop(2)
                Node nth = get(n); // get(2) == c
                return nth == null ? NodeStream.empty()
                                   : StreamImpl.ancestorsOrSelf(nth, filter); // c.ancestorsOrSelf() == [c]
            }
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return new FilteredAncestorOrSelfStream<>(node, filterMap);
        }

        @Override
        public @Nullable T first() {
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
            while (last.jjtGetParent() != null) {
                last = last.jjtGetParent();
            }
            return last;
        }
    }

    static abstract class DescendantStreamBase<T extends Node> extends AxisStream<T> implements DescendantNodeStream<T> {

        final TraversalConfig config;

        DescendantStreamBase(@NonNull Node root,
                             TraversalConfig config,
                             Filtermap<Node, T> filter) {
            super(root, filter);
            this.config = config;
        }

        protected abstract <S extends Node> DescendantNodeStream<S> copyWithConfig(Filtermap<Node, S> filterMap, TraversalConfig config);

        @Override
        public DescendantNodeStream<T> crossFindBoundaries(boolean cross) {
            return copyWithConfig(this.filter, config.crossFindBoundaries(cross));
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
            return copyWithConfig(filterMap, config);
        }
    }

    static class FilteredDescendantStream<T extends Node> extends DescendantStreamBase<T> {

        FilteredDescendantStream(Node node,
                                 TraversalConfig config,
                                 Filtermap<Node, T> target) {
            super(node, config, target);
        }

        @Override
        protected Iterator<Node> baseIterator() {
            DescendantOrSelfIterator iter = new DescendantOrSelfIterator(node, config);
            iter.next(); // skip self
            return iter;
        }

        @Override
        protected <S extends Node> DescendantNodeStream<S> copyWithConfig(Filtermap<Node, S> filterMap, TraversalConfig config) {
            return new FilteredDescendantStream<>(node, config, filterMap);
        }

        @Override
        public @Nullable T first() {
            return TraversalUtils.getFirstDescendantOfType(node, filter, config);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstDescendantOfType(node, filter, config) != null;
        }

        @Override
        public List<T> toList() {
            return TraversalUtils.findDescendantsMatching(node, filter, config);
        }
    }

    static class DescendantStream extends FilteredDescendantStream<Node> {

        DescendantStream(Node node, TraversalConfig config) {
            super(node, config, Filtermap.NODE_IDENTITY);
        }

        @Override
        public DescendantNodeStream<Node> crossFindBoundaries(boolean cross) {
            return new DescendantStream(node, config.crossFindBoundaries(cross));
        }


        @Override
        public boolean nonEmpty() {
            return node.jjtGetNumChildren() > 0;
        }
    }

    static class FilteredDescendantOrSelfStream<T extends Node> extends DescendantStreamBase<T> {

        FilteredDescendantOrSelfStream(Node node,
                                       TraversalConfig config,
                                       Filtermap<Node, T> filtermap) {
            super(node, config, filtermap);
        }

        @Override
        public Iterator<Node> baseIterator() {
            return new DescendantOrSelfIterator(node, config);
        }

        @Override
        protected <S extends Node> DescendantNodeStream<S> copyWithConfig(Filtermap<Node, S> filterMap, TraversalConfig config) {
            return new FilteredDescendantOrSelfStream<>(node, config, filterMap);
        }

        @Override
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            T top = filter.apply(node);
            if (top != null) {
                result.add(top);
            }
            TraversalUtils.findDescendantsMatching(node, filter, result, config);
            return result;
        }
    }

    static final class DescendantOrSelfStream extends FilteredDescendantOrSelfStream<Node> {

        DescendantOrSelfStream(Node node, TraversalConfig config) {
            super(node, config, Filtermap.NODE_IDENTITY);
        }

        @Override
        public DescendantNodeStream<Node> crossFindBoundaries(boolean cross) {
            return new DescendantOrSelfStream(node, config.crossFindBoundaries(cross));
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

        FilteredChildrenStream(@NonNull Node root, Filtermap<Node, T> filtermap, int low, int len) {
            super(root, filtermap);
            this.low = low;
            this.len = len;
        }

        FilteredChildrenStream(Node root, Filtermap<Node, T> filtermap) {
            this(root, filtermap, 0, root.jjtGetNumChildren());
        }

        @Override
        protected <S extends Node> NodeStream<S> copyWithFilter(Filtermap<Node, S> filterMap) {
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
        public @Nullable T first() {
            return TraversalUtils.getFirstChildMatching(node, filter, low, len);
        }

        @Override
        public @Nullable T last() {
            return TraversalUtils.getLastChildMatching(node, filter, low, len);
        }


        @Override
        public <R extends Node> @Nullable R first(Class<R> rClass) {
            return TraversalUtils.getFirstChildMatching(node, filter.thenCast(rClass), low, len);
        }

        @Override
        public <R extends Node> @Nullable R last(Class<R> rClass) {
            return TraversalUtils.getLastChildMatching(node, filter.thenCast(rClass), low, len);
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
        public List<T> toList() {
            return TraversalUtils.findChildrenMatching(node, filter, low, len);
        }


        @Override
        public NodeStream<T> take(int maxSize) {
            AssertionUtil.requireNonNegative("maxSize", maxSize);
            return StreamImpl.sliceChildren(node, filter, low, min(maxSize, len));
        }

        @Override
        public NodeStream<T> drop(int n) {
            AssertionUtil.requireNonNegative("n", n);
            int newLow = min(low + n, node.jjtGetNumChildren());
            int newLen = max(len - n, 0);

            return n == 0 ? this : StreamImpl.sliceChildren(node, filter, newLow, newLen);
        }

        @Override
        public String toString() {
            return "Slice[" + node + ", " + low + ".." + (low + len) + "] -> " + toList();
        }
    }


    /** Implements following/preceding sibling streams. */
    static class ChildrenStream extends FilteredChildrenStream<Node> {

        ChildrenStream(@NonNull Node root, int low, int len) {
            super(root, Filtermap.NODE_IDENTITY, low, len);
        }

        ChildrenStream(@NonNull Node root) {
            super(root, Filtermap.NODE_IDENTITY);
        }

        @Nullable
        @Override
        public Node first() {
            return len > 0 ? node.jjtGetChild(low) : null;
        }

        @Nullable
        @Override
        public Node last() {
            return len > 0 ? node.jjtGetChild(low + len - 1) : null;
        }

        @Nullable
        @Override
        public Node get(int n) {
            AssertionUtil.requireNonNegative("n", n);
            return len > 0 && n < len ? node.jjtGetChild(low + n) : null;
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
