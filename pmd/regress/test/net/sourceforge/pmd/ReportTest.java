/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 1:18:30 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.renderers.Renderer;
import net.sourceforge.pmd.renderers.XMLRenderer;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.Report;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReportTest extends TestCase {

    public ReportTest(String name) {
        super(name);
    }

    public void testBasic() {
        Report r = new Report();
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "foo"));
        assertTrue(!r.isEmpty());
    }

    public void testSortedReport() {
        Report r = new Report();
        r.addRuleViolation(new RuleViolation(new MockRule(), 10, "foo"));
        r.addRuleViolation(new RuleViolation(new MockRule(), 20, "bar"));
        Renderer rend = new XMLRenderer();
        String result = rend.render(r);
        assertTrue(result.indexOf("foo") < result.indexOf("bar"));
    }

/*
    public void testRenderXML() {
        Renderer r = new Renderer("xml");
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "foo"));
        String rpt = r.render();
        assertTrue(rpt.indexOf("foo") != -1);
        assertTrue(rpt.indexOf("<pmd>") != -1);
        assertTrue(rpt.indexOf("<file>") != -1);
    }

    public void testRenderHTML() {
        Renderer r = new Renderer("html", "format");
        r.addRuleViolation(new RuleViolation(new MockRule(), 5, "filename"));
        String rpt = r.render();
        assertTrue(rpt.indexOf("format") != -1);
        assertTrue(rpt.indexOf("<table>") != -1);
        assertTrue(rpt.indexOf("filename") != -1);
    }
*/
}
