/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleWithProperties;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.util.IOUtil;

public abstract class AbstractRendererTest {

    @org.junit.Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
        report.addRuleViolation(newRuleViolation(1, 1, 1, 1, createFooRule()));
        return report;
    }

    protected Report reportTwoViolations() {
        Report report = new Report();
        RuleViolation informationalRuleViolation = newRuleViolation(1, 1, 1, 1, createFooRule());
        report.addRuleViolation(informationalRuleViolation);
        RuleViolation severeRuleViolation = newRuleViolation(1, 1, 1, 2, createBooRule());
        report.addRuleViolation(severeRuleViolation);
        return report;
    }

    protected DummyNode createNode(int beginLine, int beginColumn, int endLine, int endColumn) {
        DummyNode node = new DummyNode(1);
        node.testingOnlySetBeginLine(beginLine);
        node.testingOnlySetBeginColumn(beginColumn);
        node.testingOnlySetEndLine(endLine);
        node.testingOnlySetEndColumn(endColumn);
        return node;
    }

    protected RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, Rule rule) {
        DummyNode node = createNode(beginLine, beginColumn, endLine, endColumn);
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File(getSourceCodeFilename()));
        return new ParametricRuleViolation<Node>(rule, ctx, node, "blah");
    }

    /**
     * Creates a new rule instance with name "Boo" and priority {@link RulePriority#HIGH}.
     */
    protected Rule createBooRule() {
        Rule booRule = new FooRule();
        booRule.setName("Boo");
        booRule.setDescription("desc");
        booRule.setPriority(RulePriority.HIGH);
        return booRule;
    }

    /**
     * Creates a new rule instance with name "Foo" and priority {@link RulePriority#LOW}.
     */
    protected Rule createFooRule() {
        Rule fooRule = new FooRule();
        fooRule.setName("Foo");
        fooRule.setPriority(RulePriority.LOW);
        return fooRule;
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
        report.addRuleViolation(newRuleViolation(1, 1, 1, 1, theRule));
        String rendered = renderReport(getRenderer(), report);
        assertEquals(filter(getExpectedWithProperties()), filter(rendered));
    }

    @Test
    public void testRenderer() throws Exception {
        testRenderer(Charset.defaultCharset());
    }

    protected void testRenderer(Charset expectedCharset) throws Exception {
        Report rep = reportOneViolation();
        String actual = renderReport(getRenderer(), rep, expectedCharset);
        assertEquals(filter(getExpected()), filter(actual));
    }

    @Test
    public void testRendererEmpty() throws Exception {
        Report rep = new Report();
        String actual = renderReport(getRenderer(), rep);
        assertEquals(filter(getExpectedEmpty()), filter(actual));
    }

    @Test
    public void testRendererMultiple() throws Exception {
        Report rep = reportTwoViolations();
        String actual = renderReport(getRenderer(), rep);
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    @Test
    public void testError() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new RuntimeException("Error"), "file");
        rep.addError(err);
        String actual = renderReport(getRenderer(), rep);
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    @Test
    public void testErrorWithoutMessage() throws Exception {
        Report rep = new Report();
        Report.ProcessingError err = new Report.ProcessingError(new NullPointerException(), "file");
        rep.addError(err);
        String actual = renderReport(getRenderer(), rep);
        assertEquals(filter(getExpectedErrorWithoutMessage(err)), filter(actual));
    }

    @Test
    public void testConfigError() throws Exception {
        Report rep = new Report();
        Report.ConfigurationError err = new Report.ConfigurationError(new FooRule(), "a configuration error");
        rep.addConfigError(err);
        String actual = renderReport(getRenderer(), rep);
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    protected String renderReport(Renderer renderer, Report report) throws IOException {
        return renderReport(renderer, report, Charset.defaultCharset());
    }

    protected String renderReport(Renderer renderer, Report report, Charset expectedEncoding) throws IOException {
        File file = temporaryFolder.newFile();
        renderer.setReportFile(file.getAbsolutePath());
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        renderer.flush();
        return IOUtil.readFileToString(file, expectedEncoding);
    }
}
