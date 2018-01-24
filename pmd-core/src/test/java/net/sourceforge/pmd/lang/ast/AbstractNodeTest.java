/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.pmd.autofix.rewriteevents.RewriteEvent;
import net.sourceforge.pmd.autofix.rewriteevents.RewriteEventFactory;

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
                indexes[i++] = new Integer[] {childIndex, grandChildIndex};
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

    // ----------------- Remove Test Cases ----------------- //

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code remove(index)} method
     */
    @Test
    @Parameters(method = "childrenIndexes")
    public void testRemoveChildOfRootNode(final int childIndex) {
        final Node child = rootNode.jjtGetChild(childIndex);
        final Node[] grandChildren = new Node[child.jjtGetNumChildren()];
        for (int i = 0; i < grandChildren.length; i++) {
            final Node grandChild = child.jjtGetChild(i);
            grandChildren[i] = grandChild;
        }

        // Do the actual removal
        child.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_CHILDREN - 1, rootNode.jjtGetNumChildren());
        assertNull(child.jjtGetParent());
        // The child node is expected to still have all its children and vice versa
        assertEquals(NUM_GRAND_CHILDREN, child.jjtGetNumChildren());
        for (final Node grandChild : grandChildren) {
            assertEquals(child, grandChild.jjtGetParent());
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code remove(index)} method.
     * This is a border case as the root node does not have any parent.
     */
    @Test
    public void testRemoveRootNode() {
        // Check that the root node has the expected properties
        final Node[] children = new Node[rootNode.jjtGetNumChildren()];
        for (int i = 0; i < children.length; i++) {
            final Node child = rootNode.jjtGetChild(i);
            children[i] = child;
        }

        // Do the actual removal
        rootNode.remove();

        // Check that conditions have been successfully changed, i.e.,
        //  the root node is expected to still have all its children and vice versa
        assertEquals(NUM_CHILDREN, rootNode.jjtGetNumChildren());
        assertNull(rootNode.jjtGetParent());
        for (final Node aChild : children) {
            assertEquals(rootNode, aChild.jjtGetParent());
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code remove(index)} method.
     * These are border cases as grandchildren nodes do not have any child.
     */
    @Test
    @Parameters(method = "childrenAndGrandChildrenIndexes")
    public void testRemoveGrandChildNode(final int childIndex, final int grandChildIndex) {
        final Node child = rootNode.jjtGetChild(childIndex);
        final Node grandChild = child.jjtGetChild(grandChildIndex);

        // Do the actual removal
        grandChild.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_GRAND_CHILDREN - 1, child.jjtGetNumChildren());
        assertEquals(0, grandChild.jjtGetNumChildren());
        assertNull(grandChild.jjtGetParent());
    }

    /**
     * Explicitly tests the {@code remove(index)} method.
     */
    @Test
    @Parameters(method = "childrenIndexes")
    public void testRemoveRootNodeChildAtIndex(final int childIndex) {
        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];

        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
        }

        // Do the actual removal
        rootNode.remove(childIndex);

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
     * Explicitly tests the {@code remove(index)} method.
     * Test that invalid indexes cases are handled without exception.
     */
    @Test
    public void testRemoveChildAtIndexWithInvalidIndex() {
        try {
            rootNode.remove(-1);
            rootNode.remove(rootNode.jjtGetNumChildren());
        } catch (final Exception e) {
            fail("No exception was expected.");
        }
    }

    /**
     * Explicitly tests the {@code remove(index)} method.
     * This is a border case as the method invocation should do nothing.
     */
    @Test
    @Parameters(method = "grandChildrenIndexes")
    public void testRemoveChildAtIndexOnNodeWithNoChildren(final int grandChildIndex) {
        // grandChild does not have any child
        final Node grandChild = rootNode.jjtGetChild(grandChildIndex).jjtGetChild(grandChildIndex);

        // Do the actual removal
        grandChild.remove(0);

        // If here, no exception has been thrown
        // Check that this node still does not have any children
        assertEquals(0, grandChild.jjtGetNumChildren());
    }

    // ----------------- Insert Test Cases ----------------- //

    @SuppressWarnings("unused") // Used by JUnitParams in `testInsertChild` test case
    private Object testInsertParameters() {
        final DummyNode node = new DummyNode(0);
        return new Object[] {
            new Object[] {node, -5, -5, false},
            new Object[] {node, -1, -1, false},
            new Object[] {node, 0, 0, true},
            new Object[] {node, NUM_CHILDREN, NUM_CHILDREN, true},
            new Object[] {node, NUM_CHILDREN + 5, NUM_CHILDREN, true},
        };
    }

    /**
     * Explicitly tests the {@code insert} method
     */
    @Test
    @Parameters(method = "testInsertParameters")
    public void testInsert(final Node newNode, final int index,
                           final int expectedInsertionIndex, final boolean expectedInsertion) {
        final int oldNumChildren = rootNode.jjtGetNumChildren();
        final int expectedNumChildren = expectedInsertion ? oldNumChildren + 1 : oldNumChildren;

        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];

        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
        }

        // Do the actual insertion
        final int insertionIndex = rootNode.insert(newNode, index);
        // Check state conditions
        assertEquals(expectedInsertionIndex, insertionIndex);
        assertEquals(expectedNumChildren, rootNode.jjtGetNumChildren());
        // Children context have been correctly updated
        int originalIndex = 0;
        int newIndex = 0;
        while (newIndex < rootNode.jjtGetNumChildren()) {
            final Node iNode = rootNode.jjtGetChild(newIndex);
            // Check that the child index has been updated
            assertEquals(newIndex, iNode.jjtGetChildIndex());
            // Check that the node's parent has been updated
            assertEquals(rootNode, iNode.jjtGetParent());

            if (expectedInsertion && newIndex == expectedInsertionIndex) {
                // Check that the new child has been rightly inserted, if expected
                assertEquals(newNode, iNode);
            } else {
                // Check that the original nodes have been rightly shifted
                assertEquals(originalChildren[originalIndex], iNode);
                originalIndex++;
            }
            newIndex++;
        }
    }

    /**
     * Explicitly tests the {@code insert} method
     */
    @Test
    public void testInsertWithInvalidChild() {
        final int expectedNumChildren = rootNode.jjtGetNumChildren();
        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];

        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
        }

        // Do the actual insertion
        try {
            // Check that even with invalid index, the first check is over the newChild variable.
            // Invalid index cases only are already tested in method `testInsert`.
            rootNode.insert(null, -1);
            fail("Should have thrown an exception because null new child has been passed as argument");
        } catch (final RuntimeException ignored) {
            // Expected flow due to newChild == null
        }

        // Check that original children context have not been updated
        assertEquals(expectedNumChildren, rootNode.jjtGetNumChildren());
        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            final Node iNode = rootNode.jjtGetChild(i);
            assertEquals(i, iNode.jjtGetChildIndex());
            assertEquals(rootNode, iNode.jjtGetParent());
            assertEquals(originalChildren[i], iNode);
        }
    }

    // ----------------- Replace Test Cases ----------------- //

    @SuppressWarnings("unused") // Used by JUnitParams in `testInsertChild` test case
    private Object testReplaceParameters() {
        final DummyNode node = new DummyNode(0);
        return new Object[] {
            new Object[] {node, -5, false},
            new Object[] {node, -1, false},
            new Object[] {node, 0, true},
            new Object[] {node, NUM_CHILDREN - 1, true},
            new Object[] {node, NUM_CHILDREN, false},
            new Object[] {node, NUM_CHILDREN + 1, false},
        };
    }

    /**
     * Explicitly tests the {@code replace} method
     */
    @Test
    @Parameters(method = "testReplaceParameters")
    public void testReplace(final Node newNode, final int index, final boolean expectedReplacement) {
        final int expectedNumChildren = rootNode.jjtGetNumChildren();
        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];
        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
        }

        // Do the actual replacement
        rootNode.replace(newNode, index);

        // Check state conditions
        assertEquals(expectedNumChildren, rootNode.jjtGetNumChildren());
        // Children context have been correctly updated
        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            final Node iNode = rootNode.jjtGetChild(i);
            assertEquals(i, iNode.jjtGetChildIndex());
            assertEquals(rootNode, iNode.jjtGetParent());
            final Node expectedNode = expectedReplacement && i == index ? newNode : originalChildren[i];
            assertEquals(expectedNode, iNode);
        }
    }

    /**
     * Explicitly tests the {@code replace} method
     */
    @Test
    public void testReplaceWithInvalidChild() {
        final int expectedNumChildren = rootNode.jjtGetNumChildren();
        final Node[] originalChildren = new Node[rootNode.jjtGetNumChildren()];
        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.jjtGetChild(i);
        }

        // Do the actual replacement
        try {
            // Check that even with invalid index, the first check is over the newChild variable.
            // Invalid index cases only are already tested in method `testReplace`.
            rootNode.replace(null, -1);
            fail("Should have thrown an exception because null new child has been passed as argument");
        } catch (final RuntimeException ignored) {
            // Expected flow due to newChild == null
        }

        // Check that original children context have not been updated
        assertEquals(expectedNumChildren, rootNode.jjtGetNumChildren());
        for (int i = 0; i < rootNode.jjtGetNumChildren(); i++) {
            final Node iNode = rootNode.jjtGetChild(i);
            assertEquals(i, iNode.jjtGetChildIndex());
            assertEquals(rootNode, iNode.jjtGetParent());
            assertEquals(originalChildren[i], iNode);
        }
    }

    // ----------------- Node Events Test Cases ----------------- //

    /**
     * Test that rewrite operations (insert, replace and remove) carried out over the AST are correctly
     *  mapped to the corresponding rewrite events
     */
    @Test
    public void testRewriteEventsOverAST() {
        // In this case, we are only testing that the parent node return the correct events associated with each
        //  type of rewrite operation. Exhaustive tests over the events manipulation are done over the
        //  class in charge of that implementation.
        final Node newDummyNode = newDummyNode();
        rootNode.insert(newDummyNode, 0);
        final Node oldReplaceNode = rootNode.jjtGetChild(1);
        rootNode.replace(newDummyNode, 1);
        final Node oldRemoveNode = rootNode.jjtGetChild(2);
        rootNode.remove(2);

        assertTrue(rootNode.haveChildrenChanged());

        final RewriteEvent insertRewriteEvent = RewriteEventFactory.newInsertRewriteEvent(rootNode, newDummyNode, 0);
        final RewriteEvent replaceRewriteEvent = RewriteEventFactory.newReplaceRewriteEvent(rootNode, oldReplaceNode, newDummyNode, 1);
        final RewriteEvent removeRewriteEvent = RewriteEventFactory.newRemoveRewriteEvent(rootNode, oldRemoveNode, 2);

        final RewriteEvent[] rewriteEvents = rootNode.getChildrenRewriteEvents();
        assertEquals(insertRewriteEvent, rewriteEvents[0]);
        assertEquals(replaceRewriteEvent, rewriteEvents[1]);
        assertEquals(removeRewriteEvent, rewriteEvents[2]);
    }
}
