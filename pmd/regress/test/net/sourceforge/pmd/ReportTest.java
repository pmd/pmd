/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
 (DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
 by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.testframework.RuleTst;

import java.util.Iterator;

public class ReportTest extends RuleTst implements ReportListener {

    private static class FooRule extends AbstractRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equals("Foo")) addViolation(ctx, c);
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

    public void testBasic() throws Throwable {
        Report r = new Report();
        runTestFromString(TEST1, new FooRule(), r);
        assertTrue(!r.isEmpty());
    }


    public void testMetric0() {
        Report r = new Report();
        assertTrue("Default report shouldn't contain metrics", !r.hasMetrics());
    }

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

    public void testExclusionsInReportWithAnnotations() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST2, new FooRule(), rpt);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    public void testExclusionsInReportWithNOPMD() throws Throwable {
        Report rpt = new Report();
        runTestFromString(TEST3, new FooRule(), rpt);
        assertTrue(rpt.isEmpty());
        assertEquals(1, rpt.getSuppressedRuleViolations().size());
    }

    private static final String TEST1 =
            "public class Foo {}" + PMD.EOL;

    private static final String TEST2 =
            "@SuppressWarnings(\"\")" + PMD.EOL +
            "public class Foo {}";

    private static final String TEST3 =
            "public class Foo {} // NOPMD";
/*

    // Files are grouped together now.
    public void testSortedReport_File() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s));
        ctx.setSourceCodeFilename("bar");
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(20);
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s1));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    public void testSortedReport_Line() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2", "msg", "rulesetname"), ctx, s));
        ctx.setSourceCodeFilename("foo2");
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(20);
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2", "msg", "rulesetname"), ctx, s1));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue("sort order wrong", result.indexOf("rule2") < result.indexOf("rule1"));
    }

    public void testListener() {
        Report rpt = new Report();
        rpt.addListener(this);
        violationSemaphore = false;
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("file");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(5);
        rpt.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s));
        assertTrue(violationSemaphore);

        metricSemaphore = false;
        rpt.addMetric(new Metric("test", 0, 0.0, 0.0, 0.0, 0.0, 0.0));

        assertTrue("no metric", metricSemaphore);
    }

    public void testSummary() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        SimpleNode s = new SimpleJavaNode(1);
        s.testingOnly__setBeginLine(10);
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg", "rulesetname"), ctx, s));
        ctx.setSourceCodeFilename("foo2");
        Rule mr = new MockRule("rule1", "rule1", "msg", "rulesetname");
        SimpleNode s1 = new SimpleJavaNode(1);
        s1.testingOnly__setBeginLine(20);
        SimpleNode s2 = new SimpleJavaNode(1);
        s2.testingOnly__setBeginLine(30);
        r.addRuleViolation(new RuleViolation(mr, ctx, s1));
        r.addRuleViolation(new RuleViolation(mr, ctx, s2));
        Map summary = r.getSummary();
        assertEquals(summary.keySet().size(), 2);
        assertTrue(summary.values().contains(new Integer(1)));
        assertTrue(summary.values().contains(new Integer(2)));
    }
*/
}
