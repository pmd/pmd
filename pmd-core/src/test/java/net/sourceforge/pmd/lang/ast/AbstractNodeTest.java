/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * Unit test for {@link AbstractNode}.
 */
@RunWith(JUnitParamsRunner.class)
public class AbstractNodeTest {
    private static final int NUM_CHILDREN = 3;
    private static final int NUM_GRAND_CHILDREN = 3;

    // Note that in order to successfully run JUnitParams, we need to explicitly use `Integer` instead of `int`

    private Integer[] childrenIndexes() {
        return getIntRange(NUM_CHILDREN);
    }

    private Integer[] grandChildrenIndexes() {
        return getIntRange(NUM_GRAND_CHILDREN);
    }

    private static Integer[] getIntRange(final int exclusiveLimit) {
        final Integer[] childIndexes = new Integer[exclusiveLimit];
        for (int i = 0; i < exclusiveLimit; i++) {
            childIndexes[i] = i;
        }
        return childIndexes;
    }

    public Object childrenAndGrandChildrenIndexes() {
        final Integer[] childrenIndexes = childrenIndexes();
        final Integer[] grandChildrenIndexes = grandChildrenIndexes();
        final Object[] indexes = new Object[childrenIndexes.length * grandChildrenIndexes.length];
        int i = 0;
        for (final int childIndex : childrenIndexes) {
            for (final int grandChildIndex : grandChildrenIndexes) {
                indexes[i++] = new Integer[] { childIndex, grandChildIndex };
            }
        }
        return indexes;
    }

    private int id;
    private Node rootNode;

    private int nextId() {
        return id++;
    }

    private Node newDummyNode() {
        return new DummyNode(nextId());
    }

    private static void addChild(final Node parent, final Node child) {
        parent.jjtAddChild(child, parent.jjtGetNumChildren()); // Append child at the end
        child.jjtSetParent(parent);
    }

    @Before
    public void setUpSampleNodeTree() {
        id = 0;
        rootNode = newDummyNode();

        for (int i = 0; i < NUM_CHILDREN; i++) {
            final Node child = newDummyNode();
            for (int j = 0; j < NUM_GRAND_CHILDREN; j++) {
                final Node grandChild = newDummyNode();
                addChild(child, grandChild);
            }
            addChild(rootNode, child);
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method
     */
    @Test
    @Parameters(method = "childrenIndexes")
    public void testRemoveChildOfRootNode(final int childIndex) {
        final Node child = rootNode.jjtGetChild(childIndex);

        // Check that the child has the expected properties
        assertEquals(NUM_CHILDREN, rootNode.jjtGetNumChildren());
        assertEquals(rootNode, child.jjtGetParent());
        assertEquals(NUM_GRAND_CHILDREN, child.jjtGetNumChildren());
        final Node[] grandChildren = new Node[child.jjtGetNumChildren()];
        for (int i = 0; i < grandChildren.length; i++) {
            final Node grandChild = child.jjtGetChild(i);
            grandChildren[i] = grandChild;
            assertEquals(child, grandChild.jjtGetParent());
        }

        // Do the actual removal
        child.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_CHILDREN - 1, rootNode.jjtGetNumChildren());
        assertNull(child.jjtGetParent());
        assertEquals(0, child.jjtGetNumChildren());
        for (final Node grandChild : grandChildren) {
            assertNull(grandChild.jjtGetParent());
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * This is a border case as the root node does not have any parent.
     */
    @Test
    public void testRemoveRootNode() {
        // Check that the root node has the expected properties
        assertEquals(NUM_CHILDREN, rootNode.jjtGetNumChildren());
        assertNull(rootNode.jjtGetParent());
        final Node[] children = new Node[rootNode.jjtGetNumChildren()];
        for (int i = 0; i < children.length; i++) {
            final Node child = rootNode.jjtGetChild(i);
            children[i] = child;
            assertEquals(rootNode, child.jjtGetParent());
        }

        // Do the actual removal
        rootNode.remove();

        // Check that conditions have been successfully changed
        assertEquals(0, rootNode.jjtGetNumChildren());
        assertNull(rootNode.jjtGetParent());
        for (final Node aChild : children) {
            assertNull(aChild.jjtGetParent());
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * These are border cases as grandchildren nodes do not have any child.
     */
    @Test
    @Parameters(method = "childrenAndGrandChildrenIndexes")
    public void testRemoveGrandChildNode(final Integer childIndex, final Integer grandChildIndex) {
        final Node child = rootNode.jjtGetChild(childIndex);
        final Node grandChild = child.jjtGetChild(grandChildIndex);

        // Check that the child has the expected properties
        assertEquals(NUM_GRAND_CHILDREN, child.jjtGetNumChildren());
        assertEquals(0, grandChild.jjtGetNumChildren());
        assertEquals(child, grandChild.jjtGetParent());

        // Do the actual removal
        grandChild.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_GRAND_CHILDREN - 1, child.jjtGetNumChildren());
        assertEquals(0, grandChild.jjtGetNumChildren());
        assertNull(grandChild.jjtGetParent());
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     */
    @Test
    @Parameters(method = "childrenIndexes")
    public void testRemoveRootNodeChildAtIndex(final Integer childIndex) {
        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];

        // Check that prior conditions are OK
        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
            assertEquals(i, originalChildren[i].jjtGetChildIndex());
            if (i > 0) {
                assertNotEquals(originalChildren[i - 1], originalChildren[i]);
            }
        }
        assertEquals(NUM_CHILDREN, rootNode.jjtGetNumChildren());

        // Do the actual removal
        rootNode.removeChildAtIndex(childIndex);

        // Check that conditions have been successfully changed
        assertEquals(NUM_CHILDREN - 1, rootNode.jjtGetNumChildren());
        int j = 0;
        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            if (j == childIndex) { // Skip the removed child
                j++;
            }
            // Check that the nodes have been rightly shifted
            assertEquals(originalChildren[j], rootNode.jjtGetChild(i));
            // Check that the child index has been updated
            assertEquals(i, rootNode.jjtGetChild(i).jjtGetChildIndex());
            j++;
        }
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     * Test how invalid indexes cases are handled.
     */
    @Test
    public void testRemoveChildAtIndexWithInvalidIndex() {
        // No assert as the test is considered passed if no exception is thrown
        rootNode.removeChildAtIndex(-1);
        rootNode.removeChildAtIndex(rootNode.jjtGetNumChildren());
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     * This is a border case as the method invocation should do nothing.
     */
    @Test
    public void testRemoveChildAtIndexOnNodeWithNoChildren() {
        final Node grandChild = rootNode.jjtGetChild(0).jjtGetChild(0);
        // Check that this node does not have any children
        assertEquals(0, grandChild.jjtGetNumChildren());

        grandChild.removeChildAtIndex(0);

        // If here, no exception has been thrown
        // Check that this node still does not have any children
        assertEquals(0, grandChild.jjtGetNumChildren());
    }
}
