/*
 * User: tom
 * Date: Jul 19, 2002
 * Time: 11:17:08 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class WhileLoopsMustUseBracesRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//WhileStatement[not(Statement/Block)]");
    }

    public void test1() throws Throwable {
        runTest("WhileLoopsNeedBraces1.java",1, rule);
    }

    public void test2() throws Throwable {
        runTest("WhileLoopsNeedBraces2.java",0, rule);
    }
}
