/*
 * User: tom
 * Date: Oct 11, 2002
 * Time: 4:41:46 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.UnusedFormalParameterRule;

public class UnusedFormalParameterRuleTest extends RuleTst {

    private UnusedFormalParameterRule rule;

    public void setUp() {
        rule = new UnusedFormalParameterRule();
        rule.setMessage("Avoid this stuff -> ''{0}''");
    }

    public void testOneParam() throws Throwable {
        runTestFromFile("UnusedFormalParam1.java", 1, rule);
    }

    public void testFullyQualified() throws Throwable {
        runTestFromFile("UnusedFormalParam2.java", 0, rule);
    }

    public void testOneParamWithMethodCall() throws Throwable {
        runTestFromFile("UnusedFormalParam3.java", 0, rule);
    }

    public void testInterface() throws Throwable {
        runTestFromFile("UnusedFormalParam4.java", 0, rule);
    }
}
