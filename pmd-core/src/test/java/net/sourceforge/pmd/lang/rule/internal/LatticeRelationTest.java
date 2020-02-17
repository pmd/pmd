/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Test;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.IteratorUtil;
import net.sourceforge.pmd.internal.util.PredicateUtil;

public class LatticeRelationTest {

    @Test
    public void testCustomTopo() {

        LatticeRelation<Set<Integer>, Set<String>> lattice = setLattice(PredicateUtil.always());


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

        LatticeRelation<Set<Integer>, Set<String>> lattice = setLattice(PredicateUtil.always());

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

        assertEquals(emptySet(), lattice.get(setOf(2)));
        assertEquals(emptySet(), lattice.get(setOf(1)));
        assertEquals(emptySet(), lattice.get(setOf(1, 2)));
        assertEquals(emptySet(), lattice.get(setOf(3)));
        assertEquals(emptySet(), lattice.get(setOf(5)));
        assertEquals(emptySet(), lattice.get(emptySet()));
    }


    @Test
    public void testTopoFilter() {

        // filter out sets with size 2
        // this cuts out one level of the graph
        // goal of the test is to ensure, that their predecessors (sets with size > 2)
        // are still connected to successors (size < 2)

        LatticeRelation<Set<Integer>, Set<String>> lattice = setLattice(it -> it.size() != 2);


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


    @Test
    public void testDiamond() {

        LatticeRelation<Set<Integer>, Set<String>> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), setOf("12"));

        lattice.freezeTopo();

        // We have

        //    {1,2}
        //    /   \
        //  {1}   {2}
        //    \   /
        //     { }

        // Goal is to assert, that when we ask first for the value of { },
        // the value of every node is correctly computed, even if they're
        // reachable from several paths

        assertEquals(setOf("12"), lattice.get(emptySet()));
        assertEquals(setOf("12"), lattice.get(setOf(1)));
        assertEquals(setOf("12"), lattice.get(setOf(2)));
        assertEquals(setOf("12"), lattice.get(setOf(1, 2)));
    }


    @Test
    public void testFilterOnChainSetup() {
        // setup for the next test (difference here is no filter)

        LatticeRelation<String, Set<String>> lattice = stringLattice(PredicateUtil.always());

        lattice.put("abc", setOf("val"));

        lattice.freezeTopo();

        // We have "abc" <: "bc" <: "c" <: ""

        assertEquals(setOf("val"), lattice.get(""));
        assertEquals(setOf("val"), lattice.get("abc"));
        assertEquals(setOf("val"), lattice.get("bc"));
        assertEquals(setOf("val"), lattice.get("c"));
        assertEquals(emptySet(), lattice.get("d"));
    }

    @Test
    public void testFilterOnChain() {

        LatticeRelation<String, Set<String>> lattice = stringLattice(s -> s.length() != 2 && s.length() != 1);

        lattice.put("abc", setOf("val"));

        lattice.freezeTopo();

        // We have "abc" <: "bc" <: "c" <: ""

        // We filter out both "bc" and "c"
        // "abc" should still be connected to ""

        assertEquals(setOf("val"), lattice.get(""));
        assertEquals(setOf("val"), lattice.get("abc"));
        assertEquals(emptySet(), lattice.get("bc"));
        assertEquals(emptySet(), lattice.get("c"));
        assertEquals(emptySet(), lattice.get("d"));
    }

    @NonNull
    public LatticeRelation<String, Set<String>> stringLattice(Predicate<String> filter) {
        return new LatticeRelation<>(
            IdMonoid.forSet(),
            IdMonoid.forMutableSet(),
            LatticeRelationTest.stringTopoOrder(),
            filter,
            Objects::toString
        );
    }


    @NonNull
    public LatticeRelation<Set<Integer>, Set<String>> setLattice(Predicate<Set<Integer>> filter) {
        return new LatticeRelation<>(
            IdMonoid.forSet(),
            IdMonoid.forMutableSet(),
            LatticeRelationTest.setTopoOrder(),
            filter,
            Objects::toString
        );
    }

    /**
     * Direct successors of a set are all the sets that have exactly
     * one less element. For example:
     * <pre>{@code
     *
     * {1, 2, 3} <: {1, 2}, {1, 3}, {2, 3}
     * {2, 3} <: {2}, {3}
     * {2} <: {}
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

    /**
     * Generates a linear topo order according to suffix order. This
     * can never form diamonds, as any string has at most one successor.
     * Eg
     * <pre>{@code
     * "abc" <: "bc" <: "c" <: ""
     * }</pre>
     */
    private static TopoOrder<String> stringTopoOrder() {
        return str -> str.isEmpty() ? emptyIterator()
                                    : IteratorUtil.singletonIterator(str.substring(1));
    }


}
