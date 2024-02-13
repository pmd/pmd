/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.Report;

import com.github.stefanbirkner.systemlambda.SystemLambda;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

class SarifRendererTest extends AbstractRendererTest {

    @Override
    Renderer getRenderer() {
        return new SarifRenderer();
    }

    @Test
    void testRendererWithASCII() throws Exception {
        SystemLambda.restoreSystemProperties(() -> {
            System.setProperty("file.encoding", StandardCharsets.US_ASCII.name());
            testRenderer(StandardCharsets.UTF_8);
        });
    }

    @Override
    String getExpected() {
        return readFile("expected.sarif.json");
    }

    @Override
    String getExpectedEmpty() {
        return readFile("empty.sarif.json");
    }

    @Override
    String getExpectedMultiple() {
        return readFile("expected-multiple.sarif.json");
    }

    @Override
    String getExpectedError(Report.ProcessingError error) {
        String expected = readFile("expected-error.sarif.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    String getExpectedError(Report.ConfigurationError error) {
        return readFile("expected-configerror.sarif.json");
    }

    @Override
    String getExpectedErrorWithoutMessage(Report.ProcessingError error) {
        String expected = readFile("expected-error-nomessage.sarif.json");
        expected = expected.replace("###REPLACE_ME###", error.getDetail()
                .replaceAll("\r", "\\\\r")
                .replaceAll("\n", "\\\\n")
                .replaceAll("\t", "\\\\t"));
        return expected;
    }

    @Override
    String filter(String expected) {
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
    void testRendererMultipleLocations() throws Exception {
        String actual = renderReport(getRenderer(), reportThreeViolationsTwoRules());

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(actual, JsonObject.class);
        JsonArray results = json.getAsJsonArray("runs").get(0).getAsJsonObject().getAsJsonArray("results");
        assertEquals(3, results.size());
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
