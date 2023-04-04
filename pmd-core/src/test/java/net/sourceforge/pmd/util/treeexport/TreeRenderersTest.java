/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import net.sourceforge.pmd.DummyParsingHelper;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.properties.PropertySource;

/**
 *
 */
class TreeRenderersTest {

    @RegisterExtension
    private final DummyParsingHelper helper = new DummyParsingHelper();

    @Test
    void testStandardRenderersAreRegistered() {

        assertEquals(TreeRenderers.XML, TreeRenderers.findById(TreeRenderers.XML.id()));

    }

    @Test
    void testXmlPropertiesAvailable() {


        PropertySource properties = TreeRenderers.XML.newPropertyBundle();

        assertThat(properties.getPropertyDescriptors(),
                          containsInAnyOrder(TreeRenderers.XML_LINE_SEPARATOR,
                                                                             TreeRenderers.XML_RENDER_COMMON_ATTRIBUTES,
                                                                             TreeRenderers.XML_RENDER_PROLOG,
                                                                             TreeRenderers.XML_USE_SINGLE_QUOTES));

    }

    @Test
    void testXmlDescriptorDump() throws IOException {

        PropertySource bundle = TreeRenderers.XML.newPropertyBundle();

        bundle.setProperty(TreeRenderers.XML_RENDER_PROLOG, false);
        bundle.setProperty(TreeRenderers.XML_USE_SINGLE_QUOTES, false);
        bundle.setProperty(TreeRenderers.XML_LINE_SEPARATOR, "\n");

        TreeRenderer renderer = TreeRenderers.XML.produceRenderer(bundle);

        StringBuilder out = new StringBuilder();

        renderer.renderSubtree(dummyTree1(helper), out);
        assertEquals("<dummyNode foo=\"bar\" ohio=\"4\">\n"
                                + "    <dummyNode o=\"ha\" />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }


    static DummyNode dummyTree1(DummyParsingHelper helper) {
        DummyNode dummy = helper.parse("(parent(child1)(child2))").getChild(0);
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
