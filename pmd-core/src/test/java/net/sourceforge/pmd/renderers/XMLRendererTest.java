/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public class XMLRendererTest extends AbstractRendererTest {
    @Rule // Restores system properties after test
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        return expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
    }

    private RuleViolation createRuleViolation(String description) {
        DummyNode node = new DummyRoot().withFileName(getSourceCodeFilename());
        node.setCoords(1, 1, 1, 1);
        return new ParametricRuleViolation(new FooRule(), node, description);
    }

    private void verifyXmlEscaping(Renderer renderer, String shouldContain, Charset charset) throws Exception {
        renderer.setProperty(XMLRenderer.ENCODING, charset.name());
        Report report = new Report();
        String surrogatePair = "\ud801\udc1c";
        String msg = "The String 'literal' \"TokénizĀr " + surrogatePair + "\" appears...";
        report.addRuleViolation(createRuleViolation(msg));
        String actual = renderTempFile(renderer, report, charset);
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
        verifyXmlEscaping(renderer, "\ud801\udc1c", StandardCharsets.UTF_8);
    }

    @Test
    public void testXMLEscapingWithUTF16() throws Exception {
        Renderer renderer = getRenderer();
        verifyXmlEscaping(renderer, "&#x1041c;", StandardCharsets.UTF_16);
    }

    @Test
    public void testXMLEscapingWithoutUTF8() throws Exception {
        Renderer renderer = getRenderer();
        verifyXmlEscaping(renderer, "&#x1041c;", StandardCharsets.ISO_8859_1);
    }

    public String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL
                + "<pmd xmlns=\"http://pmd.sourceforge.net/report/2.0.0\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd\""
                + " version=\"" + PMDVersion.VERSION + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL;
    }

    @Test
    public void testCorrectCharset() throws Exception {
        System.setProperty("file.encoding", StandardCharsets.ISO_8859_1.name());

        Renderer renderer = getRenderer();

        Report report = new Report();
        String formFeed = "\u000C";
        // é = U+00E9 : can be represented in ISO-8859-1 as is
        // Ā = U+0100 : cannot be represented in ISO-8859-1 -> would be a unmappable character, needs to be escaped
        String specialChars = "éĀ";
        String originalChars = formFeed + specialChars; // u000C should be removed, é should be encoded correctly as UTF-8
        String msg = "The String literal \"" + originalChars + "\" appears...";
        report.addRuleViolation(createRuleViolation(msg));
        String actual = renderTempFile(renderer, report, StandardCharsets.UTF_8);
        Assert.assertTrue(actual.contains(specialChars));
        Assert.assertFalse(actual.contains(formFeed));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(actual)));
        NodeList violations = doc.getElementsByTagName("violation");
        Assert.assertEquals(1, violations.getLength());
        Assert.assertEquals(msg.replaceAll(formFeed, ""), violations.item(0).getTextContent().trim());
    }

    private String renderTempFile(Renderer renderer, Report report, Charset expectedCharset) throws IOException {
        File reportFile = folder.newFile();

        renderer.setReportFile(reportFile.getAbsolutePath());
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        renderer.flush();

        try (FileInputStream input = new FileInputStream(reportFile)) {
            return IOUtils.toString(input, expectedCharset);
        }
    }
}
