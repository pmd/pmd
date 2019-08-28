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
 * A heap is a kind of structured map. The internal representation is a lattice
 * of {@code <T>}, ordered according to a {@link TopoOrder}. The value
 * {@code <U>} associated to a node is the recursive combination of the values of
 * all its children, plus its own value, as defined by a {@link Monoid}.
 *
 * <p>Despite the resemblance to the {@link Map} interface, a heap doesn't follow its contract.
 * <ul>
 * <li>{@link #get(Object) get} doesn't necessarily reflect values previously
 * {@link #put(Object, Object) put} into the heap. In fact it never returns
 * null, rather using the {@link Monoid#zero() zero} element.</li>
 * <li>A heap does not *contain* elements, rather it represents a relation
 * on the elements. For that reason there is no equality relation defined
 * on a heap, and no operation to test if a key is contained in the heap.
 * </li>
 * </ul>
 *
 * @param <T> Type of keys, must have a corresponding {@link TopoOrder}
 * @param <U> Type of values, must have a corresponding {@link Monoid}
 */
public class Heap<T, U> {

    private final Monoid<U> valueMonoid;
    private final TopoOrder<T> keyOrder;
    private boolean frozen;

    private final Map<T, HNode> nodes;

    public Heap(Monoid<U> valueMonoid, TopoOrder<T> keyOrder, int initialCapacity) {
        this.valueMonoid = valueMonoid;
        this.keyOrder = keyOrder;
        nodes = new HashMap<>(initialCapacity);
    }

    public Heap(Monoid<U> valueMonoid, TopoOrder<T> keyOrder) {
        this.valueMonoid = valueMonoid;
        this.keyOrder = keyOrder;
        nodes = new HashMap<>();
    }

    private HNode getNode(T key) {
        return nodes.computeIfAbsent(key, k -> {
            HNode n = new HNode(k);
            keyOrder.strictParents(k).map(this::getNode).forEach(it -> it.children.add(n));
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
            throw new IllegalStateException("A frozen heap may not be mutated");
        }
        HNode node = getNode(key);
        U p = node.getProperVal();
        node.properVal = valueMonoid.combine(p, value);
        return p;
    }

    /**
     * Returns the computed value for the given key, or {@link Monoid#zero()}
     * if the key is not recorded in this heap.
     */
    @NonNull
    public U get(T key) {
        HNode n = nodes.get(key);
        return n == null ? valueMonoid.zero() : n.computeValue();
    }

    /**
     * Mark this heap as read-only. Values of the nodes will subsequently
     * be computed at most once.
     */
    public void freeze() {
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
