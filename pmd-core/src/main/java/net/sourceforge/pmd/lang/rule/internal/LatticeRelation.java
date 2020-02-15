/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
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
    private final Function<? super T, String> keyToString;
    private final Map<T, LNode> nodes;
    private boolean frozen;
    private boolean up2DateTopo;

    LatticeRelation(Monoid<U> combine, TopoOrder<T> keyOrder) {
        this(combine, combine, keyOrder, Object::toString);
    }

    LatticeRelation(Monoid<U> combine, Monoid<U> accumulate, TopoOrder<T> keyOrder, Function<? super T, String> keyToString) {
        this.combine = combine;
        this.accumulate = accumulate;
        this.keyOrder = keyOrder;
        this.keyToString = keyToString;
        nodes = new HashMap<>();
    }

    private LNode getOrCreateNode(T key) {
        assert key != null : "null key is not allowed";
        if (nodes.containsKey(key)) {
            return nodes.get(key);
        } else {
            up2DateTopo = false;
            LNode n = new LNode(key);
            nodes.put(key, n);
            // add all successors recursively
            addSuccessors(key, n);
            return n;
        }
    }

    private void addSuccessors(T key, LNode n) {
        Set<T> seen = new HashSet<>();
        keyOrder.directSuccessors(key)
                .forEachRemaining(s -> {
                    if (seen.add(s)) { // only add distinct ones
                        n.succ.add(this.getOrCreateNode(s));
                    }
                });
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
            value.resetValue();
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
        frozen = true; // non-thread-safe
        if (up2DateTopo) {
            // topology up to date
            return;
        }

        for (LNode node : nodes.values()) {
            node.resetFrozenData();
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
        // this inverts the graph
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

    @Override
    public String toString() {
        // generates a DOT representation of the lattice
        // Visualize eg
        // https://dreampuf.github.io/GraphvizOnline/#strict%20digraph%20%7B%0An0%20%5B%20shape%3Dbox%3B%20label%3D%22SignedNode%22%20%5D%3B%0An1%20%5B%20shape%3Ddiamond%3B%20label%3D%22JavaNode%22%20%5D%3B%0An2%20%5B%20shape%3Dbox%3B%20label%3D%22ASTFieldDeclaration%22%20%5D%3B%0An3%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJavaAccessNode%22%20%5D%3B%0An4%20%5B%20shape%3Dbox%3B%20label%3D%22ASTVariableDeclarator%22%20%5D%3B%0An5%20%5B%20shape%3Dbox%3B%20label%3D%22ASTCompilationUnit%22%20%5D%3B%0An6%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractNode%22%20%5D%3B%0An7%20%5B%20shape%3Dbox%3B%20label%3D%22ASTClassOrInterfaceBodyDeclaration%22%20%5D%3B%0An8%20%5B%20shape%3Dbox%3B%20label%3D%22ASTPrimitiveType%22%20%5D%3B%0An9%20%5B%20shape%3Ddiamond%3B%20label%3D%22Node%22%20%5D%3B%0An10%20%5B%20shape%3Dbox%3B%20label%3D%22ASTClassOrInterfaceBody%22%20%5D%3B%0An11%20%5B%20shape%3Ddiamond%3B%20label%3D%22TextAvailableNode%22%20%5D%3B%0An12%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJavaAnnotatableNode%22%20%5D%3B%0An13%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractAnyTypeDeclaration%22%20%5D%3B%0An14%20%5B%20shape%3Ddiamond%3B%20label%3D%22TypeNode%22%20%5D%3B%0An15%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractTypeBodyDeclaration%22%20%5D%3B%0An16%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJavaTypeNode%22%20%5D%3B%0An17%20%5B%20shape%3Dbox%3B%20label%3D%22RootNode%22%20%5D%3B%0An18%20%5B%20shape%3Dbox%3B%20label%3D%22JavaQualifiableNode%22%20%5D%3B%0An19%20%5B%20shape%3Ddiamond%3B%20label%3D%22ScopedNode%22%20%5D%3B%0An20%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJjtreeNode%22%20%5D%3B%0An21%20%5B%20shape%3Dbox%3B%20label%3D%22Iterable%22%20%5D%3B%0An22%20%5B%20shape%3Dbox%3B%20label%3D%22Dimensionable%22%20%5D%3B%0An23%20%5B%20shape%3Ddiamond%3B%20label%3D%22Object%22%20%5D%3B%0An24%20%5B%20shape%3Dbox%3B%20label%3D%22Annotatable%22%20%5D%3B%0An25%20%5B%20shape%3Dbox%3B%20label%3D%22ASTClassOrInterfaceDeclaration%22%20%5D%3B%0An26%20%5B%20shape%3Ddiamond%3B%20label%3D%22AccessNode%22%20%5D%3B%0An27%20%5B%20shape%3Dbox%3B%20label%3D%22CanSuppressWarnings%22%20%5D%3B%0An28%20%5B%20shape%3Dbox%3B%20label%3D%22ASTAnyTypeDeclaration%22%20%5D%3B%0An29%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJavaAccessTypeNode%22%20%5D%3B%0An30%20%5B%20shape%3Dbox%3B%20label%3D%22ASTVariableDeclaratorId%22%20%5D%3B%0An31%20%5B%20shape%3Dbox%3B%20label%3D%22QualifiableNode%22%20%5D%3B%0An32%20%5B%20shape%3Dbox%3B%20label%3D%22AbstractJavaNode%22%20%5D%3B%0An33%20%5B%20shape%3Dbox%3B%20label%3D%22ASTAnyTypeBodyDeclaration%22%20%5D%3B%0An34%20%5B%20shape%3Dbox%3B%20label%3D%22ASTTypeDeclaration%22%20%5D%3B%0An35%20%5B%20shape%3Dbox%3B%20label%3D%22ASTType%22%20%5D%3B%0An2%20-%3E%20n0%3B%0An33%20-%3E%20n1%3B%0An14%20-%3E%20n1%3B%0An24%20-%3E%20n1%3B%0An32%20-%3E%20n1%3B%0An29%20-%3E%20n3%3B%0An20%20-%3E%20n6%3B%0An31%20-%3E%20n9%3B%0An17%20-%3E%20n9%3B%0An26%20-%3E%20n9%3B%0An6%20-%3E%20n9%3B%0An11%20-%3E%20n9%3B%0An19%20-%3E%20n9%3B%0An0%20-%3E%20n9%3B%0An20%20-%3E%20n11%3B%0An1%20-%3E%20n11%3B%0An3%20-%3E%20n12%3B%0An25%20-%3E%20n13%3B%0An28%20-%3E%20n14%3B%0An16%20-%3E%20n14%3B%0An29%20-%3E%20n14%3B%0An7%20-%3E%20n15%3B%0An35%20-%3E%20n16%3B%0An34%20-%3E%20n16%3B%0An30%20-%3E%20n16%3B%0An8%20-%3E%20n16%3B%0An5%20-%3E%20n16%3B%0An4%20-%3E%20n16%3B%0An5%20-%3E%20n17%3B%0An28%20-%3E%20n18%3B%0An1%20-%3E%20n19%3B%0An32%20-%3E%20n20%3B%0An2%20-%3E%20n21%3B%0An30%20-%3E%20n22%3B%0An8%20-%3E%20n22%3B%0An2%20-%3E%20n22%3B%0An27%20-%3E%20n23%3B%0An21%20-%3E%20n23%3B%0An22%20-%3E%20n23%3B%0An9%20-%3E%20n23%3B%0An12%20-%3E%20n24%3B%0An28%20-%3E%20n26%3B%0An3%20-%3E%20n26%3B%0An34%20-%3E%20n27%3B%0An7%20-%3E%20n27%3B%0An13%20-%3E%20n28%3B%0An13%20-%3E%20n29%3B%0An2%20-%3E%20n29%3B%0An18%20-%3E%20n31%3B%0An10%20-%3E%20n32%3B%0An15%20-%3E%20n32%3B%0An16%20-%3E%20n32%3B%0An12%20-%3E%20n32%3B%0An15%20-%3E%20n33%3B%0A%7D
        StringBuilder sb = new StringBuilder("strict digraph {\n");
        Map<LNode, String> ids = new HashMap<>();
        int i = 0;
        for (LNode node : nodes.values()) {
            String id = "n" + i++;
            ids.put(node, id);
            String shape = node.hasDiamond ? "diamond" : "box";
            sb.append(id).append(" [ shape=").append(shape)
              .append(", label=\"").append(escapeDotString(keyToString.apply(node.key)))
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

    //test only
    final class LNode {

        private final @NonNull T key;
        // before freezing this contains the successors of a node
        // after, it contains its direct predecessors
        private final Set<LNode> succ = new LinkedHashSet<>(0);

        // topological state, to be reset between freeze cycles
        boolean hasDiamond = false;
        private int topoMark = UNDEFINED_TOPOMARK;
        private int idx = -1;

        /** Proper value associated with this node (independent of topology). */
        private @NonNull U properVal = accumulate.zero();
        /** Cached value */
        private @Nullable U frozenVal;

        private LNode(@NonNull T key) {
            this.key = key;
        }

        U computeValue() {
            if (frozenVal == null) {
                frozenVal = computeVal();
            }
            return frozenVal;
        }

        private U computeVal() {
            // we use the combine monoid here, but properVal's default value is accumulate#zero
            U zero = combine.apply(combine.zero(), properVal);

            if (hasDiamond) {
                // then we can't reuse values of children, because some
                // descendants are reachable through several paths
                return descendantsAndSelf().distinct().map(it -> it.properVal).reduce(zero, combine);
            }

            return succ.stream().map(LNode::computeValue).reduce(zero, combine);
        }

        private Stream<LNode> descendantsAndSelf() {
            return Stream.concat(Stream.of(this), succ.stream().flatMap(LNode::descendantsAndSelf));
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
            hasDiamond = false;
            frozenVal = null;
            succ.clear();
            addSuccessors(key, this);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }

        private LatticeRelation<T, U> getOwner() {
            return LatticeRelation.this;
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
            return getOwner() == lNode.getOwner()
                && key.equals(lNode.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

}
