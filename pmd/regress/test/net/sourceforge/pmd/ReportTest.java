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

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import test.net.sourceforge.pmd.testframework.MockRule;

import java.util.Iterator;
import java.util.Map;

public class ReportTest extends TestCase implements ReportListener {

    private boolean violationSemaphore;
    private boolean metricSemaphore;

    public void testBasic() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg"), 5, ctx));
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


    // Files are grouped together now.
    public void testSortedReport_File() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo");
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg"), 10, ctx));
        ctx.setSourceCodeFilename("bar");
        r.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg"), 20, ctx));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue("sort order wrong", result.indexOf("bar") < result.indexOf("foo"));
    }

    public void testSortedReport_Line() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2", "msg"), 10, ctx));
        ctx.setSourceCodeFilename("foo2");
        r.addRuleViolation(new RuleViolation(new MockRule("rule1", "rule1", "msg"), 20, ctx));
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
        rpt.addRuleViolation(new RuleViolation(new MockRule("name", "desc", "msg"), 5, ctx));
        assertTrue(violationSemaphore);

        metricSemaphore = false;
        rpt.addMetric(new Metric("test", 0, 0.0, 0.0, 0.0, 0.0, 0.0));

        assertTrue("no metric", metricSemaphore);
    }

    public void ruleViolationAdded(RuleViolation ruleViolation) {
        violationSemaphore = true;
    }

    public void metricAdded(Metric metric) {
        metricSemaphore = true;
    }

    public void testSummary() {
        Report r = new Report();
        RuleContext ctx = new RuleContext();
        ctx.setSourceCodeFilename("foo1");
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2", "msg"), 10, ctx));
        ctx.setSourceCodeFilename("foo2");
        Rule mr = new MockRule("rule1", "rule1", "msg");
        r.addRuleViolation(new RuleViolation(mr, 20, ctx));
        r.addRuleViolation(new RuleViolation(mr, 30, ctx));
        Map summary = r.getSummary();
        assertEquals(summary.keySet().size(), 2);
        assertTrue(summary.values().contains(new Integer(1)));
        assertTrue(summary.values().contains(new Integer(2)));
    }
}
