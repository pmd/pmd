/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;

/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode(1);
        dummyNode.testingOnlySetBeginLine(1);
        dummyNode.testingOnlySetBeginColumn(1);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = new HashMap<>();
        while (it.hasNext()) {
            Attribute attribute = it.next();
            atts.put(attribute.getName(), attribute);
        }
        Assert.assertEquals(7, atts.size());
        Assert.assertTrue(atts.containsKey("BeginColumn"));
        Assert.assertTrue(atts.containsKey("BeginLine"));
        Assert.assertTrue(atts.containsKey("FindBoundary"));
        Assert.assertTrue(atts.containsKey("Image"));
        Assert.assertTrue(atts.containsKey("SingleLine"));
        Assert.assertTrue(atts.containsKey("EndColumn"));
        Assert.assertTrue(atts.containsKey("EndLine"));
    }
}
