/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 2:45:29 PM
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.rules.junit.JUnitStaticSuiteRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitStaticSuiteRuleTest extends RuleTst {

    public void testNonstatic() throws Throwable {
        runTest("junit/JUnitStaticSuite1.java", 1, new JUnitStaticSuiteRule());
    }

    public void testGoodOK() throws Throwable {
        runTest("junit/JUnitStaticSuite2.java", 0, new JUnitStaticSuiteRule());
    }

    public void testPrivateSuite() throws Throwable {
        runTest("junit/JUnitStaticSuite3.java", 1, new JUnitStaticSuiteRule());
    }
}
