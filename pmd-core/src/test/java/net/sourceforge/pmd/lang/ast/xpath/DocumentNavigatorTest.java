/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.Collections;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;

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

        Node root = new DummyRoot();
        Node n = new DummyNode(1);
        root.jjtAddChild(n, 0);
        n.jjtSetParent(root);
        assertSame(root, nav.getDocumentNode(n));
    }
}
