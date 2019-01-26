/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.tree;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
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
            hook(tree1.descendantStream(), numEvals::increment)
                .forkJoin(
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("0")),
                    n -> NodeStream.of(n).filter(m -> m.hasImageEqualTo("1"))
                );

        assertThat(numEvals.getValue(), equalTo(0)); // not evaluated yet

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(5)); // evaluated *once* every element of the upper stream

        assertThat(stream.count(), equalTo(2));

        assertThat(numEvals.getValue(), equalTo(10)); // reevaluated
    }




    private static <T extends Node> NodeStream<T> hook(NodeStream<T> stream, Runnable hook) {
        return stream.filter(t -> {
            hook.run();
            return true;
        });
    }


    private static List<String> pathsOf(NodeStream<?> stream) {
        return stream.toList(Node::getImage);
    }
}
