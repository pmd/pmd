/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;


import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.Assert.assertEquals;

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
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Unit test for {@link AttributeAxisIterator}
 */
public class AttributeAxisIteratorTest {

    private static final Set<String> DEFAULT_ATTRS = setOf("BeginColumn", "BeginLine", "Image", "EndColumn", "EndLine");

    /**
     * Test hasNext and next.
     */
    @Test
    public void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode();
        dummyNode.setCoords(1, 1, 2, 2);

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        assertEquals(DEFAULT_ATTRS, toMap(it).keySet());
    }

    @Test
    public void testAttributeAxisIteratorWithEnum() {
        DummyNodeWithEnum dummyNode = new DummyNodeWithEnum();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        Set<String> expected = CollectionUtil.setUnion(DEFAULT_ATTRS, "Enum");

        assertEquals(expected, toMap(it).keySet());
    }

    @Test
    public void testAttributeAxisIteratorWithList() {
        // list attributes are not supported anymore
        DummyNodeWithList dummyNode = new DummyNodeWithList();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        assertEquals(DEFAULT_ATTRS, toMap(it).keySet());
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
