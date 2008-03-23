/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import junit.framework.JUnit4TestAdapter;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.IRuleViolation;
import net.sourceforge.pmd.MockRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.SourceType;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.symboltable.SourceFileScope;

import org.junit.Test;

import test.net.sourceforge.pmd.testframework.RuleTst;

public class ReportTest extends RuleTst implements ReportListener {

    private static class FooRule extends AbstractRule {
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

    public void ruleViolationAdded(IRuleViolation ruleViolation) {
        violationSemaphore = true;
    }

    public void metricAdded(Metric metric) {
        metricSemaphore = true;
    }

    @Test
    public void testBasic() throws Throwable {
        Report r = new Report();
        runTestFromString(TEST1, new FooRule(), r);
        assertTrue(!r.isEmpty());
    }

    @Test
    public void testMetric0() {
        Report r = new Report();
        assertTrue("Default report shouldn't contain metrics", !r.hasMetrics());
    }

    @Test
    public void testMetric1() {
        Report r = new Report();
        assertTrue("Default report shouldn't contain metrics", !r.hasMetrics());

        r.addMetric(new Metric("m1", 0, 0.0, 1.0, 2.0, 3.0, 4.0));
        assertTrue("Expected metrics weren't there", r.hasMetrics());

        Iterator ms = r.metrics();
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
    public void testExclusionsInReportWithAnnotations() throws Throwable {
        Report rpt = new Report();
        runTestFromString(TEST2, new FooRule(), rpt, SourceType.JAVA_15);
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
    public void testSortedReport_File() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        SimpleNode s = getNode(10, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s));
        ctx.setSourceCodeFilename("bar");
        SimpleNode s1 = getNode(10, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s1));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    @Test
    public void testSortedReport_Line() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        SimpleNode s = getNode(10, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2", "msg", "rulesetname"), ctx, s));
        ctx.setSourceCodeFilename("foo2");
        SimpleNode s1 = getNode(20, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new RuleViolation(new MockRule("rule1", "rule1", "msg", "rulesetname"), ctx, s1));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue("sort order wrong", result.indexOf("rule2") < result.indexOf("rule1"));
    }

    @Test
    public void testListener() {
        Report rpt = new Report();
        rpt.addListener(this);
        violationSemaphore = false;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("file");
        SimpleNode s = getNode(5, 5, ctx.getSourceCodeFilename());
        rpt.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s));
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
        SimpleNode s = getNode(5, 5, ctx.getSourceCodeFilename());
        Rule rule = new MockRule("name", "desc", "msg", "rulesetname");
        r.addRuleViolation(new RuleViolation(rule, ctx, s));
        ctx.setSourceCodeFilename("foo2");
        Rule mr = new MockRule("rule1", "rule1", "msg", "rulesetname");
        SimpleNode s1 = getNode(20, 5, ctx.getSourceCodeFilename());
        SimpleNode s2 = getNode(30, 5, ctx.getSourceCodeFilename());
        r.addRuleViolation(new RuleViolation(mr, ctx, s1));
        r.addRuleViolation(new RuleViolation(mr, ctx, s2));
        Map summary = r.getSummary();
        assertEquals(summary.keySet().size(), 2);
        assertTrue(summary.values().contains(new Integer(1)));
        assertTrue(summary.values().contains(new Integer(2)));
    }
    
    private SimpleNode getNode(int line, int column, String scopeName){
        SimpleNode s = new SimpleJavaNode(2);
        SimpleNode parent = new SimpleJavaNode(1);
        parent.testingOnly__setBeginLine(line);
        parent.testingOnly__setBeginColumn(column);
        s.jjtSetParent(parent);
        s.setScope(new SourceFileScope(scopeName));
        s.testingOnly__setBeginLine(10);
        s.testingOnly__setBeginColumn(5);
        return s;
    }
    
    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(ReportTest.class);
    }

}
