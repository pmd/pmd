package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class ForLoopsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ForStatement[not(Statement/Block)]");
    }

    public void test1() throws Throwable {
        runTestFromFile("ForLoopsNeedBraces1.java",1, rule);
    }

    public void test2() throws Throwable {
        runTestFromFile("ForLoopsNeedBraces2.java",0, rule);
    }

    public void test3() throws Throwable {
        runTestFromFile("ForLoopsNeedBraces3.java",1, rule);
    }

    public void test4() throws Throwable {
        runTestFromFile("ForLoopsNeedBraces4.java",1, rule);
    }

    public void test5() throws Throwable {
        runTestFromFile("ForLoopsNeedBraces5.java",1, rule);
    }
}
