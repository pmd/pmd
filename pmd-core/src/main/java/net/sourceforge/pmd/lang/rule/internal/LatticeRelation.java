/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.rule.internal.GraphUtils.DotColor;
import net.sourceforge.pmd.util.CollectionUtil;

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
 * @param <V> Type of values
 * @param <C> Type of output value. Values are accumulated using a collector
 */
class LatticeRelation<K, @NonNull V, C> {

    private final Predicate<? super K> queryKeySelector;
    private final TopoOrder<K> keyOrder;
    private final Function<? super K, String> keyToString;

    private final Collector<? super V, ?, ? extends C> collector;
    private final C emptyValue; // empty value of the collector

    /** Those nodes that can be queried (match {@link #queryKeySelector}). */
    private final Map<K, QueryNode<?>> qNodes = new HashMap<>();

    /**
     * Those nodes that were added explicitly through #put, but may not be queried.
     * These can be fetched efficiently, which is nice since we're trying to index
     * the same keys over and over. If the node has no query node successor, then
     * {@link LNode#addValue(Object)} is a noop for it.
     */
    private final Map<K, LNode> otherNodes = new HashMap<>();

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
        this.emptyValue = CollectionUtil.finish(collector, collector.supplier().get());
    }

    /**
     * Works like the other constructor, the filter being containment
     * in the given query set. This means, only keys that are in this
     * set may be queried.
     */
    <A> LatticeRelation(TopoOrder<K> keyOrder,
                        Set<? extends K> querySet,
                        Function<? super K, String> keyToString,
                        Collector<? super V, A, ? extends C> collector) {
        this.keyOrder = keyOrder;
        this.queryKeySelector = querySet::contains;
        this.keyToString = keyToString;
        this.collector = collector;
        this.emptyValue = CollectionUtil.finish(collector, collector.supplier().get());

        for (K k : querySet) {
            putInternal(k, null);
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
     * @param pred Predecessor node (in recursive calls, this is set,
     *             to link the predecessors to the node for the key to add)
     * @param k    Key to add
     * @param val  Proper value to add to the given key (if null, nothing is to be added)
     * @param seen Recursion guard: if we see a node twice in the same recursion,
     *             there is a cycle
     */
    private void addSucc(final @NonNull PSet<LNode> pred, final K k, final @Nullable V val, final PSet<K> seen) {
        if (seen.contains(k)) {
            throw new IllegalStateException("Cycle in graph generated by " + keyOrder);
        }

        { // keep the scope of leaf small, outside of this it would be null anyway
            LNode n = otherNodes.get(k);
            if (n != null) {
                n.addValue(val); // propagate new val to all query node successors
                return;
            }
        }

        { // keep the scope of n small, outside of this it would be null anyway
            QueryNode<?> n = qNodes.get(k);
            if (n != null) { // already exists
                // propagate new val to all successors
                n.addValue(val);
                linkTransitive(pred, n);
                return;
            }
        }

        final LNode n = queryKeySelector.test(k) ? newQnode(pred, k) : newLeafNode(k);

        n.addValue(val);
        PSet<LNode> newPreds = pred.plus(n);
        PSet<K> newSeen = seen.plus(k);

        keyOrder.directSuccessors(k)
                .forEach(next -> addSucc(newPreds, next, val, newSeen));
    }

    @NonNull
    private LNode newLeafNode(K k) {
        NormalNode n = new NormalNode(k);
        otherNodes.put(k, n);
        return n;
    }

    @NonNull
    private LNode newQnode(@NonNull PSet<LNode> pred, K k) {
        QueryNode<?> n = new QueryNode<>(k);
        qNodes.put(k, n);
        linkTransitive(pred, n);
        return n;
    }

    private void linkTransitive(Set<LNode> preds, QueryNode<?> succ) {
        if (succ == null) {
            return;
        }
        for (LNode pred : preds) {
            pred.transitiveSuccs.add(succ);
            pred.transitiveSuccs.addAll(succ.transitiveSuccs);
        }
    }

    // test only
    Set<K> transitiveQuerySuccs(K key) {
        LNode lNode = otherNodes.get(key);
        if (lNode == null) {
            lNode = qNodes.get(key);
        }
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
     * @throws NullPointerException  If any of the parameter is null
     */
    public void put(@NonNull K key, @NonNull V value) {
        AssertionUtil.requireParamNotNull("key", key);
        AssertionUtil.requireParamNotNull("value", value);
        putInternal(key, value);
    }

    private void putInternal(@NonNull K key, @Nullable V value) {
        addSucc(HashTreePSet.empty(), key, value, HashTreePSet.empty());
    }

    /**
     * Returns the computed value for the given key, or an empty set.
     * Only keys matching the filter given when constructing the lattice
     * can be queried, if that is not the case, then this will return
     * the empty set even if some values were {@link #put(Object, Object)}
     * for it.
     *
     * @throws NullPointerException If the key is null
     */
    @NonNull
    public C get(@NonNull K key) {
        AssertionUtil.requireParamNotNull("key", key);
        QueryNode<?> n = qNodes.get(key);
        return n == null ? emptyValue : n.computeValue();
    }

    public void clearValues() {
        for (QueryNode<?> n : qNodes.values()) {
            n.resetValue();
        }
    }

    @Override
    public String toString() {
        // generates a DOT representation of the lattice
        // Visualize eg at http://webgraphviz.com/
        return GraphUtils.toDot(
            allNodes(),
            n -> n.transitiveSuccs,
            n -> n.getClass() == QueryNode.class ? DotColor.GREEN : DotColor.BLACK,
            LNode::describe
        );
    }

    private Set<LNode> allNodes() {
        return CollectionUtil.union(qNodes.values(), otherNodes.values());
    }

    private abstract class LNode { // "Lattice Node"

        // note the key is non-null except in BlackHoleNode,
        // so that class must override methods that use the key
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

        /**
         * Add a value to this node and all its transitive successors.
         */
        void addValue(@Nullable V v) {
            if (v == null) {
                return;
            }
            this.addValueNonRecursive(v);
            transitiveSuccs.forEach(s -> s.addValueNonRecursive(v));
        }

        abstract void addValueNonRecursive(@NonNull V v);

        /** Describe the key. */
        protected String describe() {
            return keyToString.apply(key);
        }
    }

    /**
     * A node that may be queried with {@link #get(Object)}.
     *
     * @param <A> Internal accumulator type of the collector, this is
     *            the second type argument of the collector of the lattice,
     *            it doesn't matter outside of this class
     */
    private final class QueryNode<A> extends LNode {

        /** Value associated with this node. */
        private A accumulator;
        private C finished;

        QueryNode(@NonNull K key) {
            super(key);
            resetValue();
        }

        @Override
        void addValueNonRecursive(@NonNull V v) {
            collector().accumulator().accept(accumulator, v);
        }

        C computeValue() {
            if (finished == null) {
                this.finished = CollectionUtil.finish(collector(), accumulator);
            }
            return this.finished;
        }

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
        void addValueNonRecursive(@NonNull V v) {
            // do nothing, leaf nodes do not store values,
            // they just forward to their transitive QNode successors
        }

        @Override
        public String toString() {
            return "leaf(" + key + ')';
        }
    }
}
