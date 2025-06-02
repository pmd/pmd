/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.reporting.Report.ConfigurationError;
import net.sourceforge.pmd.reporting.Report.ProcessingError;
import net.sourceforge.pmd.reporting.Report.SuppressedViolation;
import net.sourceforge.pmd.reporting.ViolationSuppressor;

class JsonRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new JsonRenderer();
    }

    @Override
    String getExpected() {
        return readFile("expected.json");
    }

    @Override
    String getExpectedEmpty() {
        return readFile("empty.json");
    }

    @Override
    String getExpectedMultiple() {
        return readFile("expected-multiple.json");
    }

    @Override
    String getExpectedError(ProcessingError error) {
        String expected = readFile("expected-processingerror.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    String getExpectedError(ConfigurationError error) {
        return readFile("expected-configurationerror.json");
    }

    @Override
    String getExpectedErrorWithoutMessage(ProcessingError error) {
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
    String filter(String expected) {
        return expected
                .replaceAll("\"timestamp\":\\s*\"[^\"]+\"", "\"timestamp\": \"--replaced--\"")
                .replaceAll("\"pmdVersion\":\\s*\"[^\"]+\"", "\"pmdVersion\": \"unknown\"")
                .replaceAll("\\R", "\n"); // make the test run on Windows, too
    }

    @Test
    void suppressedViolations() throws IOException {
        SuppressedViolation suppressed = new SuppressedViolation(
            newRuleViolation(1, 1, 1, 1, new FooRule()),
            ViolationSuppressor.NOPMD_COMMENT_SUPPRESSOR,
            "test"
        );
        String actual = renderReport(getRenderer(), it -> it.onSuppressedRuleViolation(suppressed));
        String expected = readFile("expected-suppressed.json");
        assertEquals(filter(expected), filter(actual));
    }
}
