/*
 * User: tom
 * Date: Sep 26, 2002
 * Time: 10:45:10 AM
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.OnlyOneReturnRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class OnlyOneReturnRuleTest extends RuleTst {

    public void testTwoReturns() throws Throwable {
        runTestFromFile("OnlyOneReturn1.java", 1, new OnlyOneReturnRule());
    }

    public void testOneReturn() throws Throwable {
        runTestFromFile("OnlyOneReturn2.java", 0, new OnlyOneReturnRule());
    }

    public void testNoReturns() throws Throwable {
        runTestFromFile("OnlyOneReturn3.java", 0, new OnlyOneReturnRule());
    }

    public void testVoidRtn() throws Throwable {
        runTestFromFile("OnlyOneReturn4.java", 0, new OnlyOneReturnRule());
    }

    public void testFinally() throws Throwable {
        runTestFromFile("OnlyOneReturn5.java", 0, new OnlyOneReturnRule());
    }

    public void testReturnInsideAnonymousInnerClass() throws Throwable {
        runTestFromFile("OnlyOneReturn6.java", 0, new OnlyOneReturnRule());
    }

}
