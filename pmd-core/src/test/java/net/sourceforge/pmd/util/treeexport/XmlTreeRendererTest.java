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

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer.XmlRenderingConfig;

/**
 */
public class XmlTreeRendererTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testRenderWithAttributes() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlRenderingConfig strat = new XmlRenderingConfig();
        strat.lineSeparator("\n");
        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                + "<dummyNode foo='bar' ohio='4'>\n"
                                + "    <dummyNode o='ha' />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }

    @Test
    public void testRenderWithCustomLineSep() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlRenderingConfig strat = new XmlRenderingConfig();
        strat.lineSeparator("\r\n");
        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>\r\n"
                                + "<dummyNode foo='bar' ohio='4'>\r\n"
                                + "    <dummyNode o='ha' />\r\n"
                                + "    <dummyNode />\r\n"
                                + "</dummyNode>\r\n", out.toString());

    }

    @Test
    public void testRenderWithCustomIndent() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlRenderingConfig strat = new XmlRenderingConfig().lineSeparator("").indentWith("");

        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>"
                                + "<dummyNode foo='bar' ohio='4'>"
                                + "<dummyNode o='ha' />"
                                + "<dummyNode />"
                                + "</dummyNode>", out.toString());

    }

    @Test
    public void testRenderWithNoAttributes() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlRenderingConfig strat = new XmlRenderingConfig() {
            @Override
            public boolean takeAttribute(Node node, Attribute attribute) {
                return false;
            }
        }.lineSeparator("\n");

        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                + "<dummyNode>\n"
                                + "    <dummyNode />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }

    @Test
    public void testRenderFilterAttributes() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlRenderingConfig strategy = new XmlRenderingConfig() {
            @Override
            public boolean takeAttribute(Node node, Attribute attribute) {
                return attribute.getName().equals("ohio");
            }
        }.lineSeparator("\n");

        XmlTreeRenderer renderer = new XmlTreeRenderer(strategy);

        StringBuilder out = new StringBuilder();

        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                + "<dummyNode ohio='4'>\n"
                                + "    <dummyNode />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }

    @Test
    public void testInvalidAttributeName() throws IOException {

        MyDummyNode dummy = dummyTree1();

        dummy.setXPathAttribute("&notAName", "foo");

        XmlRenderingConfig config = new XmlRenderingConfig();
        config.lineSeparator("\n");

        XmlTreeRenderer renderer = new XmlTreeRenderer(config);

        StringBuilder out = new StringBuilder();

        expect.expect(IllegalArgumentException.class);

        renderer.renderSubtree(dummy, out);

    }


    @Test
    public void testEscapeAttributes() throws IOException {

        MyDummyNode dummy = dummyTree1();

        dummy.setXPathAttribute("eh", " 'a &> b\" ");

        XmlRenderingConfig strat = new XmlRenderingConfig().lineSeparator("\n");
        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>\n"
                                + "<dummyNode eh=' &apos;a &amp;> b\" ' foo='bar' ohio='4'>\n"
                                + "    <dummyNode o='ha' />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }

    @Test
    public void testEscapeDoubleAttributes() throws IOException {

        MyDummyNode dummy = dummyTree1();

        dummy.setXPathAttribute("eh", " 'a &> b\" ");

        XmlRenderingConfig strat = new XmlRenderingConfig().lineSeparator("\n").singleQuoteAttributes(false);
        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                                + "<dummyNode eh=\" 'a &amp;> b&quot; \" foo=\"bar\" ohio=\"4\">\n"
                                + "    <dummyNode o=\"ha\" />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }

    @Test
    public void testNoProlog() throws IOException {

        DummyNode dummy = dummyTree1();


        XmlRenderingConfig strat = new XmlRenderingConfig().lineSeparator("\n").renderProlog(false);
        XmlTreeRenderer renderer = new XmlTreeRenderer(strat);

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<dummyNode foo='bar' ohio='4'>\n"
                                + "    <dummyNode o='ha' />\n"
                                + "    <dummyNode />\n"
                                + "</dummyNode>\n", out.toString());

    }


    @Test
    public void testDefaultLineSep() throws IOException {

        DummyNode dummy = dummyTree1();

        XmlTreeRenderer renderer = new XmlTreeRenderer();

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>" + System.lineSeparator()
                                + "<dummyNode foo='bar' ohio='4'>" + System.lineSeparator()
                                + "    <dummyNode o='ha' />" + System.lineSeparator()
                                + "    <dummyNode />" + System.lineSeparator()
                                + "</dummyNode>" + System.lineSeparator(), out.toString());

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
