/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a property of type {@code <U>} on a datatype {@code <T>}.
 * The internal representation is a directed acyclic graph of {@code <T>},
 * built according to a {@link TopoOrder}. The value {@code <U>} associated
 * to a node is the recursive combination of the values of all its children,
 * plus its own value, as defined by a {@link Monoid  Monoid&lt;U&gt;}.
 *
 * <p>An instance has two states:
 * <ul>
 * <li>Read-only: mutation is impossible, but querying data is.
 * <li>Write-only: querying data is impossible, but mutating the structure is.
 * </ul>
 * If the internal structure is not changed during a write phase,
 * most of the work may be avoided when {@linkplain #freezeTopo() freezing}
 * the structure.
 *
 * <p>The {@link TopoOrder TopoOrder<T>} must generate an acyclic graph,
 * this implementation handles cycles by throwing an exception upon freezing.
 *
 * <p>There is no equality relation defined on a lattice, and no
 * operation to test if an element is contained in the lattice.
 *
 * <p>This implementation is not thread-safe.
 *
 * @param <T> Type of keys, must have a corresponding {@link TopoOrder},
 *           must implement a consistent {@link Object#equals(Object) equals} and
 *           {@link Object#hashCode() hashcode} and be immutable.
 * @param <U> Type of values, must have a corresponding {@link Monoid}
 */
class LatticeRelation<T, @NonNull U> {

    private static final int UNDEFINED_TOPOMARK = -1;
    private static final int PERMANENT_TOPOMARK = 0;
    private static final int TMP_TOPOMARK = 1;

    /** Used to combine values of the predecessors of a node. */
    private final Monoid<U> combine;
    /** Used to accumulate proper values into the same node. */
    private final Monoid<U> accumulate;

    private final TopoOrder<T> keyOrder;
    private boolean frozen;
    private boolean up2DateTopo;

    private final Map<T, LNode> nodes;

    LatticeRelation(Monoid<U> combine, TopoOrder<T> keyOrder) {
        this(combine, combine, keyOrder);
    }

    LatticeRelation(Monoid<U> combine, Monoid<U> accumulate, TopoOrder<T> keyOrder) {

        this.combine = combine;
        this.accumulate = accumulate;
        this.keyOrder = keyOrder;
        nodes = new HashMap<>();
    }

    private LNode getOrCreateNode(T key) {
        if (nodes.containsKey(key)) {
            return nodes.get(key);
        } else {
            up2DateTopo = false;
            LNode n = new LNode(key);
            nodes.put(key, n);
            // add all successors recursively
            keyOrder.directSuccessors(key).distinct().map(this::getOrCreateNode).forEach(n.succ::add);
            return n;
        }
    }

    /**
     * Associate the value to the given key. If the key already had a
     * value, it is accumulated using the {@link #accumulate} monoid.
     */
    public void put(T key, U value) {
        if (frozen) {
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }
        LNode node = getOrCreateNode(key);
        node.properVal = accumulate.apply(node.properVal, value);
    }

    /**
     * Returns the computed value for the given key, or the {@link Monoid#zero() zero}
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

    // test only
    Map<T, LNode> getNodes() {
        return nodes;
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
            value.frozenVal = null;
            value.properVal = accumulate.zero();
        }
    }

    /**
     * Mark this instance as write-only. Mutating the topology becomes
     * possible, but querying data is impossible.
     */
    void unfreezeTopo() {
        frozen = false;
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
        frozen = true; // TODO non-thread-safe
        if (up2DateTopo) {
            // topology up to date
            return;
        }

        for (LNode node : nodes.values()) {
            node.reset();
        }

        /*
            We need to do all this shit because there may be diamonds
            in the lattice, in which case some nodes are reachable through
            several paths and we would risk combining their proper values
            several times.
         */

        int n = nodes.size();

        // topological sort
        List<LNode> lst = toposort();

        for (int i = 0; i < lst.size(); i++) {
            lst.get(i).idx = i;
        }

        // todo use a BitSet[]
        boolean[][] path = new boolean[n][n];

        for (LNode k : lst) {
            for (LNode t : k.succ) {
                if (k.idx != t.idx) {
                    // ignore self loop
                    path[k.idx][t.idx] = true;
                }
            }
        }

        // here path is an adjacency matrix
        // (ie path[i][j] means "j is a direct successor of i")

        // we turn it into a path matrix

        // since nodes are toposorted,
        //  path[i][j] => i < j
        // so we can avoid being completely cubic

        // transitive closure
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (path[i][j]) {
                    for (int k = j + 1; k < n; k++) {
                        if (path[j][k]) {
                            // i -> j -> k
                            path[i][k] = true;
                        }
                    }
                }
            }
        }

        // now path[i][j] means "j is reachable from i"


        // diamond detection
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (path[i][j]) {
                    for (int k = j + 1; k < n; k++) {
                        if (path[j][k]) {
                            // i -> j -> k
                            // Look for an "m" s.t.
                            // i -> m -> k
                            for (int m = i + 1; m < n; m++) {
                                if (m != j && !path[j][m] && !path[m][j] && path[i][m] && path[m][k]) {
                                    lst.get(k).hasDiamond = true;
                                    for (int o = k; o < n; o++) {
                                        if (path[k][o]) {
                                            lst.get(o).hasDiamond = true;
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // transitive reduction
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (path[i][j]) {
                    for (int k = j + 1; k < n; k++) {
                        if (path[j][k]) {
                            // i -> j -> k
                            path[i][k] = false;
                        }
                    }
                }
            }
        }


        // assign predecessors to all nodes
        for (int i = 0; i < n; i++) {
            LNode ln = lst.get(i);
            ln.succ.clear();

            for (int j = 0; j < i; j++) {
                if (path[j][i]) {
                    // succ means "pred" now
                    ln.succ.add(lst.get(j));
                }
            }
        }

        up2DateTopo = true;
    }


    /**
     * Returns a list in which the vertices of this graph are sorted
     * in the following way:
     *
     * if there exists an edge u -> v, then indexOf(u) &lt; indexOf(v) in the list.
     *
     * @throws IllegalStateException If the lattice has a cycle
     */
    private List<LNode> toposort() {
        Deque<LNode> sorted = new ArrayDeque<>(nodes.size());
        for (LNode n : nodes.values()) {
            doToposort(n, sorted);
        }
        return new ArrayList<>(sorted);
    }

    private void doToposort(LNode v, Deque<LNode> sorted) {
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
        sorted.addFirst(v);
    }

    //test only
    class LNode {

        // topological state, to be reset

        // before freezing this contains the successors of a node
        // after, it contains its direct predecessors
        private final Set<LNode> succ = new LinkedHashSet<>(0);
        boolean hasDiamond = false;
        private int topoMark = UNDEFINED_TOPOMARK;
        private int idx = -1;


        private final T key;


        /** Proper value associated with this node (independent of topology). */
        private @NonNull U properVal = accumulate.zero();
        /** Cached value. */
        private @Nullable U frozenVal;

        private LNode(T key) {
            this.key = key;
        }

        U computeValue() {
            if (frozenVal == null) {
                frozenVal = computeVal();
            }
            return frozenVal;
        }

        private U computeVal() {
            // we use the combine monoid here

            if (hasDiamond) {
                // then we can't reuse values of children, because some
                // descendants are reachable through several paths
                return descendantsAndSelf().distinct()
                                           .map(it -> it.properVal)
                                           .reduce(combine.zero(), combine);
            }
            return succ.stream().map(LNode::computeValue).reduce(properVal, combine);
        }

        private Stream<LNode> descendantsAndSelf() {
            return Stream.concat(Stream.of(this), succ.stream().flatMap(LNode::descendantsAndSelf));
        }


        private void reset() {
            topoMark = UNDEFINED_TOPOMARK;
            idx = -1;
            hasDiamond = false;
            frozenVal = null;
            succ.clear();
            keyOrder.directSuccessors(key).distinct().map(LatticeRelation.this::getOrCreateNode).forEach(succ::add);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }

        @Override
        public boolean equals(Object data) {
            if (this == data) {
                return true;
            }
            if (data == null || getClass() != data.getClass()) {
                return false;
            }
            LNode lNode = (LNode) data;
            return Objects.equals(key, lNode.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

}
