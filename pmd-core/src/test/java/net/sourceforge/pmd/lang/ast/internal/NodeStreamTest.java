/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.followPath;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.nodeB;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.pathsOf;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.root;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.tree;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyNodeTypeB;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream;


/**
 * @author Clément Fournier
 */
public class NodeStreamTest {


    private final DummyNode tree1 = tree(
        () ->
            root(// ""
                 nodeB(// 0
                       node(), // 00
                       nodeB(// 01
                             node(), // 010
                             node(// 011
                                     node() // 0110
                             ),
                             node(), // 012
                             node()  // 013
                       )
                 ),
                 node() // 1
            )
    );


    private final DummyNode tree2 = tree(
        () ->
            root(
                node(),
                node(),
                node(
                    node()
                ),
                node()
            )
    );


    @Test
    public void testStreamConstructionIsNullSafe() {
        assertTrue(NodeStream.of((Node) null).isEmpty());
        assertThat(NodeStream.of(null, null, tree1).count(), equalTo(1));
        assertThat(NodeStream.fromIterable(Arrays.asList(tree1, null, null)).count(), equalTo(1));
        assertThat(NodeStream.ofOptional(Optional.empty()).count(), equalTo(0));
    }


    @Test
    public void testMapIsNullSafe() {
        assertTrue(tree1.descendantsOrSelf().map(n -> null).isEmpty());
    }


    @Test
    public void testFlatMapIsNullSafe() {
        assertTrue(tree1.descendantsOrSelf().flatMap(n -> null).isEmpty());
    }


    @Test
    public void testChildrenStream() {
        assertThat(pathsOf(tree1.children()), contains("0", "1"));
        assertThat(pathsOf(tree1.asStream().children()), contains("0", "1"));
    }


    @Test
    public void testChildrenEagerEvaluation() {
        NodeStream<? extends Node> children = tree1.children();
        assertEquals(AxisStream.ChildrenStream.class, children.getClass());
        NodeStream<Node> children1 = children.children();
        assertEquals(GreedyNStream.GreedyKnownNStream.class, children1.getClass());
        assertEquals(SingletonNodeStream.class, children1.filter(it -> it.getImage().endsWith("1")).getClass());
    }


    @Test
    public void testDescendantStream() {
        assertThat(pathsOf(tree1.descendants()), contains("0", "00", "01", "010", "011", "0110", "012", "013", "1"));
        assertThat(pathsOf(tree1.asStream().descendants()), contains("0", "00", "01", "010", "011", "0110", "012", "013", "1"));
    }

    @Test
    public void testSingletonStream() {
        assertThat(pathsOf(tree1.asStream()), contains(""));
        assertThat(pathsOf(NodeStream.of(tree1)), contains(""));
    }


    @Test
    public void testDescendantOrSelfStream() {
        assertThat(pathsOf(tree1.descendantsOrSelf()), contains("", "0", "00", "01", "010", "011", "0110", "012", "013", "1"));
        assertThat(pathsOf(NodeStream.of(tree1).descendantsOrSelf()), contains("", "0", "00", "01", "010", "011", "0110", "012", "013", "1"));
    }

    @Test
    public void testAncestors() {
        // 010
        Node node = tree1.children().children().children().first();
        assertEquals("010", node.getImage());
        assertThat(pathsOf(node.ancestors()), contains("01", "0", ""));
        assertThat(pathsOf(node.ancestorsOrSelf()), contains("010", "01", "0", ""));

        assertEquals("01", node.getNthParent(1).getImage());
        assertEquals("0", node.getNthParent(2).getImage());
        assertEquals("", node.getNthParent(3).getImage());
        assertNull(node.getNthParent(4));
    }

    @Test
    public void testAncestorsFiltered() {
        // 0110
        Node node = tree1.children().children().children().children().first();
        assertEquals("0110", node.getImage());
        assertThat(pathsOf(node.ancestors(DummyNodeTypeB.class)), contains("01", "0"));
        assertThat(pathsOf(node.ancestorsOrSelf().filterIs(DummyNodeTypeB.class)), contains("01", "0"));

    }

    @Test
    public void testAncestorsFilteredDrop() {
        // 0110
        Node node = tree1.children().children().children().children().first();
        assertEquals("0110", node.getImage());
        assertThat(pathsOf(node.ancestors(DummyNodeTypeB.class).drop(1)), contains("0"));
        assertThat(pathsOf(node.ancestorsOrSelf().filterIs(DummyNodeTypeB.class).drop(1)), contains("0"));

    }


    @Test
    public void testFollowingSiblings() {
        assertThat(pathsOf(followPath(tree2, "2").asStream().followingSiblings()), contains("3"));
        assertThat(pathsOf(followPath(tree2, "0").asStream().followingSiblings()), contains("1", "2", "3"));
        assertTrue(pathsOf(followPath(tree2, "3").asStream().followingSiblings()).isEmpty());
    }


    @Test
    public void testPrecedingSiblings() {
        assertThat(pathsOf(followPath(tree2, "2").asStream().precedingSiblings()), contains("0", "1"));
        assertThat(pathsOf(followPath(tree2, "3").asStream().precedingSiblings()), contains("0", "1", "2"));
        assertTrue(pathsOf(followPath(tree2, "0").asStream().precedingSiblings()).isEmpty());
    }

    @Test
    public void testRootSiblings() {
        assertTrue(tree2.asStream().precedingSiblings().isEmpty());
        assertTrue(tree2.asStream().followingSiblings().isEmpty());
    }

    @Test
    public void testAncestorStream() {
        assertThat(pathsOf(followPath(tree1, "01").ancestors()), contains("0", ""));
        assertThat(pathsOf(followPath(tree1, "01").asStream().ancestors()), contains("0", ""));
    }


    @Test
    public void testParentStream() {
        assertThat(pathsOf(followPath(tree1, "01").asStream().parents()), contains("0"));
    }


    @Test
    public void testAncestorStreamUnion() {
        assertThat(pathsOf(NodeStream.union(followPath(tree1, "01").ancestors(),
                                            tree2.children().ancestors())), contains("0", "", "", "", "", ""));
    }


    @Test
    public void testDistinct() {
        assertThat(pathsOf(NodeStream.union(followPath(tree1, "01").ancestors(),
                                            tree2.children().ancestors()).distinct()), contains("0", "", "")); // roots of both trees
    }


    @Test
    public void testGet() {
        // ("0", "00", "01", "010", "011", "0110", "012", "013", "1")
        DescendantNodeStream<DummyNode> stream = tree1.descendants();

        assertEquals("0", stream.get(0).getImage());
        assertEquals("00", stream.get(1).getImage());
        assertEquals("010", stream.get(3).getImage());
        assertEquals("011", stream.get(4).getImage());
        assertEquals("0110", stream.get(5).getImage());
        assertNull(stream.get(9));
    }

    @Test
    public void testNodeStreamsCanBeIteratedSeveralTimes() {
        DescendantNodeStream<DummyNode> stream = tree1.descendants();

        assertThat(stream.count(), equalTo(9));
        assertThat(stream.count(), equalTo(9));

        assertThat(pathsOf(stream), contains("0", "00", "01", "010", "011", "0110", "012", "013", "1"));
        assertThat(pathsOf(stream.filter(n -> n.getNumChildren() == 0)),
                   contains("00", "010", "0110", "012", "013", "1"));
    }


    @Test
    public void testNodeStreamPipelineIsLazy() {

        MutableInt numEvals = new MutableInt();

        tree1.descendants().filter(n -> {
            numEvals.increment();
            return true;
        });

        assertThat(numEvals.getValue(), equalTo(0));
    }


    @Test
    public void testForkJoinUpstreamPipelineIsExecutedAtMostOnce() {

        MutableInt numEvals = new MutableInt();
        NodeStream<Node> stream =
            NodeStream
                .forkJoin(
                    hook(numEvals::increment, tree1.descendants()),
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("0")),
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("1"))
                );

        // assertThat(numEvals.getValue(), equalTo(0)); // not evaluated yet

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(9)); // evaluated *once* every element of the upper stream

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(9)); // not reevaluated
    }


    @Test
    public void testCachedStreamUpstreamPipelineIsExecutedAtMostOnce() {

        MutableInt upstreamEvals = new MutableInt();
        MutableInt downstreamEvals = new MutableInt();

        NodeStream<DummyNode> stream =
            tree1.descendants()
                 .filter(n -> n.getImage().matches("0.*"))
                 .take(4)
                 .peek(n -> upstreamEvals.increment())
                 .cached()
                 .filter(n -> true)
                 .peek(n -> downstreamEvals.increment());

        // assertThat(upstreamEvals.getValue(), equalTo(0));   // not evaluated yet

        assertThat(stream.count(), equalTo(4));

        assertThat(upstreamEvals.getValue(), equalTo(4));   // evaluated once
        assertThat(downstreamEvals.getValue(), equalTo(4)); // evaluated once

        assertThat(stream.count(), equalTo(4));

        assertThat(upstreamEvals.getValue(), equalTo(4));   // upstream was not reevaluated
        assertThat(downstreamEvals.getValue(), equalTo(4)); // downstream was not reevaluated
    }


    @Test
    public void testUnionIsLazy() {

        MutableInt tree1Evals = new MutableInt();
        MutableInt tree2Evals = new MutableInt();

        NodeStream<Node> unionStream = NodeStream.union(tree1.descendantsOrSelf().peek(n -> tree1Evals.increment()),
                                                        tree2.descendantsOrSelf().peek(n -> tree2Evals.increment()));

        assertThat(tree1Evals.getValue(), equalTo(0));   // not evaluated yet
        assertThat(tree2Evals.getValue(), equalTo(0));   // not evaluated yet

        assertSame(unionStream.first(), tree1);

        assertThat(tree1Evals.getValue(), equalTo(1));   // evaluated once
        assertThat(tree2Evals.getValue(), equalTo(0));   // not evaluated
    }


    @Test
    public void testSomeOperationsAreLazy() {

        MutableInt tree1Evals = new MutableInt();

        NodeStream<DummyNode> unionStream = tree1.descendantsOrSelf().peek(n -> tree1Evals.increment());

        int i = 0;

        assertThat(tree1Evals.getValue(), equalTo(i));      // not evaluated yet

        unionStream.first();
        assertThat(tree1Evals.getValue(), equalTo(++i));    // evaluated once

        unionStream.nonEmpty();
        assertThat(tree1Evals.getValue(), equalTo(i));     // not evaluated, because of optimised implementation

        unionStream.isEmpty();
        assertThat(tree1Evals.getValue(), equalTo(i));     // not evaluated, because of optimised implementation

        // those don't trigger any evaluation

        unionStream.map(p -> p);
        unionStream.filter(p -> true);
        unionStream.append(tree2.descendantsOrSelf());
        unionStream.prepend(tree2.descendantsOrSelf());
        unionStream.flatMap(Node::descendantsOrSelf);
        unionStream.iterator();
        // unionStream.cached();
        unionStream.descendants();
        unionStream.ancestors();
        unionStream.followingSiblings();
        unionStream.precedingSiblings();
        unionStream.children();
        unionStream.distinct();
        unionStream.take(4);
        unionStream.drop(4);

        assertThat(tree1Evals.getValue(), equalTo(i));      // not evaluated
    }


    @Test
    public void testFollowingSiblingsNonEmpty() {
        DummyNode node = followPath(tree1, "012");

        NodeStream<Node> nodes = node.asStream().followingSiblings();

        assertTrue(nodes instanceof SingletonNodeStream);
        assertEquals("013", nodes.first().getImage());
    }

    @Test
    public void testPrecedingSiblingsNonEmpty() {
        DummyNode node = followPath(tree1, "011");

        NodeStream<Node> nodes = node.asStream().precedingSiblings();

        assertTrue(nodes instanceof SingletonNodeStream);
        assertEquals("010", nodes.first().getImage());
    }

    @Test
    public void testPrecedingSiblingsDrop() {
        DummyNode node = followPath(tree1, "012");

        NodeStream<Node> nodes = node.asStream().precedingSiblings().drop(1);

        assertThat(pathsOf(nodes), contains("011"));
    }

    @Test
    public void testFollowingSiblingsDrop() {
        DummyNode node = followPath(tree1, "011");

        NodeStream<Node> nodes = node.asStream().followingSiblings().drop(1);

        assertThat(pathsOf(nodes), contains("013"));
    }


    private static <T extends Node> NodeStream<T> hook(Runnable hook, NodeStream<T> stream) {
        return stream.filter(t -> {
            hook.run();
            return true;
        });
    }


}
