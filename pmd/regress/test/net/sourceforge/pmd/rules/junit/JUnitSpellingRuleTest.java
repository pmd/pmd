/*
 * User: tom
 * Date: Sep 10, 2002
 * Time: 1:15:05 PM
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitSpellingRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclarator[(not(@Image = 'setUp') and translate(@Image, 'SETuP', 'setUp') = 'setUp') or (not(@Image = 'tearDown') and translate(@Image, 'TEARdOWN', 'tearDown') = 'tearDown')][FormalParameters[count(*) = 0]]");
    }


    public void testSetupMisspellings1() throws Throwable {
        runTest("junit/JUnitSpelling1.java", 2, rule);
    }

    public void testTeardownMisspellings() throws Throwable {
        runTest("junit/JUnitSpelling2.java", 2, rule);
    }

    public void testMethodsSpelledOK() throws Throwable {
        runTest("junit/JUnitSpelling3.java", 0, rule);
    }

    public void testUnrelatedMethods() throws Throwable {
        runTest("junit/JUnitSpelling4.java", 0, rule);
    }

    public void testMethodWithParams() throws Throwable {
        runTest("junit/JUnitSpelling5.java", 0, rule);
    }
}
