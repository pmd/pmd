/*
 * User: tom
 * Date: Sep 26, 2002
 * Time: 10:45:10 AM
 */
package test.net.sourceforge.pmd.rules.design;

import test.net.sourceforge.pmd.rules.RuleTst;
import net.sourceforge.pmd.rules.design.OnlyOneReturnRule;

public class OnlyOneReturnRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("OnlyOneReturn1.java", 1, new OnlyOneReturnRule());
    }
    public void test2() throws Throwable {
        runTest("OnlyOneReturn2.java", 0, new OnlyOneReturnRule());
    }
    public void test3() throws Throwable {
        runTest("OnlyOneReturn3.java", 0, new OnlyOneReturnRule());
    }
}
