/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;


import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;


/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode();
        dummyNode.setCoords(1, 1, 2, 2);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        Set<String> expected = setOf("BeginColumn",
                                     "BeginLine",
                                     "FindBoundary",
                                     "Image",
                                     "EndColumn",
                                     "EndLine");
        assertEquals(expected, atts.keySet());
    }

    @Test
    public void testAttributeAxisIteratorWithEnum() {
        DummyNodeWithEnum dummyNode = new DummyNodeWithEnum();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        assertEquals(7, atts.size());
        assertTrue(atts.containsKey("Enum"));
        assertEquals(DummyNodeWithEnum.MyEnum.FOO, atts.get("Enum").getValue());
    }

    @Test
    public void testAttributeAxisIteratorWithList() {
        DummyNodeWithList dummyNode = new DummyNodeWithList();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);
        Map<String, Attribute> atts = toMap(it);
        assertEquals(6, atts.size());
        assertFalse(atts.containsKey("List"));
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

        public enum MyEnum {
            FOO, BAR
        }

        public MyEnum getEnum() {
            return MyEnum.FOO;
        }
    }

    public static class DummyNodeWithList extends DummyNode {

        public List<String> getList() {
            return Arrays.asList("A", "B");
        }

        public List<Node> getNodeList() {
            return Collections.emptyList();
        }
    }
}
