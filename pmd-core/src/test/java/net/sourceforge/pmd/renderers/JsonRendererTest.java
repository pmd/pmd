/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.renderers;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.util.IOUtil;

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

    private String readFile(String name) {
        try (InputStream in = JsonRendererTest.class.getResourceAsStream("json/" + name)) {
            return IOUtil.readToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String filter(String expected) {
        String result = expected
                .replaceAll("\"timestamp\":\\s*\"[^\"]+\"", "\"timestamp\": \"--replaced--\"")
                .replaceAll("\"pmdVersion\":\\s*\"[^\"]+\"", "\"pmdVersion\": \"unknown\"")
                .replaceAll("\r\n", "\n"); // make the test run on Windows, too
        return result;
    }

    @Test
    public void suppressedViolations() throws IOException {
        Report rep = new Report();
        Map<Integer, String> suppressedLines = new HashMap<>();
        suppressedLines.put(1, "test");
        rep.suppress(suppressedLines);
        rep.addRuleViolation(newRuleViolation(1, 1, 1, 1, new FooRule()));
        String actual = renderReport(getRenderer(), rep);
        String expected = readFile("expected-suppressed.json");
        Assert.assertEquals(filter(expected), filter(actual));
    }
}
