/*
 * User: tom
 * Date: Sep 5, 2002
 * Time: 2:37:46 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.IfStmtsMustUseBracesRule;

public class IfStmtsMustUseBracesRuleTest extends RuleTst {


    public void test1() throws Throwable {
        runTest("IfStmtsMustUseBraces1.java", 1, new IfStmtsMustUseBracesRule());
    }

    public void test2() throws Throwable {
        runTest("IfStmtsMustUseBraces2.java", 0, new IfStmtsMustUseBracesRule());
    }
}
