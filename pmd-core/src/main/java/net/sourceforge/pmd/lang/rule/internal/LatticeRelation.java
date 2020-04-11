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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.lang.rule.internal.GraphUtils.DotColor;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Indexes data of type {@code <V>} with keys of type {@code <K>}, where
 * a partial order exists between the keys. The internal representation
 * is a directed acyclic graph on {@code <K>}. The value associated to
 * a key is the recursive union of the values of all the keys it covers.
 *
 * <p>The internal structure only allows <i>some</i> keys to be queried
 * among all keys encountered.
 *
 * @param <K> Type of keys, must have a corresponding {@link TopoOrder},
 *            must be suitable for use as a map key (immutable, consistent
 *            equals/hashcode)
 * @param <V> Type of values
 */
class LatticeRelation<K, @NonNull V> {

    /*
        Each lattice node stores *all* its transitive successors.
        This makes it so, that each #put operation adds the value to
        all transitive successors at once, and each #get operation is
        constant time.

        In a previous iteration, nodes just stored direct successors,
        and query nodes computed their value from their predecessors
        on each #get.
        That was clumsy because then, that procedure needed to care about
        diamond situations explicitly. And while that optimises the #put
        operation, it makes #get possibly very costly (because one has to
        recurse on all predecessors).

        By contrast, optimising the #get operation works well with the
        assumption that all query nodes will be queried at some point,
        which will be the case for rulechain application.
     */

    private final Predicate<? super K> queryKeySelector;
    private final TopoOrder<K> keyOrder;
    private final Function<? super K, String> keyToString;

    /**
     * Those nodes that can be queried (match {@link #queryKeySelector}).
     */
    private final Map<K, QueryNode> qNodes = new HashMap<>();

    /**
     * Those nodes that were added explicitly through #put, but may not be queried.
     * These can be fetched efficiently, which is nice since we're trying to index
     * the same keys over and over. If the node has no query node successor, then
     * {@link LNode#addValue(Object)} is a noop for it.
     */
    private final Map<K, LNode> leaves = new HashMap<>();

    /**
     * Creates a new relation with the given configuration.
     *
     * @param keyOrder         Partial order generating the lattice
     * @param queryKeySelector Filter determining which keys can be queried
     *                         through {@link #get(Object)}
     * @param keyToString      Strategy to render keys when dumping the lattice to a graph
     */
    LatticeRelation(TopoOrder<K> keyOrder,
                    Predicate<? super K> queryKeySelector,
                    Function<? super K, String> keyToString) {
        this.keyOrder = keyOrder;
        this.queryKeySelector = queryKeySelector;
        this.keyToString = keyToString;
    }

    /**
     * Works like the other constructor, the filter being containment
     * in the given query set. This means, only keys that are in this
     * set may be queried.
     */
    LatticeRelation(TopoOrder<K> keyOrder,
                    Set<? extends K> querySet,
                    Function<? super K, String> keyToString) {
        this.keyOrder = keyOrder;
        this.queryKeySelector = querySet::contains;
        this.keyToString = keyToString;

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

        LNode leaf = leaves.get(k);
        if (leaf != null) {
            leaf.addValue(val); // propagate new val to all query node successors
            return;
        }

        { // keep the scope of n small, outside of this it would be null anyway
            QueryNode n = qNodes.get(k);
            if (n != null) { // already exists
                // propagate new val to all successors
                n.addValue(val);
                linkTransitive(pred, n);
                return;
            }
        }

        if (queryKeySelector.test(k)) { // needs a new query node
            // (3)
            QueryNode n = new QueryNode(k);
            qNodes.put(k, n);
            n.addValue(val);
            linkTransitive(pred, n);

            PSet<LNode> newPreds = pred.plus(n);
            PSet<K> newSeen = seen.plus(k);

            keyOrder.directSuccessors(k)
                    .forEachRemaining(next -> addSucc(newPreds, next, val, newSeen));
        } else {
            // This is a leaf, we need to check its successors. If any
            // are query nodes, then it will be linked to them. Otherwise
            // its successors will remain empty, and addValue will be a
            // noop for it.
            LeafNode leafOfK = new LeafNode(k);
            leafOfK.addValue(val);
            leaves.put(k, leafOfK);

            @NonNull PSet<LNode> predWithK = pred.plus(leafOfK);
            PSet<K> nextSeen = seen.plus(k);

            keyOrder.directSuccessors(k)
                    .forEachRemaining(next -> addSucc(predWithK, next, val, nextSeen));
        }
    }

    private void linkTransitive(Set<LNode> preds, QueryNode succ) {
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
        LNode lNode = leaves.get(key);
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
    public Set<V> get(@NonNull K key) {
        AssertionUtil.requireParamNotNull("key", key);
        QueryNode n = qNodes.get(key);
        return n == null ? HashTreePSet.empty() : n.computeValue();
    }

    void clearValues() {
        for (QueryNode n : qNodes.values()) {
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
        return CollectionUtil.union(qNodes.values(), leaves.values());
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
        final Set<QueryNode> transitiveSuccs = new LinkedHashSet<>();

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
     */
    private final class QueryNode extends LNode {

        /** Value associated with this node. */
        private @NonNull Set<V> properVal = new LinkedHashSet<>();

        QueryNode(@NonNull K key) {
            super(key);
        }

        @Override
        void addValueNonRecursive(@NonNull V v) {
            properVal.add(v);
        }

        private Set<V> computeValue() {
            return properVal;
        }

        void resetValue() {
            properVal = new LinkedHashSet<>();
        }

        @Override
        public String toString() {
            return "qnode(" + key + ')';
        }
    }

    private final class LeafNode extends LNode {

        LeafNode(@NonNull K key) {
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
