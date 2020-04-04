/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.table.internal.coreimpl;

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

import net.sourceforge.pmd.internal.util.AssertionUtil;

/**
 * An unmodifiable multimap type, efficient if the single-value case is the
 * most common.
 */
class MostlySingularMultimap<K, V> {

    @SuppressWarnings("rawtypes")
    private static final MostlySingularMultimap EMPTY = new MostlySingularMultimap(Collections.emptyMap());

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
    public static class Builder<K, V> {

        private final MapMaker<K> mapMaker;
        private @Nullable Map<K, Object> map;
        private boolean consumed;
        /** True unless some entry has a list of values. */
        private boolean isSingular = true;

        private Builder(MapMaker<K> mapMaker) {
            this.mapMaker = mapMaker;
        }

        private Map<K, Object> getMap() {
            if (map == null) {
                map = mapMaker.copy(Collections.emptyMap());
                Validate.isTrue(map.isEmpty(), "Map should be empty");
            }
            return map;
        }

        public Builder<K, V> appendValue(K key, V v) {
            ensureOpen();
            AssertionUtil.requireParamNotNull("value", v);
            AssertionUtil.requireParamNotNull("key", key);

            getMap().compute(key, (k, oldV) -> {
                if (oldV != null && isSingular) {
                    isSingular = false;
                }
                return appendSingle(oldV, v);
            });
            return this;
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

        private Object appendSingle(@Nullable Object vs, V v) {
            if (vs == null) {
                return v;
            } else if (vs instanceof VList) {
                // note that we grow the list without making a copy
                // This means that to design a Map -> Builder conversion
                // we'd need to copy all vlists
                ((VList) vs).add(v);
                return vs;
            } else {
                VList<V> vs2 = new VList<>(2);
                vs2.add(v);
                vs2.add((V) vs);
                return vs2;
            }
        }

        public MostlySingularMultimap<K, V> build() {
            consume();
            return isEmpty() ? empty() : new MostlySingularMultimap<>(getMap());
        }

        public @Nullable Map<K, V> buildAsSingular() {
            consume();
            if (!isSingular) {
                return null;
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
            for (Entry<K, Object> entry : getMap().entrySet()) {
                mutable.put(entry.getKey(), interpretValue(entry.getValue()));
            }
            return mutable;
        }

        public boolean isEmpty() {
            return map == null || map.isEmpty();
        }
    }

}
