/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 1:51:40 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class EmptySwitchStmtRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//SwitchStatement[count(*) = 1]");
    }

    public void test1() throws Throwable {
        runTest("EmptySwitchStmt1.java", 1, rule);
    }

    public void test2() throws Throwable {
        runTest("EmptySwitchStmt2.java", 0, rule);
    }
}
