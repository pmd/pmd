/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XMLRendererTest extends AbstractRendererTst {

    @Override
    public Renderer getRenderer() {
        return new XMLRenderer();
    }

    @Override
    public String getExpected() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL + "<file name=\"n/a\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.239\">" + PMD.EOL + "<file name=\"n/a\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL + "<pmd version=\"" + PMD.VERSION
                + "\" timestamp=\"2014-10-06T19:30:51.222\">" + PMD.EOL + "<error filename=\"file\" msg=\"Error\"/>"
                + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String filter(String expected) {
        String result = expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
        return result;
    }

    private static RuleViolation createRuleViolation(String description) {
        DummyNode node = new DummyNode(1);
        node.testingOnly__setBeginLine(1);
        node.testingOnly__setBeginColumn(1);
        node.testingOnly__setEndLine(1);
        node.testingOnly__setEndColumn(1);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("n/a");
        return new ParametricRuleViolation<Node>(new FooRule(), ctx, node, description);
    }

    private void verifyXmlEscaping(Renderer renderer, String shouldContain) throws Exception {
        Report report = new Report();
        String surrogatePair = "\ud801\udc1c";
        String msg = "The String literal \"Tokenizer " + surrogatePair + "\" appears...";
        report.addRuleViolation(createRuleViolation(msg));
        String actual = ReportTest.render(renderer, report);
        Assert.assertTrue(actual.contains(shouldContain));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(actual)));
        NodeList violations = doc.getElementsByTagName("violation");
        Assert.assertEquals(1, violations.getLength());
        Assert.assertEquals(msg, violations.item(0).getTextContent().trim());
    }

    @Test
    public void testXMLEscapingWithUTF8() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setProperty(XMLRenderer.ENCODING, "UTF-8");
        verifyXmlEscaping(renderer, "\ud801\udc1c");
    }

    @Test
    public void testXMLEscapingWithoutUTF8() throws Exception {
        Renderer renderer = getRenderer();
        renderer.setProperty(XMLRenderer.ENCODING, "ISO-8859-1");
        verifyXmlEscaping(renderer, "&#x1041c;");
    }
}
