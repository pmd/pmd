/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static net.sourceforge.pmd.util.CollectionUtil.any;
import static net.sourceforge.pmd.util.CollectionUtil.finish;
import static net.sourceforge.pmd.util.CollectionUtil.map;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.rule.internal.GraphUtils.DotColor;

/**
 * Indexes data of type {@code <V>} with keys of type {@code <K>}, where
 * a partial order exists between the keys. Values are accumulated into
 * a type {@code <C>} (can be an arbitrary collection). The value associated
 * to a key is the recursive union of the values of all its *predecessors*
 * according to the partial order.
 *
 * <p>For example if your type of keys is {@link Class}, and you use
 * subtyping as a partial order, then the value associated to a class C
 * will be the union of the individual values added for C, and those
 * added for all its subtypes.
 *
 * <p>The internal structure only allows <i>some</i> keys to be queried
 * among all keys encountered. This optimises the structure, because it
 * allows accumulate values nobody cares about. This is configured by
 * a predicate given to the constructor.
 *
 * @param <K> Type of keys, must have a corresponding {@link TopoOrder},
 *            must be suitable for use as a map key (immutable, consistent
 *            equals/hashcode)
 * @param <V> Type of elements indexed by K
 * @param <C> Type of collection the values are accumulated in (configurable with an arbitrary collector)
 */
class LatticeRelation<K, @NonNull V, C> {

    private final Predicate<? super K> queryKeySelector;
    private final TopoOrder<K> keyOrder;
    private final Function<? super K, String> keyToString;
    private final Collector<? super V, ?, ? extends C> collector;
    private final C emptyValue; // empty value of the collector

    private final Map<K, LNode> nodes = new HashMap<>();

    /**
     * Creates a new relation with the given configuration.
     *
     * @param keyOrder         Partial order generating the lattice
     * @param queryKeySelector Filter determining which keys can be queried
     *                         through {@link #get(Object)}
     * @param keyToString      Strategy to render keys when dumping the lattice to a graph
     * @param collector        Collector used to accumulate values
     * @param <A>              Internal accumulator type of the collector
     */
    <A> LatticeRelation(TopoOrder<K> keyOrder,
                        Predicate<? super K> queryKeySelector,
                        Function<? super K, String> keyToString,
                        Collector<? super V, A, ? extends C> collector) {
        this.keyOrder = keyOrder;
        this.queryKeySelector = queryKeySelector;
        this.keyToString = keyToString;
        this.collector = collector;
        this.emptyValue = finish(collector, collector.supplier().get());
    }

    /**
     * Works like the other constructor, the filter being containment
     * in the given query set. This means, only keys that are in this
     * set may be queried.
     *
     * @throws IllegalArgumentException If the query set contains a null key
     * @throws IllegalStateException    If the topo order generates a cycle
     */
    <A> LatticeRelation(TopoOrder<K> keyOrder,
                        Set<? extends K> querySet,
                        Function<? super K, String> keyToString,
                        Collector<? super V, A, ? extends C> collector) {
        this.keyOrder = keyOrder;
        this.queryKeySelector = querySet::contains;
        this.keyToString = keyToString;
        this.collector = collector;
        this.emptyValue = finish(collector, collector.supplier().get());

        for (K k : querySet) {
            if (k == null) {
                throw new IllegalArgumentException("Query set " + querySet + " contains a null key");
            }
            putDontCheckParams(k, null);
        }
    }

    /**
     * Adds the val to the node corresponding to the [key], creating it
     * if needed. If the key matches the filter, a QueryNode is created.
     * Otherwise, either a LeafNode (if there is some QueryNode that cares),
     * or the key is linked to the black hole. This is only done the first
     * time we encounter the key, which means subsequently, #get and #put
     * access will be "constant time" (uses one of the maps).
     *
     * <p>All successors of the key are recursively added to the structure.
     *
     * @param preds Predecessor node (in recursive calls, this is set,
     *              to link them to the given key). This also is used
     *              to check for cycles
     * @param k     Key to add
     * @param val   Proper value to add to the given key (if null, nothing is to be added)
     */
    private void addSucc(@Nullable Deque<LNode> preds, final K k, final @Nullable V val) {
        if (any(preds, n -> n.key.equals(k))) {
            throw cycleError(preds, k);
        }

        LNode n = nodes.get(k);
        if (n != null) {
            link(preds, n, val);
            return;
        }

        n = queryKeySelector.test(k) ? new QueryNode<>(k) : new NormalNode(k);
        nodes.put(k, n);
        link(preds, n, val);

        if (preds == null) {
            preds = new ArrayDeque<>();
        }
        preds.addLast(n);

        for (K next : keyOrder.directSuccessors(k)) {
            addSucc(preds, next, val);
        }

        if (preds.removeLast() != n) {
            throw new IllegalStateException("Unbalanced stack push/pop");
        }
    }

    private void link(@Nullable Iterable<LNode> preds, LNode n, @Nullable V val) {
        if (preds != null) {
            n.addAsSuccessorTo(preds);
        }
        n.addValueTransitive(val);
    }

    @NonNull
    private IllegalStateException cycleError(@NonNull Deque<LNode> preds, K k) {
        List<String> toStrings = new ArrayList<>(map(preds.iterator(), n -> keyToString.apply(n.key)));
        toStrings.add(keyToString.apply(k));
        return new IllegalStateException(
            "Cycle in graph generated by " + keyOrder + ": " + String.join(" -> ", toStrings));
    }

    // test only
    Set<K> transitiveQuerySuccs(K key) {
        LNode lNode = nodes.get(key);
        if (lNode == null) {
            return Collections.emptySet();
        } else {
            Set<K> succKeys = new LinkedHashSet<>();
            lNode.transitiveSuccs.forEach(s -> succKeys.add(s.key));
            return succKeys;
        }
    }

    /**
     * Adds one value to the given key. This value will be joined to the
     * values of all keys inferior to it when calling {@link #get(Object)}.
     *
     * @throws IllegalStateException If the order has a cycle
     * @throws NullPointerException  If either of the parameters is null
     */
    public void put(@NonNull K key, @NonNull V value) {
        AssertionUtil.requireParamNotNull("key", key);
        AssertionUtil.requireParamNotNull("value", value);
        putDontCheckParams(key, value);
    }

    private void putDontCheckParams(@NonNull K key, @Nullable V value) {
        addSucc(null, key, value);
    }

    /**
     * Returns the computed value for the given key, or the default value
     * of the collector.
     * <p>Only keys matching the filter given when constructing the lattice
     * can be queried, if that is not the case, then this will return
     * the default value even if some values were {@link #put(Object, Object)}
     * for it.
     *
     * @throws NullPointerException If the key is null
     */
    @NonNull
    public C get(@NonNull K key) {
        AssertionUtil.requireParamNotNull("key", key);
        LNode n = nodes.get(key);
        return n == null ? emptyValue : n.computeValue();
    }

    public void clearValues() {
        for (LNode n : nodes.values()) {
            n.resetValue();
        }
    }

    @Override
    public String toString() {
        // generates a DOT representation of the lattice
        // Visualize eg at http://webgraphviz.com/
        return GraphUtils.toDot(
            nodes.values(),
            n -> n.transitiveSuccs,
            n -> n.getClass() == QueryNode.class ? DotColor.GREEN : DotColor.BLACK,
            n -> keyToString.apply(n.key)
        );
    }

    private abstract class LNode { // "Lattice Node"

        protected final @NonNull K key;

        /**
         * These are all the transitive strict successors, not just the direct
         * ones. Each time a value is added to this node, it is added to
         * all these successors too.
         */
        final Set<QueryNode<?>> transitiveSuccs = new LinkedHashSet<>();

        private LNode(@NonNull K key) {
            this.key = key;
        }

        /** Add a value to this node and all its transitive successors. */
        void addValueTransitive(@Nullable V v) {
            if (v == null) {
                return;
            }
            this.addValue(v);
            transitiveSuccs.forEach(s -> s.addValue(v));
        }

        abstract void addAsSuccessorTo(Iterable<LNode> preds);


        abstract @NonNull C computeValue();


        abstract void resetValue();


        abstract void addValue(@NonNull V v);

    }

    /**
     * A node that may be queried with {@link #get(Object)}.
     *
     * @param <A> Internal accumulator type of the collector, this is
     *            the second type argument of the collector of the lattice,
     *            it doesn't matter outside of this class
     */
    private final class QueryNode<A> extends LNode {

        private A accumulator;
        private C finished;

        QueryNode(@NonNull K key) {
            super(key);
            resetValue();
        }

        @Override
        void addAsSuccessorTo(Iterable<LNode> preds) {
            preds.forEach(n -> {
                if (n.transitiveSuccs.add(this)) {
                    // otherwise the transitive successors are also already here
                    n.transitiveSuccs.addAll(this.transitiveSuccs);
                }
            });
        }

        @Override
        void addValue(@NonNull V v) {
            collector().accumulator().accept(accumulator, v);
        }

        @Override
        @NonNull C computeValue() {
            if (finished == null) {
                this.finished = finish(collector(), accumulator);
            }
            return this.finished;
        }

        @Override
        void resetValue() {
            accumulator = collector().supplier().get();
            finished = null;
        }

        @Override
        public String toString() {
            return "qnode(" + key + ')';
        }

        @SuppressWarnings("unchecked")
        private Collector<? super V, A, ? extends C> collector() {
            return (Collector<? super V, A, ? extends C>) collector;
        }
    }

    private final class NormalNode extends LNode {

        NormalNode(@NonNull K key) {
            super(key);
        }

        @Override
        void addAsSuccessorTo(Iterable<LNode> preds) {
            // do nothing, this cannot be a successor of anything
        }

        @Override
        void addValue(@NonNull V v) {
            // do nothing, leaf nodes do not store values,
            // they just forward to their transitive QNode successors
        }

        @Override
        @NonNull C computeValue() {
            return emptyValue;
        }

        @Override
        void resetValue() {
            // do nothing
        }

        @Override
        public String toString() {
            return "leaf(" + key + ')';
        }
    }
}
