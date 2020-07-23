/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;

public class ReportTest implements ThreadSafeReportListener {

    private boolean violationSemaphore;

    @Override
    public void ruleViolationAdded(RuleViolation ruleViolation) {
        violationSemaphore = true;
    }

    // Files are grouped together now.
    @Test
    public void testSortedReportFile() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("foo"));
        Node s = getNode(10, 5);
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new ParametricRuleViolation<>(rule1, ctx, s, rule1.getMessage()));
        ctx.setSourceCodeFile(new File("bar"));
        Node s1 = getNode(10, 5);
        Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new ParametricRuleViolation<>(rule2, ctx, s1, rule2.getMessage()));
        Renderer rend = new XMLRenderer();
        String result = render(rend, r);
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    @Test
    public void testSortedReportLine() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("foo1")); // same file!!
        Node node1 = getNode(20, 5); // line 20: after rule2 violation
        Rule rule1 = new MockRule("rule1", "rule1", "msg", "rulesetname");
        r.addRuleViolation(new ParametricRuleViolation<>(rule1, ctx, node1, rule1.getMessage()));

        ctx.setSourceCodeFile(new File("foo1")); // same file!!
        Node node2 = getNode(10, 5); // line 10: before rule1 violation
        Rule rule2 = new MockRule("rule2", "rule2", "msg", "rulesetname");
        r.addRuleViolation(new ParametricRuleViolation<>(rule2, ctx, node2, rule2.getMessage()));
        Renderer rend = new XMLRenderer();
        String result = render(rend, r);
        assertTrue("sort order wrong", result.indexOf("rule2") < result.indexOf("rule1"));
    }

    @Test
    public void testListener() {
        Report rpt = new Report();
        rpt.addListener(this);
        violationSemaphore = false;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("file"));
        Node s = getNode(5, 5);
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        rpt.addRuleViolation(new ParametricRuleViolation<>(rule1, ctx, s, rule1.getMessage()));
        assertTrue(violationSemaphore);
    }

    @Test
    public void testSummary() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFile(new File("foo1"));
        Node s = getNode(5, 5);
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new ParametricRuleViolation<>(rule, ctx, s, rule.getMessage()));
        ctx.setSourceCodeFile(new File("foo2"));
        Rule mr = new MockRule("rule1", "rule1", "msg", "rulesetname");
        Node s1 = getNode(20, 5);
        Node s2 = getNode(30, 5);
        r.addRuleViolation(new ParametricRuleViolation<>(mr, ctx, s1, mr.getMessage()));
        r.addRuleViolation(new ParametricRuleViolation<>(mr, ctx, s2, mr.getMessage()));
        Map<String, Integer> summary = r.getSummary();
        assertEquals(summary.keySet().size(), 2);
        assertTrue(summary.containsValue(1));
        assertTrue(summary.containsValue(2));
    }

    @Test
    public void testTreeIterator() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        Node node1 = getNode(5, 5, true);
        r.addRuleViolation(new ParametricRuleViolation<>(rule, ctx, node1, rule.getMessage()));
        Node node2 = getNode(5, 6, true);
        r.addRuleViolation(new ParametricRuleViolation<>(rule, ctx, node2, rule.getMessage()));

        Iterator<RuleViolation> violations = r.iterator();
        int violationCount = 0;
        while (violations.hasNext()) {
            violations.next();
            violationCount++;
        }
        assertEquals(2, violationCount);

        Iterator<RuleViolation> treeIterator = r.treeIterator();
        int treeCount = 0;
        while (treeIterator.hasNext()) {
            treeIterator.next();
            treeCount++;
        }
        assertEquals(2, treeCount);
    }

    private static Node getNode(int line, int column) {
        DummyNode s = new DummyNode();
        DummyNode parent = new DummyNode();
        parent.setCoords(line, column, line, column + 1);
        parent.addChild(s, 0);
        s.setCoords(line, column, line, column + 1);
        return s;
    }

    private static Node getNode(int line, int column, boolean nextLine) {
        DummyNode s = (DummyNode) getNode(line, column);
        if (nextLine) {
            s.setCoords(line + 1, column + 4, line + 4, 1);
        }
        return s;
    }

    public static String render(Renderer renderer, Report report) throws IOException {
        StringWriter writer = new StringWriter();
        renderer.setWriter(writer);
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        return writer.toString();
    }

    public static String renderTempFile(Renderer renderer, Report report, Charset expectedCharset) throws IOException {
        Path tempFile = Files.createTempFile("pmd-report-test", null);
        String absolutePath = tempFile.toAbsolutePath().toString();

        renderer.setReportFile(absolutePath);
        renderer.start();
        renderer.renderFileReport(report);
        renderer.end();
        renderer.flush();

        try (FileInputStream input = new FileInputStream(absolutePath)) {
            return IOUtils.toString(input, expectedCharset);
        } finally {
            Files.delete(tempFile);
        }
    }
}
