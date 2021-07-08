/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;

public class SarifRendererTest extends AbstractRendererTest {
    @Override
    public Renderer getRenderer() {
        return new SarifRenderer();
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
        return expected.replaceAll("\r\n", "\n"); // make the test run on Windows, too
    }

    @Override
    @Test
    public void testRendererMultiple() throws Exception {
        // Setup
        Report rep = reportTwoViolations();

        // Exercise
        String actual = ReportTest.render(getRenderer(), rep);

        // Verify that both rules are and rule ids are linked in the results 
        // Initially was comparing whole files but order of rules rendered can't be guaranteed when the report is being rendered
        // Refer to pmd-core/src/test/resources/net/sourceforge/pmd/renderers/sarif/expected-multiple.sarif.json to see an example data structure
        assertThat(filter(actual), containsString("\"ruleId\": \"Foo\""));
        assertThat(filter(actual), containsString("\"ruleId\": \"Boo\""));
        assertThat(filter(actual), containsString("\"id\": \"Foo\""));
        assertThat(filter(actual), containsString("\"id\": \"Boo\""));
    }

    private Report reportTwoViolations() {
        return Report.buildReport(reportBuilder -> {
            RuleViolation informationalRuleViolation = newRuleViolation(1, "Foo");
            informationalRuleViolation.getRule().setPriority(RulePriority.LOW);
            reportBuilder.onRuleViolation(informationalRuleViolation);
            RuleViolation severeRuleViolation = newRuleViolation(2, "Boo");
            severeRuleViolation.getRule().setPriority(RulePriority.HIGH);
            reportBuilder.onRuleViolation(severeRuleViolation);
        });
    }

    protected String readFile(String relativePath) {
        return super.readFile("sarif/" + relativePath);
    }
}
