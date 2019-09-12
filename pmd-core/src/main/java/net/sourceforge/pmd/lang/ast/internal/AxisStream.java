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
    protected final Filtermap<Node, T> target;

    AxisStream(@NonNull Node root, Filtermap<Node, T> target) {
        super();
        this.node = root;
        this.target = target;
    }

    @Override
    public final Iterator<T> iterator() {
        return target.filterMap(baseIterator());
    }

    protected abstract Iterator<Node> baseIterator();


    @Override
    public <R extends Node> NodeStream<@NonNull R> map(Function<? super T, ? extends @Nullable R> mapper) {
        return copyWithFilter(target.thenApply(mapper));
    }

    @Override
    public NodeStream<T> filter(Predicate<? super T> predicate) {
        return copyWithFilter(target.thenApply(Filtermap.filter(predicate)));
    }

    @Override
    public <S extends Node> NodeStream<S> filterIs(Class<S> r1Class) {
        return copyWithFilter(target.thenCast(r1Class));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + node + "] -> " + toList();
    }

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
        public @Nullable T first() {
            return TraversalUtils.getFirstParentOrSelfMatching(node, target);
        }

        protected NodeStream<T> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start, target);
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

        @Override
        protected NodeStream<Node> copy(Node start) {
            return StreamImpl.ancestorsOrSelf(start);
        }
    }

    static class FilteredDescendantStream<T extends Node> extends AxisStream<T> {

        FilteredDescendantStream(Node node, Filtermap<Node, T> target) {
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
        public @Nullable T first() {
            return TraversalUtils.getFirstDescendantOfType(node, target);
        }

        @Override
        public boolean nonEmpty() {
            return TraversalUtils.getFirstDescendantOfType(node, target) != null;
        }

        @Override
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    static class DescendantStream extends FilteredDescendantStream<Node> {

        DescendantStream(Node node) {
            super(node, Filtermap.NODE_IDENTITY);
        }

        @Override
        public boolean nonEmpty() {
            return node.jjtGetNumChildren() > 0;
        }
    }

    static class FilteredDescendantOrSelfStream<T extends Node> extends AxisStream<T> {

        FilteredDescendantOrSelfStream(Node node, Filtermap<Node, T> filtermap) {
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
        public List<T> toList() {
            List<T> result = new ArrayList<>();
            T top = target.apply(node);
            if (top != null) {
                result.add(top);
            }
            TraversalUtils.findDescendantsOfType(node, target, result, false);
            return result;
        }
    }

    static final class DescendantOrSelfStream extends FilteredDescendantOrSelfStream<Node> {

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


    /**
     * Implements following/preceding sibling streams.
     */
    static class FilteredChildrenStream<T extends Node> extends AxisStream<T> {

        final int low; // inclusive
        final int len;

        FilteredChildrenStream(@NonNull Node root, Filtermap<Node, T> filtermap, int low, int len) {
            super(root, filtermap);
            this.low = low;
            this.len = len;
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
            return TraversalUtils.getFirstChildMatching(node, target, low, len);
        }

        @Override
        public @Nullable T last() {
            return TraversalUtils.getLastChildMatching(node, target, low, len);
        }


        @Override
        public <R extends Node> @Nullable R first(Class<R> rClass) {
            return TraversalUtils.getFirstChildMatching(node, target.thenCast(rClass), low, len);
        }

        @Override
        public <R extends Node> @Nullable R last(Class<R> rClass) {
            return TraversalUtils.getLastChildMatching(node, target.thenCast(rClass), low, len);
        }

        @Override
        public int count() {
            return TraversalUtils.countChildrenMatching(node, target, low, len);
        }

        @Override
        public boolean nonEmpty() {
            return first() != null;
        }

        @Override
        public List<T> toList() {
            return TraversalUtils.findChildrenMatching(node, target, low, len);
        }


        @Override
        public NodeStream<T> take(int maxSize) {
            AssertionUtil.assertArgNonNegative(maxSize);
            return StreamImpl.sliceChildren(node, target, low, min(maxSize, len));
        }

        @Override
        public NodeStream<T> drop(int n) {
            AssertionUtil.assertArgNonNegative(n);
            int newLow = min(low + n, node.jjtGetNumChildren());
            int newLen = max(len - n, 0);

            return n == 0 ? this : StreamImpl.sliceChildren(node, target, newLow, newLen);
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
