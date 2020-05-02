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
 * Unit test for {@link Node} tree traversal methods
 */
public class BoundaryTraversalTest {

    private DummyNode rootNode;

    private DummyNode newDummyNode(boolean boundary) {
        return new DummyNode(boundary);
    }

    private DummyNode addChild(final DummyNode parent, final DummyNode child) {
        parent.addChild(child, parent.getNumChildren()); // Append child at the end
        return parent;
    }

    @Before
    public void setUpSampleNodeTree() {
        rootNode = newDummyNode(false);
    }

    @Test
    public void testBoundaryIsHonored() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).toList();
        assertEquals(1, descendantsOfType.size());
        assertTrue(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchFromBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).first().descendants(DummyNode.class).toList();
        assertEquals(1, descendantsOfType.size());
        assertFalse(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    public void testSearchFromBoundaryFromNonOptimisedStream() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).take(1).descendants(DummyNode.class).toList();
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
