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
        runTestFromFile("UnusedPrivateField1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("UnusedPrivateField2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("UnusedPrivateField3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTestFromFile("UnusedPrivateField4.java", 0, rule);
    }

    public void test6() throws Throwable {
        runTestFromFile("UnusedPrivateField6.java", 0, rule);
    }

    public void test7() throws Throwable {
        runTestFromFile("UnusedPrivateField7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTestFromFile("UnusedPrivateField8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTestFromFile("UnusedPrivateField9.java", 1, rule);
    }

    // TODO
    // this test defines the current behavior of this rule
    // i.e., it doesn't check instance vars in inner classes
    // when that's fixed, this test will break
    // and we should replace the current test with the commented out test
    // TODO
    public void test10() throws Throwable {
        runTestFromFile("UnusedPrivateField10.java", 0, rule);
        //runTestFromFile("UnusedPrivateField10.java", 1, rule);
    }

    public void test11() throws Throwable {
        runTestFromFile("UnusedPrivateField11.java", 1, rule);
    }

    public void test12() throws Throwable {
        runTestFromFile("UnusedPrivateField12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTestFromFile("UnusedPrivateField13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTestFromFile("UnusedPrivateField14.java", 1, rule);
    }

    public void test15() throws Throwable {
        runTestFromFile("UnusedPrivateField15.java", 2, rule);
    }

    public void test16() throws Throwable {
        runTestFromFile("UnusedPrivateField16.java", 1, rule);
    }

    public void test17() throws Throwable {
        runTestFromFile("UnusedPrivateField17.java", 0, rule);
    }

    public void test18() throws Throwable {
        runTestFromFile("UnusedPrivateField18.java", 0, rule);
    }

    public void test19() throws Throwable {
        runTestFromFile("UnusedPrivateField19.java", 0, rule);
    }

    public void test20() throws Throwable {
        runTestFromFile("UnusedPrivateField20.java", 0, rule);
    }
}
