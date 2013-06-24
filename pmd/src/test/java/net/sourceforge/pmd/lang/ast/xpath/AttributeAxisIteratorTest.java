/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ast.xpath;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;

import org.junit.Assert;
import org.junit.Test;

/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyJavaNode dummyNode = new DummyJavaNode(1);
        dummyNode.testingOnly__setBeginLine(1);
        dummyNode.testingOnly__setBeginColumn(1);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = new HashMap<String, Attribute>();
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
