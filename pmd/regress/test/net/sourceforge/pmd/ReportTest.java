/*
 * User: tom
 * Date: Jun 14, 2002
 * Time: 1:18:30 PM
 */
package test.net.sourceforge.pmd;

import junit.framework.TestCase;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.Rule;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class ReportTest extends TestCase {

    private static class MyInv implements InvocationHandler {
        private String in;
        public MyInv() {}
        public MyInv(String in) {
            this.in = in;
        }
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            return in;
        }
    }
    public ReportTest(String name) {
        super(name);
    }

    public void testBasic() {
        Report r = new Report("xml", "foo");
        Rule rule = (Rule) Proxy.newProxyInstance(Rule.class.getClassLoader(), new Class[] {Rule.class },  new MyInv());
        r.addRuleViolation(new RuleViolation(rule, 5));
        assertTrue(!r.currentFileHasNoViolations());
    }

    public void testRenderXML() {
        Report r = new Report("xml", "foo");
        InvocationHandler ih = new MyInv("foo");
        Rule rule = (Rule) Proxy.newProxyInstance(Rule.class.getClassLoader(), new Class[] {Rule.class },  ih);
        r.addRuleViolation(new RuleViolation(rule, 5));
        String rpt = r.render();
        assertTrue(rpt.indexOf("foo") != -1);
        assertTrue(rpt.indexOf("<pmd>") != -1);
    }

    public void testRenderText() {
        Report r = new Report("text", "foo");
        InvocationHandler ih = new MyInv("foo");
        Rule rule = (Rule) Proxy.newProxyInstance(Rule.class.getClassLoader(), new Class[] {Rule.class },  ih);
        r.addRuleViolation(new RuleViolation(rule, 5));
        assertTrue(r.render().indexOf("foo") != -1);
    }
}
