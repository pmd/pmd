/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link Node} tree traversal methods
 */
class BoundaryTraversalTest {

    private DummyNode rootNode;

    private DummyNode newDummyNode(boolean boundary) {
        return new DummyNode(boundary);
    }

    private DummyNode addChild(final DummyNode parent, final DummyNode child) {
        parent.addChild(child, parent.getNumChildren()); // Append child at the end
        return parent;
    }

    @BeforeEach
    void setUpSampleNodeTree() {
        rootNode = newDummyNode(false);
    }

    @Test
    void testBoundaryIsHonored() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).toList();
        assertEquals(1, descendantsOfType.size());
        assertTrue(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    void testSearchFromBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).first().descendants(DummyNode.class).toList();
        assertEquals(1, descendantsOfType.size());
        assertFalse(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    void testSearchFromBoundaryFromNonOptimisedStream() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).take(1).descendants(DummyNode.class).toList();
        assertEquals(1, descendantsOfType.size());
        assertFalse(descendantsOfType.get(0).isFindBoundary());
    }

    @Test
    void testSearchIgnoringBoundary() {
        addChild(rootNode, addChild(newDummyNode(true), newDummyNode(false)));

        List<DummyNode> descendantsOfType = rootNode.descendants(DummyNode.class).crossFindBoundaries().toList();
        assertEquals(2, descendantsOfType.size());
        assertTrue(descendantsOfType.get(0).isFindBoundary());
        assertFalse(descendantsOfType.get(1).isFindBoundary());
    }
}
