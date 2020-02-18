/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XMLRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new XMLRenderer();
    }

    @Override
    public String getExpected() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedEmpty() {
        return getHeader() + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedMultiple() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"RuntimeException: Error\">"
                + PMD.EOL + "<![CDATA[" + error.getDetail() + "]]>" + PMD.EOL + "</error>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedErrorWithoutMessage(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"NullPointerException: null\">"
                + PMD.EOL + "<![CDATA[" + error.getDetail() + "]]>" + PMD.EOL + "</error>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return getHeader() + "<configerror rule=\"Foo\" msg=\"a configuration error\"/>"
                + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    public String filter(String expected) {
        String result = expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
        return result;
    }

    private RuleViolation createRuleViolation(String description) {
        DummyNode node = new DummyNode(1);
        node.testingOnlySetBeginLine(1);
        node.testingOnlySetBeginColumn(1);
        node.testingOnlySetEndLine(1);
        node.testingOnlySetEndColumn(1);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
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

    public String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL
                + "<pmd xmlns=\"http://pmd.sourceforge.net/report/2.0.0\"" + PMD.EOL
                + "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + PMD.EOL
                + "    xsi:schemaLocation=\"http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd\"" + PMD.EOL
                + "    version=\"" + PMDVersion.VERSION + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL;
    }
}
