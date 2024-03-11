/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.PMDVersion;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.reporting.Report;
import net.sourceforge.pmd.reporting.RuleViolation;

class XSLTRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new XSLTRenderer();
    }

    @Override
    String getExpected() {
        return readFile("expected.html");
    }

    @Override
    String getExpectedEmpty() {
        return readFile("empty.html");
    }

    @Override
    String getExpectedMultiple() {
        return readFile("expected-multiple.html");
    }

    @Override
    String getExpectedError(Report.ProcessingError error) {
        return readFile("expected-error.html");
    }

    @Override
    String getExpectedError(Report.ConfigurationError error) {
        return readFile("expected-error.html");
    }

    @Override
    String filter(String expected) {
        return expected.replaceAll("<h2>PMD " + PMDVersion.VERSION + " Report\\. Generated on .+</h2>",
                        "<h2>PMD unknown Report. Generated on ...</h2>")
                .replaceAll("<title>PMD " + PMDVersion.VERSION + " Report</title>",
                        "<title>PMD unknown Report</title>")
                .replaceAll("\r\n", "\n"); // make the test run on Windows, too
    }

    @Test
    void testDefaultStylesheet() throws Exception {
        XSLTRenderer renderer = new XSLTRenderer();
        FileLocation loc = FileLocation.range(FileId.UNKNOWN, TextRange2d.range2d(1, 1, 1, 2));
        RuleViolation rv = newRuleViolation(new FooRule(), loc, "violation message");
        String result = renderReport(renderer, it -> it.onRuleViolation(rv));
        assertTrue(result.contains("violation message"));
    }

    protected String readFile(String relativePath) {
        return super.readFile("xslt/" + relativePath);
    }
}
