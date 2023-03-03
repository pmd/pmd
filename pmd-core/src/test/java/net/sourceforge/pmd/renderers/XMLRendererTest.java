/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
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
import net.sourceforge.pmd.internal.util.IOUtil;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class XMLRendererTest extends AbstractRendererTest {

    @TempDir
    private Path folder;

    @Override
    Renderer getRenderer() {
        return new XMLRenderer();
    }

    @Override
    String getExpected() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    String getExpectedEmpty() {
        return getHeader() + "</pmd>" + PMD.EOL;
    }

    @Override
    String getExpectedMultiple() {
        return getHeader() + "<file name=\"" + getSourceCodeFilename() + "\">" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"1\" rule=\"Foo\" ruleset=\"RuleSet\" priority=\"5\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL
                + "<violation beginline=\"1\" endline=\"1\" begincolumn=\"1\" endcolumn=\"2\" rule=\"Boo\" ruleset=\"RuleSet\" priority=\"1\">"
                + PMD.EOL + "blah" + PMD.EOL + "</violation>" + PMD.EOL + "</file>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    String getExpectedError(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"RuntimeException: Error\">"
                + PMD.EOL + "<![CDATA[" + error.getDetail() + "]]>" + PMD.EOL + "</error>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
        return getHeader() + "<error filename=\"file\" msg=\"NullPointerException: null\">"
                + PMD.EOL + "<![CDATA[" + error.getDetail() + "]]>" + PMD.EOL + "</error>" + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return getHeader() + "<configerror rule=\"Foo\" msg=\"a configuration error\"/>"
                + PMD.EOL + "</pmd>" + PMD.EOL;
    }

    @Override
    String filter(String expected) {
        return expected.replaceAll(" timestamp=\"[^\"]+\">", " timestamp=\"\">");
    }

    private RuleViolation createRuleViolation(String description) {
        FileLocation loc = FileLocation.range(getSourceCodeFilename(), TextRange2d.range2d(1, 1, 1, 1));
        return new ParametricRuleViolation(new FooRule(), loc, description);
    }

    private void verifyXmlEscaping(Renderer renderer, String shouldContain, Charset charset) throws Exception {
        renderer.setProperty(XMLRenderer.ENCODING, charset.name());
        String surrogatePair = "\ud801\udc1c";
        String msg = "The String 'literal' \"TokénizĀr " + surrogatePair + "\" appears...";
        Report report = Report.buildReport(it -> it.onRuleViolation(createRuleViolation(msg)));
        String actual = renderTempFile(renderer, report, charset);
        assertTrue(actual.contains(shouldContain));
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                .parse(new InputSource(new StringReader(actual)));
        NodeList violations = doc.getElementsByTagName("violation");
        assertEquals(1, violations.getLength());
        assertEquals(msg, violations.item(0).getTextContent().trim());
    }

    @Test
    void testXMLEscapingWithUTF8() throws Exception {
        Renderer renderer = getRenderer();
        verifyXmlEscaping(renderer, "\ud801\udc1c", StandardCharsets.UTF_8);
    }

    @Test
    void testXMLEscapingWithUTF16() throws Exception {
        Renderer renderer = getRenderer();
        verifyXmlEscaping(renderer, "&#x1041c;", StandardCharsets.UTF_16);
    }

    @Test
    void testXMLEscapingWithoutUTF8() throws Exception {
        Renderer renderer = getRenderer();
        verifyXmlEscaping(renderer, "&#x1041c;", StandardCharsets.ISO_8859_1);
    }

    String getHeader() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + PMD.EOL
                + "<pmd xmlns=\"http://pmd.sourceforge.net/report/2.0.0\""
                + " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""
                + " xsi:schemaLocation=\"http://pmd.sourceforge.net/report/2.0.0 http://pmd.sourceforge.net/report_2_0_0.xsd\""
                + " version=\"" + PMDVersion.VERSION + "\" timestamp=\"2014-10-06T19:30:51.262\">" + PMD.EOL;
    }

    @Test
    void testCorrectCharset() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty("file.encoding", StandardCharsets.ISO_8859_1.name());

            Renderer renderer = getRenderer();

            String formFeed = "\u000C";
            // é = U+00E9 : can be represented in ISO-8859-1 as is
            // Ā = U+0100 : cannot be represented in ISO-8859-1 -> would be a unmappable character, needs to be escaped
            String specialChars = "éĀ";
            String originalChars = formFeed + specialChars; // u000C should be removed, é should be encoded correctly as UTF-8
            String msg = "The String literal \"" + originalChars + "\" appears...";
            Report report = Report.buildReport(it -> it.onRuleViolation(createRuleViolation(msg)));
            String actual = renderTempFile(renderer, report, StandardCharsets.UTF_8);
            assertTrue(actual.contains(specialChars));
            assertFalse(actual.contains(formFeed));
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
                    .parse(new InputSource(new StringReader(actual)));
            NodeList violations = doc.getElementsByTagName("violation");
            assertEquals(1, violations.getLength());
            assertEquals(msg.replaceAll(formFeed, ""), violations.item(0).getTextContent().trim());
        });
    }

    private String renderTempFile(Renderer renderer, Report report, Charset expectedCharset) throws IOException {
        File reportFile = folder.resolve("report.out").toFile();

        renderer.setReportFile(reportFile.getAbsolutePath());
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        renderer.flush();

        try (FileInputStream input = new FileInputStream(reportFile)) {
            return IOUtil.readToString(input, expectedCharset);
        }
    }
}
