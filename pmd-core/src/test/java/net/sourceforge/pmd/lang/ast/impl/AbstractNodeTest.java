/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl;

import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.node;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.root;
import static net.sourceforge.pmd.lang.ast.impl.DummyTreeUtil.tree;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * Unit test for {@link AbstractNode}.
 */
class AbstractNodeTest {
    private static final int NUM_CHILDREN = 3;
    private static final int NUM_GRAND_CHILDREN = 3;

    // Note that in order to successfully run JUnitParams, we need to explicitly use `Integer` instead of `int`

    static Integer[] childrenIndexes() {
        return getIntRange(NUM_CHILDREN);
    }

    static Integer[] grandChildrenIndexes() {
        return getIntRange(NUM_GRAND_CHILDREN);
    }

    private static Integer[] getIntRange(final int exclusiveLimit) {
        final Integer[] childIndexes = new Integer[exclusiveLimit];
        for (int i = 0; i < exclusiveLimit; i++) {
            childIndexes[i] = i;
        }
        return childIndexes;
    }

    static Object childrenAndGrandChildrenIndexes() {
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

    private DummyRootNode rootNode;

    @BeforeEach
    void setUpSampleNodeTree() {
        rootNode = tree(
            () -> {
                DummyRootNode root = root();

                for (int i = 0; i < NUM_CHILDREN; i++) {
                    final DummyNode child = node();
                    for (int j = 0; j < NUM_GRAND_CHILDREN; j++) {
                        child.addChild(node(), j);
                    }
                    root.addChild(child, i);
                }
                return root;
            }
        );

    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method
     */
    @ParameterizedTest
    @MethodSource("childrenIndexes")
    void testRemoveChildOfRootNode(final int childIndex) {
        final DummyNode child = rootNode.getChild(childIndex);
        final List<? extends DummyNode> grandChildren = child.children().toList();

        // Do the actual removal
        child.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_CHILDREN - 1, rootNode.getNumChildren());
        assertNull(child.getParent());
        // The child node is expected to still have all its children and vice versa
        assertEquals(NUM_GRAND_CHILDREN, child.getNumChildren());
        for (final Node grandChild : grandChildren) {
            assertEquals(child, grandChild.getParent());
        }
    }

    @Test
    void testPrevNextSiblings() {
        DummyRootNode root = tree(() -> root(node(), node()));

        assertNull(root.getNextSibling());
        assertNull(root.getPreviousSibling());

        DummyNode c0 = root.getChild(0);
        DummyNode c1 = root.getChild(1);

        assertSame(c0, c1.getPreviousSibling());
        assertSame(c1, c0.getNextSibling());
        assertNull(c1.getNextSibling());
        assertNull(c0.getPreviousSibling());
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * This is a border case as the root node does not have any parent.
     */
    @Test
    void testRemoveRootNode() {
        // Check that the root node has the expected properties
        final List<? extends DummyNode> children = rootNode.children().toList();

        // Do the actual removal
        rootNode.remove();

        // Check that conditions have been successfully changed, i.e.,
        //  the root node is expected to still have all its children and vice versa
        assertEquals(NUM_CHILDREN, rootNode.getNumChildren());
        assertNull(rootNode.getParent());
        for (final Node aChild : children) {
            assertEquals(rootNode, aChild.getParent());
        }
    }

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * These are border cases as grandchildren nodes do not have any child.
     */
    @ParameterizedTest
    @MethodSource("childrenAndGrandChildrenIndexes")
    void testRemoveGrandChildNode(final int childIndex, final int grandChildIndex) {
        final DummyNode child = rootNode.getChild(childIndex);
        final DummyNode grandChild = child.getChild(grandChildIndex);

        // Do the actual removal
        grandChild.remove();

        // Check that conditions have been successfully changed
        assertEquals(NUM_GRAND_CHILDREN - 1, child.getNumChildren());
        assertEquals(0, grandChild.getNumChildren());
        assertNull(grandChild.getParent());
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     */
    @ParameterizedTest
    @MethodSource("childrenIndexes")
    void testRemoveRootNodeChildAtIndex(final int childIndex) {
        final List<? extends DummyNode> originalChildren = rootNode.children().toList();

        // Do the actual removal
        rootNode.removeChildAtIndex(childIndex);

        // Check that conditions have been successfully changed
        assertEquals(NUM_CHILDREN - 1, rootNode.getNumChildren());
        int j = 0;
        for (int i = 0; i < rootNode.getNumChildren(); i++) {
            if (j == childIndex) { // Skip the removed child
                j++;
            }
            // Check that the nodes have been rightly shifted
            assertEquals(originalChildren.get(j), rootNode.getChild(i));
            // Check that the child index has been updated
            assertEquals(i, rootNode.getChild(i).getIndexInParent());
            j++;
        }
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     * Test that invalid indexes cases are handled without exception.
     */
    @Test
    void testRemoveChildAtIndexWithInvalidIndex() {
        try {
            rootNode.removeChildAtIndex(-1);
            rootNode.removeChildAtIndex(rootNode.getNumChildren());
        } catch (final Exception e) {
            fail("No exception was expected.");
        }
    }

    /**
     * Explicitly tests the {@code removeChildAtIndex} method.
     * This is a border case as the method invocation should do nothing.
     */
    @ParameterizedTest
    @MethodSource("grandChildrenIndexes")
    void testRemoveChildAtIndexOnNodeWithNoChildren(final int grandChildIndex) {
        // grandChild does not have any child
        final DummyNode grandChild = rootNode.getChild(grandChildIndex).getChild(grandChildIndex);

        // Do the actual removal
        grandChild.removeChildAtIndex(0);

        // If here, no exception has been thrown
        // Check that this node still does not have any children
        assertEquals(0, grandChild.getNumChildren());
    }

}
