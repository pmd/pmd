/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import static java.util.Collections.emptyIterator;
import static java.util.Collections.emptySet;
import static net.sourceforge.pmd.internal.util.IteratorUtil.singletonIterator;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pcollections.HashTreePSet;
import org.pcollections.PSet;

import net.sourceforge.pmd.internal.util.PredicateUtil;

public class LatticeRelationTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testCustomTopo() {

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());


        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(4), "4");
        lattice.put(setOf(4, 3), "43");

        // http://bit.ly/39J3KOu

        lattice.makeReadable();

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(setOf("4", "43"), lattice.get(setOf(4)));
        assertEquals(setOf("43", "123"), lattice.get(setOf(3)));
        assertEquals(setOf("43", "123", "4"), lattice.get(emptySet()));
        assertEquals(emptySet(), lattice.get(setOf(5)));
    }

    @Test
    public void testClearing() {

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), "12");
        lattice.put(setOf(1), "1");
        lattice.put(setOf(3), "3");

        lattice.makeReadable();

        assertEquals(setOf("12"), lattice.get(setOf(2)));
        assertEquals(setOf("12", "1"), lattice.get(setOf(1)));
        assertEquals(setOf("12"), lattice.get(setOf(1, 2)));
        assertEquals(setOf("3"), lattice.get(setOf(3)));
        assertEquals(emptySet(), lattice.get(setOf(5)));
        assertEquals(setOf("1", "12", "3"), lattice.get(emptySet()));

        lattice.makeWritable();
        lattice.clearValues();
        lattice.makeReadable();

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

        LatticeRelation<Set<Integer>, String> lattice = setLattice(it -> it.size() != 2);


        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(4), "4");
        lattice.put(setOf(4, 3), "43");
        lattice.put(setOf(4, 3, 5), "435");

        // before filter:

        // http://bit.ly/38vRsce

        // after filter:

        // http://bit.ly/2SxejyC

        lattice.makeReadable();

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(setOf("4", "43", "435"), lattice.get(setOf(4)));
        assertEquals(setOf("123", "43", "435"), lattice.get(setOf(3)));
        assertEquals(setOf("123", "4", "43", "435"), lattice.get(emptySet()));

        lattice.makeWritable();

        lattice.put(setOf(4, 3, 6), "436");

        lattice.makeReadable();

        assertEquals(setOf("4", "43", "435", "436"), lattice.get(setOf(4)));
    }


    @Test
    public void testInitialSetFilter() {

        LatticeRelation<Set<Integer>, String> lattice =
            new LatticeRelation<>(
                setTopoOrder(),
                setOf(setOf(1, 2), setOf(1, 2, 3), setOf(2, 3), emptySet()),
                Objects::toString
            );

        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(1, 2), "12");
        lattice.put(setOf(1), "1");
        lattice.put(setOf(2, 3, 4), "234");
        lattice.put(setOf(4, 3, 5, 6), "435");

        lattice.makeReadable();

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(setOf("12", "123"), lattice.get(setOf(1, 2)));
        assertEquals(setOf("123", "234"), lattice.get(setOf(2, 3)));
        assertEquals(setOf("1", "12", "123", "234", "435"), lattice.get(emptySet()));

        assertEquals(emptySet(), lattice.get(setOf(4))); // not in initial set
        assertEquals(emptySet(), lattice.get(setOf(4, 5))); // not in initial set
        assertEquals(emptySet(), lattice.get(setOf(2, 3, 4))); // not in initial set

        lattice.makeWritable();

        lattice.put(setOf(2, 3, 4), "234*");

        lattice.makeReadable();

        assertEquals(setOf("123", "234", "234*"), lattice.get(setOf(2, 3))); // value "43" has been pruned
    }


    @Test
    public void testDiamond() {

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), "12");

        // We have

        //    {1,2}
        //    /   \
        //  {1}   {2}
        //    \   /
        //     { }

        // Goal is to assert, that when we ask first for the value of { },
        // the value of every node is correctly computed, even if they're
        // reachable from several paths
        lattice.makeReadable();

        assertEquals(setOf("12"), lattice.get(emptySet()));
        assertEquals(setOf("12"), lattice.get(setOf(1)));
        assertEquals(setOf("12"), lattice.get(setOf(2)));
        assertEquals(setOf("12"), lattice.get(setOf(1, 2)));
    }


    @Test
    public void testFilterOnChainSetup() {
        // setup for the next test (difference here is no filter)

        LatticeRelation<String, String> lattice = stringLattice(PredicateUtil.always());

        lattice.put("abc", "val");

        // We have "abc" <: "bc" <: "c" <: ""
        lattice.makeReadable();

        assertEquals(setOf("val"), lattice.get(""));
        assertEquals(setOf("val"), lattice.get("abc"));
        assertEquals(setOf("val"), lattice.get("bc"));
        assertEquals(setOf("val"), lattice.get("c"));
        assertEquals(emptySet(), lattice.get("d"));
    }

    @Test
    public void testFilterOnChain() {

        LatticeRelation<String, String> lattice = stringLattice(s -> s.length() != 2 && s.length() != 1);

        lattice.put("abc", "val");

        // We have "abc" <: "bc" <: "c" <: ""

        // We filter out both "bc" and "c"
        // "abc" should still be connected to ""

        lattice.makeReadable();

        assertEquals(setOf("val"), lattice.get(""));
        assertEquals(setOf("val"), lattice.get("abc"));
        assertEquals(emptySet(), lattice.get("bc"));
        assertEquals(emptySet(), lattice.get("c"));
        assertEquals(emptySet(), lattice.get("d"));
    }

    @Test
    public void testTransitiveSucc() {

        LatticeRelation<String, String> lattice =
            stringLattice(s -> s.equals("c") || s.equals("bc"));

        lattice.put("abc", "val");
        lattice.put("bc", "v2");

        // We have "abc" <: "bc" <: "c" <: ""
        lattice.makeReadable();

        assertEquals(emptySet(), lattice.transitiveQuerySuccs(""));
        assertEquals(emptySet(), lattice.get(""));

        assertEquals(setOf("c", "bc"), lattice.transitiveQuerySuccs("abc"));
        assertEquals(emptySet(), lattice.get("abc"));

        assertEquals(setOf("c"), lattice.transitiveQuerySuccs("bc"));
        assertEquals(setOf("val", "v2"), lattice.get("bc"));

        assertEquals(emptySet(), lattice.transitiveQuerySuccs("c"));
        assertEquals(setOf("val", "v2"), lattice.get("c"));

        assertEquals(emptySet(), lattice.transitiveQuerySuccs("d"));
        assertEquals(emptySet(), lattice.get("d"));
    }


    @Test
    public void testToString() {
        LatticeRelation<Set<Integer>, String> lattice = setLattice(set -> set.size() < 2);

        lattice.put(setOf(1, 2), "12");

        //    {1,2}
        //    /   \
        //  {1}   {2}
        //    \   /
        //     { }

        // all {1}, {2}, and { } are query nodes, not {1,2}

        assertEquals("strict digraph {\n"
                         + "n0 [ shape=box, color=green, label=\"[]\" ];\n"
                         + "n1 [ shape=box, color=green, label=\"[1]\" ];\n"
                         + "n2 [ shape=box, color=green, label=\"[2]\" ];\n"
                         + "n3 [ shape=box, color=black, label=\"[1, 2]\" ];\n"
                         + "n1 -> n0;\n" // {1}   -> { }
                         + "n2 -> n0;\n" // {2}   -> { }
                         + "n3 -> n0;\n" // {1,2} -> { }
                         + "n3 -> n1;\n" // {1,2} -> {1}
                         + "n3 -> n2;\n" // {1,2} -> {2}
                         + "}", lattice.toString());
    }

    @Test
    public void testCycleDetection() {
        List<String> cycle = Arrays.asList("a", "b", "c", "d");

        TopoOrder<String> cyclicOrder = str -> {
            int i = cycle.indexOf(str);
            return i < 0 ? emptyIterator()
                         : singletonIterator(cycle.get((i + 1) % cycle.size()));
        };

        LatticeRelation<String, String> lattice = new LatticeRelation<>(cyclicOrder, PredicateUtil.always(), Objects::toString);

        expect.expect(IllegalStateException.class);

        lattice.put("a", "1");

    }

    @NonNull
    private LatticeRelation<String, String> stringLattice(Predicate<String> filter) {
        return new LatticeRelation<>(stringTopoOrder(), filter, Objects::toString);
    }


    @NonNull
    private LatticeRelation<Set<Integer>, String> setLattice(Predicate<Set<Integer>> filter) {
        return new LatticeRelation<>(setTopoOrder(), filter, Objects::toString);
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
                                    : singletonIterator(str.substring(1));
    }


}
