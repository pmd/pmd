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
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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

    /**
     * Implementations are based on the iterator rather than the stream.
     */
    private static abstract class IteratorBasedStream<R extends Node> implements NodeStream<R> {

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
            Iterator<R> iter = iterator();
            R result = null;
            while (n-- >= 0 && iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }

        @Override
        public NodeStream<R> drop(int n) {
            return new IteratorBasedStream<R>() {
                @Override
                public Iterator iterator() {
                    Iterator<R> iter = IteratorBasedStream.this.iterator();
                    IteratorUtil.drop(iter, n);
                    return iter;
                }
            };
        }

        @Override
        public boolean nonEmpty() {
            return iterator().hasNext();
        }

        @Override
        public @Nullable R first() {
            return get(0);
        }
    }

    private static abstract class AxisStream<R extends Node> extends IteratorBasedStream<R> {

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

    private static final class ChildrenStream extends FilteredChildrenStream<Node> {

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
}
