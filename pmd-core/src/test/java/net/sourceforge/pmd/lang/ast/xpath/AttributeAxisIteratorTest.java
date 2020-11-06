/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.xpath;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;


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
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(7, atts.size());
        assertTrue(atts.containsKey("BeginColumn"));
        assertTrue(atts.containsKey("BeginLine"));
        assertTrue(atts.containsKey("FindBoundary"));
        assertTrue(atts.containsKey("Image"));
        assertTrue(atts.containsKey("SingleLine"));
        assertTrue(atts.containsKey("EndColumn"));
        assertTrue(atts.containsKey("EndLine"));
    }

    @Test
    public void testAttributeAxisIteratorWithEnum() {
        DummyNodeWithEnum dummyNode = new DummyNodeWithEnum(1);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(8, atts.size());
        assertTrue(atts.containsKey("Enum"));
        assertEquals(DummyNodeWithEnum.MyEnum.FOO, atts.get("Enum").getValue());
    }

    @Test
    public void testAttributeAxisIteratorWithList() {
        DummyNodeWithList dummyNode = new DummyNodeWithList(1);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Assert.assertEquals(8, atts.size());
        assertTrue(atts.containsKey("List"));
        assertEquals(Arrays.asList("A", "B"), atts.get("List").getValue());
        assertFalse(atts.containsKey("NodeList"));
    }

    private Map<String, Attribute> toMap(AttributeAxisIterator it) {
        Map<String, Attribute> atts = new HashMap<>();
        while (it.hasNext()) {
            Attribute attribute = it.next();
            atts.put(attribute.getName(), attribute);
        }
        return atts;
    }

    public static class DummyNodeWithEnum extends DummyNode {

        public DummyNodeWithEnum(int id) {
            super(id);
        }

        public enum MyEnum {
            FOO, BAR
        }

        public MyEnum getEnum() {
            return MyEnum.FOO;
        }
    }

    public static class DummyNodeWithList extends DummyNode {

        public DummyNodeWithList(int id) {
            super(id);
        }

        public List<String> getList() {
            return Arrays.asList("A", "B");
        }

        public List<Node> getNodeList() {
            return Collections.emptyList();
        }
    }
}
