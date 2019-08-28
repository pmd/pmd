/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

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
public class LatticeRelation<T, U> {

    private final Monoid<U> valueMonoid;
    private final TopoOrder<T> keyOrder;
    private boolean frozen;

    private final Map<T, HNode> nodes;

    /**
     * Builds a new relation with the specified monoid and topological
     * ordering.
     */
    public LatticeRelation(Monoid<U> valueMonoid, TopoOrder<T> keyOrder) {
        this.valueMonoid = valueMonoid;
        this.keyOrder = keyOrder;
        nodes = new HashMap<>();
    }

    private HNode getNode(T key) {
        return nodes.computeIfAbsent(key, k -> {
            HNode n = new HNode(k);
            keyOrder.directSuccessors(k).map(this::getNode).forEach(it -> it.children.add(n));
            return n;
        });
    }

    /**
     * Associate the value to the given key. If the key already had a
     * value, it is combined using the {@link Monoid}.
     *
     * @return The previous value
     */
    public U put(T key, U value) {
        if (frozen) {
            throw new IllegalStateException("A frozen lattice may not be mutated");
        }
        HNode node = getNode(key);
        U p = node.getProperVal();
        node.properVal = valueMonoid.combine(p, value);
        return p;
    }

    /**
     * Returns the computed value for the given key, or {@link Monoid#zero()}
     * if the key is not recorded in this lattice.
     */
    @NonNull
    public U get(T key) {
        HNode n = nodes.get(key);
        return n == null ? valueMonoid.zero() : n.computeValue();
    }

    /**
     * Mark this lattice as read-only. The values of the nodes will
     * subsequently be computed at most once.
     */
    void freeze() {
        frozen = true;
    }

    private class HNode {

        private final T key;
        private final Set<HNode> children = new LinkedHashSet<>(0);
        private @Nullable U properVal;

        private @Nullable U frozenVal;

        private HNode(T key) {
            this.key = key;
        }

        @NonNull
        U getProperVal() {
            return properVal == null ? valueMonoid.zero() : properVal;
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
            Stream<U> childrenVals = Stream.concat(Stream.of(getProperVal()), children.stream().map(HNode::computeValue));
            return childrenVals.reduce(valueMonoid.zero(), valueMonoid::combine);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
