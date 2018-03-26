/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;

import static junit.framework.TestCase.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNodeWithDeprecatedAttribute;
import net.sourceforge.pmd.lang.ast.Node;


/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {

    @Test
    public void testAttributeDeprecation() {
        Node dummy = new DummyNodeWithDeprecatedAttribute(2);
        assertTrue(toMap(new AttributeAxisIterator(dummy)).containsKey("Size"));
    }

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode(1);
        dummyNode.testingOnlySetBeginLine(1);
        dummyNode.testingOnlySetBeginColumn(1);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(7, atts.size());
        Assert.assertTrue(atts.containsKey("BeginColumn"));
        Assert.assertTrue(atts.containsKey("BeginLine"));
        Assert.assertTrue(atts.containsKey("FindBoundary"));
        Assert.assertTrue(atts.containsKey("Image"));
        Assert.assertTrue(atts.containsKey("SingleLine"));
        Assert.assertTrue(atts.containsKey("EndColumn"));
        Assert.assertTrue(atts.containsKey("EndLine"));
    }


    private Map<String, Attribute> toMap(AttributeAxisIterator it) {
        Map<String, Attribute> atts = new HashMap<>();
        while (it.hasNext()) {
            Attribute attribute = it.next();
            atts.put(attribute.getName(), attribute);
        }
        return atts;
    }
}
