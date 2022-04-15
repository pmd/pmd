/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.reporting.FileAnalysisListener;


public class SarifRendererTest extends AbstractRendererTest {

    @org.junit.Rule
    public RestoreSystemProperties systemProperties = new RestoreSystemProperties();

    @Override
    public Renderer getRenderer() {
        return new SarifRenderer();
    }

    @Test
    public void testRendererWithASCII() throws Exception {
        System.setProperty("file.encoding", StandardCharsets.US_ASCII.name());
        testRenderer(StandardCharsets.UTF_8);
    }

    @Override
    public String getExpected() {
        return readFile("expected.sarif.json");
    }

    @Override
    public String getExpectedEmpty() {
        return readFile("empty.sarif.json");
    }

    @Override
    public String getExpectedMultiple() {
        return readFile("expected-multiple.sarif.json");
    }

    @Override
    public String getExpectedError(Report.ProcessingError error) {
        String expected = readFile("expected-error.sarif.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    public String getExpectedError(Report.ConfigurationError error) {
        return readFile("expected-configerror.sarif.json");
    }

    @Override
    public String getExpectedErrorWithoutMessage(Report.ProcessingError error) {
        String expected = readFile("expected-error-nomessage.sarif.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    public String filter(String expected) {
        return expected.replaceAll("\r\n", "\n") // make the test run on Windows, too
                .replaceAll("\"version\": \".+\",", "\"version\": \"unknown\",");
    }

    /**
     * Multiple occurrences of the same rule should be reported as individual results.
     * 
     * @see <a href="https://github.com/pmd/pmd/issues/3768"> [core] SARIF formatter reports multiple locations
     *      when it should report multiple results #3768</a>
     */
    @Test
    public void testRendererMultipleLocations() throws Exception {
        String actual = renderReport(getRenderer(), reportThreeViolationsTwoRules());

        JSONObject json = new JSONObject(actual);
        JSONArray results = json.getJSONArray("runs").getJSONObject(0).getJSONArray("results");
        assertEquals(3, results.length());
        assertEquals(filter(readFile("expected-multiple-locations.sarif.json")), filter(actual));
    }

    private Consumer<FileAnalysisListener> reportThreeViolationsTwoRules() {
        Rule fooRule = createFooRule();
        Rule booRule = createBooRule();

        return reportBuilder -> {
            reportBuilder.onRuleViolation(newRuleViolation(1, 1, 1, 10, fooRule));
            reportBuilder.onRuleViolation(newRuleViolation(5, 1, 5, 11, fooRule));
            reportBuilder.onRuleViolation(newRuleViolation(2, 2, 3, 1, booRule));
        };
    }

    protected String readFile(String relativePath) {
        return super.readFile("sarif/" + relativePath);
    }
}
