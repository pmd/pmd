/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.xpath;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.NoAttribute.NoAttrScope;
import net.sourceforge.pmd.util.IteratorUtil;

/**
 * @author Clément Fournier
 */
class NoAttributeTest {


    @Test
    void testNoAttrInherited() {
        Node child = new NodeNoInherited();

        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        assertTrue(attrNames.contains("SomeInt"));
        assertTrue(attrNames.contains("Child"));
        // from Node
        assertTrue(attrNames.contains("BeginLine"));

        assertFalse(attrNames.contains("SomeLong"));
        assertFalse(attrNames.contains("Image"));
        assertFalse(attrNames.contains("SomeName"));
    }


    @Test
    void testNoAttrAll() {

        assertTrue(0 < IteratorUtil.count(new NodeAllAttr(12).getXPathAttributesIterator()));

        NodeNoAttrAll child = new NodeNoAttrAll();
        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        // from Noded, so not suppressed
        assertTrue(attrNames.contains("Image"));
        assertFalse(attrNames.contains("MySuppressedAttr"));

    }

    @Test
    void testNoAttrAllIsNotInherited() {

        NodeNoAttrAllChild child = new NodeNoAttrAllChild();

        Set<String> attrNames = IteratorUtil.toList(child.getXPathAttributesIterator()).stream().map(Attribute::getName).collect(Collectors.toSet());

        // suppressed because the parent has NoAttribute(scope = ALL)
        assertFalse(attrNames.contains("MySuppressedAttr"));
        // not suppressed because defined in the class, which has no annotation
        assertTrue(attrNames.contains("NotSuppressedAttr"));
    }


    private static class DummyNodeParent extends DummyNode {

        DummyNodeParent() {
            super();
        }

        public String getSomeName() {
            return "Foo";
        }

        public int getSomeInt() {
            return 42;
        }

        public long getSomeLong() {
            return 42;
        }

        public long getSomeLong2() {
            return 42;
        }


    }

    @NoAttribute(scope = NoAttrScope.INHERITED)
    private static class NodeNoInherited extends DummyNodeParent {

        // getSomeName is inherited and filtered out by NoAttrScope.INHERITED
        // getSomeInt is inherited but overridden here, so NoAttrScope.INHERITED has no effect
        // getSomeLong is inherited and overridden here,
        //      and even with scope INHERITED its @NoAttribute takes precedence

        // isChild overrides nothing so with INHERITED it's not filtered out


        @Override
        public int getSomeInt() {
            return 43;
        }

        @NoAttribute // Notice
        @Override
        public long getSomeLong() {
            return 43;
        }


        @NoAttribute(scope = NoAttrScope.INHERITED)
        @Override
        public String getImage() {
            return super.getImage();
        }

        public boolean isChild() {
            return true;
        }


    }

    private static class NodeAllAttr extends DummyNodeParent {

        NodeAllAttr(int id) {
            super();
        }
    }

    @NoAttribute(scope = NoAttrScope.ALL)
    private static class NodeNoAttrAll extends DummyNodeParent {

        public int getMySuppressedAttr() {
            return 12;
        }

    }


    private static class NodeNoAttrAllChild extends NodeNoAttrAll {

        public int getNotSuppressedAttr() {
            return 12;
        }


    }


}
