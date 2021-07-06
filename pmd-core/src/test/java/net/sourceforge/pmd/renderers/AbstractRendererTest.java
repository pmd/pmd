/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleWithProperties;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

public abstract class AbstractRendererTest {

    public abstract Renderer getRenderer();

    public abstract String getExpected();

    public String getExpectedWithProperties() {
        return getExpected();
    }

    public abstract String getExpectedEmpty();

    public abstract String getExpectedMultiple();

    public String getExpectedError(ProcessingError error) {
        return "";
    }

    public String getExpectedErrorWithoutMessage(ProcessingError error) {
        return getExpectedError(error);
    }

    public String getExpectedError(ConfigurationError error) {
        return "";
    }

    public String filter(String expected) {
        return expected;
    }

    protected String getSourceCodeFilename() {
        return "notAvailable.ext";
    }

    @Test(expected = NullPointerException.class)
    public void testNullPassedIn() throws Exception {
        getRenderer().renderFileReport(null);
    }

    protected Report reportOneViolation() {
        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1));
        return report;
    }

    private Report reportTwoViolations() {
        Report report = new Report();
        RuleViolation informationalRuleViolation = newRuleViolation(1);
        informationalRuleViolation.getRule().setPriority(RulePriority.LOW);
        report.addRuleViolation(informationalRuleViolation);
        RuleViolation severeRuleViolation = newRuleViolation(2);
        severeRuleViolation.getRule().setPriority(RulePriority.HIGH);
        report.addRuleViolation(severeRuleViolation);
        return report;
    }


    protected RuleViolation newRuleViolation(int endColumn) {
        return newRuleViolation(new FooRule(), endColumn);
    }

    protected RuleViolation newRuleViolation(Rule theRule, int endColumn) {
        DummyNode node = createNode(endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        return new ParametricRuleViolation<Node>(theRule, ctx, node, "blah");
    }

    protected static DummyNode createNode(int endColumn) {
        DummyNode node = new DummyNode(1);
        node.testingOnlySetBeginLine(1);
        node.testingOnlySetBeginColumn(1);
        node.testingOnlySetEndLine(1);
        node.testingOnlySetEndColumn(endColumn);
        return node;
    }

    @Test
    public void testRuleWithProperties() throws Exception {
        Report report = new Report();
        RuleWithProperties theRule = new RuleWithProperties();
        theRule.setProperty(RuleWithProperties.STRING_PROPERTY_DESCRIPTOR,
                "the string value\nsecond line with \"quotes\"");
        report.addRuleViolation(newRuleViolation(theRule, 1));
        String rendered = ReportTest.render(getRenderer(), report);
        assertEquals(filter(getExpectedWithProperties()), filter(rendered));
    }

    @Test
    public void testRenderer() throws Exception {
        Report rep = reportOneViolation();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpected()), filter(actual));
    }

    @Test
    public void testRendererEmpty() throws Exception {
        Report rep = new Report();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedEmpty()), filter(actual));
    }

    @Test
    public void testRendererMultiple() throws Exception {
        Report rep = reportTwoViolations();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    @Test
    public void testError() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new RuntimeException("Error"), "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    @Test
    public void testErrorWithoutMessage() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new NullPointerException(), "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedErrorWithoutMessage(err)), filter(actual));
    }

    @Test
    public void testConfigError() throws Exception {
        Report rep = new Report();
        Report.ConfigurationError err = new Report.ConfigurationError(new FooRule(), "a configuration error");
        rep.addConfigError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }
}
