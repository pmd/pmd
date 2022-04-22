/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.ast.DummyNode;
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

        MatcherAssert.assertThat(properties.getPropertyDescriptors(),
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


    public static DummyNode dummyTree1() {
        DummyNode dummy = DummyLanguageModule.parse("(parent(child1)(child2))").getChild(0);
        dummy.clearXPathAttributes();
        dummy.setXPathAttribute("foo", "bar");
        dummy.setXPathAttribute("ohio", "4");

        DummyNode dummy1 = dummy.getChild(0);
        dummy1.clearXPathAttributes();
        dummy1.setXPathAttribute("o", "ha");

        dummy.getChild(1).clearXPathAttributes();
        return dummy;
    }


}
