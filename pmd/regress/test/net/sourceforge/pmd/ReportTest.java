/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 1:18:30 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.reports.ReportFactory;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Rule;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReportTest extends TestCase {

    public ReportTest(String name) {
        super(name);
    }

    public void testBasic() {
        ReportFactory rf = new ReportFactory();
        Report r = rf.createReport("xml");
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "foo"));
        assertTrue(!r.isEmpty());
    }

/*
    public void testRenderXML() {
        Report r = new Report("xml");
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "foo"));
        String rpt = r.render();
        assertTrue(rpt.indexOf("foo") != -1);
        assertTrue(rpt.indexOf("<pmd>") != -1);
        assertTrue(rpt.indexOf("<file>") != -1);
    }

    public void testRenderHTML() {
        Report r = new Report("html", "format");
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "filename"));
        String rpt = r.render();
        assertTrue(rpt.indexOf("format") != -1);
        assertTrue(rpt.indexOf("<table>") != -1);
        assertTrue(rpt.indexOf("filename") != -1);
    }
*/
}
