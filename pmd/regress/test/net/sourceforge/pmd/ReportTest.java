/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 1:18:30 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.stat.Metric;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ReportListener;

import java.lang.reflect.Proxy;
import java.lang.reflect.Method;
import java.util.Iterator;

public class ReportTest extends TestCase implements ReportListener {

    private boolean violationSemaphore;
	private boolean metricSemaphore;
	
    public void testBasic() {
        Report r = new Report();
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "foo"));
        assertTrue(!r.isEmpty());
    }

	public void testMetric0() {
		Report r = new Report();	
		assertTrue( !r.hasMetrics() );
	}
	
	public void testMetric1() {
		Report r = new Report();
		assertTrue( !r.hasMetrics() );
		
		r.addMetric( new Metric("m1", 1.0, 2.0, 3.0, 4.0));
		assertTrue( r.hasMetrics() );
		
		Iterator ms = r.metrics();
		assertTrue( ms.hasNext());
		
		Object o = ms.next();
		assertTrue( o instanceof Metric );
		
		Metric m = (Metric) o;
		assertEquals("m1", m.getMetricName());
		assertEquals(1.0, m.getLowValue(), 0.05);
		assertEquals(2.0, m.getHighValue(), 0.05);
		assertEquals(3.0, m.getAverage(), 0.05);
		assertEquals(4.0, m.getStandardDeviation(), 0.05);			
	}
	
	
    // Files are grouped together now.
    public void testSortedReport_File() {
        Report r = new Report();
        r.addRuleViolation(new RuleViolation(new MockRule(), 10, "foo"));
        r.addRuleViolation(new RuleViolation(new MockRule(), 20, "bar"));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue(result.indexOf("bar") < result.indexOf("foo"));
    }

    public void testSortedReport_Line() {
        Report r = new Report();
        r.addRuleViolation(new RuleViolation(new MockRule("rule2", "rule2"), 
					     10, "foo1"));
        r.addRuleViolation(new RuleViolation(new MockRule("rule1", "rule1"), 
					     20, "foo2"));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue(result.indexOf("rule2") < result.indexOf("rule1"));
    }

    public void testListener() {
        Report rpt  = new Report();
        rpt.addListener(this);
        violationSemaphore = false;
        rpt.addRuleViolation(new RuleViolation(new MockRule(), 5, "file"));
        assertTrue(violationSemaphore);
        
        metricSemaphore = false;
        rpt.addMetric( new Metric("test", 0.0, 0.0, 0.0, 0.0 ));
        
        assertTrue(metricSemaphore);
    }

    public void ruleViolationAdded(RuleViolation ruleViolation) {
        violationSemaphore = true;
    }
    
    public void metricAdded( Metric metric ) {
    	metricSemaphore = true;
    }

}
