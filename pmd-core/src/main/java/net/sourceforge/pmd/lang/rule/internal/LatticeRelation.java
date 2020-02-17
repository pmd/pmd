/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

/**
 * Represents a property of type {@code Set<U>} on a datatype {@code <T>}.
 * The internal representation is a directed acyclic graph of {@code <T>},
 * built according to a {@link TopoOrder}. The value associated to a node
 * is the recursive union of the values of all the nodes it covers, plus
 * its own value.
 *
 * <p>An instance has two states:
 * <ul>
 * <li>Read-only: mutation is impossible, but querying data is.
 * <li>Write-only: querying data is impossible, but mutating the structure is.
 * </ul>
 *
 * <p>Initially the structure is created in write-only mode. Use
 * {@link #freezeTopo()} and {@link #unfreezeTopo()} to toggle the mode.
 * The expensive checks of {@link #freezeTopo()} may be avoided if the
 * internal structure is not changed during a write phase.
 *
 * <p><b>Limitations</b>
 * <ul>
 * <li>The {@link TopoOrder TopoOrder<T>} must generate an acyclic graph,
 *  this implementation handles cycles by throwing an exception upon freezing.
 * <li>There is no equality relation defined on a lattice, and no
 *   operation to test if an element is contained in the lattice.
 * <li>A lattice can only grow, and not be pruned.
 * <li>This implementation is not thread-safe.
 * </ul>
 * <p>
 *
 * @param <K> Type of keys, must have a corresponding {@link TopoOrder},
 *            must implement a consistent {@link Object#equals(Object) equals} and
 *            {@link Object#hashCode() hashcode} and be immutable.
 * @param <V> Type of values
 */
class LatticeRelation<K, @NonNull V> {

    // constants for the toposort
    private static final int UNDEFINED_TOPOMARK = -1;
    private static final int PERMANENT_TOPOMARK = 0;
    private static final int TMP_TOPOMARK = 1;

    private final Predicate<? super K> filter;
    private final TopoOrder<K> keyOrder;
    private final Function<? super K, String> keyToString;

    // state
    private final Map<K, LNode> nodes = new HashMap<>();
    /** Keys that have been submitted to {@link #put(Object, Object)} as of now. */
    private PSet<K> seeds = HashTreePSet.empty();

    /**
     * Value of {@link #seeds} after the last freeze cycle. If this has not changed
     * during the write phase, then the topo is already up to date and we can avoid
     * doing the computations of {@link #freezeTopo()}.
     */
    private PSet<K> lastSeeds = seeds;
    private boolean frozen;

    /**
     * Creates a new relation with the given configuration.
     *
     * @param keyOrder    Partial order generating the lattice
     * @param filter      Filter for nodes to keep. During the construction phase,
     *                    all nodes are in the lattice. When freezing, the lattice
     *                    is completely expanded, then only nodes satisfying this
     *                    filter are kept.
     * @param keyToString Strategy to render keys when dumping the lattice to a graph
     */
    LatticeRelation(TopoOrder<K> keyOrder,
                    Predicate<? super K> filter,
                    Function<? super K, String> keyToString) {
        this.keyOrder = keyOrder;
        this.filter = filter;
        this.keyToString = keyToString;
    }

    /**
     * Works like the other constructor, the filter being containment
     * in the given query set. This means, only keys that are in this
     * set, or keys that were added individually through {@link #put(Object, Object)}
     * may be queried.
     */
    LatticeRelation(TopoOrder<K> keyOrder,
                    Set<? extends K> querySet,
                    Function<? super K, String> keyToString) {
        this.keyOrder = keyOrder;
        this.filter = k -> seeds.contains(k);
        this.keyToString = keyToString;
        seeds = seeds.plusAll(querySet);

        for (K k : querySet) {
            put(k, null);
        }
    }

    private @Nullable LNode getOrCreateNode(K key) {
        assert key != null : "null key is not allowed";
        return addSucc(null, key);
    }

    private @Nullable LNode addSucc(@Nullable LNode pred, K key) {
        if (nodes.containsKey(key)) {
            LNode n = nodes.get(key);
            link(pred, n);
            return n;
        }

        LNode n = null;
        if (filter.test(key)) {
            n = new LNode(key);
            nodes.put(key, n);
            link(pred, n);
            pred = n;
        }

        // even if we didn't create a node, we carry on with the successors
        Iterator<K> successors = keyOrder.directSuccessors(key);
        while (successors.hasNext()) {
            addSucc(pred, successors.next());
        }
        return n;
    }

    private void link(LNode pred, LNode succ) {
        if (pred == null || succ == null) {
            return;
        }
        pred.succ = pred.succ.plus(succ);
        succ.preds = succ.preds.plus(pred);
    }

    private void addSuccessors(K key, LNode n) {
        keyOrder.directSuccessors(key)
                .forEachRemaining(s -> n.succ = n.succ.plus(this.getOrCreateNode(s)));
    }

    /**
     * Adds the value to the given key, unless it is null.
     */
    public void put(K key, V value) {
        ensureMutable();
        seeds = seeds.plus(key);
        LNode node = getOrCreateNode(key);
        if (node != null && value != null) {
            node.addProperVal(value);
        }
    }

    /**
     * Returns the computed value for the given key, or an empty set.
     */
    @NonNull
    public PSet<V> get(K key) {
        if (!frozen) {
            throw new IllegalStateException("Lattice topology is not frozen");
        }
        LNode n = nodes.get(key);
        return n == null ? HashTreePSet.empty() : n.computeValue();
    }

    /**
     * Clear values on the lattice nodes. The lattice topology is preserved.
     * Reusing the lattice for another run may avoid having to make topological
     * checks again, provided the topology is not modified.
     *
     * <p>If you want to clear the topology, use another instance.
     */
    void clearValues() {
        ensureMutable();

        for (LNode value : nodes.values()) {
            value.resetValue();
        }
    }

    private void ensureMutable() {
        if (frozen) {
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }
    }

    /**
     * Mark this instance as write-only. Mutating the topology becomes
     * possible, but querying data is impossible.
     */
    void unfreezeTopo() {
        frozen = false;
        lastSeeds = seeds;
    }

    /**
     * Marks this instance as read-only. Insertions of nodes are prohibited,
     * querying values is allowed. This method checks that the lattice is
     * acyclic, and performs other topological checks and transformations
     * to optimize performance of queries.
     *
     * @throws IllegalStateException If the lattice has a cycle
     */
    void freezeTopo() {
        if (frozen) {
            return;
        }
        frozen = true;
        if (lastSeeds.equals(seeds)) {
            // no new seeds have been encountered, topo is up to date
            // (but maybe not proper vals)

            for (LNode node : nodes.values()) {
                node.resetFrozenValue();
            }
            return;
        }

        for (LNode node : new HashSet<>(nodes.values())) {
            node.resetFrozenValue();
            node.resetTopoData();
        }

        int n = nodes.size();

//        // topological sort
//        List<LNode> lst = toposort();
//        // also LNode#idx is set to the index in the list
//
//        // here path is an adjacency matrix
//        // (ie path[i][j] means "j is a direct successor of i")
//
//        // since nodes are toposorted,
//        //  path[i][j] => i < j
//        // so we can avoid being completely cubic
//
//        boolean[] kept = new boolean[n];
//        for (int i = 0; i < n; i++) {
//            if (filter.test(lst.get(i).key)) {
//                kept[i] = true;
//            }
//        }
//
//        // kept[i] means the node is not pruned
//
//        for (LNode jn : lst) {
//            final int j = jn.idx;
//            if (!kept[j]) {
//                // node j is pruned, so for all paths i -> j -> k,
//                // we must add a path i -> k, otherwise path is lost
//
//                // This works in one pass because nodes are toposorted
//                // Eg for i -> j -> k -> l, where both j and k are filtered out,
//                // this will first add i -> k, then i -> l, because i -> k already exists
//                for (LNode kn : jn.succ) {
//                    for (int i = j + 1; i < n; i++) { // find all i s.t. i -> j
//                        LNode in = lst.get(i);
//                        if (in.succ.contains(jn)) {
//                            in.succ = in.succ.plus(kn);
//                        }
//                    }
//                }
//            }
//        }
//
//        // transitive reduction
//        for (LNode in : lst) {
//            for (LNode jn : in.succ) {
//                if (kept[jn.idx]) { // only if j is kept
//                    for (LNode kn : jn.succ) {
//                        // i -> j -> k
//                        // delete i -> k
//                        in.succ = in.succ.minus(kn);
//                    }
//                }
//            }
//        }
//
//        // assign predecessors to all nodes
//        // this inverts the graph
//        for (int i = 0; i < n; i++) {
//            LNode in = lst.get(i);
//
//            in.succ = HashTreePSet.empty();
//
//            if (!kept[i]) {
//                nodes.remove(in.key);
//                continue;
//            }
//
//            for (int j = i + 1; j < n; j++) {
//                LNode jn = lst.get(j);
//                if (kept[j] && jn.succ.contains(in)) {
//                    // succ means "pred" now
//                    in.succ = in.succ.plus(jn);
//                }
//            }
//        }
    }


    /**
     * Returns a list in which the vertices of this graph are sorted
     * in the following way:
     *
     * {@code if there exists an edge u -> v, then v comes before u in the list}
     *
     * <p>This means, {@code u -> v => idx(v) < idx(u)}, and the reverse,
     * {@code idx(v) >= idx(v) => not(u -> v)}, which allows simplifying
     * some algorithms.
     *
     * @throws IllegalStateException If the lattice has a cycle
     */
    private List<LNode> toposort() {
        List<LNode> sorted = new ArrayList<>(nodes.size());
        for (LNode n : nodes.values()) {
            doToposort(n, sorted);
        }
        return sorted;
    }

    private void doToposort(LNode v, List<LNode> sorted) {
        if (v.topoMark == PERMANENT_TOPOMARK) {
            return;
        } else if (v.topoMark == TMP_TOPOMARK) {
            throw new IllegalStateException("This lattice has cycles");
        }

        v.topoMark = TMP_TOPOMARK;

        for (LNode w : v.succ) {
            doToposort(w, sorted);
        }

        v.topoMark = PERMANENT_TOPOMARK;
        v.idx = sorted.size();
        sorted.add(v);
    }

    @Override
    public String toString() {
        // generates a DOT representation of the lattice
        // Visualize eg at http://webgraphviz.com/
        StringBuilder sb = new StringBuilder("strict digraph {\n");
        Map<LNode, String> ids = new HashMap<>();
        int i = 0;
        for (LNode node : nodes.values()) {
            String id = "n" + i++;
            ids.put(node, id);
            sb.append(id).append(" [ shape=box, label=\"")
              .append(escapeDotString(keyToString.apply(node.key)))
              .append("\" ];\n");
        }

        for (LNode node : nodes.values()) {
            // edges
            String id = ids.get(node);
            for (LNode succ : node.succ) {
                String succId = ids.get(succ);
                sb.append(succId).append(" -> ").append(id).append(";\n");
            }
        }

        return sb.append('}').toString();
    }

    @NonNull
    public String escapeDotString(String string) {
        return string.replaceAll("\\R", "\\\n")
                     .replaceAll("\"", "\\\"");
    }

    private final class LNode { // "Lattice Node"

        private final @NonNull K key;
        // before freezing this contains the successors of a node
        // after, it contains its direct predecessors
        private PSet<LNode> succ = HashTreePSet.empty();
        private PSet<LNode> preds = HashTreePSet.empty();

        // topological state, to be reset between freeze cycles
        private int topoMark = UNDEFINED_TOPOMARK;
        private int idx = -1;

        /** Proper value associated with this node (independent of topology). */
        private @Nullable Set<V> properVal;
        /** Cached value */
        private @Nullable PSet<V> frozenVal;
        /** Transient state */
        private boolean isFullyComputed = false;

        private LNode(@NonNull K key) {
            this.key = key;
        }

        private void addProperVal(V v) {
            if (properVal == null) {
                properVal = new LinkedHashSet<>();
            }
            properVal.add(v);
        }


        PSet<V> computeValue() {
            if (frozenVal != null) {
                return frozenVal;
            }

            PSet<V> value = reduceSuccessors(new HashSet<>());
            frozenVal = value;
            return value;
        }

        private PSet<V> accStart() {
            return properVal == null ? HashTreePSet.empty() : HashTreePSet.from(properVal);
        }

        private PSet<V> reduceSuccessors(Set<LNode> seen) {
            if (frozenVal != null) {
                return frozenVal;
            }

            isFullyComputed = true;

            PSet<V> val = accStart();

            for (LNode child : preds) {
                if (seen.add(child)) {
                    val = val.plusAll(child.reduceSuccessors(seen));
                    isFullyComputed &= child.isFullyComputed;
                } else {
                    isFullyComputed = false;
                }
            }

            if (isFullyComputed) {
                this.frozenVal = val;
            }

            return val;
        }

        private void resetValue() {
            frozenVal = null;
            properVal = null;
        }

        private void resetFrozenValue() {
            frozenVal = null;
        }

        /**
         * Resets the data that was frozen in the last freeze cycle,
         * Does not clear the proper value.
         */
        private void resetTopoData() {
            topoMark = UNDEFINED_TOPOMARK;
            idx = -1;
            // since "succ" means "pred" here, we need to readd everything
//            succ = HashTreePSet.empty();
//            addSuccessors(key, this);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
