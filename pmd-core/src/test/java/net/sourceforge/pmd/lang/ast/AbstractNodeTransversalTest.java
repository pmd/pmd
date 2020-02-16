/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link AbstractNode} tree transversal methods
 */
public class AbstractNodeTransversalTest {
    private int id;
    private Node rootNode;

    private int nextId() {
        return id++;
    }

    private Node newDummyNode(boolean boundary) {
        return new DummyNode(nextId(), boundary);
    }

    private Node addChild(final Node parent, final Node child) {
        parent.jjtAddChild(child, parent.getNumChildren()); // Append child at the end
        child.jjtSetParent(parent);
        return parent;
    }

    @Before
    public void setUpSampleNodeTree() {
        id = 0;
        rootNode = newDummyNode(false);
    }

    @Test
    public void testBoundaryIsHonored() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.findDescendantsOfType(DummyNode.class);
        assertEquals(1, descendantsOfType.size());
        assertTrue(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchFromBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.findDescendantsOfType(DummyNode.class).get(0).findDescendantsOfType(DummyNode.class);
        assertEquals(1, descendantsOfType.size());
        assertFalse(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchIgnoringBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.findDescendantsOfType(DummyNode.class, true);
        assertEquals(2, descendantsOfType.size());
        assertTrue(descendantsOfType.get(0).isFindBoundary());
        assertFalse(descendantsOfType.get(1).isFindBoundary());
    }
}
