/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a property of type {@code <U>} on a datatype {@code <T>}.
 * The internal representation is a directed acyclic graph of {@code <T>},
 * ordered according to a {@link TopoOrder}. The value {@code <U>} associated
 * to a node is the recursive combination of the values of all its children,
 * plus its own value, as defined by a {@link Monoid  Monoid&lt;U&gt;}.
 *
 * <p>The {@link TopoOrder TopoOrder<T>} must generate an acyclic graph,
 * cycles are not handled by this implementation.
 *
 * <p>There is no equality relation defined on a lattice, and no
 * operation to test if an element is contained in the lattice.
 *
 * <p>A lattice may be frozen to make it read-only, when the construction
 * of the property is done. This optimizes subsequent calls to {@link #get(Object)}.
 * Once frozen a lattice may not be unfrozen.
 *
 * @param <T> Type of keys, must have a corresponding {@link TopoOrder},
 *           must implement a consistent {@link Object#equals(Object) equals} and
 *           {@link Object#hashCode() hashcode} and be immutable.
 * @param <U> Type of values, must have a corresponding {@link Monoid}
 */
class LatticeRelation<T, U> {
    /* For example, a simple lattice on types:

                 Object
                    ^
                    |
                    +
              Serializable <-------+
                    ^              |
                    |              |
                    +              |
                  Number           |
                  ^    ^           |
                  |    |           |
                  |    |           |
                  +    +           +
               Long  Integer    String

       Say now that the monoid is (emptySet(), Set.add), and that every
       node has its key as proper value. Then the lattice associates the
       set of know subtypes to each type.
     */

    private final Monoid<U> valueMonoid;
    private final TopoOrder<T> keyOrder;
    private boolean frozen;

    private final Map<T, LNode> nodes;

    /**
     * Builds a new relation with the specified monoid and topological
     * ordering.
     */
    LatticeRelation(Monoid<U> valueMonoid, TopoOrder<T> keyOrder) {
        this.valueMonoid = valueMonoid;
        this.keyOrder = keyOrder;
        nodes = new HashMap<>();
    }

    private LNode getNode(T key) {
        return nodes.computeIfAbsent(key, k -> {
            LNode n = new LNode(k);
            keyOrder.directSuccessors(k).distinct().map(this::getNode).forEach(it -> it.parents.add(n));
            return n;
        });
    }

    /**
     * Associate the value to the given key. If the key already had a
     * value, it is combined using the {@link Monoid}.
     */
    public void put(T key, U value) {
        if (frozen) {
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }
        LNode node = getNode(key);
        node.properVal = valueMonoid.apply(node.properVal, value);
    }

    /**
     * Returns the computed value for the given key, or {@link Monoid#zero()}
     * if the key is not recorded in this lattice.
     */
    @NonNull
    public U get(T key) {
        LNode n = nodes.get(key);
        return n == null ? valueMonoid.zero() : n.computeValue();
    }

    /**
     * Mark this lattice as read-only. The values of the nodes will
     * subsequently be computed at most once.
     */
    void freeze() {
        frozen = true;
    }

    private class LNode {

        private final T key;
        private final Set<LNode> parents = new LinkedHashSet<>(0);
        /** Proper value associated with this node (independent of parents). */
        private @NonNull U properVal = valueMonoid.zero();

        private @Nullable U frozenVal;

        private LNode(T key) {
            this.key = key;
        }

        U computeValue() {
            if (frozen) {
                if (frozenVal == null) {
                    frozenVal = computeVal();
                }
                return frozenVal;
            }
            return computeVal();
        }

        private U computeVal() {
            return parents.stream().map(LNode::computeValue).reduce(properVal, valueMonoid);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
