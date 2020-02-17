/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
 * Represents a property of type {@code <U>} on a datatype {@code <T>}.
 * The internal representation is a directed acyclic graph of {@code <T>},
 * built according to a {@link TopoOrder}. The value {@code <U>} associated
 * to a node is the recursive combination of the values of all its children,
 * plus its own value, as defined by a {@link IdMonoid  Monoid&lt;U&gt;}.
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
 * @param <T> Type of keys, must have a corresponding {@link TopoOrder},
 *            must implement a consistent {@link Object#equals(Object) equals} and
 *            {@link Object#hashCode() hashcode} and be immutable.
 * @param <U> Type of values, must have a conformant {@link IdMonoid}
 */
class LatticeRelation<T, @NonNull U> {

    // constants for the toposort
    private static final int UNDEFINED_TOPOMARK = -1;
    private static final int PERMANENT_TOPOMARK = 0;
    private static final int TMP_TOPOMARK = 1;

    // behavior parameters for this lattice
    private final IdMonoid<U> combine;
    private final IdMonoid<U> accumulate;
    private final Predicate<? super T> filter;
    private final TopoOrder<T> keyOrder;
    private final Function<? super T, String> keyToString;

    // state
    private final Map<T, LNode> nodes = new HashMap<>();
    /** Keys that have been submitted to {@link #put(Object, Object)} as of now. */
    private PSet<T> seeds = HashTreePSet.empty();

    /**
     * Value of {@link #seeds} after the last freeze cycle. If this has not changed
     * during the write phase, then the topo is already up to date and we can avoid
     * doing the computations of {@link #freezeTopo()}.
     */
    private PSet<T> lastSeeds = seeds;
    private boolean frozen;

    /**
     * Creates a new relation with the given configuration.
     *
     * @param combine     Monoid used to combine values of the predecessors of a node
     * @param accumulate  Monoid used to accumulate proper values (those added directly
     *                    through {@link #put(Object, Object)}) into the same node
     * @param keyOrder    Partial order generating the lattice
     * @param filter      Filter for nodes to keep. During the construction phase,
     *                    all nodes are in the lattice. When freezing, the lattice
     *                    is completely expanded, then only nodes satisfying this
     *                    filter are kept.
     * @param keyToString Strategy to render keys when dumping the lattice to a graph
     */
    LatticeRelation(IdMonoid<U> combine,
                    IdMonoid<U> accumulate,
                    TopoOrder<T> keyOrder,
                    Predicate<? super T> filter,
                    Function<? super T, String> keyToString) {
        this.combine = combine;
        this.accumulate = accumulate;
        this.keyOrder = keyOrder;
        this.filter = filter;
        this.keyToString = keyToString;
    }

    private LNode getOrCreateNode(T key) {
        assert key != null : "null key is not allowed";
        if (nodes.containsKey(key)) {
            return nodes.get(key);
        } else {
            LNode n = new LNode(key);
            nodes.put(key, n);
            // add all successors recursively
            addSuccessors(key, n);
            return n;
        }
    }

    private void addSuccessors(T key, LNode n) {
        keyOrder.directSuccessors(key)
                .forEachRemaining(s -> n.succ.add(this.getOrCreateNode(s)));
    }

    /**
     * Associate the value to the given key. If the key already had a
     * value, it is accumulated using the {@link #accumulate} monoid.
     */
    public void put(T key, U value) {
        if (frozen) {
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }
        seeds = seeds.plus(key);
        LNode node = getOrCreateNode(key);
        node.properVal = accumulate.apply(node.properVal, value);
    }

    /**
     * Returns the computed value for the given key, or the {@link IdMonoid#zero() zero}
     * of the {@link #combine} monoid if the key is not recorded in this lattice.
     */
    @NonNull
    public U get(T key) {
        if (!frozen) {
            throw new IllegalStateException("Lattice topology is not frozen");
        }
        LNode n = nodes.get(key);
        return n == null ? combine.zero() : n.computeValue();
    }

    /**
     * Clear values on the lattice nodes. The lattice topology is preserved.
     * Reusing the lattice for another run may avoid having to make topological
     * checks again, provided the topology is not modified.
     *
     * <p>If you want to clear the topology, use another instance.
     */
    void clearValues() {
        if (frozen) {
            // this is actually unnecessary
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }

        for (LNode value : nodes.values()) {
            value.resetValue();
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
        frozen = true; // non-thread-safe
        if (lastSeeds.equals(seeds)) {
            // no new seeds have been encountered, topo is up to date
            return;
        }

        for (LNode node : new HashSet<>(nodes.values())) {
            node.resetFrozenData();
        }

        int n = nodes.size();

        // topological sort
        List<LNode> lst = toposort();
        // also LNode#idx is set to the index in the list

        // here path is an adjacency matrix
        // (ie path[i][j] means "j is a direct successor of i")

        // since nodes are toposorted,
        //  path[i][j] => i < j
        // so we can avoid being completely cubic

        boolean[] kept = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (filter.test(lst.get(i).key)) {
                kept[i] = true;
            }
        }

        // kept[i] means the node is not pruned

        for (LNode jn : lst) {
            final int j = jn.idx;
            if (!kept[j]) {
                // node j is pruned, so for all paths i -> j -> k,
                // we must add a path i -> k, otherwise path is lost

                // This works in one pass because nodes are toposorted
                // Eg for i -> j -> k -> l, where both j and k are filtered out,
                // this will first add i -> k, then i -> l, because i -> k already exists
                for (LNode kn : jn.succ) {
                    for (int i = j + 1; i < n; i++) { // find all i s.t. i -> j
                        LNode in = lst.get(i);
                        if (in.succ.contains(jn)) {
                            in.succ.add(kn);
                        }
                    }
                }
            }
        }

        // transitive reduction
        for (LNode in : lst) {
            for (LNode jn : in.succ) {
                if (kept[jn.idx]) { // only if j is kept
                    for (LNode kn : jn.succ) {
                        // i -> j -> k
                        // delete i -> k
                        in.succ.remove(kn);
                    }
                }
            }
        }

        // assign predecessors to all nodes
        // this inverts the graph
        for (int i = 0; i < n; i++) {
            LNode in = lst.get(i);

            in.succ.clear();

            if (!kept[i]) {
                nodes.remove(in.key);
                continue;
            }

            for (int j = i + 1; j < n; j++) {
                LNode jn = lst.get(j);
                if (kept[j] && jn.succ.contains(in)) {
                    // succ means "pred" now
                    in.succ.add(jn);
                }
            }
        }
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
                sb.append(id).append(" -> ").append(succId).append(";\n");
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

        private final @NonNull T key;
        // before freezing this contains the successors of a node
        // after, it contains its direct predecessors
        private final Set<LNode> succ = new LinkedHashSet<>(0);

        // topological state, to be reset between freeze cycles
        private int topoMark = UNDEFINED_TOPOMARK;
        private int idx = -1;

        /** Proper value associated with this node (independent of topology). */
        private @NonNull U properVal = accumulate.zero();
        /** Cached value */
        private @Nullable U frozenVal;
        /** Transient state */
        private boolean isFullyComputed = false;

        private LNode(@NonNull T key) {
            this.key = key;
        }

        U computeValue() {
            if (frozenVal != null) {
                return frozenVal;
            }

            U value = reduceSuccessors(new HashSet<>());
            frozenVal = value;
            return value;
        }

        private U reduceSuccessors(Set<LNode> seen) {
            if (frozenVal != null) {
                return frozenVal;
            }

            isFullyComputed = true;

            // we use the #combine monoid here, but properVal was made from #accumulate
            // so we lift the proper val to the representation of #combine
            U val = combine.lift(properVal);

            for (LNode child : succ) {
                if (seen.add(child)) {
                    U vs = child.reduceSuccessors(seen);
                    val = combine.apply(val, vs);
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
            properVal = accumulate.zero();
        }

        /**
         * Resets the data that was frozen in the last freeze cycle,
         * Does not clear the proper value.
         */
        private void resetFrozenData() {
            topoMark = UNDEFINED_TOPOMARK;
            idx = -1;
            frozenVal = null;
            // since "succ" means "pred" here, we need to readd everything
            succ.clear();
            addSuccessors(key, this);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
