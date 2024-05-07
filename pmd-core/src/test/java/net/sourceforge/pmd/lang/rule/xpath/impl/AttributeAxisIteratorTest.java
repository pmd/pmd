/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath.impl;


import static net.sourceforge.pmd.util.CollectionUtil.setOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.lang.rule.xpath.impl.dummyast.ConcreteNode;
import net.sourceforge.pmd.util.CollectionUtil;


/**
 * Unit test for {@link AttributeAxisIterator}
 */
class AttributeAxisIteratorTest {

    private static final Set<String> DEFAULT_ATTRS = setOf("BeginColumn", "BeginLine", "Image", "EndColumn", "EndLine");

    /**
     * Test hasNext and next.
     */
    @Test
    void testAttributeAxisIterator() {
        DummyNode dummyNode = new DummyNode();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        Set<String> expected = CollectionUtil.setUnion(DEFAULT_ATTRS, "Lines");

        assertEquals(expected, toMap(it).keySet());
    }

    @Test
    void testAttributeAxisIteratorWithEnum() {
        DummyNodeWithEnum dummyNode = new DummyNodeWithEnum();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        Set<String> expected = CollectionUtil.setUnion(DEFAULT_ATTRS, "Enum", "Lines");

        assertEquals(expected, toMap(it).keySet());
    }

    @Test
    void testAttributeAxisIteratorWithList() {
        // list attributes are not supported anymore
        DummyNodeWithList dummyNode = new DummyNodeWithList();

        AttributeAxisIterator it = new AttributeAxisIterator(dummyNode);

        Set<String> expected = CollectionUtil.setUnion(DEFAULT_ATTRS, "List", "Lines");

        assertEquals(expected, toMap(it).keySet());
    }

    /**
     * Exercises the case described in
     * <a href="https://github.com/pmd/pmd/issues/4885">[java] AssertionError: Method should be accessible #4885</a>.
     */
    @Test
    void accessPublicMethodWithAPackagePrivateImplementationInSuperclass() {
        final String ATTRIBUTE_NAME = "Value";
        ConcreteNode node = new ConcreteNode();
        AttributeAxisIterator it = new AttributeAxisIterator(node);
        Map<String, Attribute> attributes = toMap(it);
        assertTrue(attributes.containsKey(ATTRIBUTE_NAME));
        assertEquals("actual_value", attributes.get(ATTRIBUTE_NAME).getValue().toString());
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
