/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;

/**
 * Unit test for {@link DocumentNavigator}
 */
public class DocumentNavigatorTest {

    @Test
    public void getDocumentNode() {
        DocumentNavigator nav = new DocumentNavigator();

        try {
            nav.getDocumentNode(null);
            fail();
        } catch (RuntimeException e) {
            assertNotNull(e);
        }

        DummyNode root = new DummyRoot();
        DummyNode n = new DummyNode();
        root.addChild(n, 0);
        assertSame(root, nav.getDocumentNode(n));
    }
}
