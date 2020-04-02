/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;

import static java.util.Collections.emptyList;
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
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

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
     * @param items
     * @return boolean
     */
    public static boolean isNotEmpty(Object[] items) {
        return !isEmpty(items);
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


    @SafeVarargs
    public static <T> Set<T> setOf(T first, T... rest) {
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
        return union;
    }

    public static <T, R> List<R> map(Collection<? extends T> from, Function<? super T, ? extends R> f) {
        return map(from.iterator(), from.size(), f);
    }

    public static <T, R> List<R> map(Iterable<? extends T> from, Function<? super T, ? extends R> f) {
        return map(from.iterator(), UNKNOWN_SIZE, f);
    }

    public static <T, R> List<R> map(Iterator<? extends T> from, Function<? super T, ? extends R> f) {
        return map(from, UNKNOWN_SIZE, f);
    }

    private static <T, R> List<R> map(Iterator<? extends T> from, int sizeHint, Function<? super T, ? extends R> f) {
        if (!from.hasNext()) {
            return emptyList();
        }
        List<R> res = sizeHint == UNKNOWN_SIZE ? new ArrayList<>() : new ArrayList<>(sizeHint);
        while (from.hasNext()) {
            res.add(f.apply(from.next()));
        }
        return res;
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
}
