/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.DummyNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.lang.rule.ParametricRuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;

public class ReportTest implements ThreadSafeReportListener {

    private boolean violationSemaphore;
    private boolean metricSemaphore;

    @Override
    public void ruleViolationAdded(RuleViolation ruleViolation) {
        violationSemaphore = true;
    }

    @Override
    public void metricAdded(Metric metric) {
        metricSemaphore = true;
    }

    @Test
    public void testMetric0() {
        Report r = new Report();
        assertFalse("Default report shouldn't contain metrics", r.hasMetrics());
    }

    @Test
    public void testMetric1() {
        Report r = new Report();
        assertFalse("Default report shouldn't contain metrics", r.hasMetrics());

        r.addMetric(new Metric("m1", 0, 0.0, 1.0, 2.0, 3.0, 4.0));
        assertTrue("Expected metrics weren't there", r.hasMetrics());

        Iterator<Metric> ms = r.metrics();
        assertTrue("Should have some metrics in there now", ms.hasNext());

        Object o = ms.next();
        assertTrue("Expected Metric, got " + o.getClass(), o instanceof Metric);

        Metric m = (Metric) o;
        assertEquals("metric name mismatch", "m1", m.getMetricName());
        assertEquals("wrong low value", 1.0, m.getLowValue(), 0.05);
        assertEquals("wrong high value", 2.0, m.getHighValue(), 0.05);
        assertEquals("wrong avg value", 3.0, m.getAverage(), 0.05);
        assertEquals("wrong std dev value", 4.0, m.getStandardDeviation(), 0.05);
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

        metricSemaphore = false;
        rpt.addMetric(new Metric("test", 0, 0.0, 0.0, 0.0, 0.0, 0.0));

        assertTrue("no metric", metricSemaphore);
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
        assertTrue(summary.values().contains(Integer.valueOf(1)));
        assertTrue(summary.values().contains(Integer.valueOf(2)));
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
        DummyNode s = new DummyNode(2);
        DummyNode parent = new DummyNode(1);
        parent.testingOnlySetBeginLine(line);
        parent.testingOnlySetBeginColumn(column);
        s.jjtSetParent(parent);
        s.testingOnlySetBeginLine(line);
        s.testingOnlySetBeginColumn(column);
        return s;
    }

    private static Node getNode(int line, int column, boolean nextLine) {
        DummyNode s = (DummyNode) getNode(line, column);
        if (nextLine) {
            s.testingOnlySetBeginLine(line + 1);
            s.testingOnlySetBeginColumn(column + 4);
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

    public static class MyMergeableData implements MergeableData {
        public int count = 0;

        @Override
        public MergeableData create() {
            return new MyMergeableData();
        }

        @Override
        public void merge(MergeableData other) {
            this.count += ((MyMergeableData)other).count;
        }
    }

    @Test
    public void testCustomData() {
        Report r = new Report();

        MyMergeableData d0 = new MyMergeableData();
        d0.count = 10;

        MyMergeableData d1 = r.getCustomData(d0);
        assertEquals(0, d1.count);
        d1.count += 1;

        MyMergeableData d2 = r.getCustomData(new MyMergeableData());
        assertEquals(d1, d2); // same instance
        assertEquals(1, d2.count);

        d2.merge(d0);
        assertEquals(11, d2.count);
    }
}
