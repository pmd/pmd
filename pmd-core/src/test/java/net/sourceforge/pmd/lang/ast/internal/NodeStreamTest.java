/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.tree;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;


/**
 * @author Clément Fournier
 */
public class NodeStreamTest {


    private final DummyNode tree1 = tree(
        () ->
            node(
                node(
                    node(),
                    node(
                        node()
                    )
                ),
                node()
            )
    );


    private final DummyNode tree2 = tree(
        () ->
            node(
                node()
            )
    );


    @Test
    public void testChildrenStream() {
        assertThat(pathsOf(tree1.childrenStream()), contains("0", "1"));
    }


    @Test
    public void testDescendantStream() {
        assertThat(pathsOf(tree1.descendantStream()), contains("0", "00", "01", "010", "1"));
    }


    @Test
    public void testSingletonStream() {
        assertThat(pathsOf(tree1.singletonStream()), contains(""));
    }


    @Test
    public void testTreeStream() {
        assertThat(pathsOf(tree1.treeStream()), contains("", "0", "00", "01", "010", "1"));
    }


    @Test
    public void testNodeStreamsCanBeIteratedSeveralTimes() {
        NodeStream<Node> stream = tree1.descendantStream();

        assertThat(stream.count(), equalTo(5));
        assertThat(stream.count(), equalTo(5));

        assertThat(pathsOf(stream), contains("0", "00", "01", "010", "1"));
        assertThat(pathsOf(stream.filter(n -> n.jjtGetNumChildren() == 0)), contains("00", "010", "1"));
    }


    @Test
    public void testNodeStreamPipelineIsLazy() {

        MutableInt numEvals = new MutableInt();

        tree1.descendantStream().filter(n -> {
            numEvals.increment();
            return true;
        });

        assertThat(numEvals.getValue(), equalTo(0));
    }


    @Test
    public void testForkJoinUpstreamPipelineIsExecutedAtMostOnce() {

        MutableInt numEvals = new MutableInt();
        NodeStream<Node> stream =
            hook(numEvals::increment, tree1.descendantStream())
                .forkJoin(
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("0")),
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("1"))
                );

        assertThat(numEvals.getValue(), equalTo(0)); // not evaluated yet

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(5)); // evaluated *once* every element of the upper stream

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(5)); // not reevaluated
    }


    @Test
    public void testCachedStreamUpstreamPipelineIsExecutedAtMostOnce() {

        MutableInt upstreamEvals = new MutableInt();
        MutableInt downstreamEvals = new MutableInt();

        NodeStream<Node> stream =
            tree1.descendantStream()
                 .imageMatching("0.*")
                 .peek(n -> upstreamEvals.increment())
                 .cached()
                 .filter(n -> true)
                 .peek(n -> downstreamEvals.increment());

        assertThat(upstreamEvals.getValue(), equalTo(0));   // not evaluated yet

        assertThat(stream.count(), equalTo(4));

        assertThat(upstreamEvals.getValue(), equalTo(4));   // evaluated once
        assertThat(downstreamEvals.getValue(), equalTo(4)); // evaluated once

        assertThat(stream.count(), equalTo(4));

        assertThat(upstreamEvals.getValue(), equalTo(4));   // upstream was not reevaluated
        assertThat(downstreamEvals.getValue(), equalTo(8)); // downstream has been reevaluated
    }


    @Test
    public void testUnionIsLazy() {

        MutableInt tree1Evals = new MutableInt();
        MutableInt tree2Evals = new MutableInt();

        NodeStream<Node> unionStream = NodeStream.union(tree1.treeStream().peek(n -> tree1Evals.increment()),
                                                        tree2.treeStream().peek(n -> tree2Evals.increment()));

        assertThat(tree1Evals.getValue(), equalTo(0));   // not evaluated yet
        assertThat(tree2Evals.getValue(), equalTo(0));   // not evaluated yet

        assertSame(unionStream.first().get(), tree1);

        assertThat(tree1Evals.getValue(), equalTo(1));   // evaluated once
        assertThat(tree2Evals.getValue(), equalTo(0));   // not evaluated
    }


    @Test
    public void testSomeOperationsAreLazy() {

        MutableInt tree1Evals = new MutableInt();

        NodeStream<Node> unionStream = tree1.treeStream().peek(n -> tree1Evals.increment());

        int i = 0;

        assertThat(tree1Evals.getValue(), equalTo(i));      // not evaluated yet

        unionStream.first();

        assertThat(tree1Evals.getValue(), equalTo(++i));    // evaluated once

        unionStream.any();

        assertThat(tree1Evals.getValue(), equalTo(++i));    // evaluated once

        unionStream.none();

        assertThat(tree1Evals.getValue(), equalTo(++i));    // evaluated once

        // those don't trigger any evaluation

        unionStream.map(p -> p);
        unionStream.filter(p -> true);
        unionStream.append(tree2.treeStream());
        unionStream.prepend(tree2.treeStream());
        unionStream.flatMap(Node::treeStream);
        unionStream.iterator();
        unionStream.cached();
        unionStream.descendants();
        unionStream.children();

        assertThat(tree1Evals.getValue(), equalTo(i));      // not evaluated
    }


    private static <T extends Node> NodeStream<T> hook(Runnable hook, NodeStream<T> stream) {
        return stream.filter(t -> {
            hook.run();
            return true;
        });
    }


    private static List<String> pathsOf(NodeStream<?> stream) {
        return stream.toList(Node::getImage);
    }
}
