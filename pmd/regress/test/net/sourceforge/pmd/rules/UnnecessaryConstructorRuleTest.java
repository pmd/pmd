/*
 * User: tom
 * Date: Nov 1, 2002
 * Time: 9:12:42 AM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class UnnecessaryConstructorRuleTest extends RuleTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//ConstructorDeclaration[1][count(//ConstructorDeclaration)=1][@Public='true'][not(FormalParameters/*)][not(BlockStatement)][not(NameList)]");
    }

    public void testSimpleFailureCase() throws Throwable {
        runTestFromFile("UnnecessaryConstructor1.java", 1, rule);
    }

    public void testPrivate() throws Throwable {
        runTestFromFile("UnnecessaryConstructor2.java", 0, rule);
    }

    public void testHasArgs() throws Throwable {
        runTestFromFile("UnnecessaryConstructor3.java", 0, rule);
    }

    public void testHasBody() throws Throwable {
        runTestFromFile("UnnecessaryConstructor4.java", 0, rule);
    }

    public void testHasExceptions() throws Throwable {
        runTestFromFile("UnnecessaryConstructor5.java", 0, rule);
    }

    public void testMultipleConstructors() throws Throwable {
        runTestFromFile("UnnecessaryConstructor6.java", 0, rule);
    }
}
