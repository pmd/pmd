/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.renderers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import net.sourceforge.pmd.FooRule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.Report.ConfigurationError;
import net.sourceforge.pmd.Report.ProcessingError;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.RuleWithProperties;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.IOUtil;
import net.sourceforge.pmd.util.datasource.DataSource;

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

    protected Consumer<FileAnalysisListener> reportOneViolation() {
        return it -> it.onRuleViolation(newRuleViolation(1, 1, 1, 1, createFooRule()));
    }

    private Consumer<FileAnalysisListener> reportTwoViolations() {
        return it -> {
            RuleViolation informationalRuleViolation = newRuleViolation(1, 1, 1, 1, createFooRule());
            it.onRuleViolation(informationalRuleViolation);
            RuleViolation severeRuleViolation = newRuleViolation(1, 1, 1, 2, createBooRule());
            it.onRuleViolation(severeRuleViolation);
        };
    }

    protected DummyNode createNode(int beginLine, int beginColumn, int endLine, int endColumn) {
        DummyNode node = new DummyRootNode().withFileName(getSourceCodeFilename());
        node.setCoords(beginLine, beginColumn, endLine, endColumn);
        return node;
    }

    protected RuleViolation newRuleViolation(int beginLine, int beginColumn, int endLine, int endColumn, Rule rule) {
        DummyNode node = createNode(beginLine, beginColumn, endLine, endColumn);
        return new ParametricRuleViolation<Node>(rule, node, "blah");
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

    /**
     * Read a resource file relative to this class's location.
     */
    protected String readFile(String relativePath) {
        try (InputStream in = getClass().getResourceAsStream(relativePath)) {
            return IOUtil.readToString(in, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRuleWithProperties() throws Exception {
        DummyNode node = createNode(1, 1, 1, 1);
        RuleWithProperties theRule = new RuleWithProperties();
        theRule.setProperty(RuleWithProperties.STRING_PROPERTY_DESCRIPTOR,
                "the string value\nsecond line with \"quotes\"");
        String rendered = renderReport(getRenderer(),
                it -> it.onRuleViolation(newRuleViolation(1, 1, 1, 1, theRule)));
        assertEquals(filter(getExpectedWithProperties()), filter(rendered));
    }

    @Test
    public void testRenderer() throws Exception {
        testRenderer(Charset.defaultCharset());
    }

    protected void testRenderer(Charset expectedCharset) throws Exception {
        String actual = renderReport(getRenderer(), reportOneViolation(), expectedCharset);
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
        return renderReport(getRenderer(), listenerEffects);
    }

    @Test
    public void testConfigError() throws Exception {
        Report.ConfigurationError err = new Report.ConfigurationError(new FooRule(), "a configuration error");
        String actual = renderGlobal(getRenderer(), it -> it.onConfigError(err));
        assertEquals(filter(getExpectedError(err)), filter(actual));
    }

    protected String renderReport(Renderer renderer, Consumer<? super FileAnalysisListener> listenerEffects) throws IOException {
        return renderReport(renderer, listenerEffects, Charset.defaultCharset());
    }

    protected String renderReport(Renderer renderer, Consumer<? super FileAnalysisListener> listenerEffects,
                                  Charset expectedEncoding) throws IOException {
        return renderGlobal(renderer, globalListener -> {
            DataSource dummyFile = DataSource.forString("dummyText", "file");
            try (FileAnalysisListener fal = globalListener.startFileAnalysis(dummyFile)) {
                listenerEffects.accept(fal);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        }, expectedEncoding);
    }

    private String renderGlobal(Renderer renderer, Consumer<? super GlobalAnalysisListener> listenerEffects) throws IOException {
        return renderGlobal(renderer, listenerEffects, Charset.defaultCharset());
    }

    private String renderGlobal(Renderer renderer, Consumer<? super GlobalAnalysisListener> listenerEffects,
                                Charset expectedEncoding) throws IOException {
        File file = temporaryFolder.newFile();
        renderer.setReportFile(file.getAbsolutePath());

        try (GlobalAnalysisListener listener = renderer.newListener()) {
            listenerEffects.accept(listener);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        return IOUtil.readFileToString(file, expectedEncoding);
    }

}
