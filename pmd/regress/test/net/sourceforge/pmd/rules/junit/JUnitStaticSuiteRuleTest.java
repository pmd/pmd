/*
 * User: tom
 * Date: Sep 11, 2002
 * Time: 2:45:29 PM
 */
package test.net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.rules.XPathRule;
import net.sourceforge.pmd.Rule;
import test.net.sourceforge.pmd.rules.RuleTst;

public class JUnitStaticSuiteRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//MethodDeclaration[not(@Static='true') or not(@Public='true')][MethodDeclarator/@Image='suite']");
    }

    public void testNonstatic() throws Throwable {
        runTestFromFile("junit/JUnitStaticSuite1.java", 1, rule);
    }

    public void testGoodOK() throws Throwable {
        runTestFromFile("junit/JUnitStaticSuite2.java", 0, rule);
    }

    public void testPrivateSuite() throws Throwable {
        runTestFromFile("junit/JUnitStaticSuite3.java", 1, rule);
    }
}
