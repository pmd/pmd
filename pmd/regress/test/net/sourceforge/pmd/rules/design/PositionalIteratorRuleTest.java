/*
 * User: tom
 * Date: Sep 4, 2002
 * Time: 11:44:14 AM
 */
package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.rules.design.PositionalIteratorRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class PositionalIteratorRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("PositionalIterator1.java", 1, new PositionalIteratorRule());
    }

    public void test2() throws Throwable {
        runTest("PositionalIterator2.java", 0, new PositionalIteratorRule());
    }

    public void test3() throws Throwable {
        runTest("PositionalIterator3.java", 0, new PositionalIteratorRule());
    }
}
