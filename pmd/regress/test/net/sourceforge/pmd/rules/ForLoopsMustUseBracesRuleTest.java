/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:32:02 PM
 */
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
        runTest("ForLoopsNeedBraces1.java",1, rule);
    }

    public void test2() throws Throwable {
        runTest("ForLoopsNeedBraces2.java",0, rule);
    }

    public void test3() throws Throwable {
        runTest("ForLoopsNeedBraces3.java",1, rule);
    }

    public void test4() throws Throwable {
        runTest("ForLoopsNeedBraces4.java",1, rule);
    }

    public void test5() throws Throwable {
        runTest("ForLoopsNeedBraces5.java",1, rule);
    }
}
