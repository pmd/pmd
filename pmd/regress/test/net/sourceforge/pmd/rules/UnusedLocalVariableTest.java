/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 1:43:05 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

public class UnusedLocalVariableTest extends RuleTst {

    private UnusedLocalVariableRule rule;

    public void setUp() {
        rule = new UnusedLocalVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTest("UnusedLocal1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("UnusedLocal2.java", 1, rule);
    }

    public void test3() throws Throwable {
        runTest("UnusedLocal3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTest("UnusedLocal4.java", 0, rule);
    }

    public void test5() throws Throwable {
        runTest("UnusedLocal5.java", 1, rule);
    }

    public void test6() throws Throwable {
        runTest("UnusedLocal6.java", 0, rule);
    }

    public void test7() throws Throwable {
        runTest("UnusedLocal7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTest("UnusedLocal8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTest("UnusedLocal9.java", 0, rule);
    }

    public void test10() throws Throwable {
        runTest("UnusedLocal10.java", 0, rule);
    }

    public void test11() throws Throwable {
        runTest("UnusedLocal11.java", 2, rule);
    }
    public void test12() throws Throwable {
        runTest("UnusedLocal12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTest("UnusedLocal13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTest("UnusedLocal14.java", 2, rule);
    }

    public void test15() throws Throwable {
        runTest("UnusedLocal15.java", 0, rule);
    }
}
