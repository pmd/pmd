/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.Test;

public class SymMonoidTest {

    @Test
    public void testSetMonoid() {

        SymMonoid<Set<String>> monoid = SymMonoid.forSet();

        this.neutralTest(monoid, Stream.of(
            emptySet(),
            setOf("a", "b"),
            setOf("a")
        ));

        assertEquals(emptySet(), monoid.apply(new HashSet<>(), new HashSet<>()));
        assertEquals(setOf("a", "c"), monoid.apply(setOf("a"), setOf("c")));
        assertEquals(setOf("a", "c"), monoid.apply(setOf("a", "c"), emptySet()));

    }

    private <T> void neutralTest(SymMonoid<T> monoid, Stream<T> generator) {
        generator.forEach(t -> {
            assertEquals(t, monoid.apply(t, monoid.zero()));
            assertEquals(t, monoid.apply(monoid.zero(), t));

            if (t.equals(monoid.zero())) {
                assertEquals(t, monoid.apply(t, t));
            }
        });
    }
}
