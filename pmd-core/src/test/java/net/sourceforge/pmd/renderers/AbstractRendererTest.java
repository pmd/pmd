/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.ReportTest;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleWithProperties;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.reporting.FileAnalysisListener;

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

    protected Consumer<FileAnalysisListener> reportOneViolation() {
        return it -> it.onRuleViolation(newRuleViolation(1));
    }

    private Consumer<FileAnalysisListener> reportTwoViolations() {
        return it -> {
            RuleViolation informationalRuleViolation = newRuleViolation(1);
            informationalRuleViolation.getRule().setPriority(RulePriority.LOW);
            it.onRuleViolation(informationalRuleViolation);
            RuleViolation severeRuleViolation = newRuleViolation(2);
            severeRuleViolation.getRule().setPriority(RulePriority.HIGH);
            it.onRuleViolation(severeRuleViolation);
        };
    }

    protected RuleViolation newRuleViolation(int endColumn) {
        return newRuleViolation(endColumn, "Foo");
    }

    protected RuleViolation newRuleViolation(int endColumn, String ruleName) {
        DummyNode node = createNode(endColumn);
        FooRule rule = new FooRule();
        rule.setName(ruleName);
        return new ParametricRuleViolation<Node>(rule, node, "blah");
    }

    /**
     * Read a resource file relative to this class's location.
     */
    protected String readFile(String relativePath) {
        try (InputStream in = getClass().getResourceAsStream(relativePath)) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected DummyNode createNode(int endColumn) {
        DummyNode node = new DummyRoot().withFileName(getSourceCodeFilename());
        node.setCoords(1, 1, 1, endColumn);
        return node;
    }

    @Test
    public void testRuleWithProperties() throws Exception {
        DummyNode node = createNode(1);
        RuleWithProperties theRule = new RuleWithProperties();
        theRule.setProperty(RuleWithProperties.STRING_PROPERTY_DESCRIPTOR,
                "the string value\nsecond line with \"quotes\"");
        String rendered = ReportTest.render(getRenderer(), it -> it.onRuleViolation(new ParametricRuleViolation<Node>(theRule, node, "blah")));
        assertEquals(filter(getExpectedWithProperties()), filter(rendered));
    }

    @Test
    public void testRenderer() throws Exception {
        String actual = render(reportOneViolation());
        assertEquals(filter(getExpected()), filter(actual));
    }

    @Test
    public void testRendererEmpty() throws Exception {
        String actual = render(it -> {});
        assertEquals(filter(getExpectedEmpty()), filter(actual));
    }

    @Test
    public void testRendererMultiple() throws Exception {
        String actual = render(reportTwoViolations());
        assertEquals(filter(getExpectedMultiple()), filter(actual));
    }

    @Test
    public void testError() throws Exception {
        Report.ProcessingError err = new Report.ProcessingError(new RuntimeException("Error"), "file");
        String actual = render(it -> it.onError(err));
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    @Test
    public void testErrorWithoutMessage() throws Exception {
        Report.ProcessingError err = new Report.ProcessingError(new NullPointerException(), "file");
        String actual = render(it -> it.onError(err));
        assertEquals(filter(getExpectedErrorWithoutMessage(err)), filter(actual));
    }

    private String render(Consumer<FileAnalysisListener> listenerEffects) throws IOException {
        return ReportTest.render(getRenderer(), listenerEffects);
    }

    @Test
    public void testConfigError() throws Exception {
        Report.ConfigurationError err = new Report.ConfigurationError(new FooRule(), "a configuration error");
        String actual = ReportTest.renderGlobal(getRenderer(), it -> it.onConfigError(err));
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }
}
