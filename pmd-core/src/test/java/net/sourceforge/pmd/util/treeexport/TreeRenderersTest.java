/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 */
public class TreeRenderersTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testStandardRenderersAreRegistered() {

        Assert.assertEquals(TreeRenderers.XML, TreeRenderers.findById(TreeRenderers.XML.id()));

    }

    @Test
    public void testXmlPropertiesAvailable() {


        PropertySource properties = TreeRenderers.XML.newPropertyBundle();

        Assert.assertThat(properties.getPropertyDescriptors(),
                          Matchers.<PropertyDescriptor<?>>containsInAnyOrder(TreeRenderers.XML_LINE_SEPARATOR,
                                                                             TreeRenderers.XML_RENDER_COMMON_ATTRIBUTES,
                                                                             TreeRenderers.XML_RENDER_PROLOG,
                                                                             TreeRenderers.XML_USE_SINGLE_QUOTES));

    }

    @Test
    public void testXmlDescriptorDump() throws IOException {

        PropertySource bundle = TreeRenderers.XML.newPropertyBundle();

        bundle.setProperty(TreeRenderers.XML_RENDER_PROLOG, false);
        bundle.setProperty(TreeRenderers.XML_USE_SINGLE_QUOTES, false);
        bundle.setProperty(TreeRenderers.XML_LINE_SEPARATOR, "\n");

        TreeRenderer renderer = TreeRenderers.XML.produceRenderer(bundle);

        StringBuilder out = new StringBuilder();

        renderer.renderSubtree(dummyTree1(), out);
        Assert.assertEquals("<dummyNode foo=\"bar\" ohio=\"4\">\n"
                                + "    <dummyNode o=\"ha\" />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }


    public MyDummyNode dummyTree1() {
        MyDummyNode dummy = new MyDummyNode();

        dummy.setXPathAttribute("foo", "bar");
        dummy.setXPathAttribute("ohio", "4");

        MyDummyNode dummy1 = new MyDummyNode();

        dummy1.setXPathAttribute("o", "ha");

        MyDummyNode dummy2 = new MyDummyNode();

        dummy.jjtAddChild(dummy1, 0);
        dummy.jjtAddChild(dummy2, 1);
        return dummy;
    }

    private static class MyDummyNode extends DummyNode {


        private final Map<String, String> attributes = new HashMap<>();

        public void setXPathAttribute(String name, String value) {
            attributes.put(name, value);
        }

        @Override
        public Iterator<Attribute> getXPathAttributesIterator() {

            List<Attribute> attrs = new ArrayList<>();
            for (String name : attributes.keySet()) {
                attrs.add(new Attribute(this, name, attributes.get(name)));
            }

            return attrs.iterator();
        }


    }


}
