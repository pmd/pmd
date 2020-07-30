/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.pcollections.HashTreePSet;
import org.pcollections.MapPSet;
import org.pcollections.PMap;
import org.pcollections.PSet;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.internal.util.AssertionUtil;
import net.sourceforge.pmd.internal.util.IteratorUtil;

/**
 * Generic collection and array-related utility functions for java.util types.
 * See ClassUtil for comparable facilities for short name lookup.
 *
 * @author Brian Remedios
 * @version $Revision$
 * @deprecated Is internal API
 */
@Deprecated
@InternalApi
public final class CollectionUtil {
    private static final int UNKNOWN_SIZE = -1;

    @SuppressWarnings("PMD.UnnecessaryFullyQualifiedName")
    public static final TypeMap COLLECTION_INTERFACES_BY_NAMES = new TypeMap(List.class, Collection.class, Map.class, Set.class);

    @SuppressWarnings({"PMD.LooseCoupling", "PMD.UnnecessaryFullyQualifiedName" })
    public static final TypeMap COLLECTION_CLASSES_BY_NAMES
        = new TypeMap(ArrayList.class, java.util.LinkedList.class, java.util.Vector.class, HashMap.class,
                      java.util.LinkedHashMap.class, java.util.TreeMap.class, java.util.TreeSet.class,
                      HashSet.class, java.util.LinkedHashSet.class, java.util.Hashtable.class);


    private CollectionUtil() {
    }

    /**
     * Add elements from the source to the target as long as they don't already
     * exist there. Return the number of items actually added.
     *
     * @param source
     * @param target
     * @return int
     */
    public static <T> int addWithoutDuplicates(Collection<T> source, Collection<T> target) {

        int added = 0;

        for (T item : source) {
            if (target.contains(item)) {
                continue;
            }
            target.add(item);
            added++;
        }

        return added;
    }

    /**
     * Returns the collection type if we recognize it by its short name.
     *
     * @param shortName
     *            String
     * @return Class
     */
    public static Class<?> getCollectionTypeFor(String shortName) {
        Class<?> cls = COLLECTION_CLASSES_BY_NAMES.typeFor(shortName);
        if (cls != null) {
            return cls;
        }

        return COLLECTION_INTERFACES_BY_NAMES.typeFor(shortName);
    }

    /**
     * Return whether we can identify the typeName as a java.util collection
     * class or interface as specified.
     *
     * @param typeName
     *            String
     * @param includeInterfaces
     *            boolean
     * @return boolean
     */
    public static boolean isCollectionType(String typeName, boolean includeInterfaces) {

        if (COLLECTION_CLASSES_BY_NAMES.contains(typeName)) {
            return true;
        }

        return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(typeName);
    }

    /**
     * Return whether we can identify the typeName as a java.util collection
     * class or interface as specified.
     *
     * @param clazzType
     *            Class
     * @param includeInterfaces
     *            boolean
     * @return boolean
     */
    public static boolean isCollectionType(Class<?> clazzType, boolean includeInterfaces) {

        if (COLLECTION_CLASSES_BY_NAMES.contains(clazzType)) {
            return true;
        }

        return includeInterfaces && COLLECTION_INTERFACES_BY_NAMES.contains(clazzType);
    }

    /**
     * Returns the items as a populated set.
     *
     * @param items
     *            Object[]
     * @return Set
     */
    public static <T> Set<T> asSet(T[] items) {

        return new HashSet<>(Arrays.asList(items));
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
     */
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
     */
    public static <K, V> Map<V, K> invertedMapFrom(Map<K, V> source) {
        Map<V, K> map = new HashMap<>(source.size());
        for (Map.Entry<K, V> entry : source.entrySet()) {
            map.put(entry.getValue(), entry.getKey());
        }
        return map;
    }


    /**
     * Consumes all the elements of the iterator and
     * returns a list containing them. The iterator is
     * then unusable
     *
     * @param it An iterator
     *
     * @return a list containing the elements remaining
     * on the iterator
     */
    public static <T> List<T> toList(Iterator<T> it) {
        List<T> list = new ArrayList<>();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    /**
     * Returns true if the objects are array instances and each of their
     * elements compares via equals as well.
     *
     * @param value
     *            Object
     * @param otherValue
     *            Object
     * @return boolean
     * @deprecated {@link Objects#deepEquals(Object, Object)}
     */
    @Deprecated
    public static boolean arraysAreEqual(Object value, Object otherValue) {
        if (value instanceof Object[]) {
            if (otherValue instanceof Object[]) {
                return valuesAreTransitivelyEqual((Object[]) value, (Object[]) otherValue);
            }
            return false;
        }
        return false;
    }

    /**
     * Returns whether the arrays are equal by examining each of their elements,
     * even if they are arrays themselves.
     *
     * @param thisArray
     *            Object[]
     * @param thatArray
     *            Object[]
     * @return boolean
     * @deprecated {@link Arrays#deepEquals(Object[], Object[])}
     */
    @Deprecated
    public static boolean valuesAreTransitivelyEqual(Object[] thisArray, Object[] thatArray) {
        if (thisArray == thatArray) {
            return true;
        }
        if (thisArray == null || thatArray == null) {
            return false;
        }
        if (thisArray.length != thatArray.length) {
            return false;
        }
        for (int i = 0; i < thisArray.length; i++) {
            if (!areEqual(thisArray[i], thatArray[i])) {
                return false; // recurse if req'd
            }
        }
        return true;
    }

    /**
     * A comprehensive isEqual method that handles nulls and arrays safely.
     *
     * @param value
     *            Object
     * @param otherValue
     *            Object
     * @return boolean
     * @deprecated {@link Objects#deepEquals(Object, Object)}
     */
    @Deprecated
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    public static boolean areEqual(Object value, Object otherValue) {
        if (value == otherValue) {
            return true;
        }
        if (value == null) {
            return false;
        }
        if (otherValue == null) {
            return false;
        }

        if (value.getClass().getComponentType() != null) {
            return arraysAreEqual(value, otherValue);
        }
        return value.equals(otherValue);
    }

    /**
     * Returns whether the items array is null or has zero length.
     *
     * @param items
     * @return boolean
     */
    public static boolean isEmpty(Object[] items) {
        return items == null || items.length == 0;
    }

    /**
     * Returns whether the items array is non-null and has at least one entry.
     *
     * @return boolean
     */
    public static boolean isNotEmpty(Object[] items) {
        return !isEmpty(items);
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
            return Collections.unmodifiableList(tail);
        } else if (tail.isEmpty()) {
            return Collections.unmodifiableList(head);
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


    @SafeVarargs
    public static <T> List<T> listOf(T first, T... rest) {
        if (rest.length == 0) {
            return Collections.singletonList(first);
        }
        List<T> union = new ArrayList<>();
        union.add(first);
        union.addAll(Arrays.asList(rest));
        return Collections.unmodifiableList(union);
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
        HashMap<K, V> newM = new HashMap<>(m);
        newM.put(k, v);
        return newM;
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

        if (from.isEmpty()) {
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
        return map(from.iterator(), from.size(), f);
    }

    /**
     * Map each element of the given iterable with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(Iterable<? extends T> from, Function<? super T, ? extends R> f) {
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
        return map(Arrays.asList(from), f);
    }

    /**
     * Map each element of the given iterator with the given function,
     * and accumulates it into an unmodifiable list.
     */
    public static <T, R> List<R> map(Iterator<? extends T> from, Function<? super T, ? extends R> f) {
        return map(from, UNKNOWN_SIZE, f);
    }

    private static <T, R> List<R> map(Iterator<? extends T> from, int sizeHint, Function<? super T, ? extends R> f) {
        if (!from.hasNext()) {
            return emptyList();
        } else if (sizeHint == 1) {
            return Collections.singletonList(f.apply(from.next()));
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
        return Collector.<T, ArrayList<T>, List<T>>of(
            ArrayList::new,
            ArrayList::add,
            (left, right) -> {
                left.addAll(right);
                return left;
            },
            a -> a,
            Characteristics.IDENTITY_FINISH
        );
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
     * A collectors that accumulates into a persistent set.
     *
     * @param <T> Type of accumulated values
     */
    public static <T> Collector<T, ?, PSet<T>> toPersistentSet() {
        class Holder {
            MapPSet<T> set = HashTreePSet.empty();
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

    /**
     * Returns true if both arrays are if both are null or have zero-length,
     * otherwise return the false if their respective elements are not equal by
     * position.
     *
     * @param <T>
     * @param a
     * @param b
     * @return boolean
     * @deprecated {@link Arrays#deepEquals(Object[], Object[])}
     */
    @Deprecated
    public static <T> boolean areSemanticEquals(T[] a, T[] b) {
        if (a == null) {
            return b == null || b.length == 0;
        }
        if (b == null) {
            return a.length == 0;
        }

        if (a.length != b.length) {
            return false;
        }

        for (int i = 0; i < a.length; i++) {
            if (!areEqual(a[i], b[i])) {
                return false;
            }
        }

        return true;
    }


    /**
     * If the newValue is already held within the values array then the values
     * array is returned, otherwise a new array is created appending the
     * newValue to the end.
     *
     * @param <T>
     * @param values
     * @param newValue
     * @return an array containing the union of values and newValue
     */
    @Deprecated
    public static <T> T[] addWithoutDuplicates(T[] values, T newValue) {

        for (T value : values) {
            if (value.equals(newValue)) {
                return values;
            }
        }

        T[] largerOne = (T[]) Array.newInstance(values.getClass().getComponentType(), values.length + 1);
        System.arraycopy(values, 0, largerOne, 0, values.length);
        largerOne[values.length] = newValue;
        return largerOne;
    }

    /**
     * Returns an array of values as a union set of the two input arrays.
     *
     * @param <T>
     * @param values
     * @param newValues
     * @return the union of the two arrays
     */
    @Deprecated
    public static <T> T[] addWithoutDuplicates(T[] values, T[] newValues) {

        Set<T> originals = new HashSet<>(values.length);
        for (T value : values) {
            originals.add(value);
        }
        List<T> newOnes = new ArrayList<>(newValues.length);
        for (T value : newValues) {
            if (originals.contains(value)) {
                continue;
            }
            newOnes.add(value);
        }

        T[] largerOne = (T[]) Array.newInstance(values.getClass().getComponentType(), values.length + newOnes.size());
        System.arraycopy(values, 0, largerOne, 0, values.length);
        for (int i = values.length; i < largerOne.length; i++) {
            largerOne[i] = newOnes.get(i - values.length);
        }
        return largerOne;
    }

    public static <T> List<T> listOfNotNull(T t) {
        return t == null ? emptyList() : singletonList(t);
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
}
