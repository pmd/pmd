/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.treeexport;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;
import net.sourceforge.pmd.util.treeexport.XmlTreeRenderer.XmlRenderingConfig;

/**
 */
public class XmlTreeRendererTest {

    @Rule
    public ExpectedException expect = ExpectedException.none();

    @Test
    public void testRenderWithAttributes() throws IOException {

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

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

        DummyNode dummy = TreeRenderersTest.dummyTree1();


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

        DummyNode dummy = TreeRenderersTest.dummyTree1();

        XmlTreeRenderer renderer = new XmlTreeRenderer();

        StringBuilder out = new StringBuilder();


        renderer.renderSubtree(dummy, out);

        Assert.assertEquals("<?xml version='1.0' encoding='UTF-8' ?>" + System.lineSeparator()
                                + "<dummyNode foo='bar' ohio='4'>" + System.lineSeparator()
                                + "    <dummyNode o='ha' />" + System.lineSeparator()
                                + "    <dummyNode />" + System.lineSeparator()
                                + "</dummyNode>" + System.lineSeparator(), out.toString());

    }
}
