/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.internal;

import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.DummyTreeUtil.tree;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
    public void testNodeStreamPipelineIsExecutedAtMostOnce() {

        MutableInt numEvals = new MutableInt();

        NodeStream<Node> stream = tree1.descendantStream().filter(n -> {
            numEvals.increment();
            return true;
        });

        assertThat(numEvals.getValue(), equalTo(0)); // not evaluated yet

        assertThat(stream.count(), equalTo(5));

        assertThat(numEvals.getValue(), equalTo(5)); // evaluated

        assertThat(stream.count(), equalTo(5));

        assertThat(pathsOf(stream), contains("0", "00", "01", "010", "1"));
        assertThat(pathsOf(stream.filter(n -> n.jjtGetNumChildren() == 0)), contains("00", "010", "1"));

        assertThat(numEvals.getValue(), equalTo(5)); // not evaluated any more than necessary
    }


    @Test
    public void testOnlyStreamHeadIsCached() {

        NodeStream<Node> treeStream = tree1.descendantStream();

        NodeStream<Node> filtered = treeStream.filter(n -> n.jjtGetNumChildren() == 0); // filter the leaves

        assertFalse(((NodeStreamImpl) treeStream).isCached());
        assertFalse(((NodeStreamImpl) filtered).isCached());

        assertThat(filtered.count(), equalTo(3));

        assertFalse(((NodeStreamImpl) treeStream).isCached());
        assertTrue(((NodeStreamImpl) filtered).isCached());

    }


    private static List<String> pathsOf(NodeStream<?> stream) {
        return stream.toList(Node::getImage);
    }
}
