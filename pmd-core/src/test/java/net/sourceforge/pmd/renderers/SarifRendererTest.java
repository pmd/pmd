/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

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
        Report rep = reportTwoViolations();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    private Report reportTwoViolations() {
        Report report = new Report();
        RuleViolation informationalRuleViolation = newRuleViolation(1, "Foo");
        informationalRuleViolation.getRule().setPriority(RulePriority.LOW);
        report.addRuleViolation(informationalRuleViolation);
        RuleViolation severeRuleViolation = newRuleViolation(2, "Boo");
        severeRuleViolation.getRule().setPriority(RulePriority.HIGH);
        report.addRuleViolation(severeRuleViolation);
        return report;
    }

    @Test
    public void testRendererMultipleLocations() throws Exception {
        Report rep = reportThreeViolationsTwoRules();
        String actual = ReportTest.render(getRenderer(), rep);

        JSONObject json = new JSONObject(actual);
        JSONArray results = json.getJSONArray("runs").getJSONObject(0).getJSONArray("results");
        assertEquals(3, results.length());
        assertEquals(filter(readFile("expected-multiple-locations.sarif.json")), filter(actual));
    }

    private Report reportThreeViolationsTwoRules() {
        Rule fooRule = new FooRule();
        fooRule.setName("Foo");
        fooRule.setPriority(RulePriority.LOW);
        Rule booRule = new FooRule();
        booRule.setName("Boo");
        booRule.setPriority(RulePriority.HIGH);

        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1, 1, 1, 10, fooRule));
        report.addRuleViolation(newRuleViolation(5, 1, 5, 11, fooRule));
        report.addRuleViolation(newRuleViolation(2, 2, 3, 1, booRule));
        return report;
    }

    private RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, Rule rule) {
        DummyNode node = new DummyNode(1);
        node.testingOnlySetBeginLine(beginLine);
        node.testingOnlySetBeginColumn(beginColumn);
        node.testingOnlySetEndLine(endLine);
        node.testingOnlySetEndColumn(endColumn);

        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        return new ParametricRuleViolation<Node>(rule, ctx, node, "blah");
    }

    private RuleViolation newRuleViolation(int endColumn, String ruleName) {
        DummyNode node = createNode(endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        AbstractRule fooRule = new FooRule();
        fooRule.setName(ruleName);
        return new ParametricRuleViolation<Node>(fooRule, ctx, node, "blah");
    }

    private String readFile(String name) {
        try (InputStream in = SarifRendererTest.class.getResourceAsStream("sarif/" + name)) {
            String asd = IOUtils.toString(in, StandardCharsets.UTF_8);
            return asd;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
