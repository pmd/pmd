/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:19:15 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class SwitchStmtsShouldHaveDefaultRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[not(SwitchLabel[count(*) = 0])]");
    }

    public void test1() throws Throwable {
        runTest("SwitchStmtsShouldHaveDefault1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("SwitchStmtsShouldHaveDefault2.java", 0, rule);
    }

}
