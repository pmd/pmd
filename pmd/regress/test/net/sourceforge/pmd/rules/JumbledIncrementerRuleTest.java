/*
 * User: tom
 * Date: Sep 27, 2002
 * Time: 4:18:43 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.JumbledIncrementerRule;

public class JumbledIncrementerRuleTest extends RuleTst {

    public void test1() throws Throwable {
        runTest("JumbledIncrementerRule1.java", 1, new JumbledIncrementerRule());
    }

    public void test2() throws Throwable {
        runTest("JumbledIncrementerRule2.java", 0, new JumbledIncrementerRule());
    }

    public void test3() throws Throwable {
        runTest("JumbledIncrementerRule3.java", 0, new JumbledIncrementerRule());
    }
}
