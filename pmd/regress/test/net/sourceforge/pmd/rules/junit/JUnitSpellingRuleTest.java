/*
 * User: tom
 * Date: Sep 10, 2002
 * Time: 1:15:05 PM
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.junit.JUnitSpellingRule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitSpellingRuleTest extends RuleTst {

    private Rule rule;


    public void setUp() {
        rule = new JUnitSpellingRule();
        rule.setMessage("when you mean ''{0}'', don't do ''{1}''");
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
