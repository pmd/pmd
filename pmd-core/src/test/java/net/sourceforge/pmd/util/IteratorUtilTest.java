/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util;


import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptyList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.jupiter.api.Test;

class IteratorUtilTest {

    @Test
    void testAnyMatchPos() {
        Iterator<String> iter = iterOf("a", "b", "cd");

        boolean match = IteratorUtil.anyMatch(iter, it -> it.length() > 1);

        assertTrue(match);
    }

    @Test
    void testAnyMatchNeg() {
        Iterator<String> iter = iterOf("a", "b", "");

        boolean match = IteratorUtil.anyMatch(iter, it -> it.length() > 1);

        assertFalse(match);
    }

    @Test
    void testAnyMatchEmpty() {
        Iterator<String> iter = emptyIterator();

        boolean match = IteratorUtil.anyMatch(iter, it -> it.length() > 1);

        assertFalse(match);
    }


    @Test
    void testAllMatchPos() {
        Iterator<String> iter = iterOf("ap", "bcd", "cd");

        boolean match = IteratorUtil.allMatch(iter, it -> it.length() > 1);

        assertTrue(match);
    }

    @Test
    void testAllMatchNeg() {
        Iterator<String> iter = iterOf("a", "bcd", "");

        boolean match = IteratorUtil.allMatch(iter, it -> it.length() > 1);

        assertFalse(match);
    }

    @Test
    void testAllMatchEmpty() {
        Iterator<String> iter = emptyIterator();

        boolean match = IteratorUtil.allMatch(iter, it -> it.length() > 1);

        assertTrue(match);
    }


    @Test
    void testNoneMatchPos() {
        Iterator<String> iter = iterOf("ap", "bcd", "cd");

        boolean match = IteratorUtil.noneMatch(iter, it -> it.length() < 1);

        assertTrue(match);
    }

    @Test
    void testNoneMatchNeg() {
        Iterator<String> iter = iterOf("a", "bcd", "");

        boolean match = IteratorUtil.noneMatch(iter, it -> it.length() < 1);

        assertFalse(match);
    }

    @Test
    void testNoneMatchEmpty() {
        Iterator<String> iter = emptyIterator();

        boolean match = IteratorUtil.noneMatch(iter, it -> it.length() < 1);

        assertTrue(match);
    }

    @Test
    void testFlatmap() {
        Iterator<String> iter = iterOf("ab", "cd", "e", "", "f");
        Function<String, Iterator<String>> fun = s -> s.chars().mapToObj(i -> (char) i).map(String::valueOf).iterator();

        Iterator<String> mapped = IteratorUtil.flatMap(iter, fun);


        assertThat(() -> mapped, contains("a", "b", "c", "d", "e", "f"));
    }


    @Test
    void testFlatmapEmpty() {
        Iterator<String> iter = emptyIterator();
        Function<String, Iterator<String>> fun = s -> s.chars().mapToObj(i -> (char) i).map(String::valueOf).iterator();

        Iterator<String> mapped = IteratorUtil.flatMap(iter, fun);


        assertExhausted(mapped);
    }

    @Test
    void testFlatmapEmpty2() {
        Iterator<String> iter = iterOf("ab", "cd", "e", "", "f");
        Function<String, Iterator<String>> fun = s -> emptyIterator();

        Iterator<String> mapped = IteratorUtil.flatMap(iter, fun);

        assertExhausted(mapped);
    }

    @Test
    void testFlatmapIsLazy() {
        Iterator<String> iter = iterOf("a", "b");
        Function<String, Iterator<String>> fun = s -> {
            if (s.equals("a")) {
                return iterOf("a");
            } else {
                throw new AssertionError("This statement shouldn't be reached");
            }
        };

        Iterator<String> mapped = IteratorUtil.flatMap(iter, fun);

        assertTrue(mapped.hasNext());
        assertEquals("a", mapped.next());

        assertThrows(AssertionError.class, () -> mapped.hasNext());
    }


    @Test
    void testFlatmapWithSelf() {
        Iterator<String> iter = iterOf("ab", "e", null, "f");
        Function<String, Iterator<String>> fun = s -> s == null ? null // test null safety
                                                                : iterOf(s + "1", s + "2");

        Iterator<String> mapped = IteratorUtil.flatMapWithSelf(iter, fun);

        assertThat(IteratorUtil.toList(mapped), contains("ab", "ab1", "ab2", "e", "e1", "e2", null, "f", "f1", "f2"));
    }

    @Test
    void testMapNotNull() {
        Iterator<String> iter = iterOf("ab", "cdde", "e", "", "f", "fe");
        Function<String, Integer> fun = s -> s.length() < 2 ? null : s.length();

        Iterator<Integer> mapped = IteratorUtil.mapNotNull(iter, fun);


        assertThat(() -> mapped, contains(2, 4, 2));
    }

    @Test
    void testMapNotNullEmpty() {
        Iterator<String> iter = emptyIterator();
        Function<String, Integer> fun = s -> s.length() < 2 ? null : s.length();

        Iterator<Integer> mapped = IteratorUtil.mapNotNull(iter, fun);


        assertExhausted(mapped);
    }

    @Test
    void testMapNotNullEmpty2() {
        Iterator<String> iter = iterOf("a", "b");
        Function<String, Iterator<String>> fun = s -> null;

        Iterator<String> mapped = IteratorUtil.flatMap(iter, fun);


        assertExhausted(mapped);
    }

    @Test
    void testFilterNotNull() {
        Iterator<String> iter = iterOf("ab", null, "e", null, "", "fe");

        Iterator<String> mapped = IteratorUtil.filterNotNull(iter);


        assertThat(() -> mapped, contains("ab", "e", "", "fe"));
        assertExhausted(iter);
    }


    @Test
    void testDistinct() {
        Iterator<String> iter = iterOf("ab", null, "e", null, "fe", "ab", "c");

        Iterator<String> mapped = IteratorUtil.distinct(iter);


        assertThat(() -> mapped, contains("ab", null, "e", "fe", "c"));
        assertExhausted(iter);
    }


    @Test
    void testTakeWhile() {
        Iterator<String> iter = iterOf("ab", "null", "e", null, "", "fe");
        Predicate<String> predicate = Objects::nonNull;

        Iterator<String> mapped = IteratorUtil.takeWhile(iter, predicate);

        assertThat(() -> mapped, contains("ab", "null", "e"));
        assertExhausted(mapped);
    }

    @Test
    void testTakeWhileWithEmpty() {
        Iterator<String> iter = iterOf();
        Predicate<String> predicate = Objects::nonNull;

        Iterator<String> mapped = IteratorUtil.takeWhile(iter, predicate);

        assertExhausted(mapped);
    }

    @Test
    void testPeek() {
        Iterator<String> iter = iterOf("ab", null, "c");
        List<String> seen = new ArrayList<>();
        Consumer<String> action = seen::add;

        Iterator<String> mapped = IteratorUtil.peek(iter, action);

        assertEquals("ab", mapped.next());
        assertThat(seen, contains("ab"));

        assertNull(mapped.next());
        assertThat(seen, contains("ab", null));

        assertEquals("c", mapped.next());
        assertThat(seen, contains("ab", null, "c"));

        assertExhausted(mapped);
    }

    @Test
    void testTakeNegative() {
        Iterator<String> iter = iterOf("a", "b", "c");

        assertThrows(IllegalArgumentException.class, () -> IteratorUtil.take(iter, -5));
    }

    @Test
    void testTake0() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.take(iter, 0);

        assertExhausted(mapped);
    }

    @Test
    void testTake() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.take(iter, 1);

        assertThat(() -> mapped, contains("a"));
        assertExhausted(mapped);
    }

    @Test
    void testTakeOverflow() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.take(iter, 12);

        assertThat(() -> mapped, contains("a", "b", "c"));
        assertExhausted(mapped);
    }

    @Test
    void testDropNegative() {
        Iterator<String> iter = iterOf("a", "b", "c");

        assertThrows(IllegalArgumentException.class, () -> IteratorUtil.advance(iter, -5));
    }

    @Test
    void testDrop0() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.drop(iter, 0);

        assertThat(() -> mapped, contains("a", "b", "c"));
        assertExhausted(mapped);
    }

    @Test
    void testDrop() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.drop(iter, 1);

        assertThat(() -> mapped, contains("b", "c"));
        assertExhausted(mapped);
    }

    @Test
    void testDropOverflow() {
        Iterator<String> iter = iterOf("a", "b", "c");

        Iterator<String> mapped = IteratorUtil.drop(iter, 12);

        assertExhausted(mapped);
    }

    @Test
    void testGetNegative() {
        Iterator<String> iter = iterOf("a", "b", "c");

        assertThrows(IllegalArgumentException.class, () -> IteratorUtil.getNth(iter, -5));
    }

    @Test
    void testGet0() {
        Iterator<String> iter = iterOf("a", "b", "c");


        String elt = IteratorUtil.getNth(iter, 0);

        assertEquals("a", elt);
    }

    @Test
    void testGetNth() {
        Iterator<String> iter = iterOf("a", "b", "c");

        String elt = IteratorUtil.getNth(iter, 1);

        assertEquals("b", elt);
    }

    @Test
    void testGetOverflow() {
        Iterator<String> iter = iterOf("a", "b", "c");

        String elt = IteratorUtil.getNth(iter, 12);

        assertNull(elt);
    }


    @Test
    void testLast() {
        Iterator<String> iter = iterOf("a", "b", "c");

        String elt = IteratorUtil.last(iter);

        assertEquals("c", elt);
        assertExhausted(iter);
    }

    @Test
    void testLastEmpty() {
        Iterator<String> iter = emptyIterator();

        String elt = IteratorUtil.last(iter);

        assertNull(elt);
    }

    @Test
    void testCount() {
        Iterator<String> iter = iterOf("a", "b", "c");

        int size = IteratorUtil.count(iter);

        assertEquals(size, 3);
        assertExhausted(iter);
    }

    @Test
    void testCountEmpty() {
        Iterator<String> iter = emptyIterator();

        int size = IteratorUtil.count(iter);

        assertEquals(size, 0);
    }

    @Test
    void testToList() {
        Iterator<String> iter = iterOf("a", "b", "c");

        List<String> lst = IteratorUtil.toList(iter);

        assertEquals(lst, listOf("a", "b", "c"));
        assertExhausted(iter);
    }

    @Test
    void testAsReversed() {
        List<String> iter = listOf("a", "b", "c");

        Iterable<String> mapped = IteratorUtil.asReversed(iter);

        assertThat(mapped, contains("c", "b", "a"));
    }

    @Test
    void testAsReversedIsRepeatable() {
        List<String> iter = listOf("a", "b", "c");

        Iterable<String> mapped = IteratorUtil.asReversed(iter);

        // doesn't exhaust iterator
        assertThat(mapped, contains("c", "b", "a"));
        assertThat(mapped, contains("c", "b", "a"));
        assertThat(mapped, contains("c", "b", "a"));
    }


    @Test
    void testDropLast() {
        Iterator<String> iter = iterOf("ab", "cdde", "e", "", "f", "fe");

        Iterator<String> dropped = IteratorUtil.dropLast(iter, 2);

        assertEquals(listOf("ab", "cdde", "e", ""), IteratorUtil.toList(dropped));
    }

    @Test
    void testDropLastOne() {
        Iterator<String> iter = iterOf("ab", "cdde", "e", "", "f", "fe");

        Iterator<String> dropped = IteratorUtil.dropLast(iter, 1);

        assertEquals(listOf("ab", "cdde", "e", "", "f"), IteratorUtil.toList(dropped));
    }

    @Test
    void testDropMoreThanSize() {
        Iterator<String> iter = iterOf("ab", "c");

        Iterator<String> dropped = IteratorUtil.dropLast(iter, 4);

        assertEquals(emptyList(), IteratorUtil.toList(dropped));
    }

    @Test
    void testDropLastZero() {
        Iterator<String> iter = iterOf("ab", "c");

        Iterator<String> dropped = IteratorUtil.dropLast(iter, 0);

        assertEquals(listOf("ab", "c"), IteratorUtil.toList(dropped));
    }

    @Test
    void testDropLastNegative() {
        Iterator<String> iter = iterOf("ab", "c");

        assertThrows(IllegalArgumentException.class, () -> IteratorUtil.dropLast(iter, -3));
    }

    private void assertExhausted(Iterator<?> mapped) {
        assertFalse(mapped.hasNext());
        assertThrows(NoSuchElementException.class, () -> mapped.next());
    }

    static <T> Iterator<T> iterOf(T... ts) {
        return Arrays.asList(ts).iterator();
    }

    static <T> List<T> listOf(T... ts) {
        return Arrays.asList(ts);
    }

}
