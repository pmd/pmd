/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.autofix.rewriteevents;

import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.INSERT;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.REMOVE;
import static net.sourceforge.pmd.autofix.rewriteevents.RewriteEventType.REPLACE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;

public class RewriteEventFactoryTest {
    private static final Node PARENT_NODE = DummyNode.newInstance();
    private static final Node OLD_CHILD_NODE = DummyNode.newInstance();
    private static final Node NEW_CHILD_NODE = DummyNode.newInstance();
    private static final int CHILD_INDEX = 1;

    @Test
    public void testCreateInsertRewriteEvent() {
        final RewriteEvent insertRewriteEvent = RewriteEventFactory.newInsertRewriteEvent(PARENT_NODE, NEW_CHILD_NODE, CHILD_INDEX);
        assertEquals(INSERT, insertRewriteEvent.getRewriteEventType());
        assertEquals(PARENT_NODE, insertRewriteEvent.getParentNode());
        assertNull(insertRewriteEvent.getOldChildNode());
        assertEquals(NEW_CHILD_NODE, insertRewriteEvent.getNewChildNode());
        assertEquals(CHILD_INDEX, insertRewriteEvent.getChildNodeIndex());
    }

    @Test
    public void testCreateRemoveRewriteEvent() {
        final RewriteEvent insertRewriteEvent = RewriteEventFactory.newRemoveRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, CHILD_INDEX);
        assertEquals(REMOVE, insertRewriteEvent.getRewriteEventType());
        assertEquals(PARENT_NODE, insertRewriteEvent.getParentNode());
        assertEquals(OLD_CHILD_NODE, insertRewriteEvent.getOldChildNode());
        assertNull(insertRewriteEvent.getNewChildNode());
        assertEquals(CHILD_INDEX, insertRewriteEvent.getChildNodeIndex());
    }

    @Test
    public void testCreateReplaceRewriteEvent() {
        final RewriteEvent insertRewriteEvent = RewriteEventFactory.newReplaceRewriteEvent(PARENT_NODE, OLD_CHILD_NODE, NEW_CHILD_NODE, CHILD_INDEX);
        assertEquals(REPLACE, insertRewriteEvent.getRewriteEventType());
        assertEquals(PARENT_NODE, insertRewriteEvent.getParentNode());
        assertEquals(OLD_CHILD_NODE, insertRewriteEvent.getOldChildNode());
        assertEquals(NEW_CHILD_NODE, insertRewriteEvent.getNewChildNode());
        assertEquals(CHILD_INDEX, insertRewriteEvent.getChildNodeIndex());
    }
}
