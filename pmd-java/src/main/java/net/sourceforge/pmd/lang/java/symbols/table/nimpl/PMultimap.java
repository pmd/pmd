/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.nimpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

/**
 * A persistent multimap type, efficient if the single-value case is the
 * most common.
 */
class PMultimap<K, V> {

    @SuppressWarnings("rawtypes")
    private static final PMultimap EMPTY = new PMultimap<>(HashTreePMap.empty());

    private final PMap<K, ConsPStack<V>> map;

    private PMultimap(PMap<K, ConsPStack<V>> map) {
        this.map = map;
    }

    public @NonNull List<V> get(K k) {
        return getInternal(map, k);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public PMultimap<K, V> appendValue(K k, V v) {
        return new PMultimap<>(appendV(map, k, v));
    }

    public PMultimap<K, V> appendAll(PMultimap<K, ? extends V> map) {
        PMap<K, ConsPStack<V>> m2 = (PMap) map.map;
        // iterate on the smallest one
        return this.map.size() < m2.size() ? new PMultimap<>(mergeMaps(this.map, m2))
                                           : new PMultimap<>(mergeMaps(m2, this.map));
    }

    // TODO maybe use builder instead
    private PMap<K, ConsPStack<V>> mergeMaps(PMap<K, ConsPStack<V>> m1,
                                             PMap<K, ConsPStack<V>> m2) {
        PMap<K, ConsPStack<V>> newMap = m2;
        for (K k : m1.keySet()) {
            ConsPStack<V> curV = getInternal(newMap, k);
            ConsPStack<V> otherV = getInternal(m1, k);
            if (curV.isEmpty()) {
                newMap = newMap.plus(k, otherV);
            } else {
                if (curV.size() < otherV.size()) {
                    ConsPStack<V> tmp = curV;
                    curV = otherV;
                    otherV = tmp;
                }
                newMap = newMap.plus(k, curV.plusAll(otherV));
            }
        }
        return newMap;
    }

    public PMultimap<K, V> appendAllGroupingBy(Iterable<? extends V> values,
                                               Function<? super V, ? extends K> keyExtractor) {
        PMap<K, ConsPStack<V>> newMap = this.map;
        for (V v : values) {
            K k = keyExtractor.apply(v);
            newMap = appendV(newMap, k, v);
        }
        return new PMultimap<>(newMap);
    }

    public PMultimap<K, V> replaceValue(K k, V v) {
        return new PMultimap<>(map.plus(k, ConsPStack.singleton(v)));
    }

    public <A, R> R overrideWith(final PMultimap<K, V> otherMap,
                                 final A zero,
                                 final BiFunction<A, K, A> onOverride,
                                 final BiFunction<PMultimap<K, V>, A, R> finisher) {

        PMap<K, ConsPStack<V>> finalMap = otherMap.map;

        A a = zero;
        for (K k : this.map.keySet()) {
            if (otherMap.containsKey(k)) {
                a = onOverride.apply(a, k);
            } else {
                finalMap = finalMap.plus(k, this.map.get(k));
            }
        }
        return finisher.apply(new PMultimap<>(finalMap), a);
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public boolean containsKey(K v) {
        return map.containsKey(v);
    }

    public static <K, V> PMultimap<K, V> singleton(K k, V v) {
        return PMultimap.<K, V>empty().appendValue(k, v);
    }

    public static <K, V> PMultimap<K, V> groupBy(Iterable<? extends V> values,
                                                 Function<? super V, ? extends K> keyExtractor) {
        Builder<K, V> builder = newBuilder();
        for (V v : values) {
            builder.appendValue(keyExtractor.apply(v), v);
        }
        return builder.build();
    }

    public static <I, K, V> PMultimap<K, V> groupBy(Iterable<? extends I> values,
                                                    Function<? super I, ? extends K> keyExtractor,
                                                    Function<? super I, ? extends V> valueExtractor) {
        Builder<K, V> builder = newBuilder();
        for (I i : values) {
            builder.appendValue(keyExtractor.apply(i), valueExtractor.apply(i));
        }
        return builder.build();
    }

    private static <K, V> PMap<K, ConsPStack<V>> appendV(PMap<K, ConsPStack<V>> map, K k, V v) {
        return map.plus(k, getInternal(map, k).plus(v));
    }

    private static <K, V> @NonNull ConsPStack<V> getInternal(PMap<K, ConsPStack<V>> map, K k) {
        ConsPStack<V> vs = map.get(k);
        return vs == null ? ConsPStack.empty() : vs;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> PMultimap<K, V> empty() {
        return EMPTY;
    }

    public static <K, V> Builder<K, V> newBuilder() {
        return new Builder<>();
    }

    public static class Builder<K, V> {

        private final HashMap<K, ConsPStack<V>> map;

        private Builder() {
            map = new HashMap<>();
        }

        public Builder<K, V> replaceValue(K key, V elt) {
            map.put(key, ConsPStack.singleton(elt));
            return this;
        }

        public Builder<K, V> appendValue(K key, V v) {
            map.compute(key, (k, oldV) -> oldV == null ? ConsPStack.singleton(v) : oldV.plus(v));
            return this;
        }

        @SuppressWarnings( {"unchecked", "rawtypes"})
        public Map<K, List<V>> getMap() {
            return (Map) map;
        }

        public PMultimap<K, V> build() {
            return isEmpty() ? empty() : new PMultimap<>(HashTreePMap.from(map));
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }
    }

}
