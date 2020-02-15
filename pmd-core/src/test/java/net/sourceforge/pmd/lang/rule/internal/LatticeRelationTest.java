/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.PredicateUtil;

public class LatticeRelationTest {

    @Test
    public void testCustomTopo() {

        LatticeRelation<Set<Integer>, Set<String>> lattice = new LatticeRelation<>(
            SymMonoid.forSet(),
            SymMonoid.forMutableSet(),
            LatticeRelationTest.setTopoOrder(),
            PredicateUtil.always(),
            Objects::toString
        );


        lattice.put(setOf(1, 2, 3), setOf("123"));
        lattice.put(setOf(4), setOf("4"));
        lattice.put(setOf(4, 3), setOf("43"));

        lattice.freezeTopo();

        // http://bit.ly/2vwwMlZ

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(setOf("4", "43"), lattice.get(setOf(4)));
        assertEquals(setOf("43", "123"), lattice.get(setOf(3)));
        assertEquals(setOf("43", "123", "4"), lattice.get(emptySet()));
        assertEquals(emptySet(), lattice.get(setOf(5)));
    }

    @Test
    public void testClearing() {

        LatticeRelation<Set<Integer>, Set<String>> lattice = new LatticeRelation<>(
            SymMonoid.forSet(),
            SymMonoid.forMutableSet(),
            LatticeRelationTest.setTopoOrder(),
            PredicateUtil.always(),
            Objects::toString
        );

        lattice.put(setOf(1, 2), setOf("12"));
        lattice.put(setOf(1), setOf("1"));
        lattice.put(setOf(3), setOf("3"));

        lattice.freezeTopo();

        assertEquals(setOf("12"), lattice.get(setOf(2)));
        assertEquals(setOf("12", "1"), lattice.get(setOf(1)));
        assertEquals(setOf("12"), lattice.get(setOf(1, 2)));
        assertEquals(setOf("3"), lattice.get(setOf(3)));
        assertEquals(emptySet(), lattice.get(setOf(5)));
        assertEquals(setOf("1", "12", "3"), lattice.get(emptySet()));

        lattice.unfreezeTopo();

        lattice.clearValues();

        lattice.freezeTopo();

        lattice.getNodes().values().forEach(it -> assertEquals(emptySet(), it.computeValue()));
    }


    @Test
    public void testTopoFilter() {

        LatticeRelation<Set<Integer>, Set<String>> lattice = new LatticeRelation<>(
            SymMonoid.forSet(),
            SymMonoid.forMutableSet(),
            LatticeRelationTest.setTopoOrder(),
            // filter out sets with size 2
            // this cuts out one level of the graph
            // goal of the test is to ensure, that their predecessors (sets with size > 2)
            // are still connected to successors (size < 2)
            it -> it.size() != 2,
            Objects::toString
        );


        lattice.put(setOf(1, 2, 3), setOf("123"));
        lattice.put(setOf(4), setOf("4"));
        lattice.put(setOf(4, 3), setOf("43"));
        lattice.put(setOf(4, 3, 5), setOf("435"));

        // before filter:

        // http://bit.ly/31Xve0v

        // after filter:

        // http://bit.ly/2Hr2F1P

        lattice.freezeTopo();

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(setOf("4", "435"), lattice.get(setOf(4))); // value "43" has been pruned
        assertEquals(setOf("123", "435"), lattice.get(setOf(3)));
        assertEquals(setOf("123", "4", "435"), lattice.get(emptySet()));

        lattice.unfreezeTopo();

        lattice.put(setOf(4, 3, 6), setOf("436"));

        lattice.freezeTopo();

        assertEquals(setOf("4", "435", "436"), lattice.get(setOf(4))); // value "43" has been pruned
    }


    /**
     * Direct successors of a set are all the sets that have exactly
     * one less element. For example:
     * <pre>{@code
     *
     * {1, 2, 3} -> {1, 2} {1, 3} {2, 3}
     * {2, 3} -> {2} {3}
     * {2} -> {}
     * etc
     *
     * }</pre>
     *
     * See eg http://bit.ly/31Xve0v
     */
    private static <T> TopoOrder<Set<T>> setTopoOrder() {
        return node -> {
            Set<Set<T>> successors = new HashSet<>();

            for (T s : node) {
                PSet<T> minus = HashTreePSet.<T>empty().plusAll(node).minus(s);
                successors.add(minus);
            }

            return successors.iterator();
        };
    }


}
