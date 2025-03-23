/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.nodeB;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.root;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyNodeTypeB;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * Asserts invariants independent of the NodeStream implementation. Error
 * messages are not great but coverage is.
 */
class NodeStreamBlanketTest<T extends Node> {

    private static final List<Node> ASTS = Arrays.asList(
        tree(
            () ->
                root(

                    node(
                        node(),
                        nodeB(
                            node(
                                nodeB()
                            )
                        ),
                        node(),
                        nodeB()
                    ),
                    node()
                )
        ),
        tree(
            () ->
                root(
                    node(),
                    node(),
                    nodeB(
                        node()
                    ),
                    node()
                )
        )
    );

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testToListConsistency(NodeStream<T> stream) {
        List<T> toList = stream.toList();
        List<T> collected = stream.collect(Collectors.toList());
        List<T> fromStream = stream.toStream().collect(Collectors.toList());
        List<T> cached = stream.cached().toList();

        assertEquals(toList, collected);
        assertEquals(toList, fromStream);
        assertEquals(toList, cached);
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testToListSize(NodeStream<T> stream) {
        List<T> toList = stream.toList();

        assertEquals(toList.size(), stream.count());
    }


    @ParameterizedTest
    @MethodSource("nodeStreamVariantsNonEmpty")
    void testLast(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("nonEmpty", NodeStream::nonEmpty),
            prop("last() == toList().last()", it -> it.last() == it.toList().get(it.count() - 1))
        );
    }

    @ParameterizedTest
    @MethodSource("nodeStreamVariantsNonEmpty")
    void testFirst(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("nonEmpty", NodeStream::nonEmpty),
            prop("first() == toList().get(0)", it -> it.first() == it.toList().get(0))
        );
    }


    @ParameterizedTest
    @MethodSource("nodeStreamVariantsNonEmpty")
    void testDrop(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("nonEmpty", NodeStream::nonEmpty),
            prop("drop(0) == this", it -> it.drop(0) == it),
            prop("drop(1).count() == count() - 1", it -> it.drop(1).count() == it.count() - 1),
            prop("drop(1).toList() == toList().tail()", it -> it.drop(1).toList().equals(tail(it.toList())))
        );
    }

    @ParameterizedTest
    @MethodSource("nodeStreamVariantsNonEmpty")
    void testDropLast(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("nonEmpty", NodeStream::nonEmpty),
            prop("dropLast(0) == this", it -> it.dropLast(0) == it),
            prop("dropLast(1).count() == count() - 1", it -> it.dropLast(1).count() == it.count() - 1),
            prop("dropLast(1).toList() == toList().init()", it -> it.dropLast(1).toList().equals(init(it.toList())))
        );
    }

    @ParameterizedTest
    @MethodSource("nodeStreamVariantsMoreThanOne")
    void testDropMoreThan1(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("count() > 1", it -> it.count() > 1),
            prop("drop(2).toList() == toList().tail().tail()", it -> it.drop(2).toList().equals(tail(tail(it.toList())))),
            prop("drop(1).drop(1) == drop(2)", it -> it.drop(1).drop(1).toList().equals(it.drop(2).toList()))
        );
    }

    @ParameterizedTest
    @MethodSource("nodeStreamVariantsNonEmpty")
    void testTake(NodeStream<T> stream) {
        assertImplication(
            stream,
            prop("nonEmpty", NodeStream::nonEmpty),
            prop("it.take(0).count() == 0", it -> it.take(0).count() == 0),
            prop("it.take(1).count() == 1", it -> it.take(1).count() == 1),
            prop("it.take(it.count()).count() == it.count()", it -> it.take(it.count()).count() == it.count())
        );
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testGet(NodeStream<T> stream) {
        for (int i = 0; i < 100; i++) {
            assertSame(stream.get(i), stream.drop(i).first(), "stream.get(i) == stream.drop(i).first()");
        }
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testGetNegative(NodeStream<T> stream) {
        assertThrows(IllegalArgumentException.class, () -> stream.get(-1));
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testDropNegative(NodeStream<T> stream) {
        assertThrows(IllegalArgumentException.class, () -> stream.drop(-1));
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testTakeNegative(NodeStream<T> stream) {
        assertThrows(IllegalArgumentException.class, () -> stream.take(-1));
    }

    @ParameterizedTest
    @MethodSource("allNodeStreamVariants")
    void testEmpty(NodeStream<T> stream) {
        assertEquivalence(
            stream,
            prop("isEmpty", NodeStream::isEmpty),
            prop("!nonEmpty", it -> !it.nonEmpty()),
            prop("last() == null", it -> it.last() == null),
            prop("first() == null", it -> it.first() == null),
            prop("first(_ -> true) == null", it -> it.first(i -> true) == null),
            prop("first(Node.class) == null", it -> it.first(Node.class) == null),
            prop("count() == 0", it -> it.count() == 0),
            prop("any(_) == false", it -> !it.any(i -> true)),
            prop("all(_) == true", it -> it.all(i -> false)),
            prop("none(_) == true", it -> it.none(i -> true))
        );
    }

    static Collection<NodeStream<?>> nodeStreamVariantsNonEmpty() {
        return allNodeStreamVariants().stream().filter(NodeStream::nonEmpty).collect(Collectors.toList());
    }

    static Collection<NodeStream<?>> nodeStreamVariantsMoreThanOne() {
        return allNodeStreamVariants().stream().filter(n -> n.count() > 1).collect(Collectors.toList());
    }


    static Collection<NodeStream<?>> allNodeStreamVariants() {
        return ASTS.stream().flatMap(
            root -> Stream.of(
                root.asStream(),
                root.children().first().asStream(),
                // keep this, so that transformation are tested on empty node streams as well
                NodeStream.empty()
            )
        ).flatMap(
            // add some transformations on each of them
            stream -> Stream.of(
                stream,
                stream.drop(1),
                stream.take(2),
                stream.filter(n -> !n.getImage().isEmpty()),
                stream.firstChild(DummyNodeTypeB.class),
                stream.children(DummyNodeTypeB.class),
                stream.descendants(DummyNodeTypeB.class),
                stream.ancestors(DummyNodeTypeB.class),
                stream.descendants(),
                stream.ancestors(),
                stream.ancestorsOrSelf(),
                stream.followingSiblings(),
                stream.precedingSiblings(),
                stream.descendantsOrSelf(),
                stream.children(),
                stream.children().filter(c -> c.getImage().equals("0")),
                stream.children(DummyNode.class)
            )
        ).flatMap(
            // add some transformations on each of them
            stream -> Stream.of(
                stream,
                stream.filterIs(DummyNode.class),
                stream.take(1),
                stream.drop(1),
                stream.filter(n -> !n.getImage().isEmpty()),
                stream.cached()
            )
        ).collect(Collectors.toCollection(ArrayList::new));
    }

    @SafeVarargs
    private static <T> void assertEquivalence(T input, Prop<? super T>... properties) {

        for (Prop<? super T> prop1 : properties) {
            for (Prop<? super T> prop2 : properties) {
                boolean p1 = prop1.test(input);
                assertEquals(
                    p1, prop2.test(input),
                    "Expected (" + prop1.description + ") === (" + prop2.description
                            + "), but the LHS was " + p1 + " and the RHS was " + !p1
                );
            }
        }
    }

    @SafeVarargs
    private static <T> void assertImplication(T input, Prop<? super T> precond, Prop<? super T>... properties) {
        assumeTrue(precond.test(input));

        for (Prop<? super T> prop : properties) {
            assertTrue(
                prop.test(input),
                "Expected (" + precond.description + ") to entail (" + prop.description
                        + "), but the latter was false"
            );
        }
    }

    static <T> Prop<T> prop(String desc, Predicate<? super T> pred) {
        return new Prop<>(pred, desc);
    }

    static <T> List<T> tail(List<T> ts) {
        return ts.subList(1, ts.size());
    }

    static <T> List<T> init(List<T> ts) {
        return ts.subList(0, ts.size() - 1);
    }

    static class Prop<T> {

        final Predicate<? super T> property;
        final String description;

        Prop(Predicate<? super T> property, String description) {
            this.property = property;
            this.description = description;
        }

        boolean test(T t) {
            return property.test(t);
        }

    }
}
