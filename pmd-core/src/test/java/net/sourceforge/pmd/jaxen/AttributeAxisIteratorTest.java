/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.jaxen;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator;

public class AttributeAxisIteratorTest {

    @Test(expected = UnsupportedOperationException.class)
    public void testRemove() {
        DummyNode n = new DummyNode(0);
        n.testingOnlySetBeginColumn(1);
        n.testingOnlySetBeginLine(1);
        AttributeAxisIterator iter = new AttributeAxisIterator(n);
        iter.remove();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(AttributeAxisIteratorTest.class);
    }
}
