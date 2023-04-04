/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePSet;
import org.pcollections.PMap;
import org.pcollections.PSequence;
import org.pcollections.PSet;

import net.sourceforge.pmd.lang.document.Chars;

/**
 * Generic collection-related utility functions for java.util types.
 *
 * @author Brian Remedios
 * @author Clément Fournier
 */
public final class CollectionUtil {

    private static final int UNKNOWN_SIZE = -1;

    private CollectionUtil() {
    }

    /**
     * Creates and returns a map populated with the keyValuesSets where the
     * value held by the tuples are they key and value in that order.
     *
     * @param keys
     *            K[]
     * @param values
     *            V[]
     * @return Map
     *
     * @deprecated Used by deprecated property types
     */
    @Deprecated
    public static <K, V> Map<K, V> mapFrom(K[] keys, V[] values) {
        if (keys.length != values.length) {
            throw new RuntimeException("mapFrom keys and values arrays have different sizes");
        }
        Map<K, V> map = new HashMap<>(keys.length);
        for (int i = 0; i < keys.length; i++) {
            map.put(keys[i], values[i]);
        }
        return map;
    }

    /**
     * Returns a map based on the source but with the key &amp; values swapped.
     *
     * @param source
     *            Map
     * @return Map
     *
     * @deprecated Used by deprecated property types
     */
    @Deprecated
    public static <K, V> Map<V, K> invertedMapFrom(Map<K, V> source) {
        Map<V, K> map = new HashMap<>(source.size());
        for (Map.Entry<K, V> entry : source.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }


    /**
     * Returns a list view that pretends it is the concatenation of
     * both lists. The returned view is unmodifiable. The implementation
     * is pretty stupid and not optimized for repeated concatenation,
     * but should be ok for smallish chains of random-access lists.
     *
     * @param head Head elements (to the left)
     * @param tail Tail elements (to the right)
     * @param <T>  Type of elements in both lists
     *
     * @return A concatenated view
     */
    public static <T> List<T> concatView(List<? extends T> head, List<? extends T> tail) {
        if (head.isEmpty()) {
            return makeUnmodifiableAndNonNull(tail);
        } else if (tail.isEmpty()) {
            return makeUnmodifiableAndNonNull(head);
        } else {
            return new ConsList<>(head, tail);
        }
    }


    /**
     * Returns the set union of the given collections.
     *
     * @param c1 First collection
     * @param c2 Second collection
     *
     * @return Union of both arguments
     */
    @SafeVarargs
    public static <T> Set<T> union(Collection<? extends T> c1, Collection<? extends T> c2, Collection<? extends T>... rest) {
        Set<T> union = new LinkedHashSet<>(c1);
        union.addAll(c2);
        for (Collection<? extends T> ts : rest) {
            union.addAll(ts);
        }
        return union;
    }

    /**
     * Returns the set intersection of the given collections.
     *
     * @param c1 First collection
     * @param c2 Second collection
     *
     * @return Intersection of both arguments
     */
    @SafeVarargs
    public static <T> Set<T> intersect(Collection<? extends T> c1, Collection<? extends T> c2, Collection<? extends T>... rest) {
        Set<T> union = new LinkedHashSet<>(c1);
        union.retainAll(c2);
        for (Collection<? extends T> ts : rest) {
            union.retainAll(ts);
        }
        return union;
    }


    /**
     * Returns the set difference of the first collection with the other
     * collections.
     *
     * @param c1 First collection
     * @param c2 Second collection
     *
     * @return Difference of arguments
     */
    @SafeVarargs
    public static <T> Set<T> diff(Collection<? extends T> c1, Collection<? extends T> c2, Collection<? extends T>... rest) {
        Set<T> union = new LinkedHashSet<>(c1);
        union.removeAll(c2);
        for (Collection<? extends T> ts : rest) {
            union.removeAll(ts);
        }
        return union;
    }

    /**
     * Returns a set containing the given elements. No guarantee is
     * made about mutability.
     *
     * @param first First element
     * @param rest  Following elements
     */
    @SafeVarargs
    public static <T> Set<T> setOf(T first, T... rest) {
        return immutableSetOf(first, rest);
    }

    /**
     * Returns an unmodifiable set containing the given elements.
     *
     * @param first First element
     * @param rest  Following elements
     */
    @SafeVarargs
    public static <T> Set<T> immutableSetOf(T first, T... rest) {
        if (rest.length == 0) {
            return Collections.singleton(first);
        }
        Set<T> union = new LinkedHashSet<>();
        union.add(first);
        Collections.addAll(union, rest);
        return Collections.unmodifiableSet(union);
    }

    /**
     * Returns an unmodifiable set containing the given elements.
     *
     * @param first First element
     * @param rest  Following elements
     */
    @SafeVarargs
    public static <T extends Enum<T>> Set<T> immutableEnumSet(T first, T... rest) {
        return Collections.unmodifiableSet(EnumSet.of(first, rest));
    }


    @SafeVarargs
    public static <T> List<T> listOf(T first, T... rest) {
        if (rest.length == 0) {
            return ConsPStack.singleton(first);
        }
        List<T> union = new ArrayList<>();
        union.add(first);
        union.addAll(asList(rest));
        return Collections.unmodifiableList(union);
    }

    public static <K, V> Map<K, V> mapOf(K k0, V v0) {
        return Collections.singletonMap(k0, v0);
    }

    public static <K, V> Map<K, V> mapOf(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new LinkedHashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> buildMap(Consumer<Map<K, V>> effect) {
        Map<K, V> map = new LinkedHashMap<>();
        effect.accept(map);
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> buildMap(Map<K, V> initialMap, Consumer<Map<K, V>> effect) {
        Map<K, V> map = new LinkedHashMap<>(initialMap);
        effect.accept(map);
        return Collections.unmodifiableMap(map);
    }

    public static <T, R> List<@NonNull R> mapNotNull(Iterable<? extends T> from, Function<? super T, ? extends @Nullable R> f) {
        Iterator<? extends T> it = from.iterator();
        if (!it.hasNext()) {
            return Collections.emptyList();
        }
        List<R> res = new ArrayList<>();
        while (it.hasNext()) {
            R r = f.apply(it.next());
            if (r != null) {
                res.add(r);
            }
        }
        return res;
    }

    /**
     * Produce a new map with the mappings of the first, and one additional
     * mapping. The returned map may be unmodifiable.
     */
    public static <K, V> Map<K, V> plus(Map<K, V> m, K k, V v) {
        if (m instanceof PMap) {
            return ((PMap<K, V>) m).plus(k, v);
        }
        if (m.isEmpty()) {
            return Collections.singletonMap(k, v);
        }
        Map<K, V> newM = new HashMap<>(m);
        newM.put(k, v);
        return newM;
    }

    /**
     * Produce a new list with the elements of the first, and one additional
     * item. The returned list is immutable.
     */
    public static <V> List<V> plus(List<V> list, V v) {
        if (list instanceof PSequence) {
            return ((PSequence<V>) list).plus(v);
        } else if (list.isEmpty()) {
            return ConsPStack.singleton(v);
        }
        return ConsPStack.from(list).plus(v);
    }

    /** Returns the empty list. */
    public static <V> List<V> emptyList() {
        // We use this implementation so that it plays well with other
        // operations that expect immutable data.
        return ConsPStack.empty();
    }

    /**
     * Returns an unmodifiable set containing the set union of the collection,
     * and the new elements.
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <V> Set<V> setUnion(Collection<? extends V> set, V first, V... newElements) {
        if (set instanceof PSet) {
            return ((PSet<V>) set).plus(first).plusAll(asList(newElements));
        }
        Set<V> newSet = new LinkedHashSet<>(set.size() + 1 + newElements.length);
        newSet.addAll(set);
        newSet.add(first);
        Collections.addAll(newSet, newElements);
        return Collections.unmodifiableSet(newSet);
    }


    /**
     * Returns a map associating each key in the first list to its
     * corresponding value in the second.
     *
     * @throws IllegalArgumentException If the list size are mismatched
     * @throws NullPointerException     If either of the parameter is null,
     *                                  or any of the keys or values are null
     */
    public static <K, V> Map<K, V> zip(List<? extends @NonNull K> from, List<? extends @NonNull V> to) {
        AssertionUtil.requireParamNotNull("keys", from);
        AssertionUtil.requireParamNotNull("values", to);
        Validate.isTrue(from.size() == to.size(), "Mismatched list sizes %s to %s", from, to);

        if (from.isEmpty()) { //NOPMD: we really want to compare references here
            return emptyMap();
        }

        Map<K, V> map = new HashMap<>(from.size());

        for (int i = 0; i < from.size(); i++) {
            K key = from.get(i);
            V val = to.get(i);

            Validate.notNull(key);
            Validate.notNull(val);

            map.put(key, val);
        }

        return map;
    }

    public static <K, V> Map<K, V> associateWith(Collection<? extends @NonNull K> keys, Function<? super K, ? extends V> mapper) {
        AssertionUtil.requireParamNotNull("keys", keys);
        if (keys.isEmpty()) {
            return emptyMap();
        }

        return associateWithTo(new HashMap<>(keys.size()), keys, mapper);
    }

    public static <K, V> Map<K, V> associateWithTo(Map<K, V> collector, Collection<? extends @NonNull K> keys, Function<? super K, ? extends V> mapper) {
        AssertionUtil.requireParamNotNull("collector", collector);
        AssertionUtil.requireParamNotNull("keys", keys);
        AssertionUtil.requireParamNotNull("mapper", mapper);
        for (K key : keys) {
            collector.put(key, mapper.apply(key));
        }
        return collector;
    }


    public static <K, V> Map<K, V> associateBy(Collection<? extends @NonNull V> values, Function<? super V, ? extends K> keyMapper) {
        AssertionUtil.requireParamNotNull("values", values);
        if (values.isEmpty()) {
            return emptyMap();
        }

        return associateByTo(new HashMap<>(values.size()), values, keyMapper);
    }


    public static <K, V> Map<K, V> associateByTo(Map<K, V> collector, Collection<? extends @NonNull V> values, Function<? super V, ? extends K> keyMapper) {
        AssertionUtil.requireParamNotNull("collector", collector);
        AssertionUtil.requireParamNotNull("values", values);
        AssertionUtil.requireParamNotNull("keyMapper", keyMapper);
        for (V v : values) {
            collector.put(keyMapper.apply(v), v);
        }
        return collector;
    }

    /**
     * Map each element of the given collection with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(Collection<? extends T> from, Function<? super T, ? extends R> f) {
        if (from == null) {
            return emptyList();
        }
        return map(from.iterator(), from.size(), f);
    }

    /**
     * Map each element of the given iterable with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(Iterable<? extends T> from, Function<? super T, ? extends R> f) {
        if (from == null) {
            return emptyList();
        }
        return map(from.iterator(), UNKNOWN_SIZE, f);
    }

    /**
     * Map each element of the given array with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(T[] from, Function<? super T, ? extends R> f) {
        if (from == null) {
            return emptyList();
        }
        return map(asList(from), f);
    }

    /**
     * Map each element of the given iterator with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(Iterator<? extends T> from, Function<? super T, ? extends R> f) {
        if (from == null) {
            return emptyList();
        }
        return map(from, UNKNOWN_SIZE, f);
    }

    private static <T, R> List<R> map(Iterator<? extends T> from, int sizeHint, Function<? super T, ? extends R> f) {
        if (!from.hasNext()) {
            return emptyList();
        } else if (sizeHint == 1) {
            return ConsPStack.singleton(f.apply(from.next()));
        }
        List<R> res = sizeHint == UNKNOWN_SIZE ? new ArrayList<>() : new ArrayList<>(sizeHint);
        while (from.hasNext()) {
            res.add(f.apply(from.next()));
        }
        return Collections.unmodifiableList(res);
    }

    /**
     * Map each element of the given iterable with the given function,
     * and accumulates it into the collector.
     */
    public static <T, U, A, C> C map(Collector<? super U, A, ? extends C> collector,
                                     Iterable<? extends T> from,
                                     Function<? super T, ? extends U> f) {
        if (from == null) {
            return map(collector, emptyIterator(), f);
        }
        return map(collector, from.iterator(), f);
    }

    /**
     * Map each element of the given iterator with the given function,
     * and accumulates it into the collector.
     */
    // one more type param and we can write tupac
    public static <T, U, A, C> C map(Collector<? super U, A, ? extends C> collector,
                                     Iterator<? extends T> from,
                                     Function<? super T, ? extends U> f) {
        A a = collector.supplier().get();
        BiConsumer<A, ? super U> accumulator = collector.accumulator();
        from.forEachRemaining(t -> accumulator.accept(a, f.apply(t)));
        return finish(collector, a);
    }

    /**
     * A collector that returns a mutable list. This contrasts with
     * {@link Collectors#toList()}, which makes no guarantee about the
     * mutability of the list.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, List<T>> toMutableList() {
        return Collectors.toCollection(ArrayList::new);
    }


    /**
     * A collector that returns a mutable set. This contrasts with
     * {@link Collectors#toSet()}, which makes no guarantee about the
     * mutability of the set. The set preserves insertion order.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, Set<T>> toMutableSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    /**
     * A collector that returns an unmodifiable list. This contrasts with
     * {@link Collectors#toList()}, which makes no guarantee about the
     * mutability of the list. {@code Collectors::toUnmodifiableList} was
     * only added in JDK 9.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, List<T>> toUnmodifiableList() {
        return Collectors.collectingAndThen(toMutableList(), Collections::unmodifiableList);
    }

    /**
     * A collector that returns an unmodifiable set. This contrasts with
     * {@link Collectors#toSet()}, which makes no guarantee about the
     * mutability of the set. {@code Collectors::toUnmodifiableSet} was
     * only added in JDK 9. The set preserves insertion order.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, Set<T>> toUnmodifiableSet() {
        return Collectors.collectingAndThen(toMutableSet(), Collections::unmodifiableSet);
    }

    /**
     * A collectors that accumulates into a persistent set.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, PSet<T>> toPersistentSet() {
        class Holder {

            PSet<T> set = HashTreePSet.empty();
        }

        return Collector.of(
            Holder::new,
            (h, t) -> h.set = h.set.plus(t),
            (left, right) -> {
                left.set = left.set.plusAll(right.set);
                return left;
            },
            a -> a.set
        );
    }

    /**
     * Finish the accumulated value of the collector.
     */
    public static <V, A, C> C finish(Collector<? super V, A, ? extends C> collector, A acc) {
        if (collector.characteristics().contains(Characteristics.IDENTITY_FINISH)) {
            return (C) acc;
        } else {
            return collector.finisher().apply(acc);
        }
    }

    public static <T> List<T> drop(List<T> list, int n) {
        AssertionUtil.requireNonNegative("n", n);

        return list.size() <= n ? emptyList()
                                : list.subList(n, list.size());
    }

    public static <T> List<T> take(List<T> list, int n) {
        AssertionUtil.requireNonNegative("n", n);
        return list.size() <= n ? list
                                : list.subList(0, n);
    }


    public static <T> List<T> listOfNotNull(T t) {
        return t == null ? emptyList() : ConsPStack.singleton(t);
    }

    /**
     * Returns true if any element of the iterable matches the predicate. Return
     * false if the list is null or empty.
     */
    public static <N> boolean any(@Nullable Iterable<? extends N> list, Predicate<? super N> predicate) {
        return list != null && IteratorUtil.anyMatch(list.iterator(), predicate);
    }

    /**
     * Returns true if all elements of the iterable match the predicate. Return
     * true if the list is null or empty.
     */
    public static <N> boolean all(@Nullable Iterable<? extends N> list, Predicate<? super N> predicate) {
        return list == null || IteratorUtil.allMatch(list.iterator(), predicate);
    }

    /**
     * Returns true if no element of the iterable matches the predicate. Return
     * true if the list is null or empty.
     */
    public static <N> boolean none(@Nullable Iterable<? extends N> list, Predicate<? super N> predicate) {
        return list == null || IteratorUtil.noneMatch(list.iterator(), predicate);
    }

    /**
     * If the set has a single element, returns it, otherwise returns null.
     * Obviously the set should not contain null elements.
     */
    public static <@NonNull T> @Nullable T asSingle(Set<T> set) {
        if (set.size() == 1) {
            return set.iterator().next();
        } else {
            return null;
        }
    }

    /**
     * Returns an unmodifiable copy of the list. This is to be preferred
     * to {@link Collections#unmodifiableList(List)} if you don't trust
     * the source of the list, because no one holds a reference to the buffer
     * except the returned unmodifiable list.
     *
     * @param list A list
     * @param <T>  Type of items
     */
    public static <T> List<T> defensiveUnmodifiableCopy(List<? extends T> list) {
        if (list instanceof PSequence) {
            return (List<T>) list; // is already immutable
        }
        if (list.isEmpty()) {
            return ConsPStack.empty();
        } else if (list.size() == 1) {
            return ConsPStack.singleton(list.get(0));
        }
        return ConsPStack.from(list);
    }

    public static <T> Set<T> defensiveUnmodifiableCopyToSet(Collection<? extends T> list) {
        if (list.isEmpty()) {
            return emptySet();
        }
        return Collections.unmodifiableSet(new LinkedHashSet<>(list));
    }

    /**
     * Like {@link String#join(CharSequence, Iterable)}, except it appends
     * on a preexisting {@link StringBuilder}. The result value is that StringBuilder.
     */
    public static <T> StringBuilder joinOn(StringBuilder sb,
                                           Iterable<? extends T> iterable,
                                           BiConsumer<? super StringBuilder, ? super T> appendItem,
                                           String delimiter) {
        boolean first = true;
        for (T t : iterable) {
            if (first) {
                first = false;
            } else {
                sb.append(delimiter);
            }
            appendItem.accept(sb, t);
        }
        return sb;
    }

    public static @NonNull StringBuilder joinCharsIntoStringBuilder(List<Chars> lines, String delimiter) {
        return joinOn(
            new StringBuilder(),
            lines,
            (buf, line) -> line.appendChars(buf),
            delimiter
        );
    }


    /**
     * Merge the second map into the first. If some keys are in common,
     * merge them using the merge function, like {@link Map#merge(Object, Object, BiFunction)}.
     */
    public static <K, V> void mergeMaps(Map<K, V> result, Map<K, V> other, BinaryOperator<V> mergeFun) {
        for (K otherKey : other.keySet()) {
            V otherInfo = other.get(otherKey); // non-null
            result.merge(otherKey, otherInfo, mergeFun);
        }
    }

    public static @NonNull <T> List<T> makeUnmodifiableAndNonNull(@Nullable List<? extends T> list) {
        if (list instanceof PSequence) {
            return (List<T>) list;
        }
        return list == null || list.isEmpty() ? emptyList()
                                              : Collections.unmodifiableList(list);
    }
}
