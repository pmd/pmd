/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.function.Consumer;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.DummyNode.DummyRootNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.reporting.FileAnalysisListener;
import net.sourceforge.pmd.reporting.GlobalAnalysisListener;
import net.sourceforge.pmd.util.datasource.DataSource;

public class ReportTest {

    // Files are grouped together now.
    @Test
    public void testSortedReportFile() throws IOException {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            Node s = getNode(10, 5, "foo");
            Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation<>(rule1, s, rule1.getMessage()));
            Node s1 = getNode(10, 5, "bar");
            Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation<>(rule2, s1, rule2.getMessage()));
        });
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    @Test
    public void testSortedReportLine() throws IOException {
        Renderer rend = new XMLRenderer();
        String result = render(rend, r -> {
            Node node1 = getNode(20, 5, "foo1"); // line 20: after rule2 violation
            Rule rule1 = new MockRule("rule1", "rule1", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation<>(rule1, node1, rule1.getMessage()));

            Node node2 = getNode(10, 5, "foo1"); // line 10: before rule1 violation
            Rule rule2 = new MockRule("rule2", "rule2", "msg", "rulesetname");
            r.onRuleViolation(new ParametricRuleViolation<>(rule2, node2, rule2.getMessage())); // same file!!
        });
        assertTrue("sort order wrong", result.indexOf("rule2") < result.indexOf("rule1"));
    }

    @Test
    public void testIterator() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = getNode(5, 5, true, "file1");
        Node node2 = getNode(5, 6, true, "file1");
        Report r = Report.buildReport(it -> {
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node1, rule.getMessage()));
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node2, rule.getMessage()));
        });

        assertEquals(2, r.getViolations().size());
    }

    private static DummyNode getNode(int line, int column, String filename) {
        DummyRootNode parent = new DummyRootNode();
        parent.withFileName(filename);
        DummyNode s = new DummyNode();
        parent.setCoords(line, column, line, column + 1);
        parent.addChild(s, 0);
        s.setCoords(line, column, line, column + 1);
        return s;
    }

    @Test
    public void testFilterViolations() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = getNode(5, 5, true, "file1");
        Node node2 = getNode(5, 6, true, "file1");
        Report r = Report.buildReport(it -> {
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node1, rule.getMessage()));
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node2, "to be filtered"));
        });

        Report filtered = r.filterViolations(ruleViolation -> !"to be filtered".equals(ruleViolation.getDescription()));

        assertEquals(1, filtered.getViolations().size());
        assertEquals("msg", filtered.getViolations().get(0).getDescription());
    }

    @Test
    public void testUnion() {
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = getNode(1, 2, true, "file1");
        Report report1 = Report.buildReport(it -> {
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node1, rule.getMessage()));
        });

        Node node2 = getNode(2, 1, true, "file1");
        Report report2 = Report.buildReport(it -> {
            it.onRuleViolation(new ParametricRuleViolation<>(rule, node2, rule.getMessage()));
        });

        Report union = report1.union(report2);
        assertEquals(2, union.getViolations().size());
    }

    private static Node getNode(int line, int column, boolean nextLine, String filename) {
        DummyNode s = getNode(line, column, filename);
        if (nextLine) {
            s.setCoords(line + 1, column + 4, line + 4, 1);
        }
        return s;
    }

    public static String render(Renderer renderer, Consumer<? super FileAnalysisListener> listenerEffects) throws IOException {
        return renderGlobal(renderer, globalListener -> {
            DataSource dummyFile = DataSource.forString("dummyText", "file");
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
