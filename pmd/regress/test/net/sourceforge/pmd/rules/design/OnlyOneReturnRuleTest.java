/*
 * User: tom
 * Date: Sep 26, 2002
 * Time: 10:45:10 AM
 */
package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.rules.RuleTst;
import net.sourceforge.pmd.rules.design.OnlyOneReturnRule;

public class OnlyOneReturnRuleTest extends RuleTst {

    public void testTwoReturns() throws Throwable {
        runTest("OnlyOneReturn1.java", 1, new OnlyOneReturnRule());
    }
    public void testOneReturn() throws Throwable {
        runTest("OnlyOneReturn2.java", 0, new OnlyOneReturnRule());
    }
    public void testNoReturns() throws Throwable {
        runTest("OnlyOneReturn3.java", 0, new OnlyOneReturnRule());
    }
    public void testVoidRtn() throws Throwable {
        runTest("OnlyOneReturn4.java", 0, new OnlyOneReturnRule());
    }
}
