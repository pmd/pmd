/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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

/**
 * Represents a property of type {@code Set<U>} on a datatype {@code <T>}.
 * The internal representation is a directed acyclic graph of {@code <T>},
 * built according to a {@link TopoOrder}. The value associated to a node
 * is the recursive union of the values of all the nodes it covers, plus
 * its own value.
 *
 * <p><b>Limitations</b>
 * <ul>
 * <li>The {@link TopoOrder TopoOrder<T>} must generate an acyclic graph,
 *  this implementation handles cycles by throwing an exception upon freezing.
 * <li>There is no equality relation defined on a lattice, and no
 *   operation to test if an element is contained in the lattice.
 * <li>A lattice can only grow
 * </ul>
 * <p>
 *
 * @param <K> Type of keys, must have a corresponding {@link TopoOrder},
 *            must implement a consistent {@link Object#equals(Object) equals} and
 *            {@link Object#hashCode() hashcode} and be immutable.
 * @param <V> Type of values
 */
class LatticeRelation<K, @NonNull V> {

    private final Predicate<? super K> filter;
    private final TopoOrder<K> keyOrder;
    private final Function<? super K, String> keyToString;

    // state
    private final Map<K, LNode> nodes = new HashMap<>();

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
        this.filter = querySet::contains;
        this.keyToString = keyToString;

        for (K k : querySet) {
            put(k, null);
        }

        // since we know in advance which nodes are in the lattice, we
        // can perform this optimisation
        transitiveReduction();
    }

    /**
     * Follows all paths starting from the key (all nodes that are greater
     * w.r.t. the ordering). New nodes are created if they match the filter.
     * Existing nodes are invalidated.
     */
    private void addSucc(@Nullable LNode pred, K key, V val) {
        if (filter.test(key)) {
            if (nodes.containsKey(key)) {
                LNode n = nodes.get(key);
                if (pred == null) {
                    n.addProperVal(val); // propagate new val to all successors, only if it was pruned
                }
                link(pred, n); // make sure the predecessor is linked
                invalidateSuccessors(n);
            } else {
                LNode n = new LNode(key);
                n.addProperVal(val);
                nodes.put(key, n);
                link(pred, n);

                Iterator<K> successors = keyOrder.directSuccessors(key);
                while (successors.hasNext()) {
                    addSucc(n, successors.next(), val);
                }
            }
        } else {
            // even if we didn't create a node, we carry on with the successors
            // (to set pred, or invalidate value)
            Iterator<K> successors = keyOrder.directSuccessors(key);
            while (successors.hasNext()) {
                addSucc(pred, successors.next(), val);
            }
        }
    }

    private void transitiveReduction() {

        // look for chains i -> j -> k, and delete i -> k if it exists

        for (LNode j : nodes.values()) {
            for (LNode i : j.preds) {
                if (i != j) {
                    for (LNode k : j.succ) {
                        // i -> j -> k
                        if (k != j) {
                            if (i.succ.contains(k)) {
                                // i -> k
                                i.succ = i.succ.minus(k);
                                k.preds = k.preds.minus(i);
                            }
                        }
                    }
                }
            }
        }

    }

    private void invalidateSuccessors(LNode node) {
        node.isValueUpToDate = false;
        for (LNode s : node.succ) {
            invalidateSuccessors(s);
        }
    }

    private void link(LNode pred, LNode succ) {
        if (pred == null || succ == null) {
            return;
        }
        pred.succ = pred.succ.plus(succ);
        succ.preds = succ.preds.plus(pred);
    }

    /**
     * Adds the value to the given key, unless it is null.
     */
    public void put(K key, V value) {
        AssertionUtil.requireParamNotNull("key", key);
        addSucc(null, key, value);
    }

    /**
     * Returns the computed value for the given key, or an empty set.
     */
    @NonNull
    public PSet<V> get(K key) {
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
        for (LNode value : nodes.values()) {
            value.resetValue();
        }
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

        private final @NonNull K key;
        private PSet<LNode> succ = HashTreePSet.empty();
        private PSet<LNode> preds = HashTreePSet.empty();

        /** Proper value associated with this node (independent of topology). */
        private @Nullable Set<V> properVal;
        /** Cached value */
        private @Nullable PSet<V> frozenVal;

        private boolean isValueUpToDate = false;

        private LNode(@NonNull K key) {
            this.key = key;
        }

        private void addProperVal(V v) {
            if (v == null) {
                return;
            } else if (properVal == null) {
                properVal = new LinkedHashSet<>();
            }
            properVal.add(v);
        }


        PSet<V> computeValue() {
            if (frozenVal != null && isValueUpToDate) {
                return frozenVal;
            }

            PSet<V> value = reduceSuccessors(new HashSet<>());
            frozenVal = value;
            isValueUpToDate = true;
            return value;
        }

        private PSet<V> accStart() {
            return properVal == null ? HashTreePSet.empty() : HashTreePSet.from(properVal);
        }

        private PSet<V> reduceSuccessors(Set<LNode> seen) {
            if (frozenVal != null && isValueUpToDate) {
                return frozenVal;
            }

            isValueUpToDate = true;

            PSet<V> val = accStart();

            for (LNode child : preds) {
                if (seen.add(child)) {
                    val = val.plusAll(child.reduceSuccessors(seen));
                    isValueUpToDate &= child.isValueUpToDate;
                } else {
                    isValueUpToDate = false;
                }
            }

            if (isValueUpToDate) {
                this.frozenVal = val;
            }

            return val;
        }

        private void resetValue() {
            frozenVal = null;
            properVal = null;
            isValueUpToDate = false;
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
