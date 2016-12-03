/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;

import org.junit.Test;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.NodeType;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;

public class DataFlowNodeTest {

    @Test
    public void testAddPathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);
        assertEquals(parent.getChildren().size(), 1);
        assertTrue(child.getParents().contains(parent));
        assertTrue(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathToChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        parent.addPathToChild(child);

        assertTrue(parent.removePathToChild(child));
        assertFalse(child.getParents().contains(parent));
        assertFalse(parent.getChildren().contains(child));
    }

    @Test
    public void testRemovePathWithNonChild() {
        DataFlowNode parent = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode child = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        assertFalse(parent.removePathToChild(child));
    }

    @Test
    public void testReverseParentPathsTo() {
        DataFlowNode parent1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        DataFlowNode parent2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 12, false);
        DataFlowNode child1 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        DataFlowNode child2 = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 13, false);
        parent1.addPathToChild(child1);
        parent2.addPathToChild(child1);
        assertTrue(parent1.getChildren().contains(child1));

        child1.reverseParentPathsTo(child2);
        assertTrue(parent1.getChildren().contains(child2));
        assertFalse(parent1.getChildren().contains(child1));
        assertTrue(parent2.getChildren().contains(child2));
        assertFalse(parent2.getChildren().contains(child1));

        assertEquals(0, child1.getParents().size());
        assertEquals(2, child2.getParents().size());
    }

    @Test
    public void testSetType() {
        DataFlowNode node = new StartOrEndDataFlowNode(new LinkedList<DataFlowNode>(), 10, false);
        node.setType(NodeType.BREAK_STATEMENT);
        assertTrue(node.isType(NodeType.BREAK_STATEMENT));
        assertFalse(node.isType(NodeType.CASE_LAST_STATEMENT));
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(DataFlowNodeTest.class);
    }
}
