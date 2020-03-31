/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal;

import java.util.List;
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

    public PMultimap<K, V> appendValue(K k, V v) {
        return new PMultimap<>(appendV(map, k, v));
    }

    public PMultimap<K, V> replaceValue(K k, V v) {
        return new PMultimap<>(map.plus(k, ConsPStack.singleton(v)));
    }

    public PMultimap<K, V> appendAllGroupingBy(Iterable<? extends V> values,
                                               Function<? super V, ? extends K> keyExtractor) {
        PMap<K, ConsPStack<V>> newMap = this.map;
        for (V v : values) {
            K k = keyExtractor.apply(v);
            newMap = appendV(newMap, k, v);
        }
        return new PMultimap<>(map);
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

    public static <K, V> PMultimap<K, V> groupBy(Iterable<? extends V> values,
                                                 Function<? super V, ? extends K> keyExtractor) {
        return PMultimap.<K, V>empty().appendAllGroupingBy(values, keyExtractor);
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

    //    public static <K, V> Builder<K, V> newBuilder() {
    //        return new Builder<>();
    //    }
    //
    //    public static class Builder<K, V> {
    //
    //        private final HashMap<K, Object> map = new HashMap<>();
    //
    //        public Builder<K, V> replaceValue(K key, V elt) {
    //            map.put(key, elt);
    //            return this;
    //        }
    //
    //        public Builder<K, V> appendValue(K key, V v) {
    //            map.merge(key, v, (oldV, newV) -> {
    //                if (oldV instanceof MyVList) {
    //                    ((MyVList) oldV).add(newV);
    //                    return oldV;
    //                } else {
    //                    MyVList<Object> newVList = new MyVList<>(2);
    //                    newVList.add(oldV);
    //                    newVList.add(newV);
    //                    return newVList;
    //                }
    //            });
    //            return this;
    //        }
    //
    //
    //        private List<V> interpretValue(Object value) {
    //            if (value instanceof MyVList) {
    //                return Collections.unmodifiableList((MyVList<V>) value);
    //            } else {
    //                return Collections.singletonList((V) value);
    //            }
    //        }
    //
    //        public PMultimap<K, V> build() {
    //            PMap<K, List<V>> result = HashTreePMap.empty();
    //            for (K k : map.keySet()) {
    //                Object v = map.get(k);
    //                if (v == null) {
    //                    continue;
    //                }
    //                result = result.plus(k, interpretValue(v));
    //            }
    //            return new PMultimap<>(result);
    //        }
    //    }

}
