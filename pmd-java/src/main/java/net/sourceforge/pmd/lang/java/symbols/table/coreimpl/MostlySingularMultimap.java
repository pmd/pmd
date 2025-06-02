/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.coreimpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.util.AssertionUtil;

/**
 * An unmodifiable multimap type, efficient if the single-value case is the
 * most common.
 */
final class MostlySingularMultimap<K, V> {

    @SuppressWarnings("rawtypes")
    private static final MostlySingularMultimap EMPTY = new MostlySingularMultimap<>(Collections.emptyMap());

    private final Map<K, Object> map;

    private MostlySingularMultimap(Map<K, Object> map) {
        this.map = map;
    }

    @FunctionalInterface
    public interface MapMaker<K> {

        /** Produce a new mutable map with the contents of the given map. */
        <V> Map<K, V> copy(Map<K, V> m);
    }

    public @NonNull List<V> get(K k) {
        Object vs = map.get(k);
        return interpretValue(vs);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Set<K> keySet() {
        return map.keySet();
    }

    public boolean containsKey(Object v) {
        return map.containsKey(v);
    }

    @Override
    public String toString() {
        return map.toString();
    }

    public void processValuesOneByOne(BiConsumer<K, V> consumer) {
        for (Entry<K, Object> entry : map.entrySet()) {
            K k = entry.getKey();
            Object vs = entry.getValue();
            if (vs instanceof VList) {
                for (V v : (VList<V>) vs) {
                    consumer.accept(k, v);
                }
            } else {
                consumer.accept(k, (V) vs);
            }
        }
    }

    @NonNull
    @SuppressWarnings("unchecked")
    private static <V> List<V> interpretValue(Object vs) {
        if (vs == null) {
            return Collections.emptyList();
        } else if (vs instanceof VList) {
            return (VList<V>) vs;
        } else {
            return Collections.singletonList((V) vs);
        }
    }

    @SuppressWarnings("unchecked")
    public static <K, V> MostlySingularMultimap<K, V> empty() {
        return EMPTY;
    }

    public static <K, V> Builder<K, V> newBuilder(MapMaker<K> mapMaker) {
        return new Builder<>(mapMaker);
    }


    // In case the value type V is an array list
    private static class VList<V> extends ArrayList<V> {

        VList(int size) {
            super(size);
        }

    }

    /**
     * Builder for a multimap. Can only be used once.
     */
    public static final class Builder<K, V> {

        private final MapMaker<K> mapMaker;
        private @Nullable Map<K, Object> map;
        private boolean consumed;
        /** True unless some entry has a list of values. */
        private boolean isSingular = true;

        private Builder(MapMaker<K> mapMaker) {
            this.mapMaker = mapMaker;
        }

        private Map<K, Object> getMapInternal() {
            if (map == null) {
                map = mapMaker.copy(Collections.emptyMap());
                Validate.isTrue(map.isEmpty(), "Map should be empty");
            }
            return map;
        }


        public void replaceValue(K key, V v) {
            checkKeyValue(key, v);
            getMapInternal().put(key, v);
        }

        public void addUnlessKeyExists(K key, V v) {
            checkKeyValue(key, v);
            getMapInternal().putIfAbsent(key, v);
        }

        public void appendValue(K key, V v) {
            appendValue(key, v, false);
        }

        public void appendValue(K key, V v, boolean noDuplicate) {
            checkKeyValue(key, v);

            getMapInternal().compute(key, (k, oldV) -> {
                return appendSingle(oldV, v, noDuplicate);
            });
        }

        private void checkKeyValue(K key, V v) {
            ensureOpen();
            AssertionUtil.requireParamNotNull("value", v);
            AssertionUtil.requireParamNotNull("key", key);
        }

        public Builder<K, V> groupBy(Iterable<? extends V> values,
                                     Function<? super V, ? extends K> keyExtractor) {
            ensureOpen();
            return groupBy(values, keyExtractor, Function.identity());
        }


        public <I> Builder<K, V> groupBy(Iterable<? extends I> values,
                                         Function<? super I, ? extends K> keyExtractor,
                                         Function<? super I, ? extends V> valueExtractor) {
            ensureOpen();
            for (I i : values) {
                appendValue(keyExtractor.apply(i), valueExtractor.apply(i));
            }
            return this;
        }

        // no duplicates
        public Builder<K, V> absorb(Builder<K, V> other) {
            ensureOpen();
            other.ensureOpen();

            if (this.map == null) {
                this.map = other.map;
                this.isSingular = other.isSingular;
            } else {
                // isSingular may be changed in the loop by appendSingle
                this.isSingular &= other.isSingular;

                for (Entry<K, Object> otherEntry : other.getMapInternal().entrySet()) {
                    K key = otherEntry.getKey();
                    Object otherV = otherEntry.getValue();
                    map.compute(key, (k, myV) -> {
                        if (myV == null) {
                            return otherV;
                        } else if (otherV instanceof VList) {
                            Object newV = myV;
                            for (V v : (VList<V>) otherV) {
                                newV = appendSingle(newV, v, true);
                            }
                            return newV;
                        } else {
                            return appendSingle(myV, (V) otherV, true);
                        }
                    });
                }
            }

            other.consume();
            return this;
        }

        private Object appendSingle(@Nullable Object vs, V v, boolean noDuplicate) {
            if (vs == null) {
                return v;
            } else if (vs instanceof VList) {
                if (noDuplicate && ((VList) vs).contains(v)) {
                    return vs;
                }
                ((VList) vs).add(v);
                return vs;
            } else {
                if (noDuplicate && vs.equals(v)) {
                    return vs;
                }
                List<V> vs2 = new VList<>(2);
                isSingular = false;
                vs2.add((V) vs);
                vs2.add(v);
                return vs2;
            }
        }

        public MostlySingularMultimap<K, V> build() {
            consume();
            return isEmpty() ? empty() : new MostlySingularMultimap<>(getMapInternal());
        }

        public @Nullable Map<K, V> buildAsSingular() {
            consume();
            if (!isSingular) {
                return null; // NOPMD: returning null as in the spec (Nullable)
            }
            return (Map<K, V>) map;
        }


        private void consume() {
            ensureOpen();
            consumed = true;
        }

        private void ensureOpen() {
            Validate.isTrue(!consumed, "Builder was already consumed");
        }

        public boolean isSingular() {
            return isSingular;
        }

        public Map<K, List<V>> getMutableMap() {
            Map<K, List<V>> mutable = mapMaker.copy(Collections.emptyMap());
            for (Entry<K, Object> entry : getMapInternal().entrySet()) {
                mutable.put(entry.getKey(), interpretValue(entry.getValue()));
            }
            return mutable;
        }

        public boolean isEmpty() {
            return map == null || map.isEmpty();
        }

    }

}
