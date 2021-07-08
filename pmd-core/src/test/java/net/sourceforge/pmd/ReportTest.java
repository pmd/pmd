/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;

import org.junit.Test;

import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyRoot;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.document.TextFile;

public class ReportTest {

    // Files are grouped together now.
    @Test
    public void testSortedReportFile() throws IOException {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            Node s = getNode(10, 5).withFileName("foo");
            Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation(rule1, s, rule1.getMessage()));
            Node s1 = getNode(10, 5).withFileName("bar");
            Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation(rule2, s1, rule2.getMessage()));
        });
        assertThat(result, containsString("bar"));
        assertThat(result, containsString("foo"));
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    @Test
    public void testSortedReportLine() throws IOException {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            Node node1 = getNode(20, 5).withFileName("foo1"); // line 20: after rule2 violation
            Rule rule1 = new MockRule("rule1", "rule1", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation(rule1, node1, rule1.getMessage()));

            Node node2 = getNode(10, 5).withFileName("foo1"); // line 10: before rule1 violation
            Rule rule2 = new MockRule("rule2", "rule2", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation(rule2, node2, rule2.getMessage())); // same file!!
        });
        assertTrue("sort order wrong", result.indexOf("rule2") < result.indexOf("rule1"));
    }

    @Test
    public void testIterator() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = getNode(5, 5, true);
        Node node2 = getNode(5, 6, true);
        Report r = Report.buildReport(it -> {
            it.onRuleViolation(new ParametricRuleViolation(rule, node1, rule.getMessage()));
            it.onRuleViolation(new ParametricRuleViolation(rule, node2, rule.getMessage()));
        });

        assertEquals(2, r.getViolations().size());
    }

    private static DummyNode getNode(int line, int column) {
        DummyNode parent = new DummyRoot();
        DummyNode s = new DummyNode();
        parent.setCoords(line, column, line, column + 1);
        parent.addChild(s, 0);
        s.setCoords(line, column, line, column + 1);
        return s;
    }

    private static Node getNode(int line, int column, boolean nextLine) {
        DummyNode s = getNode(line, column);
        if (nextLine) {
            s.setCoords(line + 1, column + 4, line + 4, 1);
        }
        return s;
    }

    public static String render(Renderer renderer, Consumer<? super FileAnalysisListener> listenerEffects) throws IOException {
        return renderGlobal(renderer, globalListener -> {
            LanguageVersion dummyVersion = LanguageRegistry.getDefaultLanguage().getDefaultVersion();

            TextFile dummyFile = TextFile.forCharSeq("dummyText", "file", dummyVersion);
            try (FileAnalysisListener fal = globalListener.startFileAnalysis(dummyFile)) {
                listenerEffects.accept(fal);
            } catch (Exception e) {
                throw new AssertionError(e);
            }
        });
    }

    public static String renderGlobal(Renderer renderer, Consumer<? super GlobalAnalysisListener> listenerEffects) throws IOException {
        StringWriter writer = new StringWriter();
        renderer.setWriter(writer);

        try (GlobalAnalysisListener listener = renderer.newListener()) {
            listenerEffects.accept(listener);
        } catch (Exception e) {
            throw new AssertionError(e);
        }

        return writer.toString();
    }

    public static String render(Renderer renderer, Report report) throws IOException {
        StringWriter writer = new StringWriter();
        renderer.setWriter(writer);
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        return writer.toString();
    }
}
