/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;


/**
 * Unit test for {@link RewriteEventTest}.
 */
@RunWith(JUnitParamsRunner.class)
public class RewriteEventTest {

    private static Node newDummyNode() {
        return new DummyNode(0);
    }

    @SuppressWarnings("unused") // Used by JUnitParams in `testInsertChild` test case
    private Object testCorrectlyAssignParameters() {
        final Node parentNode = newDummyNode();
        final Node oldChildNode = newDummyNode();
        final Node newChildNode = newDummyNode();
        return new Object[] {
            // Valid cases
            new Object[] {RewriteEventType.INSERT, parentNode, null, newChildNode, 0, false},
            new Object[] {RewriteEventType.REMOVE, parentNode, oldChildNode, null, 0, false},
            new Object[] {RewriteEventType.REPLACE, parentNode, oldChildNode, newChildNode, 0, false},
            // Invalid cases
            // - Insert
            new Object[] {RewriteEventType.INSERT, parentNode, null, null, 0, true}, // Null equal children
            new Object[] {RewriteEventType.INSERT, parentNode, oldChildNode, oldChildNode, 0, true}, // Non-null equal children
            new Object[] {RewriteEventType.INSERT, null, oldChildNode, newChildNode, 0, true}, // Null parent
            new Object[] {RewriteEventType.INSERT, parentNode, oldChildNode, newChildNode, -1, true}, // Invalid Index
            // - Remove
            new Object[] {RewriteEventType.REMOVE, parentNode, null, null, 0, true}, // Null equal children
            new Object[] {RewriteEventType.REMOVE, parentNode, oldChildNode, oldChildNode, 0, true},
            new Object[] {RewriteEventType.REMOVE, null, oldChildNode, newChildNode, 0, true},
            new Object[] {RewriteEventType.REMOVE, parentNode, oldChildNode, newChildNode, -1, true},
            // - Replace
            new Object[] {RewriteEventType.REPLACE, parentNode, null, null, 0, true}, // Null equal children
            new Object[] {RewriteEventType.REPLACE, parentNode, oldChildNode, oldChildNode, 0, true}, // Non-null equal children
            new Object[] {RewriteEventType.REPLACE, null, oldChildNode, newChildNode, 0, true}, // Null parent
            new Object[] {RewriteEventType.REPLACE, parentNode, oldChildNode, newChildNode, -1, true}, // Invalid Index
        };
    }

    @Test
    @Parameters(method = "testCorrectlyAssignParameters")
    public void testCorrectlyAssignParameters(final RewriteEventType rewriteEventType,
                                              final Node parentNode,
                                              final Node oldChildNode,
                                              final Node newChildNode,
                                              final int childIndex,
                                              final boolean expectValidationFail
    ) {
        try {
            final RewriteEvent rewriteEvent = new RewriteEvent(parentNode, oldChildNode, newChildNode, childIndex);
            assertEquals(rewriteEventType, rewriteEvent.getRewriteEventType());
            assertEquals(parentNode, rewriteEvent.getParentNode());
            assertEquals(oldChildNode, rewriteEvent.getOldChildNode());
            assertEquals(newChildNode, rewriteEvent.getNewChildNode());
            assertEquals(childIndex, rewriteEvent.getChildNodeIndex());
        } catch (final Exception e) {
            assertTrue(expectValidationFail);
        }
    }
}
