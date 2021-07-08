/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.renderers;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Report.SuppressedViolation;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.ViolationSuppressor;

public class JsonRendererTest extends AbstractRendererTest {

    @Override
    public Renderer getRenderer() {
        return new JsonRenderer();
    }

    @Override
    public String getExpected() {
        return readFile("expected.json");
    }

    @Override
    public String getExpectedEmpty() {
        return readFile("empty.json");
    }

    @Override
    public String getExpectedMultiple() {
        return readFile("expected-multiple.json");
    }

    @Override
    public String getExpectedError(ProcessingError error) {
        String expected = readFile("expected-processingerror.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    public String getExpectedError(ConfigurationError error) {
        return readFile("expected-configurationerror.json");
    }

    @Override
    public String getExpectedErrorWithoutMessage(ProcessingError error) {
        String expected = readFile("expected-processingerror-no-message.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    protected String readFile(String relativePath) {
        return super.readFile("json/" + relativePath);
    }

    @Override
    public String filter(String expected) {
        return expected
                .replaceAll("\"timestamp\":\\s*\"[^\"]+\"", "\"timestamp\": \"--replaced--\"")
                .replaceAll("\"pmdVersion\":\\s*\"[^\"]+\"", "\"pmdVersion\": \"unknown\"")
                .replaceAll("\r\n", "\n"); // make the test run on Windows, too
    }

    @Test
    public void suppressedViolations() throws IOException {
        SuppressedViolation suppressed = new SuppressedViolation(
            newRuleViolation(1),
            ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR,
            "test"
        );
        String actual = ReportTest.render(getRenderer(), it -> it.onSuppressedRuleViolation(suppressed));
        String expected = readFile("expected-suppressed.json");
        Assert.assertEquals(filter(expected), filter(actual));
    }
}
