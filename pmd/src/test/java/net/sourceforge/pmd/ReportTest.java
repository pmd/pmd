/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.DummyJavaNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.JavaRuleViolation;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.MockRule;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.testframework.RuleTst;

import org.junit.Test;


public class ReportTest extends RuleTst implements ReportListener {

    public static class FooRule extends AbstractJavaRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if ("Foo".equals(c.getImage())) addViolation(ctx, c);
            return ctx;
        }

        public String getMessage() {
            return "blah";
        }

        public String getName() {
            return "Foo";
        }

        public String getRuleSetName() {
            return "RuleSet";
        }

        public String getDescription() {
            return "desc";
        }
    }

    private boolean violationSemaphore;
    private boolean metricSemaphore;

    public void ruleViolationAdded(RuleViolation ruleViolation) {
        violationSemaphore = true;
    }

    public void metricAdded(Metric metric) {
        metricSemaphore = true;
    }

    @Test
    public void testBasic() throws Throwable {
        Report r = new Report();
        runTestFromString(TEST1, new FooRule(), r);
        assertFalse(r.isEmpty());
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

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressRegex() throws Throwable {
        Report rpt = new Report();
        Rule rule =  new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_REGEX_DESCRIPTOR, ".*blah.*");
        runTestFromString(TEST1, rule, rpt);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithRuleViolationSuppressXPath() throws Throwable {
        Report rpt = new Report();
        Rule rule =  new FooRule();
        rule.setProperty(Rule.VIOLATION_SUPPRESS_XPATH_DESCRIPTOR, ".[@Image = 'Foo']");
        runTestFromString(TEST1, rule, rpt);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithAnnotations() throws Throwable {
        Report rpt = new Report();
        runTestFromString(TEST2, new FooRule(), rpt, LanguageVersion.JAVA_15);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    @Test
    public void testExclusionsInReportWithNOPMD() throws Throwable {
        Report rpt = new Report();
        runTestFromString(TEST3, new FooRule(), rpt);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "@SuppressWarnings(\"PMD\")" + PMD.EOL +
            "public class Foo {}";

    private static final String TEST3 =
            "public class Foo {} // NOPMD";

    // Files are grouped together now.
    @Test
    public void testSortedReport_File() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        JavaNode s = getNode(10, 5, ctx.getSourceCodeFilename());
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new JavaRuleViolation(rule1, ctx, s, rule1.getMessage()));
        ctx.setSourceCodeFilename("bar");
        JavaNode s1 = getNode(10, 5, ctx.getSourceCodeFilename());
        Rule rule2 = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new JavaRuleViolation(rule2, ctx, s1, rule2.getMessage()));
        Renderer rend = new XMLRenderer();
        String result = render(rend, r);
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    @Test
    public void testSortedReport_Line() throws IOException {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        JavaNode s = getNode(10, 5, ctx.getSourceCodeFilename());
        Rule rule1 = new MockRule("rule2", "rule2", "msg", "rulesetname");
        r.addRuleViolation(new JavaRuleViolation(rule1, ctx, s, rule1.getMessage()));
        ctx.setSourceCodeFilename("foo2");
        JavaNode s1 = getNode(20, 5, ctx.getSourceCodeFilename());
        Rule rule2 = new MockRule("rule1", "rule1", "msg", "rulesetname");
        r.addRuleViolation(new JavaRuleViolation(rule2, ctx, s1, rule2.getMessage()));
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
        ctx.setSourceCodeFilename("file");
        JavaNode s = getNode(5, 5, ctx.getSourceCodeFilename());
        Rule rule1 = new MockRule("name", "desc", "msg", "rulesetname");
        rpt.addRuleViolation(new JavaRuleViolation(rule1, ctx, s, rule1.getMessage()));
        assertTrue(violationSemaphore);

        metricSemaphore = false;
        rpt.addMetric(new Metric("test", 0, 0.0, 0.0, 0.0, 0.0, 0.0));

        assertTrue("no metric", metricSemaphore);
    }

    @Test
    public void testSummary() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        JavaNode s = getNode(5, 5, ctx.getSourceCodeFilename());
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new JavaRuleViolation(rule, ctx, s, rule.getMessage()));
        ctx.setSourceCodeFilename("foo2");
        Rule mr = new MockRule("rule1", "rule1", "msg", "rulesetname");
        JavaNode s1 = getNode(20, 5, ctx.getSourceCodeFilename());
        JavaNode s2 = getNode(30, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new JavaRuleViolation(mr, ctx, s1, mr.getMessage()));
        r.addRuleViolation(new JavaRuleViolation(mr, ctx, s2, mr.getMessage()));
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
        JavaNode node1 = getNode(5, 5, ctx.getSourceCodeFilename(), true);
        r.addRuleViolation(new JavaRuleViolation(rule, ctx, node1, rule.getMessage()));
        JavaNode node2 = getNode(5, 6, ctx.getSourceCodeFilename(), true);
        r.addRuleViolation(new JavaRuleViolation(rule, ctx, node2, rule.getMessage()));

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
    
    public static JavaNode getNode(int line, int column, String scopeName){
	DummyJavaNode s = new DummyJavaNode(2);
        DummyJavaNode parent = new DummyJavaNode(1);
        parent.testingOnly__setBeginLine(line);
        parent.testingOnly__setBeginColumn(column);
        s.jjtSetParent(parent);
        s.setScope(new SourceFileScope(scopeName));
        s.testingOnly__setBeginLine(10);
        s.testingOnly__setBeginColumn(5);
        return s;
    }

    public static JavaNode getNode(int line, int column, String scopeName, boolean nextLine) {
        DummyJavaNode s = (DummyJavaNode)getNode(line, column, scopeName);
        if (nextLine) {
            s.testingOnly__setBeginLine(line + 1);
            s.testingOnly__setBeginColumn(column + 4);
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

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ReportTest.class);
    }

}
