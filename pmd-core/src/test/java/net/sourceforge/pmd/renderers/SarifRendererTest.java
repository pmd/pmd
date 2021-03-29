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
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportTest;
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
        // Setup
        Report rep = reportTwoViolations();

        // Exercise
        String actual = ReportTest.render(getRenderer(), rep);

        // Verify
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
