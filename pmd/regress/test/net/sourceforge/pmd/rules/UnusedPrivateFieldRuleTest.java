/*
 * User: tom
 * Date: Jun 28, 2002
 * Time: 2:35:51 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedPrivateFieldRule;

public class UnusedPrivateFieldRuleTest extends RuleTst {

    private UnusedPrivateFieldRule rule;

    public void setUp() {
        rule = new UnusedPrivateFieldRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

   public void test1() throws Throwable {
       runTest("UnusedPrivateField1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("UnusedPrivateField2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTest("UnusedPrivateField3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTest("UnusedPrivateField4.java", 0, rule);
    }

    public void test6() throws Throwable {
        runTest("UnusedPrivateField6.java", 0, rule);
    }
    public void test7() throws Throwable {
        runTest("UnusedPrivateField7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTest("UnusedPrivateField8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTest("UnusedPrivateField9.java", 1, rule);
    }

    // TODO
    // this test defines the current behavior of this rule
    // i.e., it doesn't check instance vars in inner classes
    // when that's fixed, this test will break
    // and we should replace the current test with the commented out test
    // TODO
    public void test10() throws Throwable {
        runTest("UnusedPrivateField10.java", 0, rule);
        //runTest("UnusedPrivateField10.java", 1, rule);
    }

    public void test11() throws Throwable {
        runTest("UnusedPrivateField11.java", 1, rule);
    }

    public void test12() throws Throwable {
        runTest("UnusedPrivateField12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTest("UnusedPrivateField13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTest("UnusedPrivateField14.java", 1, rule);
    }
    public void test15() throws Throwable {
        runTest("UnusedPrivateField15.java", 2, rule);
    }

    public void test16() throws Throwable {
        runTest("UnusedPrivateField16.java", 1, rule);
    }

    public void test17() throws Throwable {
        runTest("UnusedPrivateField17.java", 0, rule);
    }

    public void test18() throws Throwable {
        runTest("UnusedPrivateField18.java", 0, rule);
    }

    public void test19() throws Throwable {
        runTest("UnusedPrivateField19.java", 0, rule);
    }

    public void test20() throws Throwable {
        runTest("UnusedPrivateField20.java", 0, rule);
    }
}
