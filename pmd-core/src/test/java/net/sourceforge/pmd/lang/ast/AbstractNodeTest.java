/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jaxen.JaxenException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.pmd.junit.JavaUtilLoggingRule;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;


/**
 * Unit test for {@link AbstractNode}.
 */
@RunWith(JUnitParamsRunner.class)
public class AbstractNodeTest {
    private static final int NUM_CHILDREN = 3;
    private static final int NUM_GRAND_CHILDREN = 3;

    @Rule
    public JavaUtilLoggingRule loggingRule = new JavaUtilLoggingRule(Attribute.class.getName());

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

    private static Node addChild(final Node parent, final Node child) {
        parent.jjtAddChild(child, parent.getNumChildren()); // Append child at the end
        child.jjtSetParent(parent);
        return parent;
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
        final Node child = rootNode.getChild(childIndex);
        final Node[] grandChildren = new Node[child.getNumChildren()];
        for (int i = 0; i < grandChildren.length; i++) {
            final Node grandChild = child.getChild(i);
            grandChildren[i] = grandChild;
        }

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

    /**
     * Explicitly tests the {@code remove} method, and implicitly the {@code removeChildAtIndex} method.
     * This is a border case as the root node does not have any parent.
     */
    @Test
    public void testRemoveRootNode() {
        // Check that the root node has the expected properties
        final Node[] children = new Node[rootNode.getNumChildren()];
        for (int i = 0; i < children.length; i++) {
            final Node child = rootNode.getChild(i);
            children[i] = child;
        }

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
    @Test
    @Parameters(method = "childrenAndGrandChildrenIndexes")
    public void testRemoveGrandChildNode(final int childIndex, final int grandChildIndex) {
        final Node child = rootNode.getChild(childIndex);
        final Node grandChild = child.getChild(grandChildIndex);

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
    @Test
    @Parameters(method = "childrenIndexes")
    public void testRemoveRootNodeChildAtIndex(final int childIndex) {
        final Node[] originalChildren = new Node[rootNode.getNumChildren()];

        for (int i = 0; i < originalChildren.length; i++) {
            originalChildren[i] = rootNode.getChild(i);
        }

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
            assertEquals(originalChildren[j], rootNode.getChild(i));
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
    public void testRemoveChildAtIndexWithInvalidIndex() {
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
    @Test
    @Parameters(method = "grandChildrenIndexes")
    public void testRemoveChildAtIndexOnNodeWithNoChildren(final int grandChildIndex) {
        // grandChild does not have any child
        final Node grandChild = rootNode.getChild(grandChildIndex).getChild(grandChildIndex);

        // Do the actual removal
        grandChild.removeChildAtIndex(0);

        // If here, no exception has been thrown
        // Check that this node still does not have any children
        assertEquals(0, grandChild.getNumChildren());
    }


    @Test
    public void testDeprecatedAttributeXPathQuery() throws JaxenException {
        class MyRootNode extends DummyNode implements RootNode {

            private MyRootNode(int id) {
                super(id);
            }
        }

        addChild(new MyRootNode(nextId()), new DummyNodeWithDeprecatedAttribute(2)).findChildNodesWithXPath("//dummyNode[@Size=1]");

        String log = loggingRule.getLog();

        assertTrue(log.contains("deprecated"));
        assertTrue(log.contains("attribute"));
        assertTrue(log.contains("dummyNode/@Size"));
    }


}
