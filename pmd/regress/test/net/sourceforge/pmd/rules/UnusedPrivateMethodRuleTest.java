/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 12:25:27 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedPrivateMethodRule;

public class UnusedPrivateMethodRuleTest extends RuleTst {
    private UnusedPrivateMethodRule rule;

    public void setUp() {
        rule = new UnusedPrivateMethodRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTest("UnusedPrivateMethod1.java", 0, rule);
    }

    public void test2() throws Throwable {
        runTest("UnusedPrivateMethod2.java", 1, rule);
    }

    public void test3() throws Throwable {
        runTest("UnusedPrivateMethod3.java", 0, rule);
    }

    public void test4() throws Throwable {
        runTest("UnusedPrivateMethod4.java", 1, rule);
    }

    public void test5() throws Throwable {
        runTest("UnusedPrivateMethod5.java", 0, rule);
    }

    public void test6() throws Throwable {
        runTest("UnusedPrivateMethod6.java", 0, rule);
    }
}
