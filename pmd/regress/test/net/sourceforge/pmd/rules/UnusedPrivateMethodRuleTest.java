/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 12:25:27 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedPrivateMethodRule;
import net.sourceforge.pmd.Report;

public class UnusedPrivateMethodRuleTest  extends RuleTst {
    private UnusedPrivateMethodRule rule;

    public void setUp() {
        rule = new UnusedPrivateMethodRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        Report report = process("UnusedPrivateMethod1.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test2() throws Throwable {
        Report report = process("UnusedPrivateMethod2.java", rule);
        assertEquals(1, report.size());
    }

    public void test3() throws Throwable {
        Report report = process("UnusedPrivateMethod3.java", rule);
        assertTrue(report.isEmpty());
    }

    public void test4() throws Throwable {
        Report report = process("UnusedPrivateMethod4.java", rule);
        assertEquals(1, report.size());
    }


   public void test5() throws Throwable {
       Report report = process("UnusedPrivateMethod5.java", rule);
        assertTrue(report.isEmpty());
    }
    public void test6() throws Throwable {
        Report report = process("UnusedPrivateMethod6.java", rule);
        assertTrue(report.isEmpty());
    }
}
