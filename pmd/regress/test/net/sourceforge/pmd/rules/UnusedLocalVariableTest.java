package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedLocalVariableRule;

public class UnusedLocalVariableTest extends RuleTst {

    private UnusedLocalVariableRule rule;

    public void setUp() {
        rule = new UnusedLocalVariableRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void test1() throws Throwable {
        runTestFromFile("UnusedLocal1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("UnusedLocal2.java", 1, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("UnusedLocal3.java", 1, rule);
    }

    public void test4() throws Throwable {
        runTestFromFile("UnusedLocal4.java", 0, rule);
    }

    public void test5() throws Throwable {
        runTestFromFile("UnusedLocal5.java", 1, rule);
    }

    public void test6() throws Throwable {
        runTestFromFile("UnusedLocal6.java", 0, rule);
    }

    public void test7() throws Throwable {
        runTestFromFile("UnusedLocal7.java", 0, rule);
    }

    public void test8() throws Throwable {
        runTestFromFile("UnusedLocal8.java", 0, rule);
    }

    public void test9() throws Throwable {
        runTestFromFile("UnusedLocal9.java", 0, rule);
    }

    public void test10() throws Throwable {
        runTestFromFile("UnusedLocal10.java", 0, rule);
    }

    public void test11() throws Throwable {
        runTestFromFile("UnusedLocal11.java", 2, rule);
    }

    public void test12() throws Throwable {
        runTestFromFile("UnusedLocal12.java", 0, rule);
    }

    public void test13() throws Throwable {
        runTestFromFile("UnusedLocal13.java", 0, rule);
    }

    public void test14() throws Throwable {
        runTestFromFile("UnusedLocal14.java", 2, rule);
    }

    public void test15() throws Throwable {
        runTestFromFile("UnusedLocal15.java", 0, rule);
    }
}
