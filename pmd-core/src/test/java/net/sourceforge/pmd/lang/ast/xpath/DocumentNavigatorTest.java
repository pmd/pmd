/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

import org.junit.Test;

/**
 * Unit test for {@link DocumentNavigator}
 */
public class DocumentNavigatorTest {

    private static class DummyRootNode extends DummyNode implements RootNode {
        public DummyRootNode(int id) {
            super(id);
        }
    }

    @Test
    public void getDocumentNode() {
        DocumentNavigator nav = new DocumentNavigator();
        
        try {
            nav.getDocumentNode(null);
            fail();
        } catch (RuntimeException e) {
            assertNotNull(e);
        }

        Node root = new DummyRootNode(1);
        Node n = new DummyNode(1);
        root.jjtAddChild(n, 0);
        n.jjtSetParent(root);
        assertSame(root, nav.getDocumentNode(n));
    }
}
