/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 1:51:40 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.EmptySwitchStmtRule;

public class EmptySwitchStmtRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("EmptySwitchStmt1.java", 1, new EmptySwitchStmtRule());
    }

    public void test2()  throws Throwable {
        runTest("EmptySwitchStmt2.java", 0, new EmptySwitchStmtRule());
    }
}
