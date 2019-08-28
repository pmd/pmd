/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singletonList;
import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

public class MonoidTest {

    @Test
    public void testSetMonoid() {

        Monoid<Set<String>> monoid = Monoid.forSet();

        this.neutralTest(monoid, Stream.of(
            setOf(),
            emptySet(),
            setOf("a", "b"),
            setOf("a")
        ));

        assertEquals(emptySet(), monoid.apply(new HashSet<>(), new HashSet<>()));
        assertEquals(setOf("a", "c"), monoid.apply(setOf("a"), setOf("c")));
        assertEquals(setOf("a", "c"), monoid.apply(setOf("a", "c"), emptySet()));
        assertEquals(setOf("a", "c"), monoid.apply(setOf("a", "c"), setOf()));

    }

    @Test
    public void testListMonoid() {

        Monoid<List<String>> monoid = Monoid.forList();

        this.neutralTest(monoid, Stream.of(
            listOf(),
            emptyList(),
            listOf("a", "b"),
            listOf("a")
        ));

        assertEquals(listOf("c"), monoid.apply(new ArrayList<>(), singletonList("c")));
        assertEquals(listOf("a", "c"), monoid.apply(listOf("a"), listOf("c")));
        assertEquals(listOf("c", "a"), monoid.apply(listOf("c"), listOf("a")));
        assertEquals(listOf("a", "c"), monoid.apply(listOf("a", "c"), emptyList()));
        assertEquals(listOf("a", "c"), monoid.apply(listOf("a", "c"), listOf()));

    }

    private <T> void neutralTest(Monoid<T> monoid, Stream<T> generator) {
        generator.forEach(t -> {
            assertEquals(t, monoid.apply(t, monoid.zero()));
            assertEquals(t, monoid.apply(monoid.zero(), t));

            if (t.equals(monoid.zero())) {
                assertEquals(t, monoid.apply(t, t));
            }
        });
    }
}
