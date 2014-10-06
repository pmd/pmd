/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;

import org.junit.Test;


public abstract class AbstractRendererTst {

    public abstract Renderer getRenderer();

    public abstract String getExpected();

    public abstract String getExpectedEmpty();

    public abstract String getExpectedMultiple();

    public String getExpectedError(ProcessingError error) {
        return "";
    }

    public String filter(String expected) {
        return expected;
    }

    @Test(expected = NullPointerException.class)
    public void testNullPassedIn() throws Throwable {
        getRenderer().renderFileReport(null);
    }

    private static Report reportOneViolation() {
        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1));
        return report;
    }

    private static Report reportTwoViolations() {
        Report report = new Report();
        report.addRuleViolation(newRuleViolation(1));
        report.addRuleViolation(newRuleViolation(2));
        return report;
    }

    private static RuleViolation newRuleViolation(int endColumn) {
        DummyNode node = new DummyNode(1);
        node.testingOnly__setBeginLine(1);
        node.testingOnly__setBeginColumn(1);
        node.testingOnly__setEndLine(1);
        node.testingOnly__setEndColumn(endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("n/a");
        return new ParametricRuleViolation<Node>(new FooRule(), ctx, node, "blah");
    }

    @Test
    public void testRenderer() throws Throwable {
        Report rep = reportOneViolation();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpected()), filter(actual));
    }

    @Test
    public void testRendererEmpty() throws Throwable {
        Report rep = new Report();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedEmpty()), filter(actual));
    }

    @Test
    public void testRendererMultiple() throws Throwable {
        Report rep = reportTwoViolations();
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    @Test
    public void testError() throws Throwable {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError("Error", "file");
        rep.addError(err);
        String actual = ReportTest.render(getRenderer(), rep);
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }
}
