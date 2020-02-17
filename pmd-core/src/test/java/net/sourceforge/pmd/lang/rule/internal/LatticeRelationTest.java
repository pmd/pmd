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

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());


        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(4), "4");
        lattice.put(setOf(4, 3), "43");

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

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), "12");
        lattice.put(setOf(1), "1");
        lattice.put(setOf(3), "3");

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

        LatticeRelation<Set<Integer>, String> lattice = setLattice(it -> it.size() != 2);


        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(4), "4");
        lattice.put(setOf(4, 3), "43");
        lattice.put(setOf(4, 3, 5), "435");

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

        lattice.put(setOf(4, 3, 6), "436");

        lattice.freezeTopo();

        assertEquals(setOf("4", "435", "436"), lattice.get(setOf(4))); // value "43" has been pruned
    }


    @Test
    public void testInitialSetFilter() {

        LatticeRelation<Set<Integer>, String> lattice =
            new LatticeRelation<>(
                setTopoOrder(),
                setOf(setOf(1, 2), setOf(2, 3), emptySet()),
                Objects::toString
            );

        lattice.put(setOf(1, 2, 3), "123");
        lattice.put(setOf(1, 2), "12");
        lattice.put(setOf(1), "1");
        lattice.put(setOf(2, 3, 4), "234");
        lattice.put(setOf(4, 3, 5, 6), "435");

        // before filter:

        // https://dreampuf.github.io/GraphvizOnline/#strict%20digraph%20%7B%0An0%20%5B%20shape%3Dbox%2C%20label%3D%22%5B%5D%22%20%5D%3B%0An1%20%5B%20shape%3Dbox%2C%20label%3D%22%5B1%5D%22%20%5D%3B%0An2%20%5B%20shape%3Dbox%2C%20label%3D%22%5B2%5D%22%20%5D%3B%0An3%20%5B%20shape%3Dbox%2C%20label%3D%22%5B1%2C%202%5D%22%20%5D%3B%0An4%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%5D%22%20%5D%3B%0An5%20%5B%20shape%3Dbox%2C%20label%3D%22%5B1%2C%203%5D%22%20%5D%3B%0An6%20%5B%20shape%3Dbox%2C%20label%3D%22%5B4%5D%22%20%5D%3B%0An7%20%5B%20shape%3Dbox%2C%20label%3D%22%5B2%2C%203%5D%22%20%5D%3B%0An8%20%5B%20shape%3Dbox%2C%20label%3D%22%5B5%5D%22%20%5D%3B%0An9%20%5B%20shape%3Dbox%2C%20label%3D%22%5B1%2C%202%2C%203%5D%22%20%5D%3B%0An10%20%5B%20shape%3Dbox%2C%20label%3D%22%5B2%2C%204%5D%22%20%5D%3B%0An11%20%5B%20shape%3Dbox%2C%20label%3D%22%5B6%5D%22%20%5D%3B%0An12%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%204%5D%22%20%5D%3B%0An13%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%205%5D%22%20%5D%3B%0An14%20%5B%20shape%3Dbox%2C%20label%3D%22%5B2%2C%203%2C%204%5D%22%20%5D%3B%0An15%20%5B%20shape%3Dbox%2C%20label%3D%22%5B4%2C%205%5D%22%20%5D%3B%0An16%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%206%5D%22%20%5D%3B%0An17%20%5B%20shape%3Dbox%2C%20label%3D%22%5B4%2C%206%5D%22%20%5D%3B%0An18%20%5B%20shape%3Dbox%2C%20label%3D%22%5B5%2C%206%5D%22%20%5D%3B%0An19%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%204%2C%205%5D%22%20%5D%3B%0An20%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%204%2C%206%5D%22%20%5D%3B%0An21%20%5B%20shape%3Dbox%2C%20label%3D%22%5B3%2C%205%2C%206%5D%22%20%5D%3B%0An22%20%5B%20shape%3Dbox%2C%20label%3D%22%5B4%2C%205%2C%206%5D%22%20%5D%3B%0An23%20%5B%20shape%3Dbox%2C%20label%3D%22%5B4%2C%203%2C%205%2C%206%5D%22%20%5D%3B%0An0%20-%3E%20n11%3B%0An0%20-%3E%20n8%3B%0An0%20-%3E%20n4%3B%0An0%20-%3E%20n1%3B%0An0%20-%3E%20n6%3B%0An0%20-%3E%20n2%3B%0An1%20-%3E%20n5%3B%0An1%20-%3E%20n3%3B%0An2%20-%3E%20n10%3B%0An2%20-%3E%20n3%3B%0An2%20-%3E%20n7%3B%0An3%20-%3E%20n9%3B%0An4%20-%3E%20n12%3B%0An4%20-%3E%20n16%3B%0An4%20-%3E%20n13%3B%0An4%20-%3E%20n5%3B%0An4%20-%3E%20n7%3B%0An5%20-%3E%20n9%3B%0An6%20-%3E%20n12%3B%0An6%20-%3E%20n15%3B%0An6%20-%3E%20n17%3B%0An6%20-%3E%20n10%3B%0An7%20-%3E%20n14%3B%0An7%20-%3E%20n9%3B%0An8%20-%3E%20n18%3B%0An8%20-%3E%20n15%3B%0An8%20-%3E%20n13%3B%0An10%20-%3E%20n14%3B%0An11%20-%3E%20n18%3B%0An11%20-%3E%20n16%3B%0An11%20-%3E%20n17%3B%0An12%20-%3E%20n19%3B%0An12%20-%3E%20n14%3B%0An12%20-%3E%20n20%3B%0An13%20-%3E%20n19%3B%0An13%20-%3E%20n21%3B%0An15%20-%3E%20n19%3B%0An15%20-%3E%20n22%3B%0An16%20-%3E%20n21%3B%0An16%20-%3E%20n20%3B%0An17%20-%3E%20n22%3B%0An17%20-%3E%20n20%3B%0An18%20-%3E%20n21%3B%0An18%20-%3E%20n22%3B%0An19%20-%3E%20n23%3B%0An20%20-%3E%20n23%3B%0An21%20-%3E%20n23%3B%0An22%20-%3E%20n23%3B%0A%7D

        // after filter:

        // http://bit.ly/2STFsum

        lattice.freezeTopo();

        assertEquals(setOf("123"), lattice.get(setOf(1, 2, 3)));
        assertEquals(emptySet(), lattice.get(setOf(4))); // pruned
        assertEquals(emptySet(), lattice.get(setOf(4, 5))); // pruned
        assertEquals(setOf("12", "123"), lattice.get(setOf(1, 2)));
        assertEquals(setOf("123", "234"), lattice.get(setOf(2, 3)));
        assertEquals(setOf("234"), lattice.get(setOf(2, 3, 4)));
        assertEquals(setOf("1", "12", "123", "234", "435"), lattice.get(emptySet()));

        lattice.unfreezeTopo();

        lattice.put(setOf(2, 3, 4), "234*");

        lattice.freezeTopo();

        assertEquals(setOf("123", "234", "234*"), lattice.get(setOf(2, 3))); // value "43" has been pruned
    }


    @Test
    public void testDiamond() {

        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), "12");

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

        LatticeRelation<String, String> lattice = stringLattice(PredicateUtil.always());

        lattice.put("abc", "val");

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

        LatticeRelation<String, String> lattice = stringLattice(s -> s.length() != 2 && s.length() != 1);

        lattice.put("abc", "val");

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


    @Test
    public void testToString() {
        LatticeRelation<Set<Integer>, String> lattice = setLattice(PredicateUtil.always());

        lattice.put(setOf(1, 2), "12");

        lattice.freezeTopo();

        //    {1,2}
        //    /   \
        //  {1}   {2}
        //    \   /
        //     { }

        assertEquals("strict digraph {\n"
                         + "n0 [ shape=box, label=\"[]\" ];\n"
                         + "n1 [ shape=box, label=\"[1]\" ];\n"
                         + "n2 [ shape=box, label=\"[2]\" ];\n"
                         + "n3 [ shape=box, label=\"[1, 2]\" ];\n"
                         + "n0 -> n1;\n"
                         + "n0 -> n2;\n"
                         + "n2 -> n3;\n"
                         + "n1 -> n3;\n"
                         + "}", lattice.toString());
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
                                    : IteratorUtil.singletonIterator(str.substring(1));
    }


}
