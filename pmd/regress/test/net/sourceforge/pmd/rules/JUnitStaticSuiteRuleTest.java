/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 2:45:29 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.JUnitStaticSuiteRule;

public class JUnitStaticSuiteRuleTest extends RuleTst {

    public JUnitStaticSuiteRuleTest(String name) {
        super(name);
    }

    public void test1() throws Throwable {
        runTest("JUnitStaticSuite1.java", 2, new JUnitStaticSuiteRule());
    }

    public void test2() throws Throwable {
        runTest("JUnitStaticSuite2.java", 0, new JUnitStaticSuiteRule());
    }
}
