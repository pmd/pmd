/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 2:45:29 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.JUnitStaticSuiteRule;

public class JUnitStaticSuiteRuleTest extends RuleTst {

    public void testNonstatic() throws Throwable {
        runTest("JUnitStaticSuite1.java", 1, new JUnitStaticSuiteRule());
    }

    public void testGoodOK() throws Throwable {
        runTest("JUnitStaticSuite2.java", 0, new JUnitStaticSuiteRule());
    }

    public void testPrivateSuite() throws Throwable {
        runTest("JUnitStaticSuite3.java", 1, new JUnitStaticSuiteRule());
    }
}
