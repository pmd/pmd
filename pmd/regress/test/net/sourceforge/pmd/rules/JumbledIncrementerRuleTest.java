/*
 * User: tom
 * Date: Sep 27, 2002
 * Time: 4:18:43 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class JumbledIncrementerRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty(
            "xpath",
            "//ForStatement[ForUpdate//Name/@Image = ancestor::ForStatement/ForInit//VariableDeclaratorId/@Image]");
    }

    public void test1() throws Throwable {
        runTest("JumbledIncrementerRule1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("JumbledIncrementerRule2.java", 0, rule);
    }

    public void test3() throws Throwable {
        runTest("JumbledIncrementerRule3.java", 0, rule);
    }
}
