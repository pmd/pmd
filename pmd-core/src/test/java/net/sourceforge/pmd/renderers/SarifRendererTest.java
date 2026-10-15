/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.org.webcompere.systemstubs.SystemStubs.restoreSystemProperties;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.document.FileLocation;
import net.sourceforge.pmd.lang.document.TextRange2d;
import net.sourceforge.pmd.lang.rule.Rule;
import net.sourceforge.pmd.reporting.ConfigurableFileNameRenderer;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.Report;

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
        restoreSystemProperties(() -> {
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

    @Test
    void testRelativizedViolationUri() throws Exception {
        Path root = Paths.get("project").toAbsolutePath();
        FileId file = FileId.fromPath(root.resolve("src/naïve 100%#.java"));
        ConfigurableFileNameRenderer fileNames = new ConfigurableFileNameRenderer();
        fileNames.relativizeWith(root);
        Renderer renderer = getRenderer();
        renderer.setFileNameRenderer(fileNames);
        FileLocation location = FileLocation.range(file, TextRange2d.range2d(1, 1, 1, 2));
        String actual = renderReport(renderer,
            listener -> listener.onRuleViolation(newRuleViolation(createFooRule(), location, "blah")));

        JsonObject run = new Gson().fromJson(actual, JsonObject.class)
            .getAsJsonArray("runs").get(0).getAsJsonObject();
        JsonObject physicalLocation = run.getAsJsonArray("results").get(0).getAsJsonObject()
            .getAsJsonArray("locations").get(0).getAsJsonObject().getAsJsonObject("physicalLocation");
        assertEquals("src/na%C3%AFve%20100%25%23.java",
            physicalLocation.getAsJsonObject("artifactLocation").get("uri").getAsString());
    }

    @Test
    void testRelativizedProcessingErrorUri() throws Exception {
        Path root = Paths.get("project").toAbsolutePath();
        FileId file = FileId.fromPath(root.resolve("src/Foo.java"));
        ConfigurableFileNameRenderer fileNames = new ConfigurableFileNameRenderer();
        fileNames.relativizeWith(root);
        Renderer renderer = getRenderer();
        renderer.setFileNameRenderer(fileNames);
        String actual = renderReport(renderer,
            listener -> listener.onError(new Report.ProcessingError(new RuntimeException("Error"), file)));

        JsonObject run = new Gson().fromJson(actual, JsonObject.class)
            .getAsJsonArray("runs").get(0).getAsJsonObject();
        JsonObject physicalLocation = run.getAsJsonArray("invocations").get(0).getAsJsonObject()
            .getAsJsonArray("toolExecutionNotifications").get(0).getAsJsonObject()
            .getAsJsonArray("locations").get(0).getAsJsonObject().getAsJsonObject("physicalLocation");
        assertEquals("src/Foo.java",
            physicalLocation.getAsJsonObject("artifactLocation").get("uri").getAsString());
    }

    protected String readFile(String relativePath) {
        return super.readFile("sarif/" + relativePath);
    }
}
