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

public class Heap<T, U> {


    private final Monoid<U> valueMonoid;
    private final TopoOrder<T> keyOrder;

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

    public int size() {
        return nodes.size();
    }

    private HNode getNode(T key) {
        return nodes.computeIfAbsent(key, k -> {
            HNode n = new HNode(k);
            keyOrder.strictParents(k).map(this::getNode).forEach(it -> it.children.add(n));
            return n;
        });
    }

    public U put(T key, U value) {
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

    public boolean containsKey(T key) {
        return nodes.containsKey(key);
    }


    private class HNode {

        private final T key;
        private final Set<HNode> children = new LinkedHashSet<>(0);
        private @Nullable U properVal;

        private HNode(T key) {
            this.key = key;
        }

        @NonNull
        U getProperVal() {
            return properVal == null ? valueMonoid.zero() : properVal;
        }

        U computeValue() {
            Stream<U> childrenVals = Stream.concat(Stream.of(getProperVal()), children.stream().map(HNode::computeValue));
            return childrenVals.reduce(valueMonoid.zero(), valueMonoid::combine);
        }

        @Override
        public String toString() {
            return "(" + key + ')';
        }
    }

}
