/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:19:15 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.SwitchStmtsShouldHaveDefaultRule;

public class SwitchStmtsShouldHaveDefaultRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("SwitchStmtsShouldHaveDefault1.java", 1, new SwitchStmtsShouldHaveDefaultRule());
    }

    public void test2() throws Throwable {
        runTest("SwitchStmtsShouldHaveDefault2.java", 0, new SwitchStmtsShouldHaveDefaultRule());
    }

}
